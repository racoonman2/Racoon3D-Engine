package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.vulkan.CommandBuffer.Level;
import racoonman.r3d.render.api.vulkan.CommandBuffer.SubmitMode;
import racoonman.r3d.render.api.vulkan.sync.VkFence;
import racoonman.r3d.render.api.vulkan.types.QueueFamily;

class WorkDispatcher {
	private Device device;
	private DeviceQueue queue;
	private CommandPool pool;
	
	public WorkDispatcher(Device device, QueueFamily queueFamily, int index) {
		this.device = device;
		this.queue = DeviceQueue.work(device, queueFamily, index);
		this.pool = new CommandPool(device, this.queue);
	}

	//TODO remove
	public void join() {
		this.queue.waitIdle();
	}
	
	public CommandBuffer dispatch(Level level, SubmitMode submitMode) {
		return this.pool.allocate(level, submitMode);
	}

	public void submit(QueueSubmission submission) {
		this.queue.submit(submission);
	}
	
	public void reset() {
		this.pool.reset();
	}
	
	public void free() {
		this.pool.free();
	}
	
	//TODO remove
	public <T> CompletableFuture<T> supplyAsync(Function<CommandBuffer, T> task) {
		CommandBuffer buffer = this.dispatch(Level.PRIMARY, SubmitMode.SINGLE);
		return CompletableFuture.supplyAsync(() -> {
			VkFence fence = new VkFence(this.device, true);
		
			buffer.begin();
			T result = task.apply(buffer);
			buffer.end();
			
			fence.reset();
			
			try(MemoryStack stack = stackPush()) {
				this.queue.submit(QueueSubmission.of().withCommandBuffer(buffer).withFence(fence));
			}
			
			fence.await(Long.MAX_VALUE);
			buffer.free();
			fence.free();
			return result;
		});
	}
	
	//TODO remove
	public CompletableFuture<Void> runAsync(Consumer<CommandBuffer> task) {
		return this.supplyAsync((cmdBuf) -> {
			task.accept(cmdBuf);
			return null;
		});
	}
}
