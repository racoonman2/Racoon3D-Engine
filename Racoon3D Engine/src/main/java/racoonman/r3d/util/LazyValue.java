package racoonman.r3d.util;

import java.util.function.Supplier;

public class LazyValue<T> {
	private T val;
	private Supplier<T> init;
	
	public LazyValue(Supplier<T> init) {
		this.init = init;
	}
	
	public T get() {
		if(this.val != null) {
			return this.val;
		} else {
			return this.init();
		}
	}
	
	private synchronized T init() {
		return this.val = this.init.get();
	}
}
