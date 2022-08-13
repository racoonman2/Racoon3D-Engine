package racoonman.r3d.render.camera;

import org.joml.Vector3f;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.matrix.IMatrixType;

public interface ICamera {
	Vector3f getPos();
	
	Vector3f getRot();
	
	default void transform(RenderContext ctx) {
		ctx.matrixType(IMatrixType.VIEW);
		
		Vector3f pos = this.getPos();
		Vector3f rot = this.getRot();
		ctx.rotateX(rot.x);
		ctx.rotateY(rot.y);
		ctx.rotateZ(rot.z);
		ctx.translate(-pos.x, -pos.y, -pos.z);
	}
	
	public static ICamera fixed(float x, float y, float z, float rX, float rY, float rZ) {
		return new ICamera() {
			private Vector3f pos = new Vector3f(x, y, z);
			private Vector3f rot = new Vector3f(rX, rY, rZ);

			@Override
			public Vector3f getPos() {
				return this.pos;
			}

			@Override
			public Vector3f getRot() {
				return this.rot;
			}
		};
	}
	
	public static ICamera dynamic() {
		return new ICamera() {
			private Vector3f pos = new Vector3f();
			private Vector3f rot = new Vector3f();

			@Override
			public Vector3f getPos() {
				return this.pos;
			}

			@Override
			public Vector3f getRot() {
				return this.rot;
			}
		};
	}
}
