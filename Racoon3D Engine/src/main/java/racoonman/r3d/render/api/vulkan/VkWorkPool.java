package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IWorkPool;
import racoonman.r3d.render.api.types.Level;
import racoonman.r3d.render.api.types.SubmitMode;
import racoonman.r3d.render.api.types.Work;

class VkWorkPool implements IWorkPool {
	private Device device;
	private VkService service;
	private DeviceQueue queue;
	private CommandPool pool;
	
	public VkWorkPool(Device device, VkService service, int queueIndex, Work... queueFlags) {
		this.device = device;
		this.service = service;
		this.queue = DeviceQueue.work(device, queueIndex, queueFlags);
		this.pool = new CommandPool(device, this.queue);
	}

	//TODO remove
	public void join() {
		this.queue.waitIdle();
	}

	@Override
	public Context dispatch() {
		return this.service.createContext(this);
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

	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		this.runAsync((cmd) -> VkUtils.copy(cmd, src, dst)).join();
	}
	
	//TODO remove
	public <T> CompletableFuture<T> supplyAsync(Function<CommandBuffer, T> task) {
		CommandBuffer buffer = this.dispatch(Level.PRIMARY, SubmitMode.SINGLE);
		return CompletableFuture.supplyAsync(() -> {
			VkHostSync fence = new VkHostSync(this.device, true);
		
			buffer.begin();
			T result = task.apply(buffer);
			buffer.end();
			
			fence.reset();
			
			try(MemoryStack stack = stackPush()) {
				this.queue.submit(QueueSubmission.of().withBuffers(buffer).withHostSync(fence));
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
