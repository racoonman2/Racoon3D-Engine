package racoonman.r3d.util;

public class Bytes {

	public static int b(int amount) {
		return amount;
	}
	
	public static int kb(int amount) {
		return amount * 1000;
	}
	
	public static int mb(int amount) {
		return amount * 1000000;
	}
	
	public static int gb(int amount) {
		return (int) (amount * 1e-9);
	}
}
