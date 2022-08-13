package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_1D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_1D_ARRAY;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D_ARRAY;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_3D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_CUBE;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_CUBE_ARRAY;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum ViewType implements IVkType {
	TYPE_1D(VK_IMAGE_VIEW_TYPE_1D),
	TYPE_2D(VK_IMAGE_VIEW_TYPE_2D),
	TYPE_3D(VK_IMAGE_VIEW_TYPE_3D),
	TYPE_CUBE(VK_IMAGE_VIEW_TYPE_CUBE),
	TYPE_1D_ARRAY(VK_IMAGE_VIEW_TYPE_1D_ARRAY),
	TYPE_2D_ARRAY(VK_IMAGE_VIEW_TYPE_2D_ARRAY),
	TYPE_CUBE_ARRAY(VK_IMAGE_VIEW_TYPE_CUBE_ARRAY);
	
	public static final ICodec<ViewType> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<ViewType> NAME_CODEC = EnumCodec.byName(ViewType::valueOf);
	
	private int vkType;

	private ViewType(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
