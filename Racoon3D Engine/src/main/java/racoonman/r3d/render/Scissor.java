package racoonman.r3d.render;

import racoonman.r3d.render.api.objects.IFramebuffer;

public class Scissor {
	private int x;
	private int y;
	private int width;
	private int height;
	
	private Scissor(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Scissor other) {
			return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height;
		} else {
			return false;
		}
	}
	
	public static Scissor of(int x, int y, int width, int height) {
		return new Scissor(x, y, width, height);
	}
	
	public static Scissor of(IFramebuffer framebuffer) {
		return of(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
	}
}
