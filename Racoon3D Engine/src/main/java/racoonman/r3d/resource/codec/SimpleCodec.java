package racoonman.r3d.resource.codec;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.IObject;
import racoonman.r3d.resource.codec.token.PlainObject;

public abstract class SimpleCodec<T> implements ICodec<T> {
	protected Set<IField<?, T>> fields;
	
	@SafeVarargs 
	public SimpleCodec(IField<?, T>... fields) {
		this.fields = ImmutableSet.copyOf(fields);
	}
	
	@Override
	public IElement encode(T t) {
		IObject object = new PlainObject();
		for(IField<?, T> field : this.fields) {
			object.set(field.name(), field.encode(t));
		}
		return object;
	}
}
