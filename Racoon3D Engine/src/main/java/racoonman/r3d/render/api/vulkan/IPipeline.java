package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.vulkan.VK10.vkDestroyPipeline;

import racoonman.r3d.render.api.vulkan.types.BindPoint;
import racoonman.r3d.render.natives.IHandle;

interface IPipeline extends IHandle {
	
	@Override
	default void free() {
		vkDestroyPipeline(this.getDevice().get(), this.asLong(), null);
	}

	PipelineLayout getLayout();
	
	BindPoint getBindPoint();
	
	Device getDevice();
}
