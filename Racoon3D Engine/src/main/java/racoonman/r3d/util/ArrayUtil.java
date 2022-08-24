package racoonman.r3d.util;

import java.util.Arrays;

public class ArrayUtil {

	public static <T> boolean has(T[] ts, T t) {
		for (T to : ts) {
			if (to.equals(t)) {
				return true;
			}
		}

		return false;
	}

	public static boolean has(float[] ts, float t) {
		for (float to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(double[] ts, double t) {
		for (double to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(byte[] ts, byte t) {
		for (byte to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(char[] ts, char t) {
		for (char to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}

	public static boolean has(short[] ts, short t) {
		for (short to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(int[] ts, int t) {
		for (int to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(long[] ts, long t) {
		for (long to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean has(boolean[] ts, boolean t) {
		for (boolean to : ts) {
			if (to == t) {
				return true;
			}
		}

		return false;
	}

	public static <T> T[] add(T[] array, T toAdd) {
		T[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static float[] add(float[] array, float toAdd) {
		float[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static double[] add(double[] array, double toAdd) {
		double[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}	
	
	public static byte[] add(byte[] array, byte toAdd) {
		byte[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static char[] add(char[] array, char toAdd) {
		char[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static short[] add(short[] array, short toAdd) {
		short[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static int[] add(int[] array, int toAdd) {
		int[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static long[] add(long[] array, long toAdd) {
		long[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
	
	public static boolean[] add(boolean[] array, boolean toAdd) {
		boolean[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = toAdd;
		return newArray;
	}
}
