package racoonman.r3d.render;

public record Viewport(float x, float y, float width, float height, float minDepth, float maxDepth) {
	
	public static Viewport flipY(float x, float y, float width, float height, float minDepth, float maxDepth) {
		return new Viewport(x, height, width, -height, minDepth, maxDepth);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Viewport other) {
			return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height && this.minDepth == other.minDepth && this.maxDepth == other.maxDepth;
		} else {
			return false;
		}
	}
}
