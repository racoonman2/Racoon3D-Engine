package racoonman.r3d.util.math;

import org.joml.Vector3i;

public enum Face {
	NORTH(new Vector3i( 0,  0,  1)),
	SOUTH(new Vector3i( 0,  0, -1)),
	EAST( new Vector3i( 1,  0,  0)),
	WEST( new Vector3i(-1,  0,  0)),
	UP(   new Vector3i( 0,  1,  0)),
	DOWN( new Vector3i( 0, -1,  0));
	
	private Vector3i normal;
	
	private Face(Vector3i normal) {
		this.normal = normal;
	}
	
	public Vector3i getNormal() {
		return this.normal;
	}
}
