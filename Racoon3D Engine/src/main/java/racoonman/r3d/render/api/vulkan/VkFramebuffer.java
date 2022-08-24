package racoonman.r3d.render.api.vulkan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;

abstract class VkFramebuffer implements IFramebuffer {
	protected Device device;
	protected int width;
	protected int height;
	protected Frame[] frames;
	protected int frameIndex;
	protected int frameCount;
	
	public VkFramebuffer(Device device, int width, int height, int frameCount) {
		this.device = device;
		this.width = width;
		this.height = height;
		this.frameCount = frameCount;
		this.frames = new Frame[frameCount]; {
			for(int i = 0; i < frameCount; i++) {
				this.frames[i] = new Frame();
			}
		}
	}
	
	@Override
	public void onRenderStart(Context context) {
		context.signal(this.frames[this.frameIndex].getFinished());
	}
	
	@Override
	public List<IAttachment> getColorAttachments() {
		return this.frames[this.frameIndex].getColorAttachments();
	}

	@Override
	public Optional<IAttachment> getDepthAttachment() {
		return this.frames[this.frameIndex].getDepthAttachment();
	}

	@Override
	public IFramebuffer withColor(IAttachment attachment) {	
		this.frames[0].withColor(attachment);
		for(int i = 1; i < this.frames.length; i++) {
			this.frames[i].withColor(attachment.makeChild());
		}
		return this;
	}

	@Override
	public IFramebuffer withDepth(IAttachment attachment) {
		this.frames[0].withDepth(attachment);
		for(int i = 1; i < this.frames.length; i++) {
			this.frames[i].withDepth(attachment.makeChild());
		}
		return this;
	}

	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this ? true : o instanceof IFramebuffer other && 
			this.getColorAttachments().equals(other.getColorAttachments()) && 
			other.getDepthAttachment().equals(this.getDepthAttachment());
	}
	
	@Override
	public void free() {
		for(Frame frame : this.frames) {
			frame.free();
		}
	}
	
	class Frame {
		private List<IAttachment> colorAttachments;
		private Optional<IAttachment> depthAttachment;
		private Semaphore available;
		private Semaphore finished;
		
		public Frame() {
			this.colorAttachments = new ArrayList<>();
			this.depthAttachment = Optional.empty();
			this.available = new Semaphore(VkFramebuffer.this.device);
			this.finished = new Semaphore(VkFramebuffer.this.device);
		}

		public List<IAttachment> getColorAttachments() {
			return this.colorAttachments;
		}

		public IAttachment getColor(int index) {
			return this.colorAttachments.get(index);
		}
		
		public Optional<IAttachment> getDepthAttachment() {
			return this.depthAttachment;
		}
		
		public boolean hasDepthAttachment() {
			return this.depthAttachment.isPresent();
		}
		
		public Frame withColor(IAttachment attachment) {
			this.colorAttachments.add(attachment);
			return this;
		}
		
		public Frame withDepth(IAttachment attachment) {
			this.depthAttachment = Optional.of(attachment);
			return this;
		}
		
		public Semaphore getAvailable() {
			return this.finished;
		}
		
		public Semaphore getFinished() {
			return this.available;
		}
		
		public void free() {
			this.colorAttachments.forEach(IAttachment::free);
			this.depthAttachment.ifPresent(IAttachment::free);
			
			this.available.free();
			this.finished.free();
		}
	}
}
