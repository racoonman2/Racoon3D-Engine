package racoonman.r3d.render.api.vulkan;

class VkOffscreenFramebuffer extends VkFramebuffer {
	private int index;
	private int frameCount;
	
	VkOffscreenFramebuffer(Device device, int width, int height, int frameCount) {
		super(width, height);
		
		this.frameCount = frameCount;
		this.frames = new VkFrame[this.frameCount]; {
			for(int i = 0; i < this.frameCount; i++) {
				this.frames[i] = new VkFrame(device);
			}
		}
	}

	@Override
	public boolean next() {
		this.index %= this.frames.length;
		return false;
	}

	@Override
	int getIndex() {
		return this.index;
	}
	
	@Override
	public void free() {
		// TODO
	}
}

