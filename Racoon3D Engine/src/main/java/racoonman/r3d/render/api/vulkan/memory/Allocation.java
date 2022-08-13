package racoonman.r3d.render.api.vulkan.memory;

import static org.lwjgl.system.MemoryUtil.memByteBuffer;

import java.nio.ByteBuffer;

import racoonman.r3d.render.natives.IHandle;

public class Allocation implements IHandle {
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
		if (this.pointer != 0L) {
			this.allocator.unmapMemory(this);
			this.pointer = 0L;
		}
	}
	
	public void free() {
		this.allocator.freeMemory(this);
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
}
