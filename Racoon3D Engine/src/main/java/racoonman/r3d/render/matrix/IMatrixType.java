package racoonman.r3d.render.matrix;

import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.joml.Matrix4fStack;

public interface IMatrixType<T> {
	IMatrixType<Matrix4fStack> PROJECTION = of(Matrix4fStack::new, Matrix4fStack::pushMatrix, Matrix4fStack::popMatrix, Matrix4fStack::identity);
	IMatrixType<Matrix4fStack> VIEW = of(Matrix4fStack::new, Matrix4fStack::pushMatrix, Matrix4fStack::popMatrix, Matrix4fStack::identity);
	IMatrixType<Matrix4fStack> MODEL = of(Matrix4fStack::new, Matrix4fStack::pushMatrix, Matrix4fStack::popMatrix, Matrix4fStack::identity);
	
	T create(int size);
	
	void push(T t);
	
	void pop(T t);
	
	void identity(T t);
	
	public static <T> IMatrixType<T> of(IntFunction<T> create, Consumer<T> push, Consumer<T> pop, Consumer<T> identity) {
		return new IMatrixType<T>() {
			
			@Override
			public void push(T t) {
				push.accept(t);
			}
			
			@Override
			public void pop(T t) {
				pop.accept(t);
			}

			@Override
			public void identity(T t) {
				identity.accept(t);
			}
			
			@Override
			public T create(int size) {
				return create.apply(size);
			}
		};
	}
}
