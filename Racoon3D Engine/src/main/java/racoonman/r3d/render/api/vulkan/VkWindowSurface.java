package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.window.Window;

//FIXME present semaphore doesn't get signaled when surface is invalid
public class VkWindowSurface implements IWindowSurface {
	private Device device;
	private Window window;
	private WindowSurface surface;
	private DeviceQueue presentQueue;
	private SwapChain swapChain;
	private VkWindowFramebuffer target;
	private boolean resized;
	
	public VkWindowSurface(Device device, Vulkan vulkan, Window window, int queueIndex) {
		this.device = device;
		this.window = window;
		this.surface = new WindowSurface(vulkan, window);
		this.presentQueue = DeviceQueue.present(device, this.surface, queueIndex);
		this.swapChain = new SwapChain(device, window, this.surface, window.getFrameCount());
		this.target = new VkWindowFramebuffer(device, this.swapChain, this.presentQueue);
	}
	
	@Override
	public boolean acquire() {
		boolean resized = false;
		for(int i = 0; i < this.window.getFrameCount() && this.isValid() && (this.resized || this.target.next()); i++) {
			this.resize();
			resized = true;
			this.resized = false;
		}
		return resized;
	}
	
	public boolean present() {
		return this.isValid() && (this.resized = this.target.present());
	}

	@Override
	public IFramebuffer getFramebuffer() {
		return this.target;
	}

	@Override
	public boolean isValid() {
		return this.window.getWidth() > 0 && this.window.getHeight() > 0;
	}

	private void resize() {
		if(this.isValid()) {
			SwapChain oldSwapchain = this.swapChain;
			IFramebuffer oldTarget = this.target;
			
			this.swapChain = new SwapChain(this.swapChain);
			this.target = new VkWindowFramebuffer(this.device, this.swapChain, this.presentQueue);
			
			RenderSystem.free(() -> {
				oldSwapchain.free();
				oldTarget.free();
			});
		}
	}
}
