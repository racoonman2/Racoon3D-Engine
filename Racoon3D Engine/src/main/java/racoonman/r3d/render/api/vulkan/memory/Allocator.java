package racoonman.r3d.render.api.vulkan.memory;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.vma.Vma.vmaCreateAllocator;
import static org.lwjgl.util.vma.Vma.vmaCreateBuffer;
import static org.lwjgl.util.vma.Vma.vmaDestroyAllocator;
import static org.lwjgl.util.vma.Vma.vmaFreeMemory;
import static org.lwjgl.util.vma.Vma.vmaMapMemory;
import static org.lwjgl.util.vma.Vma.vmaUnmapMemory;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;

import racoonman.r3d.render.api.vulkan.Device;
import racoonman.r3d.render.api.vulkan.PhysicalDevice;
import racoonman.r3d.render.api.vulkan.Vulkan;
import racoonman.r3d.render.natives.IHandle;

public class Allocator implements IHandle{
	private long handle;
	
	public Allocator(Vulkan vulkan, PhysicalDevice physicalDevice, Device logicalDevice) {
		try(MemoryStack stack = stackPush()) {
			VkInstance vkInstance = vulkan.get();
			VkDevice vkDevice = logicalDevice.get();
			
			VmaVulkanFunctions functions = VmaVulkanFunctions.calloc(stack)
				.set(vkInstance, vkDevice);
			
			VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc(stack)
				.instance(vkInstance)
				.device(vkDevice)
				.physicalDevice(physicalDevice.get())
				.pVulkanFunctions(functions);
				
			PointerBuffer pointer = stack.mallocPointer(1);
			vkAssert(vmaCreateAllocator(createInfo, pointer), "Error creating allocator");
			this.handle = pointer.get(0);
		}
	}
	
	public long mapMemory(Allocation allocation) {
		try(MemoryStack stack = stackPush()) {
			PointerBuffer pointerBuf = stack.mallocPointer(1);
			vmaMapMemory(this.handle, allocation.asLong(), pointerBuf);
			return pointerBuf.get(0);
		}
	}

	public void unmapMemory(Allocation allocation) {
		vmaUnmapMemory(this.handle, allocation.asLong());
	}
	
	public void freeMemory(Allocation allocation) {
		vmaFreeMemory(this.handle, allocation.asLong());
	}
	
	public long createBuffer(VmaAllocationCreateInfo allocInfo, VkBufferCreateInfo bufInfo, PointerBuffer allocPointer) {
		try(MemoryStack stack = stackPush()) {
			LongBuffer buffer = stack.mallocLong(1);
			vkAssert(vmaCreateBuffer(this.handle, bufInfo, allocInfo, buffer, allocPointer, null), "Error creating buffer");
			return buffer.get(0);
		}
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vmaDestroyAllocator(this.handle);
	}
}
