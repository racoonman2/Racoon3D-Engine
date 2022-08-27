package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_1D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_3D;

public enum ImageType implements IVkType {
	TYPE_1D(VK_IMAGE_TYPE_1D),
	TYPE_2D(VK_IMAGE_TYPE_2D),
	TYPE_3D(VK_IMAGE_TYPE_3D);

	private int vkType;
	
	private ImageType(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
