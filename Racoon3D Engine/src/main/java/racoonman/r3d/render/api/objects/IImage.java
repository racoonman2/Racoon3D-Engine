package racoonman.r3d.render.api.objects;

import org.lwjgl.system.NativeResource;

import racoonman.r3d.render.api.ICopyable;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.ImageUsage;
import racoonman.r3d.render.memory.IMemoryCopier;

public interface IImage extends ICopyable<IImage>, NativeResource {
	long getHandle();
	
	Format getFormat();
	
	ImageUsage[] getUsage();
	
	int getWidth();
	
	int getHeight();
	
	int getLayerCount();

	int getMipLevels();
	
	//int getLayers(int begin, int end);
	
	default void copy(IMemoryCopier copier, IImage image) {
		copier.copy(image, this);
	}
}
