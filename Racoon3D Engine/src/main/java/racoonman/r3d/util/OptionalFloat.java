package racoonman.r3d.util;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public final class OptionalFloat {
	private static final OptionalFloat EMPTY = new OptionalFloat();

	private final boolean isPresent;
	private final float value;

	private OptionalFloat() {
		this.isPresent = false;
		this.value = Float.NaN;
	}

	public static OptionalFloat empty() {
		return EMPTY;
	}

	private OptionalFloat(float value) {
		this.isPresent = true;
		this.value = value;
	}

	public static OptionalFloat of(float value) {
		return new OptionalFloat(value);
	}

	public float getAsFloat() {
		if (!this.isPresent) {
			throw new NoSuchElementException("No value present");
		}
		return this.value;
	}

	public boolean isPresent() {
		return this.isPresent;
	}

	public boolean isEmpty() {
		return !this.isPresent;
	}

	public void ifPresent(FloatConsumer action) {
		if (this.isPresent) {
			action.accept(this.value);
		}
	}

	public void ifPresentOrElse(FloatConsumer action, Runnable emptyAction) {
		if (this.isPresent) {
			action.accept(this.value);
		} else {
			emptyAction.run();
		}
	}

	public float orElse(float other) {
		return this.isPresent ? this.value : other;
	}

	public float orElseGet(FloatSupplier supplier) {
		return this.isPresent ? this.value : supplier.getAsFloat();
	}

	public float orElseThrow() {
		if (!this.isPresent) {
			throw new NoSuchElementException("No value present");
		}
		return this.value;
	}

	public <X extends Throwable> float orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (this.isPresent) {
			return this.value;
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		return obj instanceof OptionalFloat other
				&& (this.isPresent && other.isPresent ? Float.compare(this.value, other.value) == 0
						: this.isPresent == other.isPresent);
	}

	@Override
	public int hashCode() {
		return this.isPresent ? Float.hashCode(this.value) : 0;
	}

	@Override
	public String toString() {
		return this.isPresent ? String.format("OptionalFloat[%s]", this.value) : "OptionalFloat.empty";
	}
}
