package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.vkEnumerateDeviceExtensionProperties;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceMemoryProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;
import static org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceFeatures2;
import static org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceProperties2;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import racoonman.r3d.render.api.types.QueueType;
import racoonman.r3d.render.natives.IHandle;
	
class PhysicalDevice implements IHandle {
	private VkPhysicalDevice device;
	
	private VkExtensionProperties.Buffer extensions;
	private VkPhysicalDeviceMemoryProperties memProps;
	private VkPhysicalDeviceFeatures2 features;
	private VkPhysicalDeviceProperties2 deviceProps;
	private VkQueueFamilyProperties.Buffer queueFamilyProps;
	
	public PhysicalDevice(VkPhysicalDevice device) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			
			IntBuffer iBuf = stack.mallocInt(1);
			this.deviceProps = VkPhysicalDeviceProperties2.calloc().sType$Default();
			vkGetPhysicalDeviceProperties2(device, this.deviceProps);
			
			vkAssert(vkEnumerateDeviceExtensionProperties(device, (String) null, iBuf, null), "Error enumerating device properties");
			
			this.extensions = VkExtensionProperties.calloc(iBuf.get(0));
			vkAssert(vkEnumerateDeviceExtensionProperties(device, (String) null, iBuf, this.extensions), "Error retrieving device extension properties");
			
			vkGetPhysicalDeviceQueueFamilyProperties(device, iBuf, null);
			this.queueFamilyProps = VkQueueFamilyProperties.calloc(iBuf.get(0));
			vkGetPhysicalDeviceQueueFamilyProperties(device, iBuf, this.queueFamilyProps);
			
			this.features = VkPhysicalDeviceFeatures2.calloc().sType$Default();
			vkGetPhysicalDeviceFeatures2(device, this.features);
			
			this.memProps = VkPhysicalDeviceMemoryProperties.calloc();
			vkGetPhysicalDeviceMemoryProperties(device, this.memProps);
		}
	}
	
	public VkPhysicalDevice get() {
		return this.device;
	}
	
	public VkPhysicalDeviceProperties2 getProperties() {
		return this.deviceProps;
	}
	
	public VkExtensionProperties.Buffer getExtensions() {
		return this.extensions;
	}
	
	public VkQueueFamilyProperties.Buffer getQueueFamilyProperties() {
		return this.queueFamilyProps;
	}
	
	public VkPhysicalDeviceFeatures2 getFeatures() {
		return this.features;
	}
	
	public VkPhysicalDeviceMemoryProperties getMemoryProperties() {
		return this.memProps;
	}
	
	public String getName() {
		return this.deviceProps.properties().deviceNameString();
	}
	
	public int align(int size) {
		long minUboAlignment = this.deviceProps.properties().limits().minUniformBufferOffsetAlignment();
        long mult = size / minUboAlignment + 1;
        return (int) (mult * minUboAlignment);
    }
	
	@Override
	public long asLong() {
		return this.device.address();
	}
	
	@Override
	public void free() {
		this.extensions.free();
		this.memProps.free();
		this.features.free();
		this.deviceProps.free();
		this.queueFamilyProps.free();
	}
	
	public boolean hasExtension(String name) {
		int extensionCount = this.extensions != null ? this.extensions.capacity() : 0;
		
		for(int i = 0; i < extensionCount; i++) {
			String extension = this.extensions.get(i).extensionNameString();
			
			if(name.equals(extension)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasQueueFamily(QueueType queue) {
		int queueFamilyCount = this.queueFamilyProps != null ? this.queueFamilyProps.capacity() : 0;
		
		for(int i = 0; i < queueFamilyCount; i++) {
			VkQueueFamilyProperties properties = this.queueFamilyProps.get(i);
			
			if((properties.queueFlags() & queue.getVkType()) != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static PhysicalDevice findPhysicalDevice(Vulkan vulkan) {
		return findPhysicalDevice(vulkan, null);
	}
	
	public static PhysicalDevice findPhysicalDevice(Vulkan vulkan, String preferredName) {
		PhysicalDevice[] devices = getPhysicalDevices(vulkan);
		PhysicalDevice result = devices[0];
		
		try {
			for(PhysicalDevice device : devices) {
				if(device.getName().equals(preferredName)) {
					result = device;
					break;
				}
			}
		} finally {
			for(PhysicalDevice device : devices) {
				if(device != result) {
					device.free();
				}
			}
		}
		
		return result;
	}
	
	public static PhysicalDevice[] getPhysicalDevices(Vulkan vulkan) {
		try(MemoryStack stack = stackPush()) {
			PointerBuffer devicePointers = getPhysicalDevices(vulkan, stack);
			int deviceCount = devicePointers != null ? devicePointers.capacity() : 0;
			
			if(deviceCount <= 0) {
				throw new IllegalStateException("Unable to locate physical device");
			}
			
			List<PhysicalDevice> devices = new ArrayList<>();
			
			for(int i = 0; i < deviceCount; i++) {
				VkPhysicalDevice vkDevice = new VkPhysicalDevice(devicePointers.get(i), vulkan.get());
				PhysicalDevice device = new PhysicalDevice(vkDevice);
			
				if(device.hasExtension(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME)) {
					for(QueueType family : QueueType.values()) {
						if(!device.hasQueueFamily(family)) {
							continue;
						}
					}
					
					devices.add(device);
				}
			}
			
			return devices.toArray(PhysicalDevice[]::new);
		}
	}

	private static PointerBuffer getPhysicalDevices(Vulkan vulkan, MemoryStack stack) {
		VkInstance instance = vulkan.get();
		
		IntBuffer countBuf = stack.mallocInt(1);
		vkAssert(vkEnumeratePhysicalDevices(instance, countBuf, null), "Error enumerating physical devices");
		int deviceCount = countBuf.get(0);
		PointerBuffer devices = stack.mallocPointer(deviceCount);
		
		vkAssert(vkEnumeratePhysicalDevices(instance, countBuf, devices), "Error retrieving physical devices");
		return devices;
	}
}
