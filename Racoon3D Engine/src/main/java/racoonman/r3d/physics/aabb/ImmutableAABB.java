package racoonman.r3d.physics.aabb;

public class ImmutableAABB extends BasicAABB {
	
	protected ImmutableAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public ImmutableAABB scale(float scaleX, float scaleY, float scaleZ) {
		return new ImmutableAABB(this.minX * scaleZ, this.minY * scaleZ, this.minZ * scaleZ, this.maxX * scaleX, this.maxY * scaleY, this.maxZ * scaleZ);
	}

	@Override
	public ImmutableAABB move(float x, float y, float z) {
		return new ImmutableAABB(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}
}
