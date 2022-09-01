package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.types.Status;
import racoonman.r3d.render.core.Driver;

public class HostSync implements IHostSync {
	private IHostSync delegate;
	
	public HostSync(boolean signaled) {
		this.delegate = Driver.createHostSync(signaled);
	}

	@Override
	public long asLong() {
		return this.delegate.asLong();
	}

	@Override
	public void free() {
		this.delegate.free();
	}

	@Override
	public void reset() {
		this.delegate.reset();
	}

	@Override
	public void await(long timeout) {
		this.delegate.await(timeout);
	}

	@Override
	public Status getStatus() {
		return this.delegate.getStatus();
	}
}
