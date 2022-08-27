package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.TextureState;
import racoonman.r3d.render.natives.IHandle;

public interface ITexture extends IHandle, IImage {
	TextureState getState();
}
