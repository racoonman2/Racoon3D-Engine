package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.natives.IHandle;

public interface ISwapchain extends IFramebuffer, IHandle {
	boolean present();
	
	ISwapchain makeChild();
	
	IWindowSurface getSurface();
	
	default boolean isValid() {
		return this.getSurface().getWidth() > 0 && this.getSurface().getHeight() > 0;
	}
}
