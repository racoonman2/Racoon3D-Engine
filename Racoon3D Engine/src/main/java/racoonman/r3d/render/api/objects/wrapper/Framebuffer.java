package racoonman.r3d.render.api.objects.wrapper;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.core.Driver;

public class Framebuffer implements IFramebuffer {
	private IFramebuffer delegate;
	
	public Framebuffer(int width, int height) {
		this.delegate = Driver.createFramebuffer(width, height);
	}
	
	@Override
	public int getWidth() {
		return this.delegate.getWidth();
	}

	@Override
	public int getHeight() {
		return this.delegate.getHeight();
	}

	@Override
	public boolean acquire() {
		return this.delegate.acquire();
	}

	@Override
	public void onRenderStart(Context context) {
		this.delegate.onRenderStart(context);
	}

	@Override
	public void free() {
		this.delegate.free();
	}

	@Override
	public List<IAttachment> getColorAttachments() {
		return this.delegate.getColorAttachments();
	}

	@Override
	public Optional<IAttachment> getDepthAttachment() {
		return this.delegate.getDepthAttachment();
	}

	@Override
	public IFramebuffer withColor(IAttachment attachment) {
		return this.delegate.withColor(attachment);
	}

	@Override
	public IFramebuffer withDepth(IAttachment attachment) {
		return this.delegate.withDepth(attachment);
	}
}
