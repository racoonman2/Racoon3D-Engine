package racoonman.r3d.physics.aabb;

public abstract class BasicAABB implements IAxisAlignedBox {
	protected float minX;
	protected float minY;
	protected float minZ;
	protected float maxX;
	protected float maxY;
	protected float maxZ;
	
	protected BasicAABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	@Override
	public float minX() {
		return this.minX;
	}

	@Override
	public float minY() {
		return this.minY;
	}

	@Override
	public float minZ() {
		return this.minZ;
	}

	@Override
	public float maxX() {
		return this.maxX;
	}

	@Override
	public float maxY() {
		return this.maxY;
	}

	@Override
	public float maxZ() {
		return this.maxZ;
	}
}
