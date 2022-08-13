package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_RELAXED_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;

public enum PresentMode implements IVkType {
	FIFO(VK_PRESENT_MODE_FIFO_KHR),
	FIFO_RELAXED(VK_PRESENT_MODE_FIFO_RELAXED_KHR),
	IMMEDIATE(VK_PRESENT_MODE_IMMEDIATE_KHR),
	MAILBOX(VK_PRESENT_MODE_MAILBOX_KHR);

	private int vkType;
	
	private PresentMode(int vkType) {
		this.vkType = vkType;
	}

	@Override
	public int getVkType() {
		return this.vkType;
	}
}
