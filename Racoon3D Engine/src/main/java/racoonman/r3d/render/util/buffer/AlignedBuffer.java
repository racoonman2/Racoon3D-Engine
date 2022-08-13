package racoonman.r3d.render.util.buffer;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class AlignedBuffer implements AutoCloseable {
	private Supplier<ByteBuffer> buffer;
	private Layout layout;
	private int index;
	
	public AlignedBuffer(Supplier<ByteBuffer> buffer, Layout layout) {
		this.buffer = buffer;
		this.layout = layout;
	}
	
	private void nextAlignment(int alignment, int length, int unitSize) {
		this.index = this.layout.align(this.index, alignment, length, unitSize);
	}
	
	public AlignedBuffer reset() {
		return this.jump(0);
	}
	
	public AlignedBuffer layout(Layout layout) {
		this.layout = layout;
		return this;
	}
	
	public AlignedBuffer jump(int index) {
		this.index = index;
		return this;
	}

	public AlignedBuffer move(int amount) {
		this.index += amount;
		return this;
	}
	
	public AlignedBuffer put(IStruct struct) {
		struct.get(this);
		return this;
	}

	public AlignedBuffer put(Matrix4f mat4) {
		return this.put(mat4.m00(), mat4.m01(), mat4.m02(), mat4.m03())
				   .put(mat4.m10(), mat4.m11(), mat4.m12(), mat4.m13())
				   .put(mat4.m20(), mat4.m21(), mat4.m22(), mat4.m23())
				   .put(mat4.m30(), mat4.m31(), mat4.m32(), mat4.m33());
	}
	
	public AlignedBuffer put(Matrix3f mat3) {
		return this.put(mat3.m00(), mat3.m01(), mat3.m02())
				   .put(mat3.m10(), mat3.m11(), mat3.m12())
				   .put(mat3.m20(), mat3.m21(), mat3.m22());
	}
	
	public AlignedBuffer putAt(int index, IStruct struct) {
		int prevIndex = this.index;
		return this.jump(index).put(struct).jump(prevIndex);
	}
	
	public AlignedBuffer putAt(int index, float x) {
		return this.putBulkAt(index, Float.BYTES, x);
	}
	
	public AlignedBuffer putAt(int index, float x, float z) {
		return this.putBulkAt(index, Float.BYTES * 2, x, z);
	}
	
	public AlignedBuffer putAt(int index, float x, float y, float z) {
		return this.putBulkAt(index, Float.BYTES * 4, x, y, z);
	}
	
	public AlignedBuffer putAt(int index, float x, float y, float z, float w) {
		return this.putBulkAt(index, Float.BYTES * 4, x, y, z, w);
	}
	
	public AlignedBuffer putAt(int index, Matrix4f mat4) {
		return this.putAt(index,      mat4.m00(), mat4.m01(), mat4.m02(), mat4.m03())
				   .putAt(index + 16, mat4.m10(), mat4.m11(), mat4.m12(), mat4.m13())
				   .putAt(index + 32, mat4.m20(), mat4.m21(), mat4.m22(), mat4.m23())
				   .putAt(index + 48, mat4.m30(), mat4.m31(), mat4.m32(), mat4.m33());
	}
	
	public AlignedBuffer putAt(int index, Vector2f vec2) {
		return this.putAt(index, vec2.x, vec2.y);
	}
	
	public AlignedBuffer putAt(int index, Vector3f vec3) {
		return this.putAt(index, vec3.x, vec3.y, vec3.z);
	}
	
	public AlignedBuffer putAt(int index, Vector4f vec4) {
		return this.putAt(index, vec4.x, vec4.y, vec4.z, vec4.w);
	}
	
	public AlignedBuffer put(float x) {
		return this.putBulk(Float.BYTES, x);
	}
	
	public AlignedBuffer put(float x, float z) {
		return this.putBulk(Float.BYTES * 2, x, z);
	}
	
	public AlignedBuffer put(float x, float y, float z) {
		return this.putBulk(Float.BYTES * 4, x, y, z);
	}
	
	public AlignedBuffer put(float x, float y, float z, float w) {
		return this.putBulk(Float.BYTES * 4, x, y, z, w);
	}
	
	public AlignedBuffer put(Vector2f vec2) {
		return this.put(vec2.x, vec2.y);
	}
	
	public AlignedBuffer put(Vector3f vec3) {
		return this.put(vec3.x, vec3.y, vec3.z);
	}
	
	public AlignedBuffer put(Vector4f vec4) {
		return this.put(vec4.x, vec4.y, vec4.z, vec4.w);
	}
	
	private AlignedBuffer putBulkAt(int index, int alignment, float...floats) {
		int prevIndex = index;
		return this.jump(index).putBulk(alignment, floats).jump(prevIndex);
	}
	
	private AlignedBuffer putBulk(int alignment, float...floats) {
		this.nextAlignment(alignment, floats.length, Float.BYTES);
	
		for(float f : floats) {
			this.buffer.get().putFloat(this.index, f);
			
			this.move(Float.BYTES);
		}
		
		return this;
	}
	
	public AlignedBuffer put(boolean b) {
		return this.put(b ? 1 : 0);
	}
	
	public AlignedBuffer put(int x) {
		return this.putBulk(Integer.BYTES, x);
	}
	
	public AlignedBuffer put(int x, int z) {
		return this.putBulk(Integer.BYTES * 2, x, z);
	}
	
	public AlignedBuffer put(int x, int y, int z) {
		return this.putBulk(Integer.BYTES * 4, x, y, z);
	}
	
	public AlignedBuffer put(int x, int y, int z, int w) {
		return this.putBulk(Integer.BYTES * 4, x, y, z, w);
	}

	public AlignedBuffer put(Vector2i vec2) {
		return this.put(vec2.x, vec2.y);
	}
	
	public AlignedBuffer put(Vector3i vec3) {
		return this.put(vec3.x, vec3.y, vec3.z);
	}
	
	public AlignedBuffer put(Vector4i vec4) {
		return this.put(vec4.x, vec4.y, vec4.z, vec4.w);
	}
	
	public AlignedBuffer putAt(int index, int x) {
		return this.putBulkAt(index, Integer.BYTES, x);
	}
	
	public AlignedBuffer putAt(int index, int x, int z) {
		return this.putBulkAt(index, Integer.BYTES * 2, x, z);
	}
	
	public AlignedBuffer putAt(int index, int x, int y, int z) {
		return this.putBulkAt(index, Integer.BYTES * 4, x, y, z);
	}
	
	public AlignedBuffer putAt(int index, int x, int y, int z, int w) {
		return this.putBulkAt(index, Integer.BYTES * 4, x, y, z, w);
	}

	public AlignedBuffer putAt(int index, Vector2i vec2) {
		return this.putAt(index, vec2.x, vec2.y);
	}
	
	public AlignedBuffer putAt(int index, Vector3i vec3) {
		return this.putAt(index, vec3.x, vec3.y, vec3.z);
	}
	
	public AlignedBuffer putAt(int index, Vector4i vec4) {
		return this.putAt(index, vec4.x, vec4.y, vec4.z, vec4.w);
	}
	
	public ByteBuffer getByteBuffer() {
		return this.buffer.get();
	}
	
	private AlignedBuffer putBulkAt(int index, int alignment, int...ints) {
		int prevIndex = index;
		return this.jump(index).putBulk(alignment, ints).jump(prevIndex);
	}
	
	private AlignedBuffer putBulk(int alignment, int...ints) {
		this.nextAlignment(alignment, ints.length, Integer.BYTES);
		
		for(int i : ints) {
			this.buffer.get().putInt(this.index, i);
			
			this.move(Integer.BYTES);
		}
		
		return this;
	}
	
	@Override
	public void close() {
		this.reset();
	}
	
	public static interface Layout {
		public static final Layout STD_140 = (int index, int alignment, int length, int unitSize) -> {
			int remaining = index % alignment;
			int next = remaining > 0 ? index + (alignment - remaining) : index;
			int size = length * unitSize;

			return size >= remaining ? next : index;
		};
		
		int align(int index, int alignment, int length, int unitSize);
	}
	
	public static interface IStruct {
		void get(AlignedBuffer buffer);
		
		int alignedSizeof();
	}
}
