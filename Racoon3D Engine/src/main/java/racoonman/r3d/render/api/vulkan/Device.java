package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;
import static org.lwjgl.vulkan.VK10.vkDestroyDevice;
import static org.lwjgl.vulkan.VK10.vkDeviceWaitIdle;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.FloatBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import racoonman.r3d.render.api.vulkan.memory.Allocator;
import racoonman.r3d.render.natives.IHandle;

//TODO add vkCreate* functions
public class Device implements IHandle {
	private VkDevice device;
	private PhysicalDevice physicalDevice;
	private Allocator memoryAllocator;
	
	public Device(Vulkan vulkan, PhysicalDevice physicalDevice, IDeviceExtension...extensions) {
		try(MemoryStack stack = stackPush()) {
			this.physicalDevice = physicalDevice;
	
			PointerBuffer required = stack.mallocPointer(extensions.length);
			
			for(int i = 0; i < extensions.length; i++) {
				required.put(i, stack.UTF8(extensions[i].getName()));
			}

			VkPhysicalDeviceFeatures2 features = physicalDevice.getFeatures();
			
			VkQueueFamilyProperties.Buffer queuePropsBuf = physicalDevice.getQueueFamilyProperties();
			
			int queueFamilyCount = queuePropsBuf.capacity();
			VkDeviceQueueCreateInfo.Buffer queueCreationBuf = VkDeviceQueueCreateInfo.calloc(queueFamilyCount, stack);
			
			for(int i = 0; i < queueFamilyCount; i++) {
				FloatBuffer priorities = stack.callocFloat(queuePropsBuf.get(i).queueCount());
				
				queueCreationBuf.get(i)
					.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
					.queueFamilyIndex(i)
					.pQueuePriorities(priorities);
			}

			VkDeviceCreateInfo info = VkDeviceCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
				.ppEnabledExtensionNames(required)
				.pQueueCreateInfos(queueCreationBuf)
				.pNext(features);
			
			for(IDeviceExtension extension : extensions) {
				extension.apply(info);
			}
			
			VkPhysicalDevice device = physicalDevice.get();
			PointerBuffer handle = stack.mallocPointer(1);
			vkAssert(vkCreateDevice(device, info, null, handle), "Error creating logical device");
			this.device = new VkDevice(handle.get(0), device, info);
			
			this.memoryAllocator = new Allocator(vulkan, physicalDevice, this);
		}
	}
	
	public VkPhysicalDeviceFeatures2 getFeatures() {
		return this.physicalDevice.getFeatures();
	}
	
	public PhysicalDevice getPhysicalDevice() {
		return this.physicalDevice;
	}
	
	public VkDevice get() {
		return this.device;
	}
	
	public Allocator getMemoryAllocator() {
		return this.memoryAllocator;
	}
	
	public void waitIdle() {
		vkDeviceWaitIdle(this.device);
	}
	
	@Override
	public long asLong() {
		return this.device.address();
	}
	
	@Override
	public void free() {
		this.memoryAllocator.free();
		
		vkDestroyDevice(this.device, null);
	}
}
