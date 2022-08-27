package racoonman.r3d.render.api.vulkan;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.objects.IFence;
import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.Holder;

//TODO this shouldn't be a record
record QueueSubmission(List<CommandBuffer> commandBuffers, List<IContextSync> waits, List<Stage> stageMasks, List<IContextSync> signals, Holder<IFence> fence) {
	
	public QueueSubmission withBuffers(CommandBuffer... buffers) {
		for(CommandBuffer buffer : buffers) {
			this.commandBuffers.add(buffer);
		}
		return this;
	}
	
	public QueueSubmission withWait(IContextSync sync, Stage stage) {
		this.waits.add(sync);
		this.stageMasks.add(stage);
		return this;
	}
	
	public QueueSubmission withSignal(IContextSync... syncs) {
		Collections.addAll(this.signals, syncs);
		return this;
	}
	
	public QueueSubmission withFence(IFence fence) {
		this.fence.map(fence);
		return this;
	}
	
	public static QueueSubmission of() {
		return new QueueSubmission(
			new ArrayList<>(), 
			new ArrayList<>(), 
			new ArrayList<>(), 
			new ArrayList<>(), 
			new Holder<>());
	}
	
	static LongBuffer toLongBuffer(List<? extends IHandle> handles, MemoryStack stack) {
		int size = handles.size();
		LongBuffer pointers = stack.mallocLong(size);
	
		for(int i = 0; i < size; i++) {
			pointers.put(i, handles.get(i).asLong());
		}
		
		return pointers;
	}
	
	static PointerBuffer toPointerBuffer(List<? extends IHandle> handles, MemoryStack stack) {
		PointerBuffer pointers = stack.mallocPointer(handles.size());
		
		for(int i = 0; i < handles.size(); i++) {
			pointers.put(i, handles.get(i).asLong());
		}
		
		return pointers;
	}
}
