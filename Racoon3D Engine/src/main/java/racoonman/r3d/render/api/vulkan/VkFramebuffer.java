package racoonman.r3d.render.api.vulkan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;

abstract class VkFramebuffer implements IFramebuffer {
	protected int width;
	protected int height;
	protected VkFrame[] frames;
	protected volatile int index;
	
	public VkFramebuffer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public List<IAttachment> getColorAttachments() {
		return this.frames[this.index].getColorAttachments();
	}

	@Override
	public Optional<IAttachment> getDepthAttachment() {
		return this.frames[this.index].getDepthAttachment();
	}

	@Override
	public IFramebuffer withColor(IAttachment attachment) {	
		this.frames[this.index].withColor(attachment);
		return this;
	}

	@Override
	public IFramebuffer withDepth(IAttachment attachment) {
		this.frames[this.index].withDepth(attachment);
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
	
	static class VkFrame {
		private List<IAttachment> colorAttachments;
		private Optional<IAttachment> depthAttachment;
		
		public VkFrame() {
			this.colorAttachments = new ArrayList<>();
			this.depthAttachment = Optional.empty();
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
		
		public VkFrame withColor(IAttachment attachment) {
			this.colorAttachments.add(attachment);
			return this;
		}
		
		public VkFrame withDepth(IAttachment attachment) {
			this.depthAttachment = Optional.of(attachment);
			return this;
		}
		
		public VkFrame copy() {
			VkFrame frame = new VkFrame();
			this.colorAttachments.stream().map(IAttachment::copy).forEach(frame::withColor);
			this.depthAttachment.map(IAttachment::copy).ifPresent(frame::withDepth);
			return frame;
		}
		
		public void free() {
			this.colorAttachments.forEach(IAttachment::free);
			this.depthAttachment.ifPresent(IAttachment::free);
		}
	}
}
