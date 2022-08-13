package racoonman.r3d.resource.codec;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.token.IElement;

public interface ISerializable {
	String getName();
	
	public static <T extends ISerializable> ICodec<T> byName(IFunction1<T, String> lookup) {
		return new ICodec<>() {

			@Override
			public IElement encode(T t) {
				return PrimitiveCodec.STRING.encode(t.getName());
			}

			@Override
			public T decode(IElement e) {
				return lookup.apply(PrimitiveCodec.STRING.decode(e));
			}
		};
	}
}
