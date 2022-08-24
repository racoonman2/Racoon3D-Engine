package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.natives.IHandle;

public interface ITexture extends IHandle, IImage {
	int getWidth();
	
	int getHeight();
	
	int getLayerCount();
	
	Format getFormat();

	TextureState getState();
	
	void free();
}
