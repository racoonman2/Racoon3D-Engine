package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_CACHED_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_LAZILY_ALLOCATED_BIT;
import static org.lwjgl.vulkan.VK11.VK_MEMORY_PROPERTY_PROTECTED_BIT;

public enum Property implements IVkType {
	DEVICE_LOCAL(VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT),
	HOST_VISIBLE(VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT),
	HOST_COHERENT(VK_MEMORY_PROPERTY_HOST_COHERENT_BIT),
	HOST_CACHED(VK_MEMORY_PROPERTY_HOST_CACHED_BIT),
	LAZILY_ALLOCATED(VK_MEMORY_PROPERTY_LAZILY_ALLOCATED_BIT),
	PROTECTED(VK_MEMORY_PROPERTY_PROTECTED_BIT);

	private int vkType;

	private Property(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}