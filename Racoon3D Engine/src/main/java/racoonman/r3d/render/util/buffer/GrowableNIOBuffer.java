package racoonman.r3d.render.util.buffer;

import java.nio.Buffer;

abstract class GrowableNIOBuffer<T extends Buffer> implements IGrowableBuffer<T> {
	private IResizer resizer;
	protected T buffer;

	public GrowableNIOBuffer(IResizer resizer, int initialSize) {
		this.resizer = resizer;
		this.buffer = this.alloc(initialSize);
	}

	@Override
	public T get() {
		return this.buffer;
	}
	
	@Override
	public void clear() {
		this.buffer.clear();
	}

	abstract T alloc(int size);

	abstract void put(T src);
	
	//FIXME sizes are not consistent between buffer types
	@Override
	public void grow(int amount) {
		int position = this.buffer.position();
		
		int newIndex = position + amount;
		int oldSize = this.buffer.capacity();

		if (newIndex > oldSize) {
			int newSize = this.resizer.resize(oldSize, newIndex);

			T oldBuffer = this.buffer;
			this.buffer = this.alloc(newSize);
			oldBuffer.rewind();
			
			this.put(oldBuffer);
			this.free(oldBuffer);
			this.buffer.position(position);
		}
		
		this.buffer.limit(newIndex);
	}
}
