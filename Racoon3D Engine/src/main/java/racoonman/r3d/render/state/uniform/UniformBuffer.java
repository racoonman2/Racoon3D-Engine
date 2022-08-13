package racoonman.r3d.render.state.uniform;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.render.util.buffer.AlignedBuffer;

public class UniformBuffer extends AlignedBuffer implements IHandle {
	private IDeviceBuffer buffer;
	
	public UniformBuffer(IDeviceBuffer buffer) {
		super(buffer::asByteBuffer, Layout.STD_140);
		
		this.buffer = buffer;
	}
	
	public UniformBuffer map() {
		this.buffer.map();
		return this;
	}
	
	public UniformBuffer unmap() {
		this.buffer.unmap();
		return this;
	}
	
	@Override
	public UniformBuffer reset() {
		super.reset();
		return this;
	}
	
	@Override
	public UniformBuffer jump(int index) {
		super.jump(index);
		return this;
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
		this.buffer.free();
	}
	
	public static UniformBuffer ofSize(int size) {
		return new UniformBuffer(RenderSystem.allocate(size, BufferUsage.UNIFORM_BUFFER));
	}
}
