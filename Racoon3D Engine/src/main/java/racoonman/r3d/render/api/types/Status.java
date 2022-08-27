package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_NOT_READY;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public enum Status implements IVkType {
	SUCCESS(VK_SUCCESS),
	NOT_READY(VK_NOT_READY);

	private int vkType;
	
	private Status(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
