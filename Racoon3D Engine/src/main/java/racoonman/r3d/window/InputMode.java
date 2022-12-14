package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_LOCK_KEY_MODS;
import static org.lwjgl.glfw.GLFW.GLFW_RAW_MOUSE_MOTION;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_MOUSE_BUTTONS;

import racoonman.r3d.window.api.glfw.IGLFWType;

public enum InputMode implements IGLFWType {
	CURSOR(GLFW_CURSOR),
	STICKY_KEYS(GLFW_STICKY_KEYS),
	STICKY_MOUSE_BUTTONS(GLFW_STICKY_MOUSE_BUTTONS),
	LOCK_KEY_MODS(GLFW_LOCK_KEY_MODS),
	RAW_MOUSE_MOTION(GLFW_RAW_MOUSE_MOTION);

	private int glfwType;

	private InputMode(int glfwType) {
		this.glfwType = glfwType;
	}

	@Override
	public int getGLFWType() {
		return this.glfwType;
	}
}
