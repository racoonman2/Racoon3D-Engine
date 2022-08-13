package racoonman.r3d.resource.codec.token;

//TODO split into individual types, ie StringElement, IntElement, LongElement...
public class PlainElement implements IElement {
	public static final PlainElement EMPTY = new PlainElement(null);
	
	public Object value;
	
	public PlainElement(Object value) {
		this.value = value;
	}

	@Override
	public IArray asArray() {
		return (IArray) this.value;
	}

	@Override
	public IObject asObject() {
		return (IObject) this.value;
	}

	@Override
	public Number asNumber() {
		return (Number) this.value;
	}

	@Override
	public char asChar() {
		return (char) this.value;
	}

	@Override
	public String asString() {
		return (String) this.value;
	}

	@Override
	public boolean asBool() {
		return (Boolean) this.value;
	}

	@Override
	public boolean isNumber() {
		return this.value instanceof Number;
	}

	@Override
	public boolean isByte() {
		return this.value instanceof Byte;
	}

	@Override
	public boolean isShort() {
		return this.value instanceof Short;
	}

	@Override
	public boolean isInt() {
		return this.value instanceof Integer;
	}

	@Override
	public boolean isLong() {
		return this.value instanceof Long;
	}

	@Override
	public boolean isFloat() {
		return this.value instanceof Float;
	}

	@Override
	public boolean isDouble() {
		return this.value instanceof Double;
	}

	@Override
	public boolean isChar() {
		return this.value instanceof Character;
	}

	@Override
	public boolean isString() {
		return this.value instanceof String;
	}

	@Override
	public boolean isBool() {
		return this.value instanceof Boolean;
	}

	@Override
	public boolean isArray() {
		return this.value instanceof IArray;
	}

	@Override
	public boolean isObject() {
		return this.value instanceof IObject;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
}
