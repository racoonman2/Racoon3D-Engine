package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_6;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_7;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_8;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public enum MouseButton implements IAction {
	BUTTON_1(GLFW_MOUSE_BUTTON_1),
	BUTTON_2(GLFW_MOUSE_BUTTON_2),
	BUTTON_3(GLFW_MOUSE_BUTTON_3),
	BUTTON_4(GLFW_MOUSE_BUTTON_4),
	BUTTON_5(GLFW_MOUSE_BUTTON_5),
	BUTTON_6(GLFW_MOUSE_BUTTON_6),
	BUTTON_7(GLFW_MOUSE_BUTTON_7),
	BUTTON_8(GLFW_MOUSE_BUTTON_8),
	LAST(GLFW_MOUSE_BUTTON_LAST),
	LEFT(GLFW_MOUSE_BUTTON_LEFT),
	RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
	MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE);
	
	private int glfwType;
	
	private MouseButton(int glfwType) {
		this.glfwType = glfwType;
	}
	
	@Override
	public int asInt() {
		return this.glfwType;
	}
	
	@Override
	public Status getStatus(Window window) {
		return window.getMouseButton(this);
	}
	
	public static MouseButton lookup(int action) {
		for(MouseButton input : MouseButton.values()) {
			if(input.asInt() == action) {
				return input;
			}
		}
		return null;
	}
}
