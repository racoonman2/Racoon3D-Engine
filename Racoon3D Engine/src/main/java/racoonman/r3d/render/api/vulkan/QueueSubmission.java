package racoonman.r3d.render.api.vulkan;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.api.objects.IFence;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.Holder;
import racoonman.r3d.util.IPair;

//TODO this shouldn't be a record
record QueueSubmission(List<CommandBuffer> commandBuffers, Set<IPair<IContextSync, Stage>> waits, Set<IContextSync> signals, Holder<IFence> fence) {
	
	public QueueSubmission withBuffers(CommandBuffer... buffers) {
		for(CommandBuffer buffer : buffers) {
			this.commandBuffers.add(buffer);
		}
		return this;
	}
	
	public QueueSubmission withWait(IContextSync sync, Stage stage) {
		this.waits.add(IPair.of(sync, stage));
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
			new HashSet<>(),
			new HashSet<>(),
			new Holder<>());
	}
	
	static LongBuffer toLongBuffer(Collection<? extends IHandle> handles, MemoryStack stack) {
		LongBuffer longs = stack.mallocLong(handles.size());
	
		for(IHandle handle : handles) {
			longs.put(handle.asLong());
		}
		
		longs.rewind();
		return longs;
	}
	
	static PointerBuffer toPointerBuffer(Collection<? extends IHandle> handles, MemoryStack stack) {
		PointerBuffer pointers = stack.mallocPointer(handles.size());

		for(IHandle handle : handles) {
			pointers.put(handle.asLong());
		}

		pointers.rewind();
		return pointers;
	}
}
