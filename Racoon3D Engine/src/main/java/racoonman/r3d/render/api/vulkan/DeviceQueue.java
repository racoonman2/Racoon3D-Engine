package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.VK_ERROR_DEVICE_LOST;
import static org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY;
import static org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_HOST_MEMORY;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_TRUE;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.IntBuffer;
import java.util.Set;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSubmitInfo;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.IFence;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.QueueType;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.util.Holder;
import racoonman.r3d.util.IPair;

abstract class DeviceQueue {
	private Device device;
	private VkQueue queue;
	private int queueFamilyIndex;
	private int queueIndex;
	
	private DeviceQueue(Device device, int queueIndex) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.queueFamilyIndex = this.getIndexForFamily();
			this.queueIndex = queueIndex;
			
			VkDevice vkDevice = device.get();
			PointerBuffer queuePointer = stack.mallocPointer(1);
			vkGetDeviceQueue(vkDevice, this.queueFamilyIndex, queueIndex, queuePointer);
			long handle = queuePointer.get(0);
			this.queue = new VkQueue(handle, vkDevice);
		}
	}
	
	public void submit(QueueSubmission submission) {
		try(MemoryStack stack = stackPush()) {
			VkSubmitInfo info = VkSubmitInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
				.pCommandBuffers(QueueSubmission.toPointerBuffer(submission.commandBuffers(), stack))
				.pSignalSemaphores(QueueSubmission.toLongBuffer(submission.signals(), stack));
			
			Set<IPair<IContextSync, Stage>> waits = submission.waits();
			info.waitSemaphoreCount(waits.size())
				.pWaitSemaphores(QueueSubmission.toLongBuffer(waits.stream().map(IPair::left).toList(), stack))
				.pWaitDstStageMask(stack.ints(IVkType.asInts(submission.waits().stream().map(IPair::right).toList())));
			
			Holder<IFence> fence = submission.fence();
			int status = vkQueueSubmit(this.queue, info, fence.isPresent() ? fence.getValue().asLong() : 0L);
			
			if(status != VK_SUCCESS) {
				R3DRuntime.critical(switch(status) {
					case VK_ERROR_OUT_OF_DEVICE_MEMORY -> "out of device RAM";
					case VK_ERROR_DEVICE_LOST -> "device lost";
					case VK_ERROR_OUT_OF_HOST_MEMORY -> "out of host RAM";
					default -> "Unknown";
				});	
			}
		}
	}

	public void waitIdle() {
		vkQueueWaitIdle(this.queue);
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public VkQueue get() {
		return this.queue;
	}

	public int getQueueFamilyIndex() {
		return this.queueFamilyIndex;
	}
	
	public int getQueueIndex() {
		return this.queueIndex;
	}

	protected abstract int getIndexForFamily();

	public static final DeviceQueue work(Device device, QueueType family, int index) {
		return new DeviceQueue(device, index) {

			@Override
			protected int getIndexForFamily() {
				VkQueueFamilyProperties.Buffer queueProps = device.getPhysicalDevice().getQueueFamilyProperties();
				
				int queueFamilyCount = queueProps.capacity();
				int vkQueueFamily = family.getVkType();
				
				for(int i = 0; i < queueFamilyCount; i++) {
					VkQueueFamilyProperties properties = queueProps.get(i);
					
					if((properties.queueFlags() & vkQueueFamily) == vkQueueFamily) {
						return i;
					}
				}
				
				throw new IllegalStateException("Unable to lookup queue family index");
			}
		};
	}
	
	public static final DeviceQueue present(Device device, IWindowSurface surface, int index) {
		return new DeviceQueue(device, index) {
			
			@Override
			protected int getIndexForFamily() {
				try(MemoryStack stack = stackPush()) {
					PhysicalDevice physicalDevice = device.getPhysicalDevice();
					VkQueueFamilyProperties.Buffer queueProps = physicalDevice.getQueueFamilyProperties();
					int queueFamilyCount = queueProps.capacity();
					
					IntBuffer iBuf = stack.mallocInt(1);
					
					for(int i = 0; i < queueFamilyCount; i++) {
						vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.get(), i, surface.asLong(), iBuf);
						
						if(iBuf.get(0) == VK_TRUE) {
							return i;
						}
					}
					
					throw new IllegalStateException("Error retrieving present queue family index");
				}
			}
		};
	}
}
