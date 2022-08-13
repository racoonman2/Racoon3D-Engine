package racoonman.r3d.render.api.vulkan;

import org.lwjgl.system.MemoryUtil;

import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.PresentMode;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.window.Window;

class VkWindowFramebuffer extends VkFramebuffer {
	private Device device;
	private Vulkan vulkan;
	private int queueIndex;
	private Window window;
	private WindowSurface surface;
	private SwapChain swapchain;
	private DeviceQueue presentQueue;
	
	VkWindowFramebuffer(Device device, Vulkan vulkan, Window window, int queueIndex) {
		super(window.getWidth(), window.getHeight());

		this.device = device;
		this.vulkan = vulkan;
		this.queueIndex = queueIndex;
		this.window = window;
		this.surface = new WindowSurface(this.vulkan, this.window);
		this.presentQueue = DeviceQueue.present(this.device, this.surface, this.queueIndex);
		
		this.init(window.getWidth(), window.getHeight());
	}

	@Override
	public boolean present() {
		return this.swapchain.present(this.presentQueue);
	}
	
	@Override
	public void acquire() {
		this.swapchain.acquire();
	}
	
	@Override
	public int getWidth() {
		return this.window.getWidth();
	}
	
	@Override
	public int getHeight() {
		return this.window.getHeight();
	}

	@Override
	public long asLong() {
		return MemoryUtil.NULL;
	}

	@Override
	public void free() {
		this.swapchain.free();
		this.surface.free();
		
		for(VkFrame frame : this.frames) {
			frame.free();
		}
	}

	@Override
	public void withSize(int newWidth, int newHeight) {
		this.free();
		
		this.init(newWidth, newHeight);
	}
	
	private void init(int width, int height) {
		int frameCount = this.window.getFrameCount();
		PresentMode presentMode = this.window.getPresentMode();
		IFramebuffer oldTarget = this.window.getTarget();

		SwapChain oldSwapchain = oldTarget != null ? ((VkWindowFramebuffer) oldTarget).swapchain : null; //FIXME bad assumption
		this.swapchain = new SwapChain(this.device, this.window, this.surface, frameCount, presentMode, oldSwapchain);
		
		this.frames = new VkFrame[frameCount]; {
			ImageView[] views = this.swapchain.getImageViews();
			
			for(int i = 0; i < views.length; i++) {
				ImageView view = views[i];
				this.frames[i] = new VkFrame()
					.withColor(new VkAttachment(view, this.device))
					.withDepth(RenderSystem.createAttachment(width, height, 1, ImageLayout.DEPTH_STENCIL_OPTIMAL, Format.D32_SFLOAT_S8_UINT, ViewType.TYPE_2D, new ImageUsage[] { ImageUsage.DEPTH_STENCIL }));
			}
		}
	}
}

