package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageMemoryBarrier;

import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.vulkan.types.Access;
import racoonman.r3d.render.api.vulkan.types.Aspect;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;

// I hate pipeline barriers
public class PipelineBarriers {
	
	public static void preWindowDraw(IAttachment attachment, CommandBuffer cmdBuf) {
		try(MemoryStack stack = stackPush()) {
			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack)
				.sType$Default()
				.dstAccessMask(IVkType.bitMask(Access.COLOR_WRITE))
				.oldLayout(ImageLayout.UNDEFINED.getVkType())
				.newLayout(ImageLayout.COLOR_OPTIMAL.getVkType())
				.image(attachment.getView().getImage().asLong())
				.subresourceRange((range) -> range
					.aspectMask(IVkType.bitMask(Aspect.COLOR))
					.baseMipLevel(0)
					.levelCount(1)
					.baseArrayLayer(0)
					.layerCount(1));
			cmdBuf.pipelineBarrier(IVkType.bitMask(PipelineStage.TOP_OF_PIPE), IVkType.bitMask(PipelineStage.COLOR_ATTACHMENT_OUTPUT), IVkType.none(), null, null, barrier);
		}
	}
	
	public static void postWindowDraw(IAttachment attachment, CommandBuffer cmdBuf) {
		try(MemoryStack stack = stackPush()) {
			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack)
				.sType$Default()
				.srcAccessMask(IVkType.bitMask(Access.COLOR_WRITE))
				.oldLayout(ImageLayout.COLOR_OPTIMAL.getVkType())
				.newLayout(ImageLayout.PRESENT_SRC_KHR.getVkType())
				.image(attachment.getView().getImage().asLong())
				.subresourceRange((range) -> range
					.aspectMask(IVkType.bitMask(Aspect.COLOR))
					.baseMipLevel(0)
					.levelCount(1)
					.baseArrayLayer(0)
					.layerCount(1));
				
			cmdBuf.pipelineBarrier(IVkType.bitMask(PipelineStage.COLOR_ATTACHMENT_OUTPUT), IVkType.bitMask(PipelineStage.BOTTOM_OF_PIPE), IVkType.none(), null, null, barrier);
		}
	}
	
	public static void preDepthDraw(IAttachment attachment, CommandBuffer cmdBuf) {
		try(MemoryStack stack = stackPush()) {
			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack)
				.sType$Default()
				.dstAccessMask(IVkType.bitMask(Access.DEPTH_STENCIL_WRITE))
				.oldLayout(ImageLayout.UNDEFINED.getVkType())
				.newLayout(ImageLayout.DEPTH_STENCIL_OPTIMAL.getVkType())
				.image(attachment.getView().getImage().asLong())
				.subresourceRange((range) -> range
					.aspectMask(IVkType.bitMask(Aspect.DEPTH, Aspect.STENCIL))
					.baseMipLevel(0)
					.levelCount(1)
					.baseArrayLayer(0)
					.layerCount(1));
				
			cmdBuf.pipelineBarrier(IVkType.bitMask(PipelineStage.EARLY_FRAGMENT_TESTS, PipelineStage.LATE_FRAGMENT_TESTS), IVkType.bitMask(PipelineStage.EARLY_FRAGMENT_TESTS, PipelineStage.LATE_FRAGMENT_TESTS), IVkType.none(), null, null, barrier);
		}
	}
	
	public static void noop(IAttachment attachment, CommandBuffer cmdBuf) {
	}
}
