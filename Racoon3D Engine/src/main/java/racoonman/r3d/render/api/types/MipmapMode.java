package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum MipmapMode implements IVkType {
	NEAREST(VK_SAMPLER_MIPMAP_MODE_NEAREST),
	LINEAR(VK_SAMPLER_MIPMAP_MODE_LINEAR);

	public static final ICodec<MipmapMode> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<MipmapMode> NAME_CODEC = EnumCodec.byName(MipmapMode::valueOf);
	
	private int vkType;

	private MipmapMode(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
