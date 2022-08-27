package racoonman.r3d.render.memory;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IImage;

//TODO add support for Image -> buffer copies
public interface IMemoryCopier {
	void copy(IDeviceBuffer src, IDeviceBuffer dst);

	default void copy(IImage src, IImage dst) {
		//TODO
	}
}
