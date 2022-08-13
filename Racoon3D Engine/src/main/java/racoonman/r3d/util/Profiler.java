package racoonman.r3d.util;

import java.util.function.Supplier;

public class Profiler {

	public static <T> T profile(Supplier<T> s) {
		long start = System.currentTimeMillis();
		T r = s.get();
		System.out.println(System.currentTimeMillis() - start);
		return r;
	}
	

	public static void profile(Runnable r) {
		long start = System.currentTimeMillis();
		r.run();
		System.out.println(System.currentTimeMillis() - start);		
	}
}
