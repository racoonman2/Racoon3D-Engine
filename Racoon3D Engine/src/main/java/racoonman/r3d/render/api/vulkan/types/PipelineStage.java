package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_DRAW_INDIRECT_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_GEOMETRY_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_HOST_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TESSELLATION_CONTROL_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TESSELLATION_EVALUATION_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_VERTEX_INPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_VERTEX_SHADER_BIT;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum PipelineStage implements IVkType {
	TOP_OF_PIPE(VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT),
	DRAW_INDIRECT(VK_PIPELINE_STAGE_DRAW_INDIRECT_BIT),
	VERTEX_INPUT(VK_PIPELINE_STAGE_VERTEX_INPUT_BIT),
	VERTEX(VK_PIPELINE_STAGE_VERTEX_SHADER_BIT),
	TESSELLATION_CONTROL(VK_PIPELINE_STAGE_TESSELLATION_CONTROL_SHADER_BIT),
	TESSELLATION_EVAL(VK_PIPELINE_STAGE_TESSELLATION_EVALUATION_SHADER_BIT),
	GEOMETRY(VK_PIPELINE_STAGE_GEOMETRY_SHADER_BIT),
	FRAGMENT(VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT),
	EARLY_FRAGMENT_TESTS(VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT),
	LATE_FRAGMENT_TESTS(VK_PIPELINE_STAGE_LATE_FRAGMENT_TESTS_BIT),
	COLOR_ATTACHMENT_OUTPUT(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
	COMPUTE_SHADER(VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT),
	TRANFER(VK_PIPELINE_STAGE_TRANSFER_BIT),
	BOTTOM_OF_PIPE(VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT),
	HOST(VK_PIPELINE_STAGE_HOST_BIT),
	ALL_GRAPHICS(VK_PIPELINE_STAGE_ALL_GRAPHICS_BIT),
	ALL_COMMANDS(VK_PIPELINE_STAGE_ALL_COMMANDS_BIT);

	public static final ICodec<PipelineStage> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<PipelineStage> NAME_CODEC = EnumCodec.byName(PipelineStage::valueOf);
	
	private int vkType;

	private PipelineStage(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
