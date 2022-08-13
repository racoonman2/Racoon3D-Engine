package racoonman.r3d.resource.format.json;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;

import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.IObject;

class GsonObject implements IObject {
	private JsonObject object;

	GsonObject() {
		this(new JsonObject());
	}

	GsonObject(JsonObject object) {
		this.object = object;
	}

	@Override
	public void set(String access, IElement a) {
		this.object.add(access, JsonEncodingFormat.toGson(a));
	}

	@Override
	public void set(String access, Number n) {
		this.object.add(access, new JsonPrimitive(n));
	}

	@Override
	public void set(String access, char c) {
		this.object.add(access, new JsonPrimitive(c));
	}

	@Override
	public void set(String access, String s) {
		this.object.add(access, new JsonPrimitive(s));
	}

	@Override
	public void set(String access, boolean b) {
		this.object.add(access, new JsonPrimitive(b));
	}
	
	@Override
	public IElement getAt(String access) {
		return JsonEncodingFormat.fromGson(this.object.get(access));
	}

	@Override
	public boolean has(String access) {
		return this.object.has(access);
	}

	@Override
	public int size() {
		return this.object.size();
	}

	@Override
	public Map<String, IElement> elements() {
		Map<String, IElement> elements = new LinkedTreeMap<>();

		for (Entry<String, JsonElement> entry : this.object.entrySet()) {
			elements.put(entry.getKey(), JsonEncodingFormat.fromGson(entry.getValue()));
		}

		return elements;
	}
}
