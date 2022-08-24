package racoonman.r3d.render.api.objects;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.core.Driver;

public interface IFramebuffer {
	int getWidth();

	int getHeight();

	boolean acquire(); //TODO remove
	
	void onRenderStart(Context context);

	void free();
	
	//TODO remove
	default void perspective(Context ctx, float fov, float near, float far) {
		ctx.perspective(fov, (float) this.getWidth() / (float) this.getHeight(), near, far);
	}

	//TODO remove
	default RenderPass begin(Context ctx) {
		return this.begin(ctx, 0.0F, 0.0F, 0.0F, 1.0F);
	}
	
	//TODO remove
	default RenderPass begin(Context ctx, float clearR, float clearG, float clearB, float clearA) {
		RenderPass pass = ctx.beginPass(this);
		pass.clear(clearR, clearG, clearB, clearA);
		return pass.begin();
	}
	
	default IFramebuffer withColor(int layers, ImageLayout layout, Format format, ViewType viewType) {
		return this.withColor(Driver.createAttachment(this.getWidth(), this.getHeight(), layers, layout, format, viewType, ImageUsage.COLOR));
	}

	default IAttachment getColorAttachment(int index) {
		return this.getColorAttachments().get(index);
	}
	
	List<IAttachment> getColorAttachments();
	
	Optional<IAttachment> getDepthAttachment();

	
	IFramebuffer withColor(IAttachment attachment);
	
	default IFramebuffer withDepth(int layers, ImageLayout layout, Format format, ViewType viewType) {
		return this.withDepth(Driver.createAttachment(this.getWidth(), this.getHeight(), layers, layout, format, viewType, ImageUsage.DEPTH_STENCIL));
	}
	
	IFramebuffer withDepth(IAttachment attachment);
	
	default Viewport getViewport(float minDepth, float maxDepth) {
		return new Viewport(0.0F, 0.0F, this.getWidth(), this.getHeight(), minDepth, maxDepth);
	}
	
	default Scissor getScissor() {
		return new Scissor(0, 0, this.getWidth(), this.getHeight());
	}
}
