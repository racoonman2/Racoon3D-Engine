package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_LINE;
import static org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_POINT;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum PolygonMode implements IVkType {
	FILL(VK_POLYGON_MODE_FILL),
	LINE(VK_POLYGON_MODE_LINE),
	POINT(VK_POLYGON_MODE_POINT);

	public static final ICodec<PolygonMode> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<PolygonMode> NAME_CODEC = EnumCodec.byName(PolygonMode::valueOf);
	
	private int vkType;
	
	private PolygonMode(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
