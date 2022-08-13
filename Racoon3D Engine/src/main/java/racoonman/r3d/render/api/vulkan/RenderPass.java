package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.LoadOp;
import racoonman.r3d.render.api.vulkan.types.StoreOp;

class RenderPass {
	private RenderContext context;
	private CommandRecorder recorder;
	private IFramebuffer framebuffer;
	
	public RenderPass(RenderContext context, CommandRecorder recorder, IFramebuffer framebuffer) {
		this.context = context;
		this.recorder = recorder;
		this.framebuffer = framebuffer;
	}
	
	public void begin() {
		this.recorder.record((cmdBuffer) -> {
			try(MemoryStack stack = stackPush()) {
				List<IAttachment> colorAttachments = this.framebuffer.getColorAttachments();
				VkRenderingInfo renderingInfo = VkRenderingInfo.calloc(stack)
					.sType$Default()
					.layerCount(1);
				
				VkRenderingAttachmentInfo.Buffer colorInfo = VkRenderingAttachmentInfo.calloc(colorAttachments.size(), stack);
				
				Vector4f clear = this.context.getClear();

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
								.float32(0, clear.x)
								.float32(1, clear.y)
								.float32(2, clear.z)
								.float32(3, clear.w)));
				}
				
				renderingInfo.pColorAttachments(colorInfo);
				
				//TODO make store & load op configurable
				this.framebuffer.getDepthAttachment().ifPresent((attachment) -> {
					VkRenderingAttachmentInfo depthInfo = VkRenderingAttachmentInfo.calloc(stack);
					depthInfo
						.sType$Default()
						.loadOp(LoadOp.CLEAR.getVkType())
						.storeOp(StoreOp.STORE.getVkType())
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
				
				cmdBuffer.beginRendering(renderingInfo);
			}
		});
	}
	
	public void end() {
		this.recorder.record(CommandBuffer::endRendering);
	}
	
	public static boolean isDependency(ITexture texture) {
		return texture instanceof IFramebuffer;
	}
	
	public static interface IDependency {
		ITexture getImage();
		
		void transition(CommandBuffer cmdBuffer);
	}
}
