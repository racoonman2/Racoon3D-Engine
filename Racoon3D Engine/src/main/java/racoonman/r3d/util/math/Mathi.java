package racoonman.r3d.util.math;

public class Mathi {
	
	public static int clamp(int min, int max, int value) {
		return Math.max(min, Math.min(max, value));
	}
}
