package racoonman.r3d.window;

import racoonman.r3d.window.api.glfw.IGLFWType;
import racoonman.r3d.window.api.glfw.Window;

public interface IAction extends IGLFWType {
	Status getStatus(Window window);
}
