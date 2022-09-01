package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.types.Status;
import racoonman.r3d.render.natives.IHandle;

public interface IHostSync extends IHandle {
	void reset();
	
	void await(long timeout);
	
	Status getStatus();
	
	default boolean is(Status status) {
		return this.getStatus() == status;
	}
}
