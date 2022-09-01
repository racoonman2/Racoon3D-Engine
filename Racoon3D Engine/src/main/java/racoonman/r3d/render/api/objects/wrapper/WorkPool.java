package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.objects.IWorkPool;
import racoonman.r3d.render.api.types.Work;
import racoonman.r3d.render.core.Driver;

public class WorkPool implements IWorkPool {
	private IWorkPool delegate;
	
	public WorkPool(int index, Work... flags) {
		this.delegate = Driver.createPool(index, flags);
	}
	
	@Override
	public Context dispatch() {
		return this.delegate.dispatch();
	}

	@Override
	public void copy(IDeviceBuffer src, IDeviceBuffer dst) {
		this.delegate.copy(src, dst);
	}
}
