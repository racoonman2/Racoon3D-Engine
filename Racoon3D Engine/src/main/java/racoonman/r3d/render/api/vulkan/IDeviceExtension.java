package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTMultiDraw;
import org.lwjgl.vulkan.KHRDynamicRendering;
import org.lwjgl.vulkan.KHRPushDescriptor;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceDynamicRenderingFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceMultiDrawFeaturesEXT;

public interface IDeviceExtension {
	public static final IDeviceExtension KHR_SWAPCHAIN = simple(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME);
	public static final IDeviceExtension DYNAMIC_RENDERING = feature(KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME, true, VkPhysicalDeviceDynamicRenderingFeatures::calloc, VkPhysicalDeviceDynamicRenderingFeatures::sType$Default, VkPhysicalDeviceDynamicRenderingFeatures::dynamicRendering, VkDeviceCreateInfo::pNext);
	public static final IDeviceExtension PUSH_DESCRIPTOR = simple(KHRPushDescriptor.VK_KHR_PUSH_DESCRIPTOR_EXTENSION_NAME);
	// allocating this on the stack causes the program to hang for some reason
	public static final IDeviceExtension MULTI_DRAW = feature(EXTMultiDraw.VK_EXT_MULTI_DRAW_EXTENSION_NAME, true, (stack) -> VkPhysicalDeviceMultiDrawFeaturesEXT.calloc(), VkPhysicalDeviceMultiDrawFeaturesEXT::sType$Default, VkPhysicalDeviceMultiDrawFeaturesEXT::multiDraw, VkDeviceCreateInfo::pNext);
	
	void apply(VkDeviceCreateInfo createInfo);
	
	String getName();
	
	static IDeviceExtension simple(String name) {
		return new IDeviceExtension() {
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public void apply(VkDeviceCreateInfo createInfo) {
				// NOOP
			}
		};
	}
	
	static <T> IDeviceExtension feature(String name, boolean enabled, Function<MemoryStack, T> calloc, Consumer<T> sType, BiConsumer<T, Boolean> enable, BiConsumer<VkDeviceCreateInfo, T> pNext) {
		return new IDeviceExtension() {
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public void apply(VkDeviceCreateInfo createInfo) {
				try(MemoryStack stack = stackPush()) {
					T t = calloc.apply(stack);
					sType.accept(t);
					enable.accept(t, enabled);
					pNext.accept(createInfo, t);
				}
			}
		};
	}
}
