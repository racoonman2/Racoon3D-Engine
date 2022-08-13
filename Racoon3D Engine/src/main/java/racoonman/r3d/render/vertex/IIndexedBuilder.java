package racoonman.r3d.render.vertex;

public interface IIndexedBuilder extends IVertexBuilder {
	int nextIndice();

	IIndexedBuilder indices(int... indices);

	int getIndexCount();
}
