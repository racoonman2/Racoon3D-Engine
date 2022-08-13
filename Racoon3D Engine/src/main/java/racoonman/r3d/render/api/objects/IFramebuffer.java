package racoonman.r3d.render.api.objects;

import java.util.List;
import java.util.Optional;

import org.joml.Matrix4f;

import racoonman.r3d.render.IBindable;
import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.math.Mathf;

public interface IFramebuffer extends IBindable, IHandle {
	int getWidth();

	int getHeight();

	boolean next();
	
	default IAttachment getColorAttachment(int index) {
		return this.getColorAttachments().get(index);
	}
	
	List<IAttachment> getColorAttachments();
	
	Optional<IAttachment> getDepthAttachment();

	default void bind(RenderContext context) {
		context.framebuffer(this);
	}
	
	default Matrix4f perspective(float fov, float near, float far, Matrix4f mat4f) {
		return mat4f.perspective(Mathf.toRadians(fov), (float) this.getWidth() / (float) this.getHeight(), near, far, true);
	}

	default Matrix4f ortho(Matrix4f mat4, float near, float far) {
		int width = this.getWidth();
		int height = this.getHeight();
		return mat4.ortho(-(width / 2), width / 2, -(height / 2), height / 2, near, far, true);
	}
	
	default IFramebuffer withColor(int layers, ImageLayout layout, Format format, ViewType viewType) {
		return this.withColor(RenderSystem.createAttachment(this.getWidth(), this.getHeight(), layers, layout, format, viewType, ImageUsage.COLOR));
	}

	IFramebuffer withColor(IAttachment attachment);
	
	default IFramebuffer withDepth(int layers, ImageLayout layout, Format format, ViewType viewType) {
		return this.withColor(RenderSystem.createAttachment(this.getWidth(), this.getHeight(), layers, layout, format, viewType, ImageUsage.DEPTH_STENCIL));
	}
	
	IFramebuffer withDepth(IAttachment attachment);
}
