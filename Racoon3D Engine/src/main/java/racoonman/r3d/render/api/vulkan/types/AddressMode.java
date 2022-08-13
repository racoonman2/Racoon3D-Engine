package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum AddressMode implements IVkType {
	REPEAT(VK_SAMPLER_ADDRESS_MODE_REPEAT),
	MIRRORED_REPEAT(VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT),
	CLAMP_TO_EDGE(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE),
	CLAMP_TO_BORDER(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER);

	public static final ICodec<AddressMode> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<AddressMode> NAME_CODEC = EnumCodec.byName(AddressMode::valueOf);
	
	private int vkType;
	
	private AddressMode(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
