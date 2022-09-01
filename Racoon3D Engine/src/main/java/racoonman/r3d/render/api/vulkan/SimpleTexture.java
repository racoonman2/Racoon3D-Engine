package racoonman.r3d.render.api.vulkan;
//package racoonman.r3d.render.api.vulkan;
//
//import static org.lwjgl.system.MemoryStack.stackPush;
//import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT;
//import static org.lwjgl.vulkan.VK10.VK_ACCESS_TRANSFER_READ_BIT;
//import static org.lwjgl.vulkan.VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
//import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
//import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
//import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
//import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
//import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL;
//import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
//import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
//import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
//import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
//import static org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED;
//import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER;
//import static org.lwjgl.vulkan.VK10.vkCmdBlitImage;
//import static org.lwjgl.vulkan.VK10.vkCmdCopyBufferToImage;
//import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
//
//import org.lwjgl.system.MemoryStack;
//import org.lwjgl.vulkan.VkBufferImageCopy;
//import org.lwjgl.vulkan.VkCommandBuffer;
//import org.lwjgl.vulkan.VkImageBlit;
//import org.lwjgl.vulkan.VkImageMemoryBarrier;
//import org.lwjgl.vulkan.VkImageSubresourceRange;
//import org.lwjgl.vulkan.VkOffset3D;
//
//import racoonman.r3d.render.RenderContext;
//import racoonman.r3d.render.api.objects.IDeviceBuffer;
//import racoonman.r3d.render.api.objects.TextureState;
//import racoonman.r3d.render.api.vulkan.Image.ImageBuilder;
//import racoonman.r3d.render.api.vulkan.ImageView.ImageViewBuilder;
//import racoonman.r3d.render.api.vulkan.types.Format;
//import racoonman.r3d.render.api.vulkan.types.ImageLayout;
//import racoonman.r3d.render.api.vulkan.types.ImageUsage;
//import racoonman.r3d.render.api.vulkan.types.PipelineStage;
//import racoonman.r3d.render.core.RenderService;
//import racoonman.r3d.render.core.RenderSystem;
//import racoonman.r3d.render.util.NativeImage;
//import racoonman.r3d.util.math.Mathf;
//
//public class SimpleTexture implements ITexture {
//	private int width;
//	private int height;
//	private int mipLevels;
//	private Image image;
//	private ImageView view;
//	private IDeviceBuffer stageBuf;
//	private TextureState state;
//
//	public SimpleTexture(NativeImage img, Format format) {
//		try(MemoryStack stack = stackPush()) {
//			
//			this.width = img.getWidth();
//			this.height = img.getHeight();
//			this.mipLevels = (int) (Math.floor(Mathf.log2(Math.max(this.width, this.height))) + 1);
//			this.stageBuf = ITexture.makeStageBuffer(img.getData());
//			
//			this.image = ImageBuilder.create()
//				.width(this.width)
//				.height(this.height)
//				.usage(ImageUsage.TRANSFER_SRC, ImageUsage.TRANSFER_DST, ImageUsage.SAMPLED)
//				.format(format)
//				.mipLevels(this.mipLevels)
//				.build(device);
//				
//			this.view = ImageViewBuilder.create()
//				.image(this.image)
//				.format(this.image.getFormat())
//				.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//				.mipLevels(this.mipLevels)
//				.build(device);
//
//			this.state = TextureState.DEFAULT.copy();
//			
//			service.getGraphicsDispatch().runAsync(this::upload);
//		}
//	}
//	
//	private void upload(CommandBuffer cmd) {
//		if(this.stageBuf != null) {
//			try(MemoryStack stack = stackPush()) {
//				this.recordImageTransition(stack, cmd, ImageLayout.UNDEFINED.getVkType(), ImageLayout.TRANSFER_DST_OPTIMAL.getVkType());
//				this.recordBufferCopy(stack, cmd, this.stageBuf);
//				this.recordGenerateMipMaps(stack, cmd);
//			}
//		}
//	}
//	
//	public void recordImageTransition(MemoryStack stack, CommandBuffer cmd, int oldLayout, int newLayout) {
//		VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack)
//			.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
//			.oldLayout(oldLayout)
//			.newLayout(newLayout)
//			.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
//			.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
//			.image(this.image.getHandle())
//			.subresourceRange((it) -> it
//				.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT) 
//				.baseMipLevel(0)
//				.levelCount(this.mipLevels)
//				.baseArrayLayer(0)
//				.layerCount(1));
//		 	
//		int srcStage;
//		int dstStage;
//		int srcAccessMask;
//		int dstAccessMask;
//		
//		if(oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
//			srcStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
//			srcAccessMask = 0;
//			dstStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
//			dstAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT;
//		} else if(oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
//			srcStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
//			srcAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT;
//			dstStage = PipelineStage.FRAGMENT.getVkType();
//			dstAccessMask = VK_ACCESS_SHADER_READ_BIT;
//		} else {
//			throw new IllegalStateException("Unsupported layout transition");
//		}
//		
//		barrier
//			.srcAccessMask(srcAccessMask)
//			.dstAccessMask(dstAccessMask);
//		
//		vkCmdPipelineBarrier(cmd.get(), srcStage, dstStage, 0, null, null, barrier);
//	}
//
//	private void recordBufferCopy(MemoryStack stack, CommandBuffer cmd, IDeviceBuffer bufferData) {
//		vkCmdCopyBufferToImage(cmd.get(), bufferData.asLong(), this.image.getHandle(), VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VkBufferImageCopy.calloc(1, stack)
//			.imageSubresource((it) -> it
//    			.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//    			.mipLevel(0)
//    			.baseArrayLayer(0)
//    			.layerCount(1)
//    		 )
//    		.imageOffset(it -> it.x(0).y(0).z(0))
//    		.imageExtent(it -> it.width(this.width).height(this.height).depth(1)));
//    }
//	
//	private void recordGenerateMipMaps(MemoryStack stack, CommandBuffer cmdBuf) {
//		long imgHandle = this.image.getHandle();
//		
//		VkImageSubresourceRange resRange = VkImageSubresourceRange.calloc(stack)
//			.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//			.baseArrayLayer(0)
//			.levelCount(1)
//			.layerCount(1);
//		
//		VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack)
//			.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
//			.image(imgHandle)
//			.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
//			.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
//			.subresourceRange(resRange);
//		
//		int mipWidth = this.width;
//		int mipHeight = this.height;
//		
//		VkCommandBuffer vkCmdBuf = cmdBuf.get();
//		
//		for(int i = 1; i < this.mipLevels; i++) {
//			resRange.baseMipLevel(i - 1);
//			barrier.subresourceRange(resRange)
//				.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//				.newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
//				.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
//				.dstAccessMask(VK_ACCESS_TRANSFER_READ_BIT);
//
//			vkCmdPipelineBarrier(vkCmdBuf,
//                    VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0,
//                    null, null, barrier);
//			
//			VkOffset3D srcOffset0 = VkOffset3D.calloc(stack).x(0).y(0).z(0);
//			VkOffset3D srcOffset1 = VkOffset3D.calloc(stack).x(mipWidth).y(mipHeight).z(1);
//			VkOffset3D dstOffset0 = VkOffset3D.calloc(stack).x(0).y(0).z(0);
//			VkOffset3D dstOffset1 = VkOffset3D.calloc(stack).x(mipWidth > 1 ? mipWidth / 2 : 1).y(mipHeight > 1 ? mipHeight / 2 : 1).z(1);
//
//			final int k = i;
//			
//			VkImageBlit.Buffer blit = VkImageBlit.calloc(1, stack)
//				.srcOffsets(0, srcOffset0)
//				.srcOffsets(1, srcOffset1)
//				.srcSubresource((it) -> it
//					.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//					.mipLevel(k - 1)
//					.baseArrayLayer(0)
//					.layerCount(1))
//				.dstOffsets(0, dstOffset0)
//				.dstOffsets(1, dstOffset1)
//				.dstSubresource((it) -> it
//					.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//					.mipLevel(k)
//					.baseArrayLayer(0)
//					.layerCount(1));
//			
//			vkCmdBlitImage(vkCmdBuf, imgHandle, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, imgHandle, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, blit, VK_FILTER_LINEAR);
//	
//			barrier.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
//				   .newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//				   .srcAccessMask(VK_ACCESS_TRANSFER_READ_BIT)
//				   .dstAccessMask(VK_ACCESS_SHADER_READ_BIT);
//			
//			vkCmdPipelineBarrier(vkCmdBuf,
//                    VK_PIPELINE_STAGE_TRANSFER_BIT, PipelineStage.FRAGMENT.getVkType(), 0,
//                    null, null, barrier);
//			
//			if(mipWidth > 1) {
//				mipWidth /= 2;
//			}
//			
//			if(mipHeight > 1) {
//				mipHeight /= 2;
//			}
//		}
//		
//		barrier.subresourceRange((it) -> it
//			.baseMipLevel(this.mipLevels - 1))
//			.oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//			.newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//			.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
//			.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);
//		vkCmdPipelineBarrier(vkCmdBuf, VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT, 0, null, null, barrier);
//	}
//	
//	@Override
//	public int getWidth() {
//		return this.width;
//	}
//
//	@Override
//	public int getHeight() {
//		return this.height;
//	}
//	
//	@Override
//	public Format getFormat() {
//		return this.view.getFormat();
//	}
//	
//	@Override
//	public void bind(int index, RenderContext context) {
//		context.bindTexture(index, this);
//	}
//
//	@Override
//	public TextureState getState() {
//		return this.state;
//	}
//	
//	@Override
//	public void free() {
//		this.stageBuf.free();
//		this.view.free();
//		this.image.free();
//	}
//}
