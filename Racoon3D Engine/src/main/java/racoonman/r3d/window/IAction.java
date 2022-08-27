package racoonman.r3d.window;

import racoonman.r3d.window.api.glfw.GLFWWindow;
import racoonman.r3d.window.api.glfw.IGLFWType;

public interface IAction extends IGLFWType {
	Status getStatus(GLFWWindow window);
}
