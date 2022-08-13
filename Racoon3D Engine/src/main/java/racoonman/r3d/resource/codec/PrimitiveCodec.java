package racoonman.r3d.resource.codec;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.PlainElement;
import racoonman.r3d.resource.errors.DecoderException;

public abstract class PrimitiveCodec<T> implements ICodec<T> {
	public static final ICodec<String> STRING = new PrimitiveCodec<>(IElement::asString) {

		@Override
		public <P> IField<String, P> fetch(String name, IFunction1<String, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public String decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().stringAt(name);
					} else if(e.isString()) {
						return e.asString();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(String value) {
					return new PlainElement(value);
				}
			};
		}
	};

	public static final ICodec<Byte> BYTE = new PrimitiveCodec<>(IElement::asByte) {

		@Override
		public <P> IField<Byte, P> fetch(String name, IFunction1<Byte, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Byte decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().byteAt(name);
					} else if(e.isByte()) {
						return e.asByte();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Byte value) {
					return new PlainElement(value);
				}
			};
		}
	};
	
	public static final ICodec<Integer> INT = new PrimitiveCodec<>(IElement::asInt) {

		@Override
		public <P> IField<Integer, P> fetch(String name, IFunction1<Integer, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Integer decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().intAt(name);
					} else if(e.isInt()) {
						return e.asInt();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Integer value) {
					return new PlainElement(value);
				}
			};
		}
	};
	
	public static final ICodec<Long> LONG = new PrimitiveCodec<>(IElement::asLong) {

		@Override
		public <P> IField<Long, P> fetch(String name, IFunction1<Long, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Long decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().longAt(name);
					} else if(e.isLong()) {
						return e.asLong();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Long value) {
					return new PlainElement(value);
				}
			};
		}
	};
	
	public static final ICodec<Float> FLOAT = new PrimitiveCodec<>(IElement::asFloat) {

		@Override
		public <P> IField<Float, P> fetch(String name, IFunction1<Float, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Float decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().floatAt(name);
					} else if(e.isFloat()) {
						return e.asFloat();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Float value) {
					return new PlainElement(value);
				}
			};
		}
	};

	public static final ICodec<Double> DOUBLE = new PrimitiveCodec<>(IElement::asDouble) {

		@Override
		public <P> IField<Double, P> fetch(String name, IFunction1<Double, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Double decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().doubleAt(name);
					} else if(e.isString()) {
						return e.asDouble();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Double value) {
					return new PlainElement(value);
				}
			};
		}
	};
	
	public static final ICodec<Boolean> BOOL = new PrimitiveCodec<>(IElement::asBool) {

		@Override
		public <P> IField<Boolean, P> fetch(String name, IFunction1<Boolean, P> fetch) {
			return new SimpleField<>(name, fetch) {

				@Override
				public Boolean decode(IElement e) {
					if(e.isObject()) {
						return e.asObject().boolAt(name);
					} else if(e.isString()) {
						return e.asBool();
					} else {
						throw DecoderException.invalidType();
					}
				}

				@Override
				IElement encodeValue(Boolean value) {
					return new PlainElement(value);
				}
			};
		}
	};
	
	private IFunction1<T, IElement> decode;
	
	PrimitiveCodec(IFunction1<T, IElement> decode) {
		this.decode = decode;
	}
	
	@Override
	public IElement encode(T t) {
		return new PlainElement(t);
	}

	@Override
	public T decode(IElement o) {
		return this.decode.apply(o);
	}
}
