package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.core.RenderSystem;

class VkWindowFramebuffer extends VkFramebuffer {
	private SwapChain swapchain;
	private DeviceQueue queue;
	
	VkWindowFramebuffer(Device device, SwapChain swapchain, DeviceQueue queue) {
		super(swapchain.getWidth(), swapchain.getHeight());

		this.swapchain = swapchain;
		this.queue = queue;
		this.frames = new VkFrame[swapchain.getFrameCount()]; {
			ImageView[] views = this.swapchain.getImageViews();
			
			for(int i = 0; i < views.length; i++) {
				ImageView view = views[i];
				this.frames[i] = new VkFrame(device)
					.withColor(new VkOSAttachment(view, device))
					.withDepth(RenderSystem.createAttachment(this.width, this.height, 1, ImageLayout.DEPTH_STENCIL_OPTIMAL, Format.D32_SFLOAT_S8_UINT, ViewType.TYPE_2D, new ImageUsage[] { ImageUsage.DEPTH_STENCIL }));
			}
		}
	}
	
	public boolean next() {
		return this.swapchain.acquire(this.frames[this.getIndex()].getAvailable());
	}

	public boolean present() {
		return this.swapchain.present(this.frames[this.getIndex()].getFinished(), this.queue);
	}
	
	@Override
	public void free() {
		for(VkFrame frame : this.frames) {
			frame.free();
		}
	}

	@Override
	int getIndex() {
		return this.swapchain.getFrame();
	}
}

