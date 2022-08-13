package racoonman.r3d.resource.codec;

import java.util.function.IntFunction;

import racoonman.r3d.resource.codec.token.IArray;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.PlainArray;
import racoonman.r3d.resource.errors.DecoderException;

public class ArrayCodec {
	public static final ICodec<String[]> STRING = of(PrimitiveCodec.STRING, String[]::new);
	public static final ICodec<byte[]> BYTE = new ICodec<>() {

		@Override
		public IElement encode(byte[] t) {
			return new PlainArray(t);
		}

		@Override
		public byte[] decode(IElement e) {
			return e.asArray().asByteArray();
		}
	};
	public static final ICodec<int[]> INT = new ICodec<>() {

		@Override
		public IElement encode(int[] t) {
			return new PlainArray(t);
		}

		@Override
		public int[] decode(IElement e) {
			return e.asArray().asIntArray();
		}
	};
	public static final ICodec<long[]> LONG = new ICodec<>() {

		@Override
		public IElement encode(long[] t) {
			return new PlainArray(t);
		}

		@Override
		public long[] decode(IElement e) {
			return e.asArray().asLongArray();
		}
	};
	public static final ICodec<float[]> FLOAT = new ICodec<>() {

		@Override
		public IElement encode(float[] t) {
			return new PlainArray(t);
		}

		@Override
		public float[] decode(IElement e) {
			return e.asArray().asFloatArray();
		}
	};
	public static final ICodec<double[]> DOUBLE = new ICodec<>() {

		@Override
		public IElement encode(double[] t) {
			return new PlainArray(t);
		}

		@Override
		public double[] decode(IElement e) {
			return e.asArray().asDoubleArray();
		}
	};
	public static final ICodec<boolean[]> BOOL = new ICodec<>() {

		@Override
		public IElement encode(boolean[] t) {
			return new PlainArray(t);
		}

		@Override
		public boolean[] decode(IElement e) {
			return e.asArray().asBoolArray();
		}
	};
	public static final ICodec<char[]> CHAR = new ICodec<>() {

		@Override
		public IElement encode(char[] t) {
			return new PlainArray(t);
		}

		@Override
		public char[] decode(IElement e) {
			return e.asArray().asCharArray();
		}
	};

	public static <T> ICodec<T[]> of(ICodec<T> elementCodec, IntFunction<T[]> newArray) {
		return new ICodec<>() {

			@Override
			public IArray encode(T[] t) {
				IArray array = new PlainArray();

				for (T value : t) {
					array.append(elementCodec.encode(value));
				}

				return array;
			}

			@Override
			public T[] decode(IElement e) {
				if (e.isArray()) {
					IArray encoded = e.asArray();
					T[] array = newArray.apply(encoded.size());

					for (int i = 0; i < array.length; i++) {
						array[i] = elementCodec.decode(encoded.getAt(i));
					}

					return array;
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}
	
	public static <T> IDecoder<T[]> of(IDecoder<T> elementDecoder, IntFunction<T[]> newArray) {
		return new IDecoder<>() {

			@Override
			public T[] decode(IElement e) {
				if (e.isArray()) {
					IArray encoded = e.asArray();
					T[] array = newArray.apply(encoded.size());

					for (int i = 0; i < array.length; i++) {
						array[i] = elementDecoder.decode(encoded.getAt(i));
					}

					return array;
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}
}
