package racoonman.r3d.render.vertex;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

import racoonman.r3d.render.buffer.IRenderBuffer;
import racoonman.r3d.render.util.Color;
import racoonman.r3d.render.vertex.VertexFormat.Attribute;

public interface IVertexBuilder extends AutoCloseable {
	default IVertexBuilder begin(VertexFormat format) {
		return this.begin(0, format);
	}
	
	IVertexBuilder begin(int size, VertexFormat format);

	IVertexBuilder begin(int index, int size, VertexFormat format);
	
	IVertexBuilder floats(Attribute attribute, float... floats);

	IVertexBuilder doubles(Attribute attribute, double... doubles);
	
	IVertexBuilder ints(Attribute attribute, int... ints);
	
	IVertexBuilder reset();
	
	IVertexBuilder end();

	IVertexBuilder transform(Matrix4f matrix);
	
	IVertexBuilder swap(int buffer);
	
	IVertexBuilder primary(int buffer);

	IVertexBuilder order(IVertexOrder order);
	
	int getVertexCount();
	
	Matrix4f getTransform();

	void finish(IRenderBuffer target);

	@Override
	default void close() {
		this.free();
	}
	
	void free();
	
	IRenderBuffer finish();

	default IVertexBuilder pos(float x, float y) {
		return this.floats(Attribute.POSITION_2F, x, y);
	}
	
	default IVertexBuilder pos(Vector2f xy) {
		return this.pos(xy.x, xy.y);
	}
	
	default IVertexBuilder pos(float x, float y, float z) {
		Matrix4f transform = this.getTransform();

		return this.floats(Attribute.POSITION_3F, 
			Math.fma(transform.m00(), x, Math.fma(transform.m10(), y, Math.fma(transform.m20(), z, transform.m30()))),
			Math.fma(transform.m01(), x, Math.fma(transform.m11(), y, Math.fma(transform.m21(), z, transform.m31()))),
			Math.fma(transform.m02(), x, Math.fma(transform.m12(), y, Math.fma(transform.m22(), z, transform.m32())))
		);
	}
	
	default IVertexBuilder pos(Vector3f xyz) {
		return this.pos(xyz.x, xyz.y, xyz.z);
	}
	
	default IVertexBuilder tex(float u, float v) {
		return this.floats(Attribute.TEX_2F, u, v);
	}
	
	default IVertexBuilder tex(Vector2f xy) {
		return this.tex(xy.x, xy.y);
	}
	
	default IVertexBuilder normal(float x, float y, float z) {
		return this.floats(Attribute.NORMAL_3F, x, y, z);
	}
	
	default IVertexBuilder normal(Vector3f xyz) {
		return this.normal(xyz.x, xyz.y, xyz.z);
	}
	
	default IVertexBuilder color(Color color) {
		return this.color(color.red(), color.green(), color.blue(), color.alpha());
	}
	
	default IVertexBuilder color(float r, float g, float b, float a) {
		return this.floats(Attribute.COLOR_4F, r, g, b, a);
	}
	
	default IVertexBuilder color(int r, int g, int b, int a) {
		return this.floats(Attribute.COLOR_4F, Color.normalize(r), Color.normalize(g), Color.normalize(b), Color.normalize(a));
	}
	
	default IVertexBuilder color(Vector4i rgba) {
		return this.floats(Attribute.COLOR_4F, Color.normalize(rgba.x), Color.normalize(rgba.y), Color.normalize(rgba.z), Color.normalize(rgba.w));
	}
	
	default IVertexBuilder vec2f(float x, float y) {
		return this.floats(Attribute.VEC_2F, x, y);
	}
	
	default IVertexBuilder vec2f(Vector2f xy) {
		return this.vec2f(xy.x, xy.y);
	}
	
	default IVertexBuilder vec3f(float x, float y, float z) {
		return this.floats(Attribute.VEC_3F, x, y, z);
	}
	
	default IVertexBuilder vec3f(Vector3f xyz) {
		return this.vec3f(xyz.x, xyz.y, xyz.z);
	}
	
	default IVertexBuilder i(int i) {
		return this.ints(Attribute.VEC_1I, i);
	}
	
	default IVertexBuilder identity() {
		this.getTransform().identity();
		return this;
	}
	
	default IVertexBuilder translate(float x, float y, float z) {
		this.getTransform().translate(x, y, z);
		return this;
	}
	
	default IVertexBuilder rotate(float x, float y, float z) {
		this.getTransform().rotateXYZ(x, y, z);
		return this;
	}
	
	default IVertexBuilder scale(float x, float y, float z) {
		this.getTransform().scale(x, y, z);
		return this;
	}
	
	default IVertexBuilder scale(float scale) {
		this.scale(scale, scale, scale);
		return this;
	}

	public static IIndexedBuilder indexed(IVertexOrder vertexOrder) {
		return indexed(0, vertexOrder);
	}
	
	public static IIndexedBuilder indexed(int indiceSize, IVertexOrder vertexOrder) {
		return new IndexedVertexBuilder(vertexOrder, indiceSize);
	}
	
	public static IVertexBuilder explicit(IVertexOrder vertexOrder) {
		return new VertexBuilderImpl(vertexOrder);
	}
	
	public static interface IVertexOrder {
		public static final IVertexOrder TRIANGLE_CW = of(3, 0, 1, 2);
		public static final IVertexOrder QUAD_CW = of(4, 0, 1, 2, 2, 3, 0);
		public static final IVertexOrder QUAD_CCW = of(4, 0, 3, 2, 2, 1, 0);
		
		int getVertexCount();
		
		int[] getOrder();
		
		public static IVertexOrder of(int vertexCount, int... order) {
			return new IVertexOrder() {

				@Override
				public int getVertexCount() {
					return vertexCount;
				}

				@Override
				public int[] getOrder() {
					return order;
				}
			};
		}
	}
}
