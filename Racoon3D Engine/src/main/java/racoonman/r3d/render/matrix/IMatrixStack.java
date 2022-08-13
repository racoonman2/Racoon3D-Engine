package racoonman.r3d.render.matrix;

import org.joml.Matrix4fStack;

import racoonman.r3d.util.math.Mathf;

public interface IMatrixStack {
	<T> T get(IMatrixType<T> type);
	
	IMatrixType<?> currentType();
	
	void matrixType(IMatrixType<?> type);
	
	@SuppressWarnings("unchecked")
	default <T> T currentMatrix() {
		return (T) this.get(this.currentType());
	}
	
	default void pushMatrix() {
		this.currentType().push(this.currentMatrix());
	}
	
	default void popMatrix() {
		this.currentType().pop(this.currentMatrix());
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
	
	default void rotateX(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateX(Mathf.toRadians(rotation));
	}
	
	default void rotateY(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateY(Mathf.toRadians(rotation));
	}
	
	default void rotateZ(float rotation) {
		this.<Matrix4fStack>currentMatrix().rotateZ(Mathf.toRadians(rotation));
	}
}
