package racoonman.r3d.render.camera;

import org.joml.Vector3f;

import racoonman.r3d.render.matrix.IMatrixType;
import racoonman.r3d.render.state.IState;

public interface ICamera {
	Vector3f getPosition();
	
	Vector3f getRotation();
	
	default void transform(IState state) {
		state.matrixType(IMatrixType.VIEW);
		
		Vector3f pos = this.getPosition();
		Vector3f rot = this.getRotation();
		state.rotateX(rot.x);
		state.rotateY(rot.y);
		state.rotateZ(rot.z);
		state.translate(-pos.x, -pos.y, -pos.z);
	}
	
	public static ICamera fixed(float x, float y, float z, float rX, float rY, float rZ) {
		return new ICamera() {
			private Vector3f pos = new Vector3f(x, y, z);
			private Vector3f rot = new Vector3f(rX, rY, rZ);

			@Override
			public Vector3f getPosition() {
				return this.pos;
			}

			@Override
			public Vector3f getRotation() {
				return this.rot;
			}
		};
	}
	
	public static ICamera dynamic() {
		return new ICamera() {
			private Vector3f pos = new Vector3f();
			private Vector3f rot = new Vector3f();

			@Override
			public Vector3f getPosition() {
				return this.pos;
			}

			@Override
			public Vector3f getRotation() {
				return this.rot;
			}
		};
	}
}
