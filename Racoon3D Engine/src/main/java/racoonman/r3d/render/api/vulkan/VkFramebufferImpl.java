package racoonman.r3d.render.api.vulkan;

class VkFramebufferImpl extends VkFramebuffer {
	
	VkFramebufferImpl(Device device, int width, int height, int frameCount) {
		super(device, width, height, frameCount);
	}
	
	@Override
	public boolean acquire() {
		this.frameIndex %= this.frames.length;
		return false;
	}
}

