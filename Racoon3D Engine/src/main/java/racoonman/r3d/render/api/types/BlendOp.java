package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_BLEND_OP_ADD;
import static org.lwjgl.vulkan.VK10.VK_BLEND_OP_MAX;
import static org.lwjgl.vulkan.VK10.VK_BLEND_OP_MIN;
import static org.lwjgl.vulkan.VK10.VK_BLEND_OP_REVERSE_SUBTRACT;
import static org.lwjgl.vulkan.VK10.VK_BLEND_OP_SUBTRACT;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum BlendOp implements IVkType {
	ADD(VK_BLEND_OP_ADD),
	SUBTRACT(VK_BLEND_OP_SUBTRACT),
	REVERSE_SUBTRACT(VK_BLEND_OP_REVERSE_SUBTRACT),
	MIN(VK_BLEND_OP_MIN),
	MAX(VK_BLEND_OP_MAX);

	public static final ICodec<BlendOp> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<BlendOp> NAME_CODEC = EnumCodec.byName(BlendOp::valueOf);
	
	private int vkType;
	
	private BlendOp(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
