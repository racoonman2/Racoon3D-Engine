package racoonman.r3d.render.api.vulkan;

import org.lwjgl.system.MemoryUtil;

class VkOffscreenFramebuffer extends VkFramebuffer {
	private int frameCount;
	
	VkOffscreenFramebuffer(Device device, int width, int height, int frameCount) {
		super(width, height);
		
		this.frameCount = frameCount;
		this.withSize(width, height);
	}

	@Override
	public void withSize(int newWidth, int newHeight) {
		this.frames = new VkFrame[this.frameCount]; {
			for(int i = 0; i < this.frameCount; i++) {
				this.frames[i] = new VkFrame();
			}
		}
	}

	@Override
	public void acquire() {
		this.index %= this.frames.length;
	}

	@Override
	public boolean present() {
		return false;
	}

	@Override
	public long asLong() {
		return MemoryUtil.NULL;
	}

	@Override
	public void free() {
	}
}

