package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;

import racoonman.r3d.window.api.glfw.IGLFWType;

public enum Cursor implements IGLFWType {
	NORMAL(GLFW_CURSOR_NORMAL),
	HIDDEN(GLFW_CURSOR_HIDDEN),
	DISABLED(GLFW_CURSOR_DISABLED);

	private int glfwType;
	
	private Cursor(int glfwType) {
		this.glfwType = glfwType;
	}
	
	@Override
	public int getGLFWType() {
		return this.glfwType;
	}
}
