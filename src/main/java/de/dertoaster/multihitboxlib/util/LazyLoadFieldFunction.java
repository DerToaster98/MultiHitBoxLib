package de.dertoaster.multihitboxlib.util;

import java.util.function.Function;

public class LazyLoadFieldFunction<A extends Object, T extends Object> implements Function<A, T> {

    private T value = null;
    private int lifetime = -1;
    private long lastSet = System.currentTimeMillis();
    private final Function<A, T> function;

    public LazyLoadFieldFunction(final Function<A, T> function) {
        this.function = function;
    }

    public LazyLoadFieldFunction(final Function<A, T> function, final int lifetime) {
        this(function);
        this.lifetime = lifetime;
    }

    public void reset() {
        this.value = null;
    }

    @Override
    public T apply(A argument) {
        if (this.lifetime > 0) {
            if (System.currentTimeMillis() - this.lastSet >= this.lifetime) {
                this.lastSet = System.currentTimeMillis();
                this.value = this.function.apply(argument);
            }
        }
        if (this.value == null) {
            this.value = this.function.apply(argument);
        }
        return this.value;
    }

}
