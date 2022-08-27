package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_CONSTANT_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_CONSTANT_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_DST_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_DST_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC1_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC1_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC1_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC1_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC_ALPHA;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC_ALPHA_SATURATE;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC_COLOR;
import static org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ZERO;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum BlendFactor implements IVkType {
	ZERO(VK_BLEND_FACTOR_ZERO),
	ONE(VK_BLEND_FACTOR_ONE),
	SRC_COLOR(VK_BLEND_FACTOR_SRC_COLOR),
	ONE_MINUS_SRC_COLOR(VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR),
	DST_COLOR(VK_BLEND_FACTOR_DST_COLOR),
	ONE_MINUS_DST_COLOR(VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR),
	SRC_ALPHA(VK_BLEND_FACTOR_SRC_ALPHA),
	ONE_MINUS_SRC_ALPHA(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA),
	DST_ALPHA(VK_BLEND_FACTOR_DST_ALPHA),
	ONE_MINUS_DST_ALPHA(VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA),
	CONSTANT_COLOR(VK_BLEND_FACTOR_CONSTANT_COLOR),
	ONE_MINUS_CONSTANT_COLOR(VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_COLOR),
	CONSTANT_ALPHA(VK_BLEND_FACTOR_CONSTANT_ALPHA),
	ONE_MINUS_CONSTANT_ALPHA(VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_ALPHA),
	SRC_ALPHA_SATURATE(VK_BLEND_FACTOR_SRC_ALPHA_SATURATE),
	SRC1_COLOR(VK_BLEND_FACTOR_SRC1_COLOR),
	ONE_MINUS_SRC1_COLOR(VK_BLEND_FACTOR_ONE_MINUS_SRC1_COLOR),
	SRC1_ALPHA(VK_BLEND_FACTOR_SRC1_ALPHA),
	ONE_MINUS_SRC1_ALPHA(VK_BLEND_FACTOR_ONE_MINUS_SRC1_ALPHA);

	public static final ICodec<BlendFactor> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<BlendFactor> NAME_CODEC = EnumCodec.byName(BlendFactor::valueOf);
	
	private int vkType;
	
	private BlendFactor(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
