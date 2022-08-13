package racoonman.r3d.window;

import java.util.Collection;

public interface IGLFWType {
	int asInt();
	
	public static int none() {
		return 0;
	}

	public static <T extends IGLFWType> T byInt(int type, T[] values) {
		for(T t : values) {
			if(t.asInt() == type) {
				return t;
			}
		}
		
		return null;
	}
	
	public static int[] asInts(IGLFWType... types) {
		int[] ints = new int[types.length];

		for (int i = 0; i < ints.length; i++) {
			ints[i] = types[i].asInt();
		}

		return ints;
	}
	
	public static int bitMask(IGLFWType... types) {
		int mask = 0;

		for (IGLFWType type : types) {
			mask |= type.asInt();
		}

		return mask;
	}
	
	public static int bitMask(Collection<? extends IGLFWType> types) {
		int mask = 0;

		for (IGLFWType type : types) {
			mask |= type.asInt();
		}

		return mask;
	}

	public static <T extends IGLFWType> T fromBitMask(int bitMask, T[] values, T fallback) {
		for (T t : values) {
			int type = t.asInt();

			if ((bitMask & type) == type) {
				return t;
			}
		}

		return fallback;
	}

	public static <T extends IGLFWType> T fromBitMask(int bitMask, T[] values) {
		return fromBitMask(bitMask, values, null);
	}
}