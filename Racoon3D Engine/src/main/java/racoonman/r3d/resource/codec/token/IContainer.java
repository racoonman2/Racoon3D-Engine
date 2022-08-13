package racoonman.r3d.resource.codec.token;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public interface IContainer<A> extends IElement, Iterable<IElement> {
	IElement getAt(A access);

	void set(A access, IElement e);
	
	void set(A access, Number n);
	
	void set(A access, char c);
	
	void set(A access, String s);
	
	void set(A access, boolean b);
	
	default <T extends Enum<T>> void set(A access, T e) {
		this.set(access, e.ordinal());
	}
	
	boolean has(A access);

	int size();
	
	Map<A, IElement> elements();
	
	default IArray arrayAt(A access) {
		return this.getAt(access).asArray();
	}
		
	default IObject objectAt(A access) {
		return this.getAt(access).asObject();
	}
	
	default Number numberAt(A access) {
		return this.getAt(access).asNumber();
	}
	
	default byte byteAt(A access) {
		return this.getAt(access).asByte();
	}
	
	default short shortAt(A access) {
		return this.getAt(access).asShort();
	}
	
	default int intAt(A access) {
		return this.getAt(access).asInt();
	}
	
	default long longAt(A access) {
		return this.getAt(access).asLong();
	}
	
	default float floatAt(A access) {
		return this.getAt(access).asFloat();
	}
	
	default double doubleAt(A access) {
		return this.getAt(access).asDouble();
	}
	
	default char charAt(A access) {
		return this.getAt(access).asChar();
	}
	
	default String stringAt(A access) {
		return this.getAt(access).asString();
	}
	
	default boolean boolAt(A access) {
		return this.getAt(access).asBool();
	}
	
	default <T extends Enum<T>> T enumAt(A access, T[] values) {
		return values[this.intAt(access)];
	}
	
	default IArray arrayAtOr(A access, IArray fallback) {
		return this.has(access) ? this.arrayAt(access) : fallback;
	}
		
	default IObject objectAtOr(A access, IObject fallback) {
		return this.has(access) ? this.objectAt(access) : fallback;
	}
	
	default Number numberAtOr(A access, Number fallback) {
		return this.has(access) ? this.numberAt(access) : fallback;
	}
	
	default byte byteAtOr(A access, byte fallback) {
		return this.has(access) ? this.byteAt(access) : fallback;
	}
	
	default short shortAtOr(A access, short fallback) {
		return this.has(access) ? this.shortAt(access) : fallback;
	}
	
	default int intAtOr(A access, int fallback) {
		return this.has(access) ? this.intAt(access) : fallback;
	}
	
	default long longAtOr(A access, long fallback) {
		return this.has(access) ? this.longAt(access) : fallback;
	}
	
	default float floatAtOr(A access, float fallback) {
		return this.has(access) ? this.floatAt(access) : fallback;
	}
	
	default double doubleAtOr(A access, double fallback) {
		return this.has(access) ? this.doubleAt(access) : fallback;
	}
	
	default char charAtOr(A access, char fallback) {
		return this.has(access) ? this.charAt(access) : fallback;
	}
	
	default String stringAtOr(A access, String fallback) {
		return this.has(access) ? this.stringAt(access) : fallback;
	}
	
	default boolean boolAtOr(A access, boolean fallback) {
		return this.has(access) ? this.boolAt(access) : fallback;
	}
	
	default <T extends Enum<T>> T enumAtOr(A access, T[] values, T fallback) {
		return this.has(access) ? this.enumAt(access, values) : fallback;
	}
	
	default IArray arrayAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.arrayAt(access);
		} else {
			throw x.get();
		}
	}
		
	default IObject objectAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.objectAt(access);
		} else {
			throw x.get();
		}
	}
	
	default Number numberAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.numberAt(access);
		} else {
			throw x.get();
		}
	}
	
	default byte byteAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.byteAt(access);
		} else {
			throw x.get();
		}
	}
	
	default short shortAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.shortAt(access);
		} else {
			throw x.get();
		}
	}
	
	default int intAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.intAt(access);
		} else {
			throw x.get();
		}
	}
	
	default long longAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.longAt(access);
		} else {
			throw x.get();
		}
	}
	
	default float floatAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.floatAt(access);
		} else {
			throw x.get();
		}
	}
	
	default double doubleAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.doubleAt(access);
		} else {
			throw x.get();
		}
	}
	
	default char charAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.charAt(access);
		} else {
			throw x.get();
		}
	}
	
	default String stringAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.stringAt(access);
		} else {
			throw x.get();
		}
	}
	
	default boolean boolAtOrThrow(A access, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.boolAt(access);
		} else {
			throw x.get();
		}
	}
	
	default <T extends Enum<T>> T enumAtOrThrow(A access, T[] values, Supplier<Exception> x) throws Exception {
		if(this.has(access)) {
			return this.enumAt(access, values);
		} else {
			throw x.get();
		}
	}
	
	@Override
	default Number asNumber() {
		throw new UnsupportedOperationException();
	}

	@Override
	default char asChar() {
		throw new UnsupportedOperationException();
	}

	@Override
	default String asString() {
		throw new UnsupportedOperationException();
	}

	@Override
	default boolean asBool() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	default boolean isNumber() {
		return false;
	}
	
	@Override
	default boolean isByte() {
		return false;
	}
	
	@Override
	default boolean isShort() {
		return false;
	}
	
	@Override
	default boolean isInt() {
		return false;
	}
	
	@Override
	default boolean isLong() {
		return false;
	}
	
	@Override
	default boolean isFloat() {
		return false;
	}
	
	@Override
	default boolean isDouble() {
		return false;
	}
	
	@Override
	default boolean isChar() {
		return false;
	}
	
	@Override
	default boolean isString() {
		return false;
	}
	
	@Override
	default boolean isBool() {
		return false;
	}
	
	@Override
	default Iterator<IElement> iterator() {
		return this.elements().values().iterator();
	}
}
