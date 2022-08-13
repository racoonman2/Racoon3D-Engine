package racoonman.r3d.resource.format.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.PlainElement;
import racoonman.r3d.resource.format.IEncodingFormat;

public class JsonEncodingFormat implements IEncodingFormat {
	private Gson gson;
	
	public JsonEncodingFormat() {
		this(new GsonBuilder().setPrettyPrinting().create());
	}
	
	public JsonEncodingFormat(Gson gson) {
		this.gson = gson;
	}

	@Override
	public IElement read(InputStream input) throws Exception {
		try(InputStreamReader baseReader = new InputStreamReader(input)) {
			return fromGson(this.gson.fromJson(baseReader, JsonElement.class));
		}
	}

	@Override
	public void write(IElement element, OutputStream output) throws Exception {
		try(OutputStreamWriter baseWriter = new OutputStreamWriter(output)) {
			this.gson.toJson(toGson(element), baseWriter);
		}
	}
	
	protected static IElement fromGson(JsonElement element) {
		if(element.isJsonArray()) {
			return new GsonArray(element.getAsJsonArray());
		} else if(element.isJsonObject()) {
			return new GsonObject(element.getAsJsonObject());
		} else {
			return new GsonElement(element);
		}
	}
	
	protected static JsonElement toGson(IElement element) {
		if(element.isArray()) {
			JsonArray jsonArray = new JsonArray();
			
			for(IElement e : element.asArray().elements().values()) {
				jsonArray.add(toGson(e));
			}
			
			return jsonArray;
		} else if(element.isNumber()) {
			return new JsonPrimitive(element.asNumber());
		} else if(element.isObject()) {
			JsonObject jsonObject = new JsonObject();

			for(Entry<String, IElement> entry : element.asObject().elements().entrySet()) {
				jsonObject.add(entry.getKey(), toGson(entry.getValue()));
			}
			
			return jsonObject;
		} else if(element.isString()) {
			return new JsonPrimitive(element.asString());
		} else if(element.isBool()) {
			return new JsonPrimitive(element.asBool());
		} else if(element.isChar()) {
			return new JsonPrimitive(element.asChar());
		} else if(element != PlainElement.EMPTY) {
			throw new JsonParseException("Unable to convert element to json");
		} else {
			return JsonNull.INSTANCE;
		}
	}
}
