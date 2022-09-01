package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.core.Driver;

public class ContextSync implements IDeviceSync {
	private IDeviceSync delegate;
	
	public ContextSync() {
		this.delegate = Driver.createDeviceSync();
	}

	@Override
	public long asLong() {
		return this.delegate.asLong();
	}
	
	@Override
	public void free() {
		this.delegate.free();
	}
}
