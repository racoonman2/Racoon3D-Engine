package racoonman.r3d.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Client {
	String id();
	
	String[] roots();
}
