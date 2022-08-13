package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public enum Status {
	RELEASE(GLFW_RELEASE),
	PRESS(GLFW_PRESS),
	REPEAT(GLFW_REPEAT);
	
	private int action;
	
	private Status(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}
	
	public static Status lookup(int button) {
		for(Status status : Status.values()) {
			if(status.getAction() == button) {
				return status;
			}
		}
		return null;
	}
}
