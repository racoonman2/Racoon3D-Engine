package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_16_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_2_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_32_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_4_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_64_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_8_BIT;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum SampleCount implements IVkType {
	COUNT_1(VK_SAMPLE_COUNT_1_BIT),
	COUNT_2(VK_SAMPLE_COUNT_2_BIT),
	COUNT_4(VK_SAMPLE_COUNT_4_BIT),
	COUNT_8(VK_SAMPLE_COUNT_8_BIT),
	COUNT_16(VK_SAMPLE_COUNT_16_BIT),
	COUNT_32(VK_SAMPLE_COUNT_32_BIT),
	COUNT_64(VK_SAMPLE_COUNT_64_BIT);

	public static final ICodec<SampleCount> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<SampleCount> NAME_CODEC = EnumCodec.byName(SampleCount::valueOf);
	
	private int vkType;

	private SampleCount(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
