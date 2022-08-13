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
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;

import racoonman.r3d.render.api.vulkan.ImageView.ImageViewBuilder;
import racoonman.r3d.render.api.vulkan.sync.Semaphore;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.window.Window;

public class SwapChain implements IHandle {
	private Device device;
	private Window window;
	private WindowSurface surface;
	private int width;
	private int height;
	private SurfaceFormat format;
	private long handle;
	private ImageView[] frames;
	private VkExtent2D extent;
	private volatile int frameIndex;
	
	public SwapChain(SwapChain old) {
		this(old.getDevice(), old.getWindow(), old.getSurface(), old.getFrameCount(), old);
	}
	
	public SwapChain(Device device, Window window, WindowSurface surface, int imgCount) {
		this(device, window, surface, imgCount, null);
	}
 
	public SwapChain(Device device, Window window, WindowSurface surface, int imgCount, SwapChain oldSwapchain) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.window = window;
			this.surface = surface;
			this.width = window.getWidth();
			this.height = window.getHeight();
			
			PhysicalDevice physicalDevice = device.getPhysicalDevice();
			
			VkSurfaceCapabilitiesKHR surfaceCaps = VkSurfaceCapabilitiesKHR.calloc(stack);
			vkAssert(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice.get(), surface.asLong(), surfaceCaps), "Error retrieving surface capabilities");
			
			this.format = getSurfaceFormat(physicalDevice, surface);
			int frameCount = getImageCount(surfaceCaps, imgCount);
			this.extent = getSwapChainExtent(window, surfaceCaps);
			
			VkSwapchainCreateInfoKHR swapchainInfo = VkSwapchainCreateInfoKHR.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
				.surface(surface.asLong())
				.minImageCount(frameCount)
				.imageFormat(this.format.imageFormat().getVkType())
				.imageColorSpace(this.format.colorSpace())
				.imageExtent(this.extent)
				.imageArrayLayers(1)
				.imageUsage(ImageUsage.COLOR.getVkType())
				.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
				.preTransform(surfaceCaps.currentTransform())
				.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
				.clipped(true)
				.presentMode(window.getPresentMode().getVkType());
			if(oldSwapchain != null) {
				swapchainInfo.oldSwapchain(oldSwapchain.getHandle());
			}
				
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateSwapchainKHR(device.get(), swapchainInfo, null, pointer), "Error creating swap chain");
			this.handle = pointer.get(0);
			
			this.frames = this.makeImageViews(stack, device, this.handle, this.format.imageFormat());
		}
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Window getWindow() {
		return this.window;
	}
	
	public WindowSurface getSurface() {
		return this.surface;
	}
	
	public int getFrameCount() {
		return this.frames.length;
	}
	
	public boolean acquire(Semaphore semaphore) {
		try(MemoryStack stack = stackPush()) {
			boolean resize = false;
			
			IntBuffer pointer = stack.mallocInt(1);
			int err = vkAcquireNextImageKHR(this.device.get(), this.handle, ~0L, semaphore.getHandle(), 0L, pointer);
			if(err == VK_ERROR_OUT_OF_DATE_KHR) {
				resize = true;
			} else if(err != VK_SUBOPTIMAL_KHR && err != VK_SUCCESS) {
				throw new IllegalStateException("Error acquiring image [" + err + "]");
			}
			
			this.frameIndex = pointer.get(0);
			return resize;
		}
	}
	
	//FIXME this method takes absurd amounts of time
	public boolean present(Semaphore semaphore, DeviceQueue queue) {
		try(MemoryStack stack = stackPush()) {
			boolean resize = false;
			
			VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
				.pWaitSemaphores(stack.longs(semaphore.getHandle()))
				.swapchainCount(1)
				.pSwapchains(stack.longs(this.handle))
				.pImageIndices(stack.ints(this.frameIndex));
			
			int err = vkQueuePresentKHR(queue.get(), present);
			if(err == VK_ERROR_OUT_OF_DATE_KHR) {
				resize = true;
			} else if(err != VK_SUBOPTIMAL_KHR && err != VK_SUCCESS) {
				throw new IllegalStateException("Error presenting image [" + err + "]");
			}
			this.frameIndex %= this.frames.length;
			return resize;
		}
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public SurfaceFormat getFormat() {
		return this.format;
	}
	
	public ImageView[] getImageViews() {
		return this.frames;
	}
	
	public ImageView getImage() {
		return this.frames[this.frameIndex];
	}
	
	public VkExtent2D getExtent() {
		return this.extent;
	}
	
	public long getHandle() {
		return this.handle;
	}
	
	public int getFrame() {
		return this.frameIndex;
	}

	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		for(ImageView view : this.frames) {
			view.free();
		}
		
		vkDestroySwapchainKHR(this.device.get(), this.handle, null);
	}
	
	private ImageView[] makeImageViews(MemoryStack stack, Device device, long swapChain, Format format) {
		VkDevice logicalDevice = device.get();
		
		IntBuffer iBuf = stack.mallocInt(1);
		vkAssert(vkGetSwapchainImagesKHR(logicalDevice, swapChain, iBuf, null), "Error retrieving surface image count");
		int imgCount = iBuf.get(0);
		
		LongBuffer imagePointers = stack.mallocLong(imgCount);
		vkAssert(vkGetSwapchainImagesKHR(logicalDevice, swapChain, iBuf, imagePointers), "Error retrieving surface images");
		
		ImageView[] images = new ImageView[imgCount];
		ImageViewBuilder builder = ImageViewBuilder.create()
			.format(format)
			.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
		
		//TODO make SampleCount configurable
		for(int i = 0; i < imgCount; i++)
			images[i] = builder.image(new Image(device, format, 1, SampleCount.COUNT_1, 1, new ImageUsage[] { ImageUsage.COLOR }, this.extent.width(), this.extent.height(), 0, imagePointers.get(i))).build(device);
		return images;
	}
	
	private static VkExtent2D getSwapChainExtent(Window window, VkSurfaceCapabilitiesKHR surfaceCaps) {
		VkExtent2D res = VkExtent2D.calloc();
		VkExtent2D currentExtent = surfaceCaps.currentExtent();

		if(currentExtent.width() == 0xFFFFFFFF) {
			VkExtent2D maxExtent = surfaceCaps.maxImageExtent();
			VkExtent2D minExtent = surfaceCaps.minImageExtent();
			
			int width = Math.min(window.getWidth(), maxExtent.width());
			width = Math.max(width, minExtent.width());
			res.width(width);
			
			int height = Math.min(window.getHeight(), maxExtent.height());
			height = Math.max(height, minExtent.height());
			res.height(height);
		} else {
			res.set(currentExtent);
		}
		
		return res;
	}
	
	private static SurfaceFormat getSurfaceFormat(PhysicalDevice device, WindowSurface surface) {
		try(MemoryStack stack = stackPush()) {
			VkPhysicalDevice physicalDevice = device.get();
			long surfaceHandle = surface.asLong();
			
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
	
	public static int getImageCount(VkSurfaceCapabilitiesKHR capabilities, int requestedImageCount) {
		int maxImageCount = capabilities.maxImageCount();
		int minImageCount = capabilities.minImageCount();
		
		return maxImageCount != 0 ? Math.min(requestedImageCount, maxImageCount) : minImageCount;
	}
	
	public record SurfaceFormat(Format imageFormat, int colorSpace) {}
}
