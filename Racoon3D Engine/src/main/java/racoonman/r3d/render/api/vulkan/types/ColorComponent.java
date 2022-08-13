package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_A_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_B_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_G_BIT;
import static org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_R_BIT;

public enum ColorComponent implements IVkType {
	RED(VK_COLOR_COMPONENT_R_BIT),
	GREEN(VK_COLOR_COMPONENT_G_BIT),
	BLUE(VK_COLOR_COMPONENT_B_BIT),
	ALPHA(VK_COLOR_COMPONENT_A_BIT);

	private int vkType;
	
	private ColorComponent(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
