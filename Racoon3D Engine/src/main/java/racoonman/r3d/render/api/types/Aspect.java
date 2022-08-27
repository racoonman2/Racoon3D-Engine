package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_DEPTH_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_METADATA_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_STENCIL_BIT;
import static org.lwjgl.vulkan.VK13.VK_IMAGE_ASPECT_NONE;

public enum Aspect implements IVkType {
	NONE(VK_IMAGE_ASPECT_NONE),
	COLOR(VK_IMAGE_ASPECT_COLOR_BIT),
	DEPTH(VK_IMAGE_ASPECT_DEPTH_BIT),
	STENCIL(VK_IMAGE_ASPECT_STENCIL_BIT),
	METADATA(VK_IMAGE_ASPECT_METADATA_BIT);

	private int vkType;

	private Aspect(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
