package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.types.Status;
import racoonman.r3d.render.natives.IHandle;

public interface IFence extends IHandle {
	void await(long timeout);
	
	Status getStatus();
}
