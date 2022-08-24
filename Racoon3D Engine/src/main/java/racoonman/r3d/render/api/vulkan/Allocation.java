package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryUtil.memByteBuffer;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import racoonman.r3d.render.natives.IHandle;

class Allocation implements IHandle {
	private Allocator allocator;
	private long size;
	private long handle;
	private long pointer;
	
	public Allocation(Allocator allocator, long size, long handle) {
		this.size = size;
		this.allocator = allocator;
		this.handle = handle;
	}
	
	public void map() {
		this.pointer = this.allocator.mapMemory(this);
	}
	
	public ByteBuffer asByteBuffer(int offset) {
		return memByteBuffer(this.pointer + offset, (int) this.size - offset);
	}
	
	public void unmap() {
		if (this.pointer != MemoryUtil.NULL) {
			this.allocator.unmapMemory(this);
			this.pointer = MemoryUtil.NULL;
		}
	}

	public boolean isMapped() {
		return this.pointer != MemoryUtil.NULL;
	}
	
	public void free() {
		this.allocator.freeMemory(this);
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
}
