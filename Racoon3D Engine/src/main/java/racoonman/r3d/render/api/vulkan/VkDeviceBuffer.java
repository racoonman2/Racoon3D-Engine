package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_AUTO_PREFER_DEVICE;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkDestroyBuffer;

import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.vulkan.memory.Allocation;
import racoonman.r3d.render.api.vulkan.memory.Allocator;
import racoonman.r3d.render.api.vulkan.types.BufferUsage;
import racoonman.r3d.render.api.vulkan.types.IVkType;

class VkDeviceBuffer implements IDeviceBuffer {
	private Device device;
	private Allocation allocation;
	private long size;
	private long handle;

	public VkDeviceBuffer(Device device, long size, BufferUsage... usages) {
		try (MemoryStack stack = stackPush()) {
			this.device = device;
			this.size = size;
			
			Allocator allocator = device.getMemoryAllocator();

			VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
				.size(size)
				.usage(IVkType.bitMask(usages))
				.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

			VmaAllocationCreateInfo allocInfo = VmaAllocationCreateInfo.calloc(stack)
				.requiredFlags(VMA_MEMORY_USAGE_AUTO_PREFER_DEVICE)
				.usage(IVkType.bitMask(usages));

			PointerBuffer pAlloc = stack.mallocPointer(1);

			this.handle = allocator.createBuffer(allocInfo, info, pAlloc);

			this.allocation = new Allocation(allocator, size, pAlloc.get(0));
		}
	}

	@Override
	public void map() {
		this.allocation.map();
	}

	@Override
	public ByteBuffer asByteBuffer(int offset) {
		return this.allocation.asByteBuffer(offset);
	}

	@Override
	public void unmap() {
		this.allocation.unmap();
	}

	@Override
	public long size() {
		return this.size;
	}

	@Override
	public void free() {
		vkDestroyBuffer(this.device.get(), this.handle, null);

		this.allocation.free();
	}

	@Override
	public long asLong() {
		return this.handle;
	}
}
