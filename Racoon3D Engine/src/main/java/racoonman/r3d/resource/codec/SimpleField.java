package racoonman.r3d.resource.codec;

import racoonman.r3d.resource.codec.IFunction.IFunction1;
import racoonman.r3d.resource.codec.token.IElement;

public abstract class SimpleField<V, P> implements IField<V, P> {
	private String name;
	private IFunction1<V, P> fetch;
	
	public SimpleField(String name, IFunction1<V, P> fetch) {
		this.name = name;
		this.fetch = fetch;
	}
	
	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public IElement encode(P parent) {
		return this.encodeValue(this.fetch.apply(parent));
	}
	
	abstract IElement encodeValue(V value);
}
