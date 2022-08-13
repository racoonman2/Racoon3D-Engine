package racoonman.r3d.resource.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.IFunction.IFunction10;
import racoonman.r3d.resource.codec.IFunction.IFunction11;
import racoonman.r3d.resource.codec.IFunction.IFunction12;
import racoonman.r3d.resource.codec.IFunction.IFunction13;
import racoonman.r3d.resource.codec.IFunction.IFunction14;
import racoonman.r3d.resource.codec.IFunction.IFunction15;
import racoonman.r3d.resource.codec.IFunction.IFunction2;
import racoonman.r3d.resource.codec.IFunction.IFunction3;
import racoonman.r3d.resource.codec.IFunction.IFunction4;
import racoonman.r3d.resource.codec.IFunction.IFunction5;
import racoonman.r3d.resource.codec.IFunction.IFunction6;
import racoonman.r3d.resource.codec.IFunction.IFunction7;
import racoonman.r3d.resource.codec.IFunction.IFunction8;
import racoonman.r3d.resource.codec.IFunction.IFunction9;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.IObject;
import racoonman.r3d.resource.codec.token.PlainElement;
import racoonman.r3d.resource.errors.DecoderException;

public interface ICodec<T> extends IDecoder<T> {
	IElement encode(T t);
	
	default <R> ICodec<R> map(IFunction1<R, T> deserialize, IFunction1<T, R> serialize) {
		return new ICodec<>() {

			@Override
			public IElement encode(R t) {
				return ICodec.this.encode(serialize.apply(t));
			}

			@Override
			public R decode(IElement o) {
				return deserialize.apply(ICodec.this.decode(o));
			}
		};
	};

	default <P> IField<T, P> fetch(String name, IFunction1<T, P> fetch) {
		return new IField<>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public IElement encode(P parent) {
				return ICodec.this.encode(fetch.apply(parent));
			}

			@Override
			public T decode(IElement e) {
				if (e.isObject()) {
					return ICodec.this.decode(e.asObject().getAt(this.name()));
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}

	default <P> IField<T, P> fetchOr(String name, IFunction1<T, P> fetch, Supplier<T> fallback) {
		return new IField<>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public IElement encode(P parent) {
				return ICodec.this.encode(fetch.apply(parent));
			}

			@Override
			public T decode(IElement e) {
				if (e.isObject()) {
					IObject obj = e.asObject();

					if (obj.has(name)) {
						return ICodec.this.decode(e.asObject().getAt(name));
					} else {
						return fallback.get();
					}
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}
	
	default <P> IField<Optional<T>, P> fetchMaybe(String name, IFunction1<Optional<T>, P> fetch) {
		return new IField<>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public IElement encode(P parent) {
				Optional<T> value = fetch.apply(parent);

				if (value.isPresent()) {
					return ICodec.this.encode(value.get());
				} else {
					return PlainElement.EMPTY;
				}
			}

			@Override
			public Optional<T> decode(IElement e) {
				if (e.isObject()) {
					IObject obj = e.asObject();

					if (obj.has(name)) {
						return Optional.ofNullable(ICodec.this.decode(e.asObject().getAt(name)));
					} else {
						return Optional.empty();
					}
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}

	static <T> ICodec<List<T>> list(ICodec<T> elementCodec, IntFunction<T[]> newArray) {
		return ArrayCodec.of(elementCodec, newArray).map((array) -> {
			List<T> list = new ArrayList<>();
			Collections.addAll(list, array);
			return list;
		}, (list) -> {
			return list.toArray(newArray);
		});
	}

	static <T> ICodec<T> simple(IFunction<T> build) {
		return new SimpleCodec<>() {

			@Override
			public T decode(IElement e) {
				return build.apply();
			}
		};
	}

	static <T, P1> ICodec<T> simple(IField<P1, T> field1, IFunction1<T, P1> build) {
		return new SimpleCodec<>(field1) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e));
			}
		};
	}

	static <T, P1, P2> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IFunction2<T, P1, P2> build) {
		return new SimpleCodec<>(field1, field2) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e));
			}
		};
	}

	static <T, P1, P2, P3> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IFunction3<T, P1, P2, P3> build) {
		return new SimpleCodec<>(field1, field2, field3) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IFunction4<T, P1, P2, P3, P4> build) {
		return new SimpleCodec<>(field1, field2, field3, field4) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IFunction5<T, P1, P2, P3, P4, P5> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IFunction6<T, P1, P2, P3, P4, P5, P6> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IFunction7<T, P1, P2, P3, P4, P5, P6, P7> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IFunction8<T, P1, P2, P3, P4, P5, P6, P7, P8> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IFunction9<T, P1, P2, P3, P4, P5, P6, P7, P8, P9> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IFunction10<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IFunction11<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IFunction12<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IFunction13<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IField<P14, T> field14, IFunction14<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e), field14.decode(e));
			}
		};
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> ICodec<T> simple(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IField<P14, T> field14, IField<P15, T> field15, IFunction15<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> build) {
		return new SimpleCodec<>(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15) {

			@Override
			public T decode(IElement e) {
				return build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e), field14.decode(e), field15.decode(e));
			}
		};
	}
}
