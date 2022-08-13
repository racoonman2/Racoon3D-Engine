package racoonman.r3d.render.state.uniform;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.render.util.buffer.AlignedBuffer;

public class UniformBuffer extends AlignedBuffer implements IHandle {
	private IDeviceBuffer buffer;
	
	public UniformBuffer(IDeviceBuffer buffer) {
		super(buffer::asByteBuffer, Layout.STD_140);
		
		this.buffer = buffer;
		this.buffer.map();
	}
	
	public IDeviceBuffer getDeviceBuffer() {
		return this.buffer;
	}
	
	@Override
	public long asLong() {
		return this.buffer.asLong();
	}
	
	@Override
	public void free() {
		this.buffer.unmap();
		this.buffer.free();
	}
}
