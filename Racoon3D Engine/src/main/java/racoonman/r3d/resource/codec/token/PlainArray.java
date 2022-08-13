package racoonman.r3d.resource.codec.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

//I hate this class
public class PlainArray implements IArray {
	private List<IElement> elements;
	
	public PlainArray() {
		this.elements = new ArrayList<>();
	}
	
	public PlainArray(IElement...elements) {
		this();
		
		Collections.addAll(this.elements, elements);
	}
	
	public PlainArray(Number... elements) {
		this();
		
		for(Number n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(byte... elements) {
		this();
		
		for(byte n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(int... elements) {
		this();
		
		for(int n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(long... elements) {
		this();
		
		for(long n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(float... elements) {
		this();

		for(float n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(double... elements) {
		this();
		
		for(double n : elements) {
			this.append(n);
		}
	}
	
	public PlainArray(char... elements) {
		this();
		
		for(char c : elements) {
			this.append(c);
		}
	}
	
	public PlainArray(String... elements) {
		this();
		
		for(String s : elements) {
			this.append(s);
		}
	}
	
	public PlainArray(boolean... elements) {
		this();
		
		for(boolean s : elements) {
			this.append(s);
		}
	}
	
	@Override
	public IElement getAt(Integer access) {
		return this.elements.get(access);
	}

	@Override
	public void set(Integer access, IElement e) {
		this.elements.set(access, e);
	}

	@Override
	public void set(Integer access, Number n) {
		this.elements.set(access, new PlainElement(n));
	}

	@Override
	public void set(Integer access, char c) {
		this.elements.set(access, new PlainElement(c));
	}

	@Override
	public void set(Integer access, String s) {
		this.elements.set(access, new PlainElement(s));
	}

	@Override
	public void set(Integer access, boolean b) {
		this.elements.set(access, new PlainElement(b));
	}

	@Override
	public boolean has(Integer access) {
		return access >= 0 && access < this.size() && this.getAt(access) != null;
	}

	@Override
	public int size() {
		return this.elements.size();
	}

	@Override
	public Map<Integer, IElement> elements() {
		Map<Integer, IElement> elements = new LinkedTreeMap<>();
		
		for(int i = 0; i < this.size(); i++) {
			elements.put(i, this.getAt(i));
		}
		
		return elements;
	}

	@Override
	public void append(IElement e) {
		this.elements.add(e);
	}

	@Override
	public void append(Number n) {
		this.elements.add(new PlainElement(n));
	}

	@Override
	public void append(String s) {
		this.elements.add(new PlainElement(s));
	}

	@Override 
	public void append(boolean b) {
		this.elements.add(new PlainElement(b));
	}
	
	@Override
	public void append(char c) {
		this.elements.add(new PlainElement(c));
	}
}
