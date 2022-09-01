package racoonman.r3d.render.api.vulkan;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.types.Level;
import racoonman.r3d.render.api.types.Status;
import racoonman.r3d.render.api.types.SubmitMode;

class FrameManager {
	private Device device;
	private Queue<Frame> cleanFrames;
	private Queue<Frame> dirtyFrames;

	public FrameManager(Device device) {
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
	
	public Frame dispatch(VkWorkPool pool) {
		Frame frame = this.cleanFrames.poll();
		return (frame != null ? frame : new Frame(pool.dispatch(Level.PRIMARY, SubmitMode.SINGLE))).markDirty();
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
		private IHostSync hostSync;
		
		public Frame(CommandBuffer cmdBuffer) {
			this.cmdBuffer = cmdBuffer;
			this.hostSync = new VkHostSync(FrameManager.this.device, false);
		}
		
		public CommandBuffer getCommandBuffer() {
			return this.cmdBuffer;
		}
		
		public IHostSync getHostSync() {
			return this.hostSync;
		}
		
		public boolean poll() {
			if(this.hostSync.is(Status.SUCCESS)) {
				this.cmdBuffer.reset();
				this.hostSync.reset();
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
