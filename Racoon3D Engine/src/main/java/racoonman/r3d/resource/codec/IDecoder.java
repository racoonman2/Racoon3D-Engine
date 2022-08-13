package racoonman.r3d.resource.codec;

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
import racoonman.r3d.resource.errors.DecoderException;

public interface IDecoder<T> {
	T decode(IElement o);
	
	default <P> IField<T, P> fetch(String name) {
		return new IField<>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public IElement encode(P parent) {
				throw new UnsupportedOperationException();
			}

			@Override
			public T decode(IElement e) {
				if (e.isObject()) {
					return IDecoder.this.decode(e.asObject().getAt(this.name()));
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}
	
	default <P> IField<T, P> fetchOr(String name, Supplier<T> fallback) {
		return new IField<>() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public IElement encode(P parent) {
				throw new UnsupportedOperationException();
			}

			@Override
			public T decode(IElement e) {
				if (e.isObject()) {
					IObject obj = e.asObject();

					if (obj.has(name)) {
						return IDecoder.this.decode(e.asObject().getAt(name));
					} else {
						return fallback.get();
					}
				} else {
					throw DecoderException.invalidType();
				}
			}
		};
	}
	
	default <R> IDecoder<R> map(IFunction1<R, T> deserialize) {
		return (e) -> deserialize.apply(IDecoder.this.decode(e));
	};
	
	static <T> IDecoder<T> of(IFunction<T> build) {
		return (e) -> build.apply();
	}

	static <T, P1> IDecoder<T> of(IField<P1, T> field1, IFunction1<T, P1> build) {
		return (e) -> build.apply(field1.decode(e));
	}

	static <T, P1, P2> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IFunction2<T, P1, P2> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e));
	}

	static <T, P1, P2, P3> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IFunction3<T, P1, P2, P3> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e));
	}

	static <T, P1, P2, P3, P4> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IFunction4<T, P1, P2, P3, P4> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e));
	}

	static <T, P1, P2, P3, P4, P5> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IFunction5<T, P1, P2, P3, P4, P5> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IFunction6<T, P1, P2, P3, P4, P5, P6> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IFunction7<T, P1, P2, P3, P4, P5, P6, P7> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IFunction8<T, P1, P2, P3, P4, P5, P6, P7, P8> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IFunction9<T, P1, P2, P3, P4, P5, P6, P7, P8, P9> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IFunction10<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IFunction11<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IFunction12<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IFunction13<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IField<P14, T> field14, IFunction14<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e), field14.decode(e));
	}

	static <T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> IDecoder<T> of(IField<P1, T> field1, IField<P2, T> field2, IField<P3, T> field3, IField<P4, T> field4, IField<P5, T> field5, IField<P6, T> field6, IField<P7, T> field7, IField<P8, T> field8, IField<P9, T> field9, IField<P10, T> field10, IField<P11, T> field11, IField<P12, T> field12, IField<P13, T> field13, IField<P14, T> field14, IField<P15, T> field15, IFunction15<T, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> build) {
		return (e) -> build.apply(field1.decode(e), field2.decode(e), field3.decode(e), field4.decode(e), field5.decode(e), field6.decode(e), field7.decode(e), field8.decode(e), field9.decode(e), field10.decode(e), field11.decode(e), field12.decode(e), field13.decode(e), field14.decode(e), field15.decode(e));
	}
}
