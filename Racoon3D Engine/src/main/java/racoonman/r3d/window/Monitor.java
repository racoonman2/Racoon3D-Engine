package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.glfwGetMonitorName;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVidMode;

import racoonman.r3d.render.natives.IHandle;

public class Monitor implements IHandle {
	private long handle;
	private String name;
	private GLFWVidMode videoMode;
	
	private Monitor(long handle) {
		this.handle = handle;
		this.name = glfwGetMonitorName(handle);
		this.videoMode = glfwGetVideoMode(handle);
	}
	
	public GLFWVidMode getVideoMode() {
		return this.videoMode;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getWidth() {
		return this.videoMode.width();
	}
	
	public int getHeight() {
		return this.videoMode.height();
	}
	
	public void getPos(IntBuffer x, IntBuffer y) {
		glfwGetMonitorPos(this.handle, x, y);
	}
	
	public int getRefreshRate() {
		return this.videoMode.refreshRate();
	}
	
	public boolean isPrimary() {
		return this.handle == glfwGetPrimaryMonitor();
	}
	
	public static Monitor getPrimaryMonitor() {
		for(Monitor monitor : monitors)
			if(monitor.isPrimary())
				return monitor;
		return null;
	}

	public static Monitor lookup(String name) {
		for(Monitor monitor : monitors) {
			if(monitor.getName().equals(name)) {
				return monitor;
			}
		}
		return null;
	}

	@Override
	public void free() {
		// NOOP
	}

	@Override
	public long asLong() {
		return this.handle;
	}
	
	public static Monitor[] getMonitors() {
		return monitors;
	}

	private static Monitor[] monitors;

	private static void findMonitors() {
		PointerBuffer pointers = glfwGetMonitors();
		Monitor[] m = new Monitor[pointers.capacity()];
		
		for(int i = 0; i < m.length; i++)
			m[i] = new Monitor(pointers.get(i));
		monitors = m;
	}
	
	static {
		glfwInit();
		findMonitors();
		glfwSetMonitorCallback((monitor, event) -> findMonitors());
 	}
}
