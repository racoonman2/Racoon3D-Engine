package racoonman.r3d.resource.codec.token;

public interface IElement {
	IArray asArray();
	
	IObject asObject();
	
	Number asNumber();
	
	char asChar();
	
	String asString();
	
	boolean asBool();
	
	default byte asByte() {
		return this.asNumber().byteValue();
	}
	
	default short asShort() {
		return this.asNumber().shortValue();
	}
	
	default int asInt() {
		return this.asNumber().intValue();
	}
	
	default long asLong() {
		return this.asNumber().longValue();
	}
	
	default float asFloat() {
		return this.asNumber().floatValue();
	}
	
	default double asDouble() {
		return this.asNumber().doubleValue();
	}
	
	default <T extends Enum<T>> T asEnum(T[] values) {
		return values[this.asInt()];
	}
	
	boolean isNumber();
	
	boolean isByte();
	
	boolean isShort();
	
	boolean isInt();
	
	boolean isLong();
	
	boolean isFloat();
	
	boolean isDouble();
	
	boolean isChar();
	
	boolean isString();
	
	boolean isBool();
	
	boolean isArray();
	
	boolean isObject();
	
	default boolean isEnum() {
		return this.isInt();
	}
}
