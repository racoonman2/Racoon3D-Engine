package racoonman.r3d.render.api;

import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.memory.IMemoryCopier;

public interface ICopyable<T> {
	default void copy(T src) {
		this.copy(Driver.getMemoryCopier(), src);
	}
	
	void copy(IMemoryCopier uploader, T src);
}
