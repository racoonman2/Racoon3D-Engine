package racoonman.r3d.render.api.vulkan;

import java.nio.ByteBuffer;
import java.util.OptionalLong;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IMappedMemoryRegion;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.core.RenderService;

//TODO
public class VkMappedRegion implements IMappedMemoryRegion {
	private RenderService service;
	private IDeviceBuffer buffer;
	private long offset;
	
	public VkMappedRegion(RenderService service, long initialSize) {
		this.service = service;
		this.buffer = service.allocate(initialSize, BufferUsage.TRANSFER_SRC);
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
		IDeviceBuffer newBuffer = this.service.allocate(this.buffer.size() + amount, BufferUsage.TRANSFER_SRC);
		newBuffer.map();
		newBuffer.asByteBuffer().put(this.buffer.asByteBuffer());
		this.buffer.unmap();
		this.service.free(this.buffer);
		this.buffer = newBuffer;
	}
	
	private OptionalLong findEmpty() {
		
		return OptionalLong.empty();
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
