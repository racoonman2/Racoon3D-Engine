package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.window.Window;

class WindowSurface implements IHandle {
	private Vulkan vulkan;
	private long handle;
	
	public WindowSurface(Vulkan vulkan, Window window) {
		try(MemoryStack stack = stackPush()) {
			this.vulkan = vulkan;
			
			LongBuffer surfaceP = stack.mallocLong(1);
			vkAssert(glfwCreateWindowSurface(vulkan.get(), window.asLong(), null, surfaceP), "Error creating window surface");
			this.handle = surfaceP.get(0);
		}
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
