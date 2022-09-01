package racoonman.r3d.render.api;

import racoonman.r3d.render.memory.IMemoryCopier;

public interface ICopyable<T> {
	void copy(IMemoryCopier uploader, T src);
}
