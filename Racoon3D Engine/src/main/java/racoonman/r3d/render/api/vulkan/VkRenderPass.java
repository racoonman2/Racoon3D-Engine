package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.RenderPass;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.LoadOp;
import racoonman.r3d.render.api.vulkan.types.StoreOp;

class VkRenderPass extends RenderPass {
	private CommandBuffer cmdBuffer;
	private IFramebuffer framebuffer;
	
	public VkRenderPass(Context context, CommandBuffer buffer, IFramebuffer framebuffer) {
		this.cmdBuffer = buffer;
		this.framebuffer = framebuffer;
		this.framebuffer.onRenderStart(context);
	}
	
	@Override
	public RenderPass begin() {
		try(MemoryStack stack = stackPush()) {
			List<IAttachment> colorAttachments = this.framebuffer.getColorAttachments();
			VkRenderingInfo renderingInfo = VkRenderingInfo.calloc(stack)
				.sType$Default()
				.layerCount(1);
				
			VkRenderingAttachmentInfo.Buffer colorInfo = VkRenderingAttachmentInfo.calloc(colorAttachments.size(), stack);
				
			//TODO make store & load op configurable
			for(int i = 0; i < colorAttachments.size(); i++) {
				IAttachment attachment = colorAttachments.get(i);
				colorInfo.get(i)
					.sType$Default()
					.loadOp(LoadOp.CLEAR.getVkType())
					.storeOp(StoreOp.STORE.getVkType())
					.imageView(attachment.asLong())
						.imageLayout(ImageLayout.COLOR_OPTIMAL.getVkType())
						.clearValue((val) -> val
							.color((col) -> col
								.float32(0, this.clear.x)
								.float32(1, this.clear.y)
								.float32(2, this.clear.z)
								.float32(3, this.clear.w)));
			}
				
			renderingInfo.pColorAttachments(colorInfo);

			this.framebuffer.getDepthAttachment().ifPresent((attachment) -> {
				VkRenderingAttachmentInfo depthInfo = VkRenderingAttachmentInfo.calloc(stack);
				depthInfo
					.sType$Default()
					.loadOp(LoadOp.CLEAR.getVkType()) //TODO make configurable
					.storeOp(StoreOp.STORE.getVkType()) //TODO make configurable
					.imageView(attachment.asLong())
					.imageLayout(ImageLayout.DEPTH_STENCIL_OPTIMAL.getVkType())
					.clearValue((val) -> 
						val.depthStencil((depth) -> depth
							.depth(1.0F)   //TODO make configurable
							.stencil(0))); //TODO make configurable
				renderingInfo.pDepthAttachment(depthInfo);
			});

			renderingInfo.renderArea((area) -> area
				.offset((offset) -> offset
					.x(0)
					.y(0))
				.extent((extent) -> extent
					.width(this.framebuffer.getWidth())
					.height(this.framebuffer.getHeight())));
				
			this.cmdBuffer.beginRendering(renderingInfo);
		}
		
		return this;
	}
	
	@Override
	public void end() {
		this.cmdBuffer.endRendering();
	}

	@Override
	public IFramebuffer getFramebuffer() {
		return this.framebuffer;
	}
}