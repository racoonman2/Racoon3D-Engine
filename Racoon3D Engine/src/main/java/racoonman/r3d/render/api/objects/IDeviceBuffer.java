package racoonman.r3d.render.api.objects;

import java.nio.ByteBuffer;

import racoonman.r3d.render.memory.IMemoryCopier;
import racoonman.r3d.render.natives.IHandle;

public interface IDeviceBuffer extends IHandle, ICopyable<IDeviceBuffer> {
	void map();

	void unmap();
	
	boolean isMapped();
	
	boolean isHostVisible();
	
	long size();
	
	ByteBuffer asByteBuffer(int offset);
	
	default void copy(IMemoryCopier copier, IDeviceBuffer buffer) {
		copier.copy(buffer, this);
	}
	
	default IDeviceBuffer allocate(int offset, int size) {
		return new IDeviceBuffer() {
			
			@Override
			public void free() {
			}
			
			@Override
			public long asLong() {
				return IDeviceBuffer.this.asLong();
			}
			
			@Override
			public void unmap() {
				// NOOP
			}
			
			@Override
			public long size() {
				return size;
			}
			
			@Override
			public void map() {
				// NOOP				
			}
			
			@Override
			public boolean isMapped() {
				return IDeviceBuffer.this.isMapped();
			}
			
			@Override
			public boolean isHostVisible() {
				return IDeviceBuffer.this.isHostVisible();
			}
			
			@Override
			public ByteBuffer asByteBuffer(int o) {
				return IDeviceBuffer.this.asByteBuffer(offset + o);
			}

			@Override
			public void copy(IMemoryCopier copier, IDeviceBuffer src) {
			}
		};
	}

	default ByteBuffer asByteBuffer() {
		if(!this.isMapped()) {
			this.map();
		}
		
		return this.asByteBuffer((int) this.getOffset());
	}
	
	default long getOffset() {
		return 0L;
	}
}
