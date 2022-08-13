package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineCache;
import static org.lwjgl.vulkan.VK10.vkDestroyPipelineCache;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;

public class PipelineCache {
	private Device device;
	private long handle;

	public PipelineCache(Device device) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreatePipelineCache(device.get(), VkPipelineCacheCreateInfo.calloc(stack)
				.sType$Default(), null, pointer), "Error creating pipeline cache");
			this.handle = pointer.get(0);
		}
	}
	
	public Device getDevice() {
		return this.device;
	}
	
	public long getHandle() {
		return this.handle;
	}
	
	public void free() {
		vkDestroyPipelineCache(this.device.get(), this.handle, null);
	}
}
