package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateCommandPool;
import static org.lwjgl.vulkan.VK10.vkDestroyCommandPool;
import static org.lwjgl.vulkan.VK10.vkResetCommandPool;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import racoonman.r3d.render.api.types.Level;
import racoonman.r3d.render.api.types.SubmitMode;
import racoonman.r3d.render.natives.IHandle;

class CommandPool implements IHandle {
	private Device device;
	private long handle;
	
	public CommandPool(Device device, DeviceQueue queue) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			
			VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
				.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
				.queueFamilyIndex(queue.getQueueFamilyIndex());
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateCommandPool(device.get(), poolInfo, null, pointer), "Error creating command pool");
			this.handle = pointer.get(0);
		}
	}
	
	public void reset() {
		vkResetCommandPool(this.device.get(), this.handle, VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT);
	}
	
	public CommandBuffer allocate(Level level, SubmitMode mode) {
		return new CommandBuffer(this, level, mode);
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}

	@Override
	public void free() {
		vkDestroyCommandPool(this.device.get(), this.handle, null);
	}
}
