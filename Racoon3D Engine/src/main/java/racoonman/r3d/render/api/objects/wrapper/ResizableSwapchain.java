package racoonman.r3d.render.api.objects.wrapper;

import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.ISwapchain;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.core.Driver;

public class ResizableSwapchain implements ISwapchain {
	private ISwapchain delegate;
	private boolean resized;
	
	public ResizableSwapchain(IWindowSurface surface, int frameCount) {
		this.delegate = surface.createSwapchain(frameCount);
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
		boolean resized = false;
		while(this.resized || this.delegate.acquire()) {
			this.resize();
			resized = true;
			this.resized = false;
		}
		return resized;
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

	@Override
	public long asLong() {
		return this.delegate.asLong();
	}

	@Override
	public boolean present() {
		return this.resized = this.delegate.present();
	}
	
	@Override
	public ISwapchain makeChild() {
		return this.delegate.makeChild();
	}
	
	private void resize() {
		if(this.isValid()) {
			ISwapchain oldSwapchain = this.delegate;
			this.delegate = this.delegate.makeChild();
			Driver.free(oldSwapchain);
		}
	}

	@Override
	public IWindowSurface getSurface() {
		return this.delegate.getSurface();
	}
}
