package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_FLOAT_TRANSPARENT_BLACK;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_INT_OPAQUE_WHITE;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_INT_TRANSPARENT_BLACK;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum BorderColor implements IVkType {
	FLOAT_TRANSPARENT_BLACK(VK_BORDER_COLOR_FLOAT_TRANSPARENT_BLACK),
	INT_TRANSPARENT_BLACK(VK_BORDER_COLOR_INT_TRANSPARENT_BLACK),
	FLOAT_OPAQUE_BLACK(VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK),
	INT_OPAQUE_BLACK(VK_BORDER_COLOR_INT_OPAQUE_BLACK),
	FLOAT_OPAQUE_WHITE(VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE),
	INT_OPAQUE_WHITE(VK_BORDER_COLOR_INT_OPAQUE_WHITE);
	
	public static final ICodec<BorderColor> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<BorderColor> NAME_CODEC = EnumCodec.byName(BorderColor::valueOf);

	private int vkType;
	
	private BorderColor(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
