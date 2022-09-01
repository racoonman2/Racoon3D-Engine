package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_QUEUE_COMPUTE_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_TRANSFER_BIT;

public enum Work implements IVkType {
	TRANSFER(VK_QUEUE_TRANSFER_BIT),
	COMPUTE(VK_QUEUE_COMPUTE_BIT),
	GRAPHICS(VK_QUEUE_GRAPHICS_BIT);
	
	private int vkType;
	
	private Work(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
