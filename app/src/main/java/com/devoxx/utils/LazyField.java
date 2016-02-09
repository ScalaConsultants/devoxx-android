package com.devoxx.utils;


public class LazyField<T> {

    public interface Initializer<T> {
        T initValue();
    }

    public LazyField(Initializer<T> initializer) {
        this.initializer = initializer;
    }

    private final Initializer<T> initializer;

    private T value;

    public T getValue() {
        if (value == null) {
            value = initializer.initValue();
        }
        return value;
    }
}
