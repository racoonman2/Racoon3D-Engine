package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.memory.IMemoryCopier;

public interface IWorkPool extends IMemoryCopier {
	Context dispatch();
}
