package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER_DYNAMIC;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_TEXEL_BUFFER;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum DescriptorType implements IVkType {
	SAMPLER(VK_DESCRIPTOR_TYPE_SAMPLER),
	COMBINED_IMAGE_SAMPLER(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER),
	SAMPLED_IMAGE(VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE),
	STORAGE_IMAGE(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE),
	UNIFORM_TEXEL_BUFFER(VK_DESCRIPTOR_TYPE_UNIFORM_TEXEL_BUFFER),
	STORAGE_TEXEL_BUFFER(VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER),
	UNIFORM_BUFFER(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER),
	STORAGE_BUFFER(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER),
	UNIFORM_BUFFER_DYNAMIC(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC),
	STORAGE_BUFFER_DYNAMIC(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER_DYNAMIC),
	INPUT_ATTACHMENT(VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT);

	public static final ICodec<DescriptorType> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<DescriptorType> NAME_CODEC = EnumCodec.byName(DescriptorType::valueOf);
	
	private int vkType;
	
	private DescriptorType(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
