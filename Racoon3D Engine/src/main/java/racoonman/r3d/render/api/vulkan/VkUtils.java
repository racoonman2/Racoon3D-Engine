package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MAX_MEMORY_TYPES;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import org.lwjgl.vulkan.VkMemoryType;

import racoonman.r3d.render.api.vulkan.types.Aspect;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;

public class VkUtils {
	
	public static void vkAssert(int status, String error) {
		if(status != VK_SUCCESS) {
			System.err.println(error + ": " + status);
		}
	}

	public static Aspect getAspect(ImageUsage... usage) {
		Aspect aspect = null;
		int usageType = IVkType.bitMask(usage);
		
		if((usageType & VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) == VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) {
			aspect = Aspect.COLOR;
		} else if((usageType & VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT) == VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT) {
			aspect = Aspect.DEPTH;
		}
		
		return aspect;
	}
	
	public static int getMemType(PhysicalDevice device, int typeBits, int reqsMask) {
		VkMemoryType.Buffer types = device.getMemoryProperties().memoryTypes();
		
		for(int i = 0; i < VK_MAX_MEMORY_TYPES; i++) {
			if((typeBits & 1) == 1 && (types.get(i).propertyFlags() & reqsMask) == reqsMask) {
				return i;
			}
			
			typeBits >>= 1;
		}
		
		throw new IllegalStateException("Unable to find memory type");
	}
}
