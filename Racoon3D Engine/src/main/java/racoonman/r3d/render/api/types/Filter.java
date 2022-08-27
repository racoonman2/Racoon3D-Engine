package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FILTER_NEAREST;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum Filter implements IVkType {
	NEAREST(VK_FILTER_NEAREST),
	LINEAR(VK_FILTER_LINEAR);

	public static final ICodec<Filter> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<Filter> NAME_CODEC = EnumCodec.byName(Filter::valueOf);
	
	private int vkType;
	
	private Filter(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
