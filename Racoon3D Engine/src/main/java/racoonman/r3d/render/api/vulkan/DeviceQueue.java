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
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.api.types.Work;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.ArrayUtil;
import racoonman.r3d.util.Holder;
import racoonman.r3d.util.IPair;

abstract class DeviceQueue implements IDispatchableHandle<VkQueue> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DeviceQueue.class);
	
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
				.pCommandBuffers(toPointerBuffer(submission.commandBuffers(), stack))
				.pSignalSemaphores(toLongBuffer(submission.signals(), stack));
			
			Set<IPair<IDeviceSync, Stage>> waits = submission.waits();
			info.waitSemaphoreCount(waits.size())
				.pWaitSemaphores(toLongBuffer(waits.stream().map(IPair::left).toList(), stack))
				.pWaitDstStageMask(stack.ints(IVkType.bitMask(submission.waits().stream().map(IPair::right).toList())));
			
			Holder<IHostSync> fence = submission.hostSync();
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
	
	@Override
	public VkQueue get() {
		return this.queue;
	}
	
	@Override
	public void free() {
		//queues are freed by the device
	}

	public int getQueueFamilyIndex() {
		return this.queueFamilyIndex;
	}
	
	public int getQueueIndex() {
		return this.queueIndex;
	}

	protected abstract int getIndexForFamily();

	public static final DeviceQueue work(Device device, int index, Work... flags) {
		return new DeviceQueue(device, index) {

			@Override
			protected int getIndexForFamily() {
				VkQueueFamilyProperties.Buffer queueProps = device.getPhysicalDevice().getQueueFamilyProperties();
				
				int queueFamilyCount = queueProps.capacity();
				
				//first pass looks for dedicated queues
				for(int i = 0; i < queueFamilyCount; i++) {
					VkQueueFamilyProperties properties = queueProps.get(i);
					Work[] queueFlags = IVkType.toArray(properties.queueFlags(), Work.values(), Work[]::new);
					
					if(ArrayUtil.softEquals(queueFlags, flags)) {
						LOGGER.info("Found dedicated queue for flags {}", Arrays.toString(flags));
						return i;
					}
				}
				
				//second pass looks for generic queues
				search:
				for(int i = 0; i < queueFamilyCount; i++) {
					VkQueueFamilyProperties properties = queueProps.get(i);
					Work[] queueFlags = IVkType.toArray(properties.queueFlags(), Work.values(), Work[]::new);
					
					for(Work flag : flags) {
						if(!ArrayUtil.has(queueFlags, flag)) {
							continue search;
						}
					}

					LOGGER.info("Found generic queue; Requested flags {}, queue flags {}", Arrays.toString(flags), Arrays.toString(queueFlags));
					return i;
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

	static LongBuffer toLongBuffer(Collection<? extends IHandle> handles, MemoryStack stack) {
		LongBuffer longs = stack.mallocLong(handles.size());
	
		for(IHandle handle : handles) {
			longs.put(handle.asLong());
		}
		
		longs.rewind();
		return longs;
	}
	
	static PointerBuffer toPointerBuffer(Collection<? extends IDispatchableHandle<?>> handles, MemoryStack stack) {
		PointerBuffer pointers = stack.mallocPointer(handles.size());

		for(IDispatchableHandle<?> handle : handles) {
			pointers.put(handle.get());
		}

		pointers.rewind();
		return pointers;
	}
}
