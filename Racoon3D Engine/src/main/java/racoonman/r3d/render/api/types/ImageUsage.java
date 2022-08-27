package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT;

public enum ImageUsage implements IVkType {
	TRANSFER_SRC(VK_IMAGE_USAGE_TRANSFER_SRC_BIT),
	TRANSFER_DST(VK_IMAGE_USAGE_TRANSFER_DST_BIT),
	SAMPLED(VK_IMAGE_USAGE_SAMPLED_BIT),
	STORAGE(VK_IMAGE_USAGE_STORAGE_BIT),
	COLOR(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT),
	DEPTH_STENCIL(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT),
	TRANSIENT(VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT),
	INPUT(VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT);
	
	private int vkType;
	
	private ImageUsage(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}

}
