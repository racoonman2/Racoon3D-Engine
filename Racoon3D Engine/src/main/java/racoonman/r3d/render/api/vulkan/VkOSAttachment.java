package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.render.api.objects.TextureState;

public class VkOSAttachment extends VkAttachment {

	public VkOSAttachment(ImageView imageView, Device device) {
		this(imageView, TextureState.DEFAULT.copy(), device);
	}
	
	public VkOSAttachment(ImageView imageView, TextureState state, Device device) {
		super(imageView, state, device);
	}
	
	@Override
	public void free() {
		// resources are owned by the swapchain and are already freed there
	}
}
