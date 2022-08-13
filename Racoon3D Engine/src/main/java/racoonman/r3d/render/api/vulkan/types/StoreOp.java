package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE;
import static org.lwjgl.vulkan.VK13.VK_ATTACHMENT_STORE_OP_NONE;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum StoreOp implements IVkType {
	STORE(VK_ATTACHMENT_STORE_OP_STORE),
	NONE(VK_ATTACHMENT_STORE_OP_NONE),
	DONT_CARE(VK_ATTACHMENT_STORE_OP_DONT_CARE);

	public static final ICodec<StoreOp> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<StoreOp> NAME_CODEC = EnumCodec.byName(StoreOp::valueOf);
	
	private int vkType;
	
	private StoreOp(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
