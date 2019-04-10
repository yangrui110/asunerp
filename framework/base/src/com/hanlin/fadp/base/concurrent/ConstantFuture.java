

package com.hanlin.fadp.base.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConstantFuture<V> implements Future<V> {
    private final V value;

    public ConstantFuture(V value) {
        this.value = value;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public V get() {
        return value;
    }

    public V get(long timeout, TimeUnit unit) {
        return value;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }
}
