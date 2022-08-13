package racoonman.r3d.resource.codec;

import racoonman.r3d.resource.codec.token.IElement;

public interface IEncoder<T> {
	IElement encode(T t);
}
