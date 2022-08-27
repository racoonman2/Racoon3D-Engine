package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateFence;
import static org.lwjgl.vulkan.VK10.vkDestroyFence;
import static org.lwjgl.vulkan.VK10.vkGetFenceStatus;
import static org.lwjgl.vulkan.VK10.vkResetFences;
import static org.lwjgl.vulkan.VK10.vkWaitForFences;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import racoonman.r3d.render.api.objects.IFence;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.Status;

class VkFence implements IFence {
	private Device device;
	private long handle;
	
	public VkFence(Device device, boolean signaled) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			
			VkFenceCreateInfo info = VkFenceCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
				.flags(signaled ? 1 : 0);
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateFence(device.get(), info, null, pointer), "Error creating fence");
			this.handle = pointer.get(0);
		}
	}
	
	public boolean is(Status status) {
		return this.getStatus() == status;
	}
	
	public Status getStatus() {
		return IVkType.byInt(vkGetFenceStatus(this.device.get(), this.handle), Status.values());
	}

	@Override
	public void await(long timeout) {
		vkWaitForFences(this.device.get(), this.handle, true, timeout);
	}

	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroyFence(this.device.get(), this.handle, null);
	}
	
	public void reset() {
		vkResetFences(this.device.get(), this.handle);
	}
}
