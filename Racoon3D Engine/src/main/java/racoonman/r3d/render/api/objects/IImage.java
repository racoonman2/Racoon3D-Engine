package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.vulkan.types.ImageUsage;
import racoonman.r3d.render.memory.IMemoryCopier;

public interface IImage extends ICopyable<IImage> {
	long getHandle();
	
	ImageUsage[] getUsage();
	
	int getLayers();

	int getMipLevels();
	
	default void copy(IMemoryCopier copier, IImage image) {
		copier.copy(image, this);
	}
}
