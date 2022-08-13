package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_STORAGE_TEXEL_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_UNIFORM_TEXEL_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

public enum BufferUsage implements IVkType {
	TRANSFER_SRC(VK_BUFFER_USAGE_TRANSFER_SRC_BIT),
	TRANSFER_DST(VK_BUFFER_USAGE_TRANSFER_DST_BIT),
	UNIFORM_TEXEL_BUFFER(VK_BUFFER_USAGE_UNIFORM_TEXEL_BUFFER_BIT),
	STORAGE_TEXEL_BUFFER(VK_BUFFER_USAGE_STORAGE_TEXEL_BUFFER_BIT),
	UNIFORM_BUFFER(VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT),
	STORAGE_BUFFER(VK_BUFFER_USAGE_STORAGE_BUFFER_BIT),
	INDEX_BUFFER(VK_BUFFER_USAGE_INDEX_BUFFER_BIT),
	VERTEX_BUFFER(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT),
	INDIRECT_BUFFER(VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT);

	private int vkType;

	private BufferUsage(int vkUsage) {
		this.vkType = vkUsage;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
