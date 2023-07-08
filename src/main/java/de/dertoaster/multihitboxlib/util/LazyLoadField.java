package de.dertoaster.multihitboxlib.util;

import com.google.common.base.Supplier;

public class LazyLoadField<T extends Object> implements Supplier<T> {

	private T value = null;
	private final Supplier<T> function;
	
	public LazyLoadField(final Supplier<T> function) {
		this.function = function;
	}

	@Override
	public T get() {
		if (this.value == null) {
			this.value = this.function.get();
		}
		return this.value;
	}
	
}
