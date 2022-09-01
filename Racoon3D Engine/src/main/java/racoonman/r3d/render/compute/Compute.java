package racoonman.r3d.render.compute;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.sync.LocalSync;

public class Compute {
	private Context ctx;
	
	public Compute(Context ctx) {
		this.ctx = ctx;
	}
	
	public void join(LocalSync sync) {
		this.ctx.await(sync);
	}
}
