package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

public class Semaphore {
	private Device device;
	private long handle;
	
	public Semaphore(Device device) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
		
			VkSemaphoreCreateInfo info = VkSemaphoreCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateSemaphore(device.get(), info, null, pointer), "Error creating semaphore");
			this.handle = pointer.get(0);
		}
	}
	
	public long getHandle() {
		return this.handle;
	}
	
	public void free() {
		vkDestroySemaphore(this.device.get(), this.handle, null);
	}
}
