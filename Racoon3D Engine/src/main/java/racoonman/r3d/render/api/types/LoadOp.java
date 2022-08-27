package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_LOAD;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum LoadOp implements IVkType {
	LOAD(VK_ATTACHMENT_LOAD_OP_LOAD),
	CLEAR(VK_ATTACHMENT_LOAD_OP_CLEAR),
	DONT_CARE(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
	
	public static final ICodec<LoadOp> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<LoadOp> NAME_CODEC = EnumCodec.byName(LoadOp::valueOf);
	
	private int vkType;
	
	private LoadOp(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}

}
