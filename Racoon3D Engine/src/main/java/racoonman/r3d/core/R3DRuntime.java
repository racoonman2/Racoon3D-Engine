package racoonman.r3d.core;

import java.util.Optional;
import java.util.function.Supplier;

import racoonman.r3d.core.launch.IExecutable;

public class R3DRuntime {
	private static Optional<Client> running = Optional.empty();
	
	public static void launch(Client client, Supplier<IExecutable> launch) throws Exception {
		if(running.isEmpty()) {
			running = Optional.of(client);
			
			try(IExecutable exe = launch.get()) {
				exe.run();
			}
		} else {
			throw new IllegalStateException("Client is already running");
		}
	}
	
	public static Client getClient() {
		return running.orElseThrow(() -> new IllegalStateException("No client has been launched"));
	}
	
	public static String getStringOr(String key, String defaultVal) {
		return System.getProperty(key, defaultVal);
	}

	public static char getCharOr(String key, char defaultVal) {
		if(hasProperty(key)) {
			return System.getProperty(key).charAt(0);
		} else {
			return defaultVal;
		}
	}

	public static byte getByteOr(String key, byte defaultVal) {
		if(hasProperty(key)) {
			return Byte.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}

	public static short getShortOr(String key, short defaultVal) {
		if(hasProperty(key)) {
			return Short.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}

	public static int getIntOr(String key, int defaultVal) {
		if(hasProperty(key)) {
			return Integer.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}

	public static long getLongOr(String key, long defaultVal) {
		if(hasProperty(key)) {
			return Long.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}
	
	public static float getFloatOr(String key, float defaultVal) {
		if(hasProperty(key)) {
			return Float.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}

	public static double getDoubleOr(String key, double defaultVal) {
		if(hasProperty(key)) {
			return Double.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}

	public static boolean getBoolOr(String key, boolean defaultVal) {
		if(hasProperty(key)) {
			return Boolean.valueOf(System.getProperty(key));
		} else {
			return defaultVal;
		}
	}
	
	public static boolean hasProperty(String property) {
		return System.getProperties().containsKey(property);
	}
	
	public static void critical(String cause) {
		throw new RuntimeException("Critical error occurred, " + cause);
	}
	
	//TODO
	public static void exit(int code) {
		System.exit(code);
	}
}
