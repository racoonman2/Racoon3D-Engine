package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_AND;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_AND_INVERTED;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_AND_REVERSE;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_COPY;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_COPY_INVERTED;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_EQUIVALENT;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_INVERT;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_NAND;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_NOR;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_NO_OP;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_OR;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_OR_INVERTED;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_OR_REVERSE;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_SET;
import static org.lwjgl.vulkan.VK10.VK_LOGIC_OP_XOR;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum LogicOp implements IVkType {
    CLEAR(VK_LOGIC_OP_CLEAR),
    AND(VK_LOGIC_OP_AND),
    AND_REVERSE(VK_LOGIC_OP_AND_REVERSE),
    COPY(VK_LOGIC_OP_COPY),
    AND_INVERTED(VK_LOGIC_OP_AND_INVERTED),
    NO_OP(VK_LOGIC_OP_NO_OP),
    XOR(VK_LOGIC_OP_XOR),
    OR(VK_LOGIC_OP_OR),
    NOR(VK_LOGIC_OP_NOR),
    EQUIVALENT(VK_LOGIC_OP_EQUIVALENT),
    INVERT(VK_LOGIC_OP_INVERT),
    OR_REVERSE(VK_LOGIC_OP_OR_REVERSE),
    COPY_INVERTED(VK_LOGIC_OP_COPY_INVERTED),
    OR_INVERTED(VK_LOGIC_OP_OR_INVERTED),
    NAND(VK_LOGIC_OP_NAND),
    SET(VK_LOGIC_OP_SET);

	public static final ICodec<LogicOp> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<LogicOp> NAME_CODEC = EnumCodec.byName(LogicOp::valueOf);
	
	private int vkType;
	
	private LogicOp(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
