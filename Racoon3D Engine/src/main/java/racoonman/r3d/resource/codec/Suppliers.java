package racoonman.r3d.resource.codec;

import java.util.function.Supplier;

public interface Suppliers {

	public static <T> Supplier<T> unit(T t) {
		return () -> t;
	}
}
