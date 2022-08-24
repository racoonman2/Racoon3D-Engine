package racoonman.r3d.window;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.DoubleBuffer;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.window.api.glfw.Window;

public class Mouse extends InputListener<MouseButton> {
	private boolean locked;
	private double sensitivity;

	private double prevX;
	private double prevY;
	private double deltaX;
	private double deltaY;

	public Mouse(Window window) {
		super(window);

		this.sensitivity = 0.1D;
	}

	public void tick() {
		try (MemoryStack stack = stackPush()) {
			super.tick();

			if(this.window.isPressed(MouseButton.LEFT)) {
				this.window.focus();
			}
			
			if (this.window.isFocused()) {
				if(!this.locked) {
					this.locked = true;
					int centerX = this.window.getWidth() / 2;
					int centerY = this.window.getHeight() / 2;
	
					this.window.setInputMode(InputMode.CURSOR, Cursor.DISABLED);
					this.window.setCursorPos(centerX, centerY);
					
					this.prevX = centerX;
					this.prevY = centerY;
				}
			} else {
				this.locked = false;
				int centerX = this.window.getWidth() / 2;
				int centerY = this.window.getHeight() / 2;

				this.window.setInputMode(InputMode.CURSOR, Cursor.NORMAL);	
				this.window.setCursorPos(centerX, centerY);
				
				this.prevX = centerX;
				this.prevY = centerY;
			}

			if(this.locked) {
				DoubleBuffer x = stack.mallocDouble(1);
				DoubleBuffer y = stack.mallocDouble(1);

				this.window.getCursorPos(x, y);
				double xPos = x.get(0);
				double yPos = y.get(0);
	
				this.deltaX = xPos - this.prevX;
				this.deltaY = yPos - this.prevY;
	
				this.prevX = xPos;
				this.prevY = yPos;
			} else {
				this.deltaX = 0.0D;
				this.deltaY = 0.0D;
			}
		}
	}

	public double getDeltaX() {
		return this.deltaX * this.sensitivity;
	}

	public double getDeltaY() {
		return this.deltaY * this.sensitivity;
	}

	public double getSensitivity() {
		return this.sensitivity;
	}

	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
}