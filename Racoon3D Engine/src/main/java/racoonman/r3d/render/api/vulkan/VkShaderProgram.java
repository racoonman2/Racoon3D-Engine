package racoonman.r3d.render.api.vulkan;

import java.util.Arrays;

import org.lwjgl.system.MemoryUtil;

import racoonman.r3d.render.RenderContext;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;

class VkShaderProgram implements IShaderProgram {
	private IShader[] shaders;
	
	public VkShaderProgram(IShader... shaders) {
		this.shaders = shaders;
	}
	
	@Override
	public IShader[] getShaders() {
		return this.shaders;
	}
	
	@Override
	public void bind(RenderContext context) {
		context.program(this);
	}
	
	@Override
	public void free() {
		for(IShader shader : this.shaders) {
			shader.free();
		}
	}

	@Override
	public long asLong() {
		return MemoryUtil.NULL;
	}
	
	@Override
	public String toString() {
		return "Program " + Arrays.toString(this.shaders);
	}
}
