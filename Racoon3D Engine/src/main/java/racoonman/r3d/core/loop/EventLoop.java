package racoonman.r3d.core.loop;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import racoonman.r3d.core.launch.IExecutable;
import racoonman.r3d.util.Util;

public abstract class EventLoop implements Executor, IExecutable {
	protected int tps;
	protected ExecutorService asyncExecutor;
	protected Queue<Runnable> queue;
	protected Queue<CompletableFuture<?>> futures;
	
	public EventLoop(int tps, ExecutorService asyncExecutor) {
		this.tps = tps;
		this.asyncExecutor = asyncExecutor;
		this.queue = new ConcurrentLinkedQueue<>();
		this.futures = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public void run() {
		long start = System.nanoTime();
		double tps = Util.NANOS_PER_SECOND / this.tps;
		double delta = 0;
		long timer = System.currentTimeMillis();

	    while(this.isRunning()) {
	        long currentTime = System.nanoTime();
	        delta += (currentTime - start) / tps;
	        start = currentTime;

	        if(delta >= 1) {
	        	this.tick();
	        	
	        	while(!this.queue.isEmpty()) {
	        		this.queue.poll().run();
	        	}

	        	while(!this.futures.isEmpty()) {
	        		this.futures.poll().join();
	        	}
	        	
	        	delta--;
	        }

	        if(System.currentTimeMillis() - timer > 1000) {
	            timer += 1000;
	        }
	    }
	}
	
	public int getTickRate() {
		return this.tps;
	}
	
	@Override
	public void execute(Runnable task) {
		this.queue.add(task);
	}
	
	public <T> CompletableFuture<T> enqueue(Supplier<T> task) {
		CompletableFuture<T> future = CompletableFuture.supplyAsync(task, this.asyncExecutor);
		this.futures.add(future);
		return future;
	}
	
	public CompletableFuture<Void> enqueue(Runnable task) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(task, this.asyncExecutor);
		this.futures.add(future);
		return future;
	}
	
	@Override
	public void close() {
		this.asyncExecutor.shutdownNow();
	}
	
	public abstract void tick();
	
	public abstract boolean isRunning();
}
