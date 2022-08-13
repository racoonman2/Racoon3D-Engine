package racoonman.r3d.resource.format.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import racoonman.r3d.resource.codec.token.IArray;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.IObject;

class GsonElement implements IElement {
	private JsonElement element;
	
	GsonElement(JsonElement element) {
		this.element = element;
	}
	
	@Override
	public IArray asArray() {
		if(this.element.isJsonArray()) {
			return (IArray) JsonEncodingFormat.fromGson(this.element);
		}

		throw new UnsupportedOperationException("Element is not an array");
	}

	@Override
	public IObject asObject() {
		if(this.element.isJsonObject()) {
			return (IObject) JsonEncodingFormat.fromGson(this.element);
		}

		throw new UnsupportedOperationException("Element is not an object");
	}

	@Override
	public Number asNumber() {
		return this.element.getAsNumber();
	}

	@Override
	public char asChar() {
		return this.element.getAsString().charAt(0);
	}

	@Override
	public String asString() {
		return this.element.getAsString();
	}

	@Override
	public boolean asBool() {
		return this.element.getAsBoolean();
	}
	
	@Override
	public boolean isNumber() {
		return this.isPrimitive() && this.element.getAsJsonPrimitive().isNumber();
	}

	@Override
	public boolean isByte() {
		return this.isOfNumberType(Byte.class);
	}

	@Override
	public boolean isShort() {
		return this.isOfNumberType(Short.class);
	}

	@Override
	public boolean isInt() {
		return this.isOfNumberType(Integer.class);
	}

	@Override
	public boolean isLong() {
		return this.isOfNumberType(Long.class);
	}

	@Override
	public boolean isFloat() {
		return this.isOfNumberType(Float.class);
	}

	@Override
	public boolean isDouble() {
		return this.isOfNumberType(Double.class);
	}

	@Override
	public boolean isChar() {
		return this.isString();
	}

	@Override
	public boolean isString() {
		return this.isPrimitive() && this.element.getAsJsonPrimitive().isString();
	}

	@Override
	public boolean isBool() {
		return this.isPrimitive() && this.element.getAsJsonPrimitive().isBoolean();
	}

	@Override
	public boolean isArray() {
		return this.element instanceof JsonArray;
	}

	@Override
	public boolean isObject() {
		return this.element instanceof JsonObject;
	}
	
	private boolean isPrimitive() {
		return this.element instanceof JsonPrimitive;
	}
	
	private boolean isOfNumberType(Class<? extends Number> number) {
		return this.isNumber() && this.element.getAsNumber().getClass().isAssignableFrom(number);
	}
}
