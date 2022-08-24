package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_SUBOPTIMAL_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Optional;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import racoonman.r3d.render.api.objects.ISwapchain;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.vulkan.ImageView.ImageViewBuilder;
import racoonman.r3d.render.api.vulkan.types.Aspect;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.ImageType;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.natives.IHandle;

//FIXME Frame[] length and actual frame count may differ if device does not support requested number of frames
//TODO clean this up, all swapchain and window code right now is pretty messy
class VkSwapchain extends VkFramebuffer implements ISwapchain {
	private IWindowSurface surface;
	private DeviceQueue queue;
	private long handle;
	
	public VkSwapchain(IWindowSurface surface, DeviceQueue queue, int frameCount) {
		this(surface, queue, frameCount, Optional.empty());
	}
 
	private VkSwapchain(VkSwapchain swapchain) {
		this(swapchain.surface, swapchain.queue, swapchain.frameCount, Optional.of(swapchain));
	}
	
	private VkSwapchain(IWindowSurface surface, DeviceQueue queue, int frameCount, Optional<ISwapchain> old) {
		super(queue.getDevice(), surface.getWidth(), surface.getHeight(), frameCount);

		try(MemoryStack stack = stackPush()) {
			this.surface = surface;
			this.queue = queue;
			
			PhysicalDevice physicalDevice = this.device.getPhysicalDevice();
			
			VkSurfaceCapabilitiesKHR surfaceCaps = VkSurfaceCapabilitiesKHR.calloc(stack);
			vkAssert(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice.get(), this.surface.asLong(), surfaceCaps), "Error retrieving surface capabilities");
			
			SurfaceFormat format = this.getSurfaceFormat(physicalDevice);
			int minFrameCount = this.getFrameCount(surfaceCaps);

			VkExtent2D extent = this.getSwapChainExtent(stack, surfaceCaps);
			
			VkSwapchainCreateInfoKHR swapchainInfo = VkSwapchainCreateInfoKHR.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
				.surface(this.surface.asLong())
				.minImageCount(minFrameCount)
				.imageFormat(format.imageFormat().getVkType())
				.imageColorSpace(format.colorSpace())
				.imageExtent(extent)
				.imageArrayLayers(1)
				.imageUsage(ImageUsage.COLOR.getVkType())
				.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.preTransform(surfaceCaps.currentTransform())
				.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
				.clipped(true)
				.presentMode(surface.getPresentMode().getVkType());
			old.ifPresent(o -> {
				swapchainInfo.oldSwapchain(IHandle.getSafely(o));
				
				o.getColorAttachments()
					.stream()
					.filter((attachment) -> !(attachment instanceof SwapchainAttachment))
					.map((attachment) -> attachment.makeChild(this.width, this.height)).forEach(this::withColor);
				o.getDepthAttachment()
					.map((attachment) -> attachment.makeChild(this.width, this.height))
					.ifPresent(this::withDepth);
			});
				
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateSwapchainKHR(this.device.get(), swapchainInfo, null, pointer), "Error creating swap chain");
			this.handle = pointer.get(0);
			
			ImageView[] views = this.getImageViews(stack, extent, format.imageFormat());
			
			for(int i = 0; i < views.length; i++) {
				this.frames[i].withColor(new SwapchainAttachment(views[i]));
			}
			
