package racoonman.r3d.render.api.objects;

public record Viewport(float x, float y, float width, float height, float minDepth, float maxDepth) {
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Viewport other) {
			return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height && this.minDepth == other.minDepth && this.maxDepth == other.maxDepth;
		} else {
			return false;
		}
	}
}
