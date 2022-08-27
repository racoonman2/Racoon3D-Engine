package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.objects.ISwapchain;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.types.PresentMode;
import racoonman.r3d.window.IWindow;

class VkWindowSurface implements IWindowSurface {
	private Vulkan vulkan;
	private IWindow window;
	private long handle;
	private DeviceQueue presentQueue;
	
	public VkWindowSurface(Device device, IWindow window, int queueIndex) {
		try(MemoryStack stack = stackPush()) {
			this.vulkan = device.getVulkan();
			this.window = window;
			
			LongBuffer buffer = stack.mallocLong(1);
			vkAssert(glfwCreateWindowSurface(this.vulkan.get(), window.asLong(), null, buffer), "Error creating window surface");
			this.handle = buffer.get(0);
			
			this.presentQueue = DeviceQueue.present(device, this, queueIndex);
		}
	}

	@Override
	public ISwapchain createSwapchain(int frameCount) {
		return new VkSwapchain(this, this.presentQueue, frameCount);
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
	public PresentMode getPresentMode() {
		return this.window.getPresentMode();
	}
		
	@Override
	public long asLong() {
		return this.handle;
	}

	@Override
	public void free() {
		vkDestroySurfaceKHR(this.vulkan.get(), this.handle, null);
	}
}
