package racoonman.r3d.render.api.types;

import java.util.Collection;
import java.util.List;

public interface IGlType {
	int getGlType();

	public static int nil() {
		return 0;
	}

	public static <T extends IGlType> T byInt(int type, T[] values) {
		for(T t : values) {
			if(t.getGlType() == type) {
				return t;
			}
		}
		
		return null;
	}

	public static int[] asInts(List<? extends IGlType> types) {
		int[] ints = new int[types.size()];

		for (int i = 0; i < ints.length; i++) {
			ints[i] = types.get(i).getGlType();
		}		
		
		return ints;
	}
	
	public static int[] asInts(IGlType... types) {
		int[] ints = new int[types.length];

		for (int i = 0; i < ints.length; i++) {
			ints[i] = types[i].getGlType();
		}

		return ints;
	}
	
	public static int bitMask(IGlType... types) {
		int mask = 0;

		for (IGlType type : types) {
			mask |= type.getGlType();
		}

		return mask;
	}
	
	public static int bitMask(Collection<? extends IGlType> types) {
		int mask = 0;

		for (IGlType type : types) {
			mask |= type.getGlType();
		}

		return mask;
	}

	public static <T extends IGlType> T fromBitMask(int bitMask, T[] values, T fallback) {
		for (T t : values) {
			int type = t.getGlType();

			if ((bitMask & type) == type) {
				return t;
			}
		}

		return fallback;
	}

	public static <T extends IGlType> T fromBitMask(int bitMask, T[] values) {
		return fromBitMask(bitMask, values, null);
	}
}
