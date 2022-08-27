package racoonman.r3d.render.matrix;

import org.joml.Matrix4fStack;
import org.joml.Vector3f;

import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.util.math.Mathf;

public interface IMatrixStack {
	<T> T getMatrix(IMatrixType<T> type);
	
	IMatrixType<?> currentType();
	
	void matrixType(IMatrixType<?> type);
	
	@SuppressWarnings("unchecked")
	default <T> T currentMatrix() {
		return (T) this.getMatrix(this.currentType());
	}
	
	default void pushMatrix() {
		this.currentType().push(this.currentMatrix());
	}
	
	default void popMatrix() {
		this.currentType().pop(this.currentMatrix());
	}
	
	default void identity() {
		this.currentType().identity(this.currentMatrix());
	}
	
	default void translate(float x, float y, float z) {
		this.<Matrix4fStack>currentMatrix().translate(x, y, z);
	}
	
	default void scale(float scale) {
		this.scale(scale, scale, scale);
	}
	
	default void scale(float scaleX, float scaleY, float scaleZ) {
		this.<Matrix4fStack>currentMatrix().scale(scaleX, scaleY, scaleZ);
	}
	
	default void rotateXYZ(Vector3f rot) {
		this.rotateXYZ(rot.x, rot.y, rot.z);
	}
	
	default void rotateXYZ(float x, float y, float z) {
		this.<Matrix4fStack>currentMatrix().rotateXYZ(Mathf.toRadians(x), Mathf.toRadians(y), Mathf.toRadians(z));
	}	
	
	default void rotateX(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateX(Mathf.toRadians(rotation));
	}
	
	default void rotateY(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateY(Mathf.toRadians(rotation));
	}
	
	default void rotateZ(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateZ(Mathf.toRadians(rotation));
	}

	default void setPerspective(float fov, float aspect, float near, float far) {
		this.<Matrix4fStack>currentMatrix().perspective(Mathf.toRadians(fov), aspect, near, far, true);
	}
	
	default void setPerspective(int width, int height, float fov, float near, float far) {
		this.matrixType(IMatrixType.PROJECTION);
		
		this.setPerspective(fov, (float) width / height, near, far);
	}

	default void setPerspective(IFramebuffer framebuf, float fov, float near, float far) {
		this.setPerspective(fov, (float) framebuf.getWidth() / (float) framebuf.getHeight(), near, far);
	}
	
	default void setOrtho(float left, float right, float top, float bottom, float near, float far) {
		this.<Matrix4fStack>currentMatrix().ortho(left, right, top, bottom, near, far, true);
	}
}
