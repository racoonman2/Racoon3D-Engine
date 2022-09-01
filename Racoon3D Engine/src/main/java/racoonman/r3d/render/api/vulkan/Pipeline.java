package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.vulkan.VK10.vkDestroyPipeline;

import racoonman.r3d.render.natives.IHandle;

abstract class Pipeline implements IHandle {
	protected PipelineLayout layout;
	protected Device device;
	
	public Pipeline(PipelineLayout layout, Device device) {
		this.layout = layout;
		this.device = device;
	}
	
	public PipelineLayout getLayout() {
		return this.layout;
	}
	
	@Override
	public void free() {
		vkDestroyPipeline(this.device.get(), this.asLong(), null);
	}
}
