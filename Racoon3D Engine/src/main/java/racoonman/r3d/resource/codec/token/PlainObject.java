package racoonman.r3d.resource.codec.token;

import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

public class PlainObject implements IObject {
	private Map<String, IElement> elements;
	
	public PlainObject() {
		this.elements = new LinkedTreeMap<>();
	}
	
	@Override
	public IElement getAt(String access) {
		return this.elements.get(access);
	}

	@Override
	public void set(String access, IElement e) {
		this.elements.put(access, e);
	}

	@Override
	public void set(String access, Number n) {
		this.elements.put(access, new PlainElement(n));
	}

	@Override
	public void set(String access, char c) {
		this.elements.put(access, new PlainElement(c));
	}

	@Override
	public void set(String access, String s) {
		this.elements.put(access, new PlainElement(s));
	}

	@Override
	public void set(String access, boolean b) {
		this.elements.put(access, new PlainElement(b));
	}

	@Override
	public boolean has(String access) {
		return this.elements.containsKey(access);
	}

	@Override
	public int size() {
		return this.elements.size();
	}

	@Override
	public Map<String, IElement> elements() {
		return this.elements;
	}

}
