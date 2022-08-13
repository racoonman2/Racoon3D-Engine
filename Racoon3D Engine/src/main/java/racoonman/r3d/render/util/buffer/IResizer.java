package racoonman.r3d.render.util.buffer;

public interface IResizer {
	int resize(int oldSize, int newSize);

	public static IResizer half() {
		return fraction(2);
	}

	public static IResizer third() {
		return fraction(3);
	}

	public static IResizer fourth() {
		return fraction(4);
	}

	public static IResizer fraction(int divisor) {
		return (oldSize, newSize) -> newSize + (oldSize / divisor);
	}
}