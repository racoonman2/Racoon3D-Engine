package racoonman.r3d.render.api.vulkan;

import java.nio.ByteBuffer;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IMappedMemory;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.memory.Allocation;

class VkMappedRegion implements IMappedMemory {
	private IDeviceBuffer buffer;
	private long offset;
	
	public VkMappedRegion(Service service, long initialSize) {
		this.buffer = service.allocate(initialSize, new BufferUsage[] { BufferUsage.TRANSFER_SRC}, new Property[] { Property.HOST_VISIBLE, Property.HOST_COHERENT });
		this.buffer.map();
	}
	
	public IDeviceBuffer allocate(long size) {
		this.grow(size);
		ChildBuffer buffer = new ChildBuffer(this.offset, size);
		this.offset += size;
		return buffer;
	}
	
	private void free(ChildBuffer buffer) {
		//TODO
	}
	
	private void grow(long amount) {
		if(this.offset + amount > this.buffer.size()) {
			IDeviceBuffer newBuffer = Allocation.ofSize(this.buffer.size() + amount)
				.withUsage(BufferUsage.TRANSFER_SRC)
				.withProperties(Property.HOST_VISIBLE, Property.HOST_COHERENT)
				.allocate();
			newBuffer.map();
			newBuffer.asByteBuffer().put(this.buffer.asByteBuffer());
			this.buffer.unmap();
			Driver.free(this.buffer);
			this.buffer = newBuffer;
		}
	}
	
	class ChildBuffer implements IDeviceBuffer {
		private long offset;
		private long size;
		
		public ChildBuffer(long offset, long size) {
			this.offset = offset;
			this.size = size;
		}

		@Override
		public void map() {
			//NOOP; parent buffer is persistently mapped
		}

		@Override
		public void unmap() {
			//NOOP; parent buffer is persistently mapped
		}


		@Override
		public boolean isHostVisible() {
			return VkMappedRegion.this.buffer.isHostVisible();
		}

		@Override
		public boolean isMapped() {
			return VkMappedRegion.this.buffer.isMapped();
		}

		@Override
		public long size() {
			return this.size;
		}

		@Override
		public ByteBuffer asByteBuffer(int offset) {
			return VkMappedRegion.this.buffer.asByteBuffer((int) this.offset + offset);
		}
		
		@Override
		public long getOffset() {
			return this.offset;
		}

		@Override
		public long asLong() {
			return VkMappedRegion.this.buffer.asLong();
		}
		
		@Override
		public void free() {
			VkMappedRegion.this.free(this);
		}
	}
}
