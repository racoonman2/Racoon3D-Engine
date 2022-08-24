package racoonman.r3d.render.api.objects;

public record Scissor(int x, int y, int width, int height) {
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Scissor other) {
			return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height;
		} else {
			return false;
		}
	}
}
