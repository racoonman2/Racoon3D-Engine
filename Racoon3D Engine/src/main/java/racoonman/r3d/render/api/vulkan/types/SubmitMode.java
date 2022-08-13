package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;

public enum SubmitMode implements IVkType {
	SINGLE(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT),
	CONTINUE(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT),
	ASYNC(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
	
	private int vkType;
	
	private SubmitMode(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
