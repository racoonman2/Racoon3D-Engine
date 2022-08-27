package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_SECONDARY;

public enum Level implements IVkType {
	PRIMARY(VK_COMMAND_BUFFER_LEVEL_PRIMARY),
	SECONDARY(VK_COMMAND_BUFFER_LEVEL_SECONDARY);
	
	private int vkType;
	
	private Level(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}