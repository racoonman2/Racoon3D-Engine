package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.vulkan.types.PresentMode;
import racoonman.r3d.render.natives.IHandle;

public interface IWindowSurface extends IHandle {
	ISwapchain makeSwapchain(int frameCount);
	
	int getWidth();
	
	int getHeight();
	
	PresentMode getPresentMode();
}
