
package com.hanlin.fadp.base.util.cache;

public abstract class HardRefCacheLine<V> extends CacheLine<V> {
    public final V value;

    public HardRefCacheLine(V value, long loadTimeNanos, long expireTimeNanos) {
        super(loadTimeNanos, expireTimeNanos);
        this.value = value;
    }

    @Override
    public V getValue() {
        return value;
    }
}
