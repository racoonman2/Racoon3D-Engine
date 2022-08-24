package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.Context;

public class RenderPassBuilder extends RenderPass {
	private Context ctx;
	private IFramebuffer framebuffer;
	
	public RenderPassBuilder(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public RenderPass begin() {
		return this.ctx.createPass(this.framebuffer).begin();
	}

	@Override
	public void end() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFramebuffer getFramebuffer() {
		return this.framebuffer;
	}

}
