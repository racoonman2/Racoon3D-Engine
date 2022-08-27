package racoonman.r3d.render.api.types;

import java.util.Collection;
import java.util.List;

public interface IVkType {
	int getVkType();

	public static int nil() {
		return 0;
	}

	public static <T extends IVkType> T byInt(int type, T[] values) {
		for(T t : values) {
			if(t.getVkType() == type) {
				return t;
			}
		}
		
		return null;
	}

	public static int[] asInts(List<? extends IVkType> types) {
		int[] ints = new int[types.size()];

		for (int i = 0; i < ints.length; i++) {
			ints[i] = types.get(i).getVkType();
		}		
		
		return ints;
	}
	
	public static int[] asInts(IVkType... types) {
		int[] ints = new int[types.length];

		for (int i = 0; i < ints.length; i++) {
			ints[i] = types[i].getVkType();
		}

		return ints;
	}
	
	public static int bitMask(IVkType... types) {
		int mask = 0;

		for (IVkType type : types) {
			mask |= type.getVkType();
		}

		return mask;
	}
	
	public static int bitMask(Collection<? extends IVkType> types) {
		int mask = 0;

		for (IVkType type : types) {
			mask |= type.getVkType();
		}

		return mask;
	}

	public static <T extends IVkType> T fromBitMask(int bitMask, T[] values, T fallback) {
		for (T t : values) {
			int type = t.getVkType();

			if ((bitMask & type) == type) {
				return t;
			}
		}

		return fallback;
	}

	public static <T extends IVkType> T fromBitMask(int bitMask, T[] values) {
		return fromBitMask(bitMask, values, null);
	}
}
