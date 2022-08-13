package racoonman.r3d.physics.aabb;

public class MutableAABB extends BasicAABB {
	
	protected MutableAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public MutableAABB scale(float scaleX, float scaleY, float scaleZ) {
		this.minX *= scaleX;
		this.minY *= scaleY;
		this.minZ *= scaleZ;
		this.maxX *= scaleX;
		this.maxY *= scaleY;
		this.maxZ *= scaleZ;
		return this;
	}

	@Override
	public MutableAABB move(float x, float y, float z) {
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		return this;
	}
}
