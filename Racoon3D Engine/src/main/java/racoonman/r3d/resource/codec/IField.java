package racoonman.r3d.resource.codec;

import java.util.function.Supplier;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.PlainElement;

public interface IField<V, P> {
	String name();
	
	V decode(IElement e);

	IElement encode(P parent);
	
	default <R> IField<R, P> map(IFunction1<R, V> mapper) {
		return new IField<>() {

			@Override
			public String name() {
				return IField.this.name();
			}

			@Override
			public R decode(IElement e) {
				return mapper.apply(IField.this.decode(e));
			}

			@Override
			public IElement encode(P parent) {
				return IField.this.encode(parent);
			}
		};
	};
	
	public static <P, V> IField<V, P> refer(String name, Supplier<V> value) {
		return new SimpleField<>(name, (p) -> value.get()) {

			@Override
			public V decode(IElement e) {
				return value.get();
			}

			IElement encodeValue(V value) {
				return PlainElement.EMPTY;
			}
		};
	}
}
