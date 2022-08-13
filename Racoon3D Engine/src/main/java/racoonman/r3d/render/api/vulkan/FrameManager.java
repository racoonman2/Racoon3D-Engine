package racoonman.r3d.render.api.vulkan;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import racoonman.r3d.render.api.vulkan.CommandBuffer.Level;
import racoonman.r3d.render.api.vulkan.CommandBuffer.SubmitMode;
import racoonman.r3d.render.api.vulkan.sync.VkFence;
import racoonman.r3d.render.api.vulkan.types.Status;

class FrameManager {
	private WorkDispatcher dispatcher;
	private Device device;
	private Queue<Frame> cleanFrames;
	private Queue<Frame> dirtyFrames;

	public FrameManager(WorkDispatcher dispatcher, Device device) {
		this.dispatcher = dispatcher;
		this.device = device;
		this.cleanFrames = new ConcurrentLinkedQueue<>();
		this.dirtyFrames = new ConcurrentLinkedQueue<>();
	}
	
	public Queue<Frame> getCleanFrames() {
		return this.cleanFrames;
	}
	
	public Queue<Frame> getDirtyFrames() {
		return this.dirtyFrames;
	}
	
	public Frame take() {
		Frame frame = this.cleanFrames.poll();
		return (frame != null ? frame : new Frame()).markDirty();
	}
	
	public void poll() {
		Iterator<Frame> it = this.dirtyFrames.iterator();

        while (it.hasNext()) {
            Frame next = it.next();
        	
        	if (next.poll()) {
                it.remove();
      
                this.cleanFrames.add(next);
            }
        }
	}
	
	//TODO add free queue for context local resource freeing
	class Frame {
		private CommandBuffer cmdBuffer;
		private VkFence fence;
		
		public Frame() {
			this.cmdBuffer = FrameManager.this.dispatcher.dispatch(Level.PRIMARY, SubmitMode.SINGLE);
			this.fence = new VkFence(FrameManager.this.device, false);
		}
		
		public CommandBuffer getCommandBuffer() {
			return this.cmdBuffer;
		}
		
		public VkFence getFence() {
			return this.fence;
		}
		
		public boolean poll() {
			if(this.fence.is(Status.SUCCESS)) {
				this.cmdBuffer.reset();
				this.fence.reset();
				return true;
			} else {
				return false;
			}
		}
		
		public Frame markDirty() {
			FrameManager.this.dirtyFrames.add(this);
			return this;
		}
	}
}
