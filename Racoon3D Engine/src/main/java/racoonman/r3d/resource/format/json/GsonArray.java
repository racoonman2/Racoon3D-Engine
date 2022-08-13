package racoonman.r3d.resource.format.json;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import racoonman.r3d.resource.codec.token.IArray;
import racoonman.r3d.resource.codec.token.IElement;

class GsonArray implements IArray {
	private JsonArray array;

	GsonArray() {
		this(new JsonArray());
	}

	GsonArray(JsonArray array) {
		this.array = array;
	}

	@Override
	public void set(Integer access, IElement e) {
		this.array.set(access, JsonEncodingFormat.toGson(e));
	}

	@Override
	public void set(Integer access, Number n) {
		this.array.set(access, new JsonPrimitive(n));
	}

	@Override
	public void set(Integer access, char c) {
		this.array.set(access, new JsonPrimitive(c));
	}

	@Override
	public void set(Integer access, String s) {
		this.array.set(access, new JsonPrimitive(s));
	}

	@Override
	public void set(Integer access, boolean b) {
		this.array.set(access, new JsonPrimitive(b));
	}

	@Override
	public void append(IElement e) {
		this.array.add(JsonEncodingFormat.toGson(e));
	}

	@Override
	public void append(Number n) {
		this.array.add(n);
	}

	@Override
	public void append(String s) {
		this.array.add(s);
	}

	@Override
	public void append(boolean b) {
		this.array.add(b);
	}
	
	@Override
	public void append(char c) {
		this.array.add(c);
	}

	@Override
	public IElement getAt(Integer access) {
		return JsonEncodingFormat.fromGson(this.array.get(access));
	}

	@Override
	public boolean has(Integer access) {
		return access >= 0 && access < this.size() - 1 && this.array.get(access) != null;
	}

	@Override
	public int size() {
		return this.array.size();
	}

	@Override
	public Map<Integer, IElement> elements() {
		Map<Integer, IElement> elements = new TreeMap<>();

		for (int i = 0; i < this.size(); i++) {
			elements.put(i, JsonEncodingFormat.fromGson(this.array.get(i)));
		}

		return elements;
	}
}
