package racoonman.r3d.resource.codec;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.token.IElement;

public class EnumCodec {

	public static <T extends Enum<T>> ICodec<T> byName(IFunction1<T, String> lookup) {
		return new ICodec<>() {

			@Override
			public IElement encode(T t) {
				return PrimitiveCodec.STRING.encode(t.name());
			}

			@Override
			public T decode(IElement e) {
				return lookup.apply(PrimitiveCodec.STRING.decode(e).toUpperCase());
			}
		};
	}
	
	public static <T extends Enum<T>> ICodec<T> byOrdinal(T[] values) {
		return new ICodec<>() {

			@Override
			public IElement encode(T t) {
				return PrimitiveCodec.INT.encode(t.ordinal());
			}

			@Override
			public T decode(IElement e) {
				return values[PrimitiveCodec.INT.decode(e)];
			}
		};
	}
}
