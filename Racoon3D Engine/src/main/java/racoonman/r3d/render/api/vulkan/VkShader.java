package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;
import static org.lwjgl.vulkan.VK10.vkDestroyShaderModule;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.shader.ShaderStage;

class VkShader implements IShader {
	private ShaderStage stage;
	private String name;
	private Device device;
	private long handle;
	
	public VkShader(Device device, ShaderStage stage, String name, ByteBuffer data) {
		try(MemoryStack stack = stackPush()) {
			this.stage = stage;
			this.name = name;
			this.device = device;
			
			VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
				.pCode(data);
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateShaderModule(device.get(), createInfo, null, pointer), "Error creating shader module");
			this.handle = pointer.get(0);
		}
	}

	@Override
	public ShaderStage stage() {
		return this.stage;
	}

	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}

	@Override
	public void free() {
		vkDestroyShaderModule(this.device.get(), this.handle, null);
	}
}