package racoonman.r3d.render.shader;

public interface IShadercType {
	int getShadercType();

	public static <T extends IShadercType> T byInt(int type, T[] values) {
		for(T t : values) {
			if(t.getShadercType() == type) {
				return t;
			}
		}
		
		throw new IllegalArgumentException("Unknown type [" + type + "]");
	}
}
