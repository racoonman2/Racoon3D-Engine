package racoonman.r3d.render.api.objects.wrapper;

import racoonman.r3d.render.api.objects.IContextSync;
import racoonman.r3d.render.core.Driver;

public class ContextSync implements IContextSync {
	private IContextSync delegate;
	
	public ContextSync() {
		this.delegate = Driver.createContextSync();
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
