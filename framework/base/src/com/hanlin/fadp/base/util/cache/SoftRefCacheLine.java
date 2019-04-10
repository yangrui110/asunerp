
package com.hanlin.fadp.base.util.cache;

@SuppressWarnings("serial")
public abstract class SoftRefCacheLine<V> extends CacheLine<V> {
    public final CacheSoftReference<V> ref;

    public SoftRefCacheLine(V value, long loadTimeNanos, long expireTimeNanos) {
        super(loadTimeNanos, expireTimeNanos);
        this.ref = new CacheSoftReference<V>(value) {
            public void remove() {
                SoftRefCacheLine.this.remove();
            }
        };
    }

    @Override
    void cancel() {
        ref.clear();
    }

    @Override
    public V getValue() {
        return ref.get();
    }
}
