package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.Context;

class VkOffscreenFramebuffer extends VkFramebuffer {
	
	VkOffscreenFramebuffer(Device device, int width, int height, int frameCount) {
		super(device, width, height, frameCount);
	}

	@Override
	public void onRenderStart(Context context) {
	//TODO context.signal(this.frames[this.frameIndex].getFinished());
	}
	
	@Override
	public boolean acquire() {
		this.frameIndex %= this.frames.length;
		return false;
	}
}

