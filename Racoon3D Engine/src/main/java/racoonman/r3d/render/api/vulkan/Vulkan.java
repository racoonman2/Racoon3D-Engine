package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkLayerProperties;

import com.google.common.collect.ImmutableList;

import racoonman.r3d.core.util.Version;

class Vulkan implements IDispatchableHandle<VkInstance> {
	private VkInstance instance;
	private Version apiVersion;
	private Version engineVersion;
	private String engineName;
	private String appName;
	private DebugMessengerUtils debugMessenger;
	
	public Vulkan(Version apiVersion, Version engineVersion, String engineName, String appName, boolean validate) {
		try(MemoryStack stack = stackPush()) {
			this.apiVersion = apiVersion;
			this.engineVersion = engineVersion;
			this.engineName = engineName;
			this.appName = appName;

			if(!glfwVulkanSupported()) {
				throw new IllegalStateException("Vulkan is not supported");
			}
			
			VkApplicationInfo info = VkApplicationInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
				.pEngineName(stack.UTF8(engineName))
				.pApplicationName(stack.UTF8(appName))
				.apiVersion(apiVersion.pack())
				.engineVersion(engineVersion.pack());

			PointerBuffer layers = getLayerPointers(validate, stack);
			boolean supportsValidation = layers != null;
			PointerBuffer requiredExtensions = getRequiredExtensions(supportsValidation, stack);

			VkInstanceCreateInfo instanceInfo = VkInstanceCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
				.pApplicationInfo(info)
				.ppEnabledLayerNames(layers)
				.ppEnabledExtensionNames(requiredExtensions);
			
			PointerBuffer instanceBuf = stack.mallocPointer(1);
			vkAssert(vkCreateInstance(instanceInfo, null, instanceBuf), "Error intializing vulkan instance");
			this.instance = new VkInstance(instanceBuf.get(0), instanceInfo);

			this.debugMessenger = new DebugMessengerUtils(this, supportsValidation);
		}
	}

	public Version getApiVersion() {
		return this.apiVersion;
	}
	
	public Version getEngineVersion() {
		return this.engineVersion;
	}
	
	public String getEngineName() {
		return this.engineName;
	}
	
	public String getAppName() {
		return this.appName;
	}
	
	@Override
	public VkInstance get() {
		return this.instance;
	}
	
	@Override
	public void free() {
		this.debugMessenger.free();
		
		vkDestroyInstance(this.instance, null);
	}

	private static PointerBuffer getRequiredExtensions(boolean validate, MemoryStack stack) {
		PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();
		PointerBuffer requiredExtensions;
		
		if(validate) {
			ByteBuffer debugUtils = stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
			requiredExtensions = stack.mallocPointer(glfwExtensions.remaining() + 1);
			requiredExtensions.put(glfwExtensions).put(debugUtils);
		} else {
			requiredExtensions = stack.mallocPointer(glfwExtensions.remaining());
			requiredExtensions.put(glfwExtensions);
		}
		
		return requiredExtensions.flip();
	}
	
	private static PointerBuffer getLayerPointers(boolean validate, MemoryStack stack) {
		List<String> layers = getValidationLayers();
		boolean supported = validate && layers.size() > 0;
		
		PointerBuffer requiredLayers = null;
		if(supported) {
			int layerCount = layers.size();
			
			requiredLayers = stack.mallocPointer(layerCount);
			
			for(int i = 0; i < layerCount; i++) {
				requiredLayers.put(i, stack.ASCII(layers.get(i)));
			}
		}
		
		return requiredLayers;
	}
	
	private static List<String> getValidationLayers() {
		try(MemoryStack stack = stackPush()) {
			List<String> layers = new ArrayList<>();
			
			IntBuffer layerCountBuf = stack.callocInt(1);
			vkEnumerateInstanceLayerProperties(layerCountBuf, null);
			
			int layerCount = layerCountBuf.get(0);
			
			VkLayerProperties.Buffer properties = VkLayerProperties.calloc(layerCount, stack);
			vkEnumerateInstanceLayerProperties(layerCountBuf, properties);
			
			for(int i = 0; i < layerCount; i++) {
				VkLayerProperties props = properties.get(i);
				String layer = props.layerNameString();
				
				layers.add(layer);
			}
			
			if (layers.contains("VK_LAYER_KHRONOS_validation")) {
				return ImmutableList.of("VK_LAYER_KHRONOS_validation");
			}

			if (layers.contains("VK_LAYER_LUNARG_standard_validation")) {
				System.out.println("ads");
				return ImmutableList.of("VK_LAYER_LUNARG_standard_validation");
			}

			List<String> requested = ImmutableList.of(
				"VK_LAYER_GOOGLE_threading",
				"VK_LAYER_LUNARG_parameter_validation",
				"VK_LAYER_LUNARG_object_tracker",
				"VK_LAYER_LUNARG_core_validation",
				"VK_LAYER_GOOGLE_unique_objects"
			);
			
			return requested.stream().filter(layers::contains).toList();
		}
	}
}
