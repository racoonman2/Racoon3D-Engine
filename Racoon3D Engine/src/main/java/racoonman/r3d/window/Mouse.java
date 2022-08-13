package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.DoubleBuffer;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.render.config.Config;

public class Mouse extends InputListener<MouseButton> {
	private boolean mouseLocked;

	private double xPos;
	private double yPos;
	private double prevX;
	private double prevY;
	private double deltaX;
	private double deltaY;
	
	public Mouse(Window window) {
		super(window);
	}
	
	public void tick() {
    	try(MemoryStack stack = stackPush()) {
    		super.tick();

    		DoubleBuffer x = stack.mallocDouble(1);
    		DoubleBuffer y = stack.mallocDouble(1);
    		
    		this.window.getCursorPos(x, y);
            
            this.xPos = x.get(0);
            this.yPos = y.get(0);
        
        	this.deltaX = this.xPos - this.prevX;
			this.deltaY = this.yPos - this.prevY;
			
			this.prevX = this.xPos; 
			this.prevY = this.yPos;
	        
			if(!this.mouseLocked) {
				this.mouseLocked = true;
				int centerX = this.window.getWidth() / 2;
				int centerY = this.window.getHeight() / 2;
				
				this.window.setInputMode(GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				this.window.setCursorPos(centerX, centerY);
				
				this.prevX = centerX;
				this.prevY = centerY;
				
				this.xPos = centerX;
				this.yPos = centerY;
			}
		}
	}
	
	public double getXPos() {
		return this.xPos;
	}
	
	public double getYPos() {
		return this.yPos;
	}
	
	public double getDeltaX() {
		return this.deltaX * Config.mouseSensitivity;
	}
	
	public double getDeltaY() {
		return this.deltaY * Config.mouseSensitivity;
	}
}