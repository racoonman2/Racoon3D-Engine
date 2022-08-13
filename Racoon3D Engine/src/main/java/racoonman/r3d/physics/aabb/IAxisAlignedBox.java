package racoonman.r3d.physics.aabb;

public interface IAxisAlignedBox {
	float minX();
	
	float minY();
	
	float minZ();
	
	float maxX();
	
	float maxY();
	
	float maxZ();
	
	default IAxisAlignedBox scale(float scale) {
		return this.scale(scale, scale, scale);
	}
	
	IAxisAlignedBox scale(float scaleX, float scaleY, float scaleZ);
	
	IAxisAlignedBox move(float x, float y, float z);
	
	default float xSize() {
		return Math.abs(this.maxX() - this.minX());
	}
	
	default float ySize() {
		return Math.abs(this.maxY() - this.minY());
	}
	
	default float zSize() {
		return Math.abs(this.maxZ() - this.minZ());
	}
	
	default float xCenter() {
		return this.minX() + (this.xSize() / 2.0F);
	}
	
	default float yCenter() {
		return this.minY() + (this.ySize() / 2.0F);
	}
	
	default float zCenter() {
		return this.minZ() + (this.zSize() / 2.0F);
	}
	
	default boolean contains(float x, float y, float z) {
		return x >= this.minX() && y >= this.minY() && z >= this.minZ() && x <= this.maxX() && y <= this.maxY() && z <= this.maxZ();
	}

	default boolean contains(IAxisAlignedBox box) {
		return this.contains(box.minX(), box.minY(), box.minZ()) || this.contains(box.maxX(), box.maxY(), box.maxZ());
	}
	
	public static IAxisAlignedBox mutable(IAxisAlignedBox box) {
		return mutable(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
	}
	
	public static IAxisAlignedBox mutable(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return new MutableAABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static IAxisAlignedBox immutable(IAxisAlignedBox box) {
		if(box instanceof ImmutableAABB) {
			return box;
		} else {
			return immutable(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
		}
	}
	
	public static IAxisAlignedBox immutable(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return new ImmutableAABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
