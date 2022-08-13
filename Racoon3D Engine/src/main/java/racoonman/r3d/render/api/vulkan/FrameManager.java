package racoonman.r3d.render.api.vulkan;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.api.vulkan.CommandBuffer.Level;
import racoonman.r3d.render.api.vulkan.CommandBuffer.SubmitMode;
import racoonman.r3d.render.api.vulkan.sync.Fence;
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
	
	class Frame {
		private CommandBuffer cmdBuffer;
		private Fence fence;
		private Queue<NativeResource> freeQueue;
		
		public Frame() {
			this.cmdBuffer = FrameManager.this.dispatcher.dispatch(Level.PRIMARY, SubmitMode.SINGLE);
			this.fence = new Fence(FrameManager.this.device, false);
			this.freeQueue = new ArrayDeque<>();
		}
		
		public CommandBuffer getCommandBuffer() {
			return this.cmdBuffer;
		}
		
		public Fence getFence() {
			return this.fence;
		}
		
		public boolean poll() {
			if(this.fence.is(Status.SUCCESS)) {
				while(!this.freeQueue.isEmpty()) {
					this.freeQueue.poll().free();
				}
				
				this.cmdBuffer.reset();
				this.fence.reset();
				return true;
			} else {
				return false;
			}
		}
		
		public void free(NativeResource resource) {
			this.freeQueue.add(resource);
		}
		
		public Frame markDirty() {
			FrameManager.this.dirtyFrames.add(this);
			return this;
		}
	}
}
