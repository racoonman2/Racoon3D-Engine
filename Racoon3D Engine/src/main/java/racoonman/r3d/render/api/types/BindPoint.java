package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_COMPUTE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;

public enum BindPoint implements IVkType {
	GRAPHICS(VK_PIPELINE_BIND_POINT_GRAPHICS),
	COMPUTE(VK_PIPELINE_BIND_POINT_COMPUTE);
	
	private int vkType;
	
	private BindPoint(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