			this.withDepth(1, ImageLayout.DEPTH_STENCIL_OPTIMAL, Format.D24_UNORM_S8_UINT, ViewType.TYPE_2D);
		}
	}

	@Override
	public IWindowSurface getSurface() {
		return this.surface;
	}

	@Override
	public boolean acquire() {
		try(MemoryStack stack = stackPush()) {
			Frame frame = this.frames[this.frameIndex];
			
			boolean resize = false;
			
			IntBuffer pointer = stack.mallocInt(1);
			int err = vkAcquireNextImageKHR(this.device.get(), this.handle, ~0L, frame.getAvailable().getHandle(), 0L, pointer);
			if(err == VK_ERROR_OUT_OF_DATE_KHR) {
				resize = true;
			} else if(err != VK_SUBOPTIMAL_KHR && err != VK_SUCCESS) {
				throw new IllegalStateException("Error acquiring image [" + err + "]");
			}
			
			this.frameIndex = pointer.get(0);
			return resize;
		}
	}
	
	@Override
	public boolean present() {
		try(MemoryStack stack = stackPush()) {
			Frame frame = this.frames[this.frameIndex];
			
			boolean resize = false;
			
			VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
				.pWaitSemaphores(stack.longs(frame.getFinished().getHandle()))
				.swapchainCount(1)
				.pSwapchains(stack.longs(this.handle))
				.pImageIndices(stack.ints(this.frameIndex));
			
			int err = vkQueuePresentKHR(this.queue.get(), present);
			if(err == VK_ERROR_OUT_OF_DATE_KHR) {
				resize = true;
			} else if(err != VK_SUBOPTIMAL_KHR && err != VK_SUCCESS) {
				throw new IllegalStateException("Error presenting image [" + err + "]");
			}
			
			return resize;
		}
	}

	@Override
	public ISwapchain makeChild() {
		return new VkSwapchain(this);
	}
	
	private ImageView[] getImageViews(MemoryStack stack, VkExtent2D extent, Format format) {
		VkDevice logicalDevice = this.device.get();
		
		IntBuffer imageBuf = stack.mallocInt(1);
		vkAssert(vkGetSwapchainImagesKHR(logicalDevice, this.handle, imageBuf, null), "Error retrieving surface image count");
		int imgCount = imageBuf.get(0);
		
		LongBuffer imagePointers = stack.mallocLong(imgCount);
		vkAssert(vkGetSwapchainImagesKHR(logicalDevice, this.handle, imageBuf, imagePointers), "Error retrieving surface images");
		
		ImageView[] images = new ImageView[imgCount];
		ImageViewBuilder builder = ImageViewBuilder.create()
			.format(format)
			.aspects(Aspect.COLOR);

		//TODO make SampleCount configurable
		for(int i = 0; i < imgCount; i++) {
			images[i] = builder.image(new VkImage(this.device, ImageType.TYPE_2D, format, 1, SampleCount.COUNT_1, 1, new ImageUsage[] { ImageUsage.COLOR }, extent.width(), extent.height(), 0, imagePointers.get(i))).build(this.device);
		}
		return images;
	}

	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		super.free();

		vkDestroySwapchainKHR(this.device.get(), this.handle, null);
	}
	
	private VkExtent2D getSwapChainExtent(MemoryStack stack, VkSurfaceCapabilitiesKHR surfaceCaps) {
		VkExtent2D res = VkExtent2D.malloc(stack);
		VkExtent2D currentExtent = surfaceCaps.currentExtent();

		if(currentExtent.width() == 0xFFFFFFFF) {
			VkExtent2D maxExtent = surfaceCaps.maxImageExtent();
			VkExtent2D minExtent = surfaceCaps.minImageExtent();
			
			int width = Math.min(this.surface.getWidth(), maxExtent.width());
			width = Math.max(width, minExtent.width());
			res.width(width);
			
			int height = Math.min(this.surface.getHeight(), maxExtent.height());
			height = Math.max(height, minExtent.height());
			res.height(height);
		} else {
			res.set(currentExtent);
		}
		
		return res;
	}
	
	private SurfaceFormat getSurfaceFormat(PhysicalDevice device) {
		try(MemoryStack stack = stackPush()) {
			VkPhysicalDevice physicalDevice = device.get();
			long surfaceHandle = this.surface.asLong();
			
			IntBuffer iBuf = stack.mallocInt(1);
			vkAssert(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surfaceHandle, iBuf, null), "Error retrieving surface format count");
			int formatCount = iBuf.get(0);
			
			if(formatCount <= 0) {
				throw new IllegalStateException("No surface formats retrieved");
			}
			
			VkSurfaceFormatKHR.Buffer formats = VkSurfaceFormatKHR.calloc(formatCount, stack);
			vkAssert(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surfaceHandle, iBuf, formats), "Error retrieving surface formats");
			
			for(int i = 0; i < formatCount; i++) {
				VkSurfaceFormatKHR format = formats.get(i);
				int imgF = format.format();
				int imgColorSpace = format.colorSpace();
				
				if(imgF == VK_FORMAT_B8G8R8A8_SRGB && imgColorSpace == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
					return new SurfaceFormat(IVkType.byInt(imgF, Format.values()), imgColorSpace);
				}
			}
			
			return new SurfaceFormat(Format.B8G8R8A8_SRGB, formats.get(0).colorSpace());
		}
	}
	
	private int getFrameCount(VkSurfaceCapabilitiesKHR capabilities) {
		int maxFrameCount = capabilities.maxImageCount();
		return maxFrameCount != 0 ? Math.min(this.frameCount, maxFrameCount) : capabilities.minImageCount();
	}
	
	record SurfaceFormat(Format imageFormat, int colorSpace) {}
	
	class SwapchainAttachment extends VkAttachment {

		public SwapchainAttachment(ImageView imageView) {
			super(imageView);
		}

		@Override
		public void free() {
			this.imageView.free();
		}
	}
}
