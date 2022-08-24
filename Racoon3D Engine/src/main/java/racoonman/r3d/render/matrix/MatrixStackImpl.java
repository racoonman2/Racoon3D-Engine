package racoonman.r3d.render.matrix;

import java.util.HashMap;
import java.util.Map;

//TODO rewrite
public class MatrixStackImpl implements IMatrixStack {
	private int defaultSize;
	private Map<IMatrixType<?>, Object> matrices;
	private IMatrixType<?> type;
	
	public MatrixStackImpl(int defaultSize, IMatrixType<?> defaultType) {
		this.defaultSize = defaultSize;
		this.matrices = new HashMap<>();
		this.type = defaultType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMatrix(IMatrixType<T> type) {
		return (T) this.matrices.computeIfAbsent(type, (t) -> t.create(this.defaultSize));
	}

	@Override
	public IMatrixType<?> currentType() {
		return this.type;
	}

	@Override
	public void matrixType(IMatrixType<?> type) {
		this.type = type;
	}
}
