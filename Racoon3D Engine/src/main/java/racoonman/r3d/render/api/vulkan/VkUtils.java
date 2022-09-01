package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_MAX_MEMORY_TYPES;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkMemoryType;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.types.Aspect;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.util.ArrayUtil;

class VkUtils {
	
	public static void vkAssert(int status, String error) {
		if(status != VK_SUCCESS) {
			System.err.println(error + ": " + status);
		}
	}

	public static void copy(CommandBuffer cmdBuffer, IDeviceBuffer src, IDeviceBuffer dst) {
		if(src.isHostVisible() && dst.isHostVisible()) {
			boolean srcMapped = src.isMapped();
			boolean dstMapped = dst.isMapped();
			
			dst.asByteBuffer().put(src.asByteBuffer());
			
			if(!srcMapped) {
				src.unmap();
			}
			
			if(!dstMapped) {
				dst.unmap();
			}
		} else {
			try(MemoryStack stack = stackPush()) {
				VkBufferCopy.Buffer copy = VkBufferCopy.calloc(1, stack);
				copy.srcOffset(src.getOffset());
				copy.dstOffset(dst.getOffset());
				copy.size(src.size());
				
				cmdBuffer.copyBuffer(src, dst, copy);
			}		
		}
	}
	
	public static Aspect[] getAspect(ImageUsage... usage) {
		List<Aspect> aspects = new ArrayList<>();
		
		if(ArrayUtil.has(usage, ImageUsage.COLOR)) {
			aspects.add(Aspect.COLOR);
		} else if(ArrayUtil.has(usage, ImageUsage.DEPTH_STENCIL)) {
			aspects.add(Aspect.DEPTH);
			aspects.add(Aspect.STENCIL);
		}
		
		return aspects.toArray(Aspect[]::new);
	}
	
	public static int getMemoryType(PhysicalDevice device, int typeBits, int reqsMask) {
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
