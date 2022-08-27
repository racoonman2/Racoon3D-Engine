package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import racoonman.r3d.render.api.objects.IContextSync;

class VkSemaphore implements IContextSync {
	private Device device;
	private long handle;
	
	public VkSemaphore(Device device) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
		
			VkSemaphoreCreateInfo info = VkSemaphoreCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateSemaphore(device.get(), info, null, pointer), "Error creating semaphore");
			this.handle = pointer.get(0);
		}
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}

	@Override
	public void free() {
		vkDestroySemaphore(this.device.get(), this.handle, null);
	}
}
