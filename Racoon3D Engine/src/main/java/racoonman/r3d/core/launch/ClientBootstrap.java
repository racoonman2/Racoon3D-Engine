package racoonman.r3d.core.launch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.function.Supplier;

import racoonman.r3d.core.Client;
import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.core.libraries.Libraries;

public class ClientBootstrap {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		String launchClass = System.getProperty("r3d.launchClass");
		
		if(launchClass == null) {
			throw new IllegalStateException("Missing launch class");
		}
		
		Class<?> cls = Class.forName(launchClass);

		if(cls.isAnnotationPresent(Client.class)) {
			if(IExecutable.class.isAssignableFrom(cls)) {
				Supplier<IExecutable> launcher = null;
				
				for(Constructor<?> c : cls.getConstructors()) {
					Parameter[] parameters = c.getParameters();
					
					if(parameters.length == 1) {
						Parameter first = parameters[0];
						if(first.getType() == String[].class) {
							launcher = () -> newInstance((Constructor<IExecutable>) c, (Object[]) args);
							break;
						}
					}
				}
				
				if(launcher == null) {
					launcher = () -> {
						try {
							return (IExecutable) newInstance(cls.getConstructor());
						} catch (NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
							return null;
						}
					};
				}
				
				R3DRuntime.launch(cls.getAnnotation(Client.class), launcher);
				return;	
			}
		} else {
			throw new IllegalStateException("Launch class is missing @Client annotation");
		}
	}
	
	private static <T> T newInstance(Constructor<T> ctor, Object... args) {
		try {
			return ctor.newInstance((Object[]) args);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static {
		Libraries.init();
	}
}
