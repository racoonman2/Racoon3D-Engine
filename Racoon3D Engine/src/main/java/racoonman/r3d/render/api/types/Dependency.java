package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK11.VK_DEPENDENCY_DEVICE_GROUP_BIT;

public enum Dependency implements IVkType {
	REGION(VK_DEPENDENCY_BY_REGION_BIT),
	GROUP(VK_DEPENDENCY_DEVICE_GROUP_BIT);

	private int vkType;
	
	private Dependency(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
