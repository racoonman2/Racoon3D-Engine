package racoonman.r3d.util;

public class Holder<T> {
	private T value;
	
	public Holder() {
	}
	
	public Holder(T value) {
		this.value = value;
	}
	
	public void map(T t) {
		this.value = t;
	}
	
	public boolean isPresent() {
		return this.value != null;
	}
	
	public T getValue() {
		return this.value;
	}
}