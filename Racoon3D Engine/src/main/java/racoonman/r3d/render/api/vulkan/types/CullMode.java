package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_BACK_BIT;
import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_FRONT_AND_BACK;
import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_FRONT_BIT;
import static org.lwjgl.vulkan.VK10.VK_CULL_MODE_NONE;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum CullMode implements IVkType {
	NONE(VK_CULL_MODE_NONE),
	FRONT(VK_CULL_MODE_FRONT_BIT),
	BACK(VK_CULL_MODE_BACK_BIT),
	FRONT_AND_BACK(VK_CULL_MODE_FRONT_AND_BACK);

	public static final ICodec<CullMode> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<CullMode> NAME_CODEC = EnumCodec.byName(CullMode::valueOf);
	
	private int vkType;
	
	private CullMode(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
