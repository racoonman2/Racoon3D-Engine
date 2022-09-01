package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.vkCreateComputePipelines;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkComputePipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.shader.ShaderStage;

class ComputePipeline extends Pipeline {
	private long handle;
	
	public ComputePipeline(PipelineLayout layout, IShader shader, Device device) {
		super(layout, device);
		
		if(shader.stage() != ShaderStage.COMPUTE) {
			throw new IllegalStateException("Shader is not a compute shader");
		}
		
		try(MemoryStack stack = stackPush()) {
			VkComputePipelineCreateInfo.Buffer createInfo = VkComputePipelineCreateInfo.calloc(1, stack)
				.sType$Default()
				.stage(VkPipelineShaderStageCreateInfo.calloc(stack)
					.sType$Default()
					.module(shader.asLong())
					.stage(shader.stage().getVkType()));
			
			LongBuffer handle = stack.mallocLong(1);
			vkCreateComputePipelines(device.get(), 0L, createInfo, null, handle);
			this.handle = handle.get(0);
		}
	}

	@Override
	public long asLong() {
		return this.handle;
	}
}
