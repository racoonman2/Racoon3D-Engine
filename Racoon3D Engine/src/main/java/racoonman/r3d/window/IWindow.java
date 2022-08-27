package racoonman.r3d.window;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.api.objects.ISwapchain;
import racoonman.r3d.render.api.types.PresentMode;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.resource.io.ClassPathReader;
import racoonman.r3d.util.NativeImage;
import racoonman.r3d.util.math.Mathi;

public interface IWindow extends IHandle {
	int getX();
	
	int getY();
	
	int getWidth();
	
	int getHeight();
	
	PresentMode getPresentMode();
	
	CharSequence getTitle();

	boolean isFocused();
	
	void focus();
	
	ISwapchain getSwapchain();
	
	Keyboard getKeyboard();
	
	Mouse getMouse();
	
	Monitor getMonitor();
	
	void setMonitor(Monitor monitor);
	
	default void setIcon(String path) {
		try(NativeImage image = ClassPathReader.read(path, NativeImage::load)) {
			this.setIcon(image);
		}
	}
	
	void setIcon(NativeImage image);
	
	default void swapBuffers() {
		this.getSwapchain().present();
	}
	
	boolean isOpen();
	
	default void toggleFullscreen() {
		this.setMonitor(this.getMonitor() != null ? null : this.getCurrentMonitor());
	}
	
	void tick();

	boolean isPressed(IAction action);

	default Monitor getCurrentMonitor() {
		int x = this.getX();
		int y = this.getY();
		int rightX = x + this.getWidth();
		int topY = y + this.getHeight();
		int prev = -1;
		Monitor result = Monitor.getPrimaryMonitor();

		for (Monitor monitor : Monitor.getMonitors()) {
			try (MemoryStack stack = stackPush()) {
				IntBuffer xBuf = stack.mallocInt(1);
				IntBuffer yBuf = stack.mallocInt(1);
				monitor.getPos(xBuf, yBuf);

				int mX = xBuf.get(0);
				int mXOffset = mX + monitor.getWidth();
				int mY = yBuf.get(0);

				int mYOffset = mY + monitor.getHeight();
				int lowestX = Mathi.clamp(mX, mXOffset, x);
				int maxX = Mathi.clamp(mX, mXOffset, rightX);
				int lowestY = Mathi.clamp(mY, mYOffset, y);
				int maxY = Mathi.clamp(mY, mYOffset, topY);
				int deltaX = Math.max(0, maxX - lowestX);
				int deltaY = Math.max(0, maxY - lowestY);
				int sqr = deltaX * deltaY;

				if (sqr > prev) {
					result = monitor;
					prev = sqr;
				}
			}
		}

		return result;
	}
}
