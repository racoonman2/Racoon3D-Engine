package racoonman.r3d.render.util.buffer;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memAllocDouble;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocShort;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public interface IGrowableBuffer<T> {
	T get();
	
	void clear();
	
	void grow(int amount);

	void free(T t);
	
	default void free() {
		this.free(this.get());
	}
	
	public static IGrowableBuffer<FloatBuffer> floats(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			FloatBuffer alloc(int size) {
				return memAllocFloat(size);
			}

			@Override
			public void free(FloatBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(FloatBuffer src) {
				this.buffer.put(src);
			}
		};
	}
	
	public static IGrowableBuffer<DoubleBuffer> doubles(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			DoubleBuffer alloc(int size) {
				return memAllocDouble(size);
			}

			@Override
			public void free(DoubleBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(DoubleBuffer src) {
				this.buffer.put(src);
			}
		};
	}

	public static IGrowableBuffer<ByteBuffer> bytes(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			ByteBuffer alloc(int size) {
				return memAlloc(size);
			}

			@Override
			public void free(ByteBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(ByteBuffer src) {
				this.buffer.put(src);
			}
		};
	}

	public static IGrowableBuffer<ShortBuffer> shorts(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			ShortBuffer alloc(int size) {
				return memAllocShort(size);
			}

			@Override
			public void free(ShortBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(ShortBuffer src) {
				this.buffer.put(src);
			}
		};
	}
	
	public static IGrowableBuffer<IntBuffer> ints(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			IntBuffer alloc(int size) {
				return memAllocInt(size);
			}

			@Override
			public void free(IntBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(IntBuffer src) {
				this.buffer.put(src);
			}
		};
	}
	
	public static IGrowableBuffer<LongBuffer> longs(IResizer resizer, int size) {
		return new GrowableNIOBuffer<>(resizer, size) {

			@Override
			LongBuffer alloc(int size) {
				return memAllocLong(size);
			}

			@Override
			public void free(LongBuffer buffer) {
				memFree(buffer);
			}

			@Override
			void put(LongBuffer src) {
				this.buffer.put(src);
			}
		};
	}
}
