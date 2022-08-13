package racoonman.r3d.render.api.vulkan;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.vulkan.sync.Fence;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.PipelineStage;
import racoonman.r3d.util.Holder;

record QueueSubmission(List<CommandBuffer> commandBuffers, List<Semaphore> waits, List<PipelineStage> stageMasks, List<Semaphore> signals, Holder<Fence> fence) {
	
	public QueueSubmission withCommandBuffer(CommandBuffer cmdBuffer) {
		this.commandBuffers.add(cmdBuffer);
		return this;
	}
	
	public QueueSubmission withWait(Semaphore semaphore) {
		this.waits.add(semaphore);
		return this;
	}
	
	public QueueSubmission withStageMask(PipelineStage stage) {
		this.stageMasks.add(stage);
		return this;
	}
	
	public QueueSubmission withSignal(Semaphore semaphore) {
		this.signals.add(semaphore);
		return this;
	}
	
	public QueueSubmission withFence(Fence fence) {
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
	
	//TODO remove
	static LongBuffer toLongBuffer(List<Semaphore> semaphores, MemoryStack stack) {
		int size = semaphores.size();
		LongBuffer pointers = stack.mallocLong(size);
	
		for(int i = 0; i < size; i++) {
			pointers.put(i, semaphores.get(i).getHandle());
		}
		
		return pointers;
	}
	
	//TODO remove
	static PointerBuffer toPointerBuffer(List<CommandBuffer> cmdBuffers, MemoryStack stack) {
		PointerBuffer pointers = stack.mallocPointer(cmdBuffers.size());
		
		for(int i = 0; i < cmdBuffers.size(); i++) {
			pointers.put(i, cmdBuffers.get(i).asLong());
		}
		
		return pointers;
	}
}
