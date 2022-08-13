package racoonman.r3d.render.shader;

import static org.lwjgl.util.shaderc.Shaderc.shaderc_compute_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_fragment_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_geometry_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_tess_control_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_tess_evaluation_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_vertex_shader;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_GEOMETRY_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum ShaderStage implements IShadercType, IVkType {
	VERTEX(shaderc_vertex_shader, VK_SHADER_STAGE_VERTEX_BIT),
	TESS_CONTROL(shaderc_tess_control_shader, VK_SHADER_STAGE_TESSELLATION_CONTROL_BIT),
	TESS_EVALUATION(shaderc_tess_evaluation_shader, VK_SHADER_STAGE_TESSELLATION_EVALUATION_BIT),
	GEOMETRY(shaderc_geometry_shader, VK_SHADER_STAGE_GEOMETRY_BIT),
	FRAGMENT(shaderc_fragment_shader, VK_SHADER_STAGE_FRAGMENT_BIT),
	COMPUTE(shaderc_compute_shader, VK_SHADER_STAGE_COMPUTE_BIT);
	
	public static final ICodec<ShaderStage> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<ShaderStage> NAME_CODEC = EnumCodec.byName(ShaderStage::valueOf);
	
	private int shadercType;
	private int vkType;
	
	private ShaderStage(int shadercType, int vkType) {
		this.shadercType = shadercType;
		this.vkType = vkType;
	}
	
	@Override
	public int getShadercType() {
		return this.shadercType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}