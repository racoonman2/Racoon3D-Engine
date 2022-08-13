package racoonman.r3d.core.libraries;

import org.lwjgl.glfw.GLFW;

public class Libraries {
	public static final NativeLibrary GLFW_LIBRARY = new NativeLibrary(GLFW::glfwInit, GLFW::glfwTerminate);
	
	public static void init() {
	}
}
