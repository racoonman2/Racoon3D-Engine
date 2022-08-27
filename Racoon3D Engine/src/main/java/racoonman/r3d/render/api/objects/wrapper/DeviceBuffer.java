package racoonman.r3d.render.api.objects.wrapper;

import java.nio.ByteBuffer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.core.Driver;

public class DeviceBuffer implements IDeviceBuffer {
	private IDeviceBuffer delegate;
	
	public DeviceBuffer(long size, BufferUsage[] usage, Property[] properties) {
		this.delegate = Driver.allocate(size, usage, properties);
	}

	@Override
	public long asLong() {
		return this.delegate.asLong();
	}

	@Override
	public void free() {
		this.delegate.free();
	}

	@Override
	public void map() {
		this.delegate.map();
	}

	@Override
	public void unmap() {
		this.delegate.unmap();
	}

	@Override
	public boolean isMapped() {
		return this.delegate.isMapped();
	}

	@Override
	public boolean isHostVisible() {
		return this.delegate.isHostVisible();
	}

	@Override
	public long size() {
		return this.delegate.size();
	}

	@Override
	public ByteBuffer asByteBuffer(int offset) {
		return this.delegate.asByteBuffer(offset);
	}
}
