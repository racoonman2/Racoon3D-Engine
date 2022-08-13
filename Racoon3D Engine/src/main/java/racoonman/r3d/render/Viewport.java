package racoonman.r3d.render;

import racoonman.r3d.render.api.objects.IFramebuffer;

public class Viewport {
	private float x;
	private float y;
	private float width;
	private float height;
	private float minDepth;
	private float maxDepth;
	
	private Viewport(float x, float y, float width, float height, float minDepth, float maxDepth) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public float getMinDepth() {
		return this.minDepth;
	}
	
	public float getMaxDepth() {
		return this.maxDepth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Viewport other) {
			return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height && this.minDepth == other.minDepth && this.maxDepth == other.maxDepth;
		} else {
			return false;
		}
	}
	
	public static Viewport of(float x, float y, float width, float height, float minDepth, float maxDepth) {
		return new Viewport(x, y, width, height, minDepth, maxDepth);
	}
	
	public static Viewport of(IFramebuffer framebuffer, float minDepth, float maxDepth) {
		return of(0, 0, framebuffer.getWidth(), framebuffer.getHeight(), minDepth, maxDepth);
	}
}
