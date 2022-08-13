package racoonman.r3d.render.api.vulkan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjgl.system.MemoryUtil;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;

abstract class VkFramebuffer implements IFramebuffer {
	protected int width;
	protected int height;
	protected VkFrame[] frames;
	
	public VkFramebuffer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void bind(RenderContext context) {
		IFramebuffer.super.bind(context);
		
		context.signal(this.getFrame().getFinished());
	}
	
	@Override
	public List<IAttachment> getColorAttachments() {
		return this.getFrame().getColorAttachments();
	}

	@Override
	public Optional<IAttachment> getDepthAttachment() {
		return this.getFrame().getDepthAttachment();
	}

	@Override
	public IFramebuffer withColor(IAttachment attachment) {	
		this.getFrame().withColor(attachment);
		return this;
	}

	@Override
	public IFramebuffer withDepth(IAttachment attachment) {
		this.getFrame().withDepth(attachment);
		return this;
	}

	@Override
	public long asLong() {
		return MemoryUtil.NULL;
	}

	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	abstract int getIndex();
	
	VkFrame getFrame() {
		return this.frames[this.getIndex()];
	}
	
	static class VkFrame {
		private Device device;
		private List<IAttachment> colorAttachments;
		private Optional<IAttachment> depthAttachment;
		private Semaphore available;
		private Semaphore finished;
		
		public VkFrame(Device device) {
			this.device = device;
			this.colorAttachments = new ArrayList<>();
			this.depthAttachment = Optional.empty();
			this.available = new Semaphore(device);
			this.finished = new Semaphore(device);
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
			VkFrame frame = new VkFrame(this.device);
			this.colorAttachments.stream().map(IAttachment::copy).forEach(frame::withColor);
			this.depthAttachment.map(IAttachment::copy).ifPresent(frame::withDepth);
			return frame;
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
		}
	}
}
