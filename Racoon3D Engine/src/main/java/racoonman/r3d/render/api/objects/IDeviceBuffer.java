package racoonman.r3d.render.api.objects;

import java.nio.ByteBuffer;

import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.natives.IHandle;

public interface IDeviceBuffer extends IHandle {
	void map();

	void unmap();
	
	long size();
	
	ByteBuffer asByteBuffer(int offset);

	default ByteBuffer asByteBuffer() {
		return this.asByteBuffer(0);
	}
	
	default void copy(IDeviceBuffer src) {
		RenderSystem.copy(src, this);
	}
	
	default long getOffset() {
		return 0L;
	}
}
