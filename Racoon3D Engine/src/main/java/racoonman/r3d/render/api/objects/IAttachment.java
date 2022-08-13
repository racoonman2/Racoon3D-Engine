package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.vulkan.ITexture;

public interface IAttachment extends ITexture {
	IAttachment copy();
}
