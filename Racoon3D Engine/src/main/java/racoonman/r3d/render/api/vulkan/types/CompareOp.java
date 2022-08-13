package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_EQUAL;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_GREATER;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_GREATER_OR_EQUAL;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_LESS;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_LESS_OR_EQUAL;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_NEVER;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_NOT_EQUAL;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum CompareOp implements IVkType {
	NEVER(VK_COMPARE_OP_NEVER),
	LESS(VK_COMPARE_OP_LESS),
	EQUAL(VK_COMPARE_OP_EQUAL),
	LESS_OR_EQUAL(VK_COMPARE_OP_LESS_OR_EQUAL),
	GREATER(VK_COMPARE_OP_GREATER),
	NOT_EQUAL(VK_COMPARE_OP_NOT_EQUAL),
	GREATER_OR_EQUAL(VK_COMPARE_OP_GREATER_OR_EQUAL),
	ALWAYS(VK_COMPARE_OP_ALWAYS);

	public static final ICodec<CompareOp> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<CompareOp> NAME_CODEC = EnumCodec.byName(CompareOp::valueOf);
	
	private int vkType;

	private CompareOp(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
