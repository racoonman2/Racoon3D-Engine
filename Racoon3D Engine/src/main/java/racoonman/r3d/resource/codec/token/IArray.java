package racoonman.r3d.resource.codec.token;

import java.util.function.IntFunction;

public interface IArray extends IContainer<Integer> {
	void append(IElement e);
	
	void append(Number n);

	void append(String s);
	
	void append(boolean b);
	
	void append(char c);
	
	default <T extends Enum<T>> void append(T t) {
		this.append(t.ordinal());
	}
	
	@Override
	default IArray asArray() {
		return this;
	}

	@Override
	default IObject asObject() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	default boolean isArray() {
		return true;
	}
	
	@Override
	default boolean isObject() {
		return false;
	}
	
	default IArray[] asArrayArray() {
		IArray[] arrays = new IArray[this.size()];
		
		for(int i = 0; i < arrays.length; i++) {
			arrays[i] = this.arrayAt(i);
		}
		
		return arrays;
	}

	default IObject[] asObjectArray() {
		IObject[] objects = new IObject[this.size()];
		
		for(int i = 0; i < objects.length; i++) {
			objects[i] = this.objectAt(i);
		}
		
		return objects;
	}
	
	default byte[] asByteArray() {
		byte[] bytes = new byte[this.size()];
		
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = this.byteAt(i);
		}
		
		return bytes;
	}
	
	default short[] asShortArray() {
		short[] shorts = new short[this.size()];
		
		for(int i = 0; i < shorts.length; i++) {
			shorts[i] = this.shortAt(i);
		}
		
		return shorts;
	}
	
	default int[] asIntArray() {
		int[] ints = new int[this.size()];
		
		for(int i = 0; i < ints.length; i++) {
			ints[i] = this.intAt(i);
		}
		
		return ints;
	}
	
	default long[] asLongArray() {
		long[] longs = new long[this.size()];
		
		for(int i = 0; i < longs.length; i++) {
			longs[i] = this.longAt(i);
		}
		
		return longs;
	}
	
	default float[] asFloatArray() {
		float[] floats = new float[this.size()];
		
		for(int i = 0; i < floats.length; i++) {
			floats[i] = this.floatAt(i);
		}
		
		return floats;
	}
	
	default double[] asDoubleArray() {
		double[] doubles = new double[this.size()];
		
		for(int i = 0; i < doubles.length; i++) {
			doubles[i] = this.doubleAt(i);
		}
		
		return doubles;
	}
	
	default char[] asCharArray() {
		char[] chars = new char[this.size()];
		
		for(int i = 0; i < chars.length; i++) {
			chars[i] = this.charAt(i);
		}
		
		return chars;
	}
	
	default String[] asStringArray() {
		String[] strings = new String[this.size()];
		
		for(int i = 0; i < strings.length; i++) {
			strings[i] = this.stringAt(i);
		}
		
		return strings;
	}
	
	default boolean[] asBoolArray() {
		boolean[] bools = new boolean[this.size()];
		
		for(int i = 0; i < bools.length; i++) {
			bools[i] = this.boolAt(i);
		}
		
		return bools;
	}
	
	default <T extends Enum<T>> T[] asEnumArray(T[] values, IntFunction<T[]> factory) {
		T[] enums = factory.apply(this.size());
		
		for(int i = 0; i < enums.length; i++) {
			enums[i] = this.enumAt(i, values);
		}
		
		return enums;
	}
}
