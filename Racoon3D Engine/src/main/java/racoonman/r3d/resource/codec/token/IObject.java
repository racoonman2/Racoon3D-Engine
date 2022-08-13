package racoonman.r3d.resource.codec.token;

public interface IObject extends IContainer<String> {

	@Override
	default IArray asArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	default IObject asObject() {
		return this;
	}
	
	@Override
	default boolean isArray() {
		return false;
	}
	
	@Override
	default boolean isObject() {
		return true;
	}
}
