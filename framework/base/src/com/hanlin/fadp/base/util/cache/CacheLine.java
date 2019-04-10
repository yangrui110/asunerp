
package com.hanlin.fadp.base.util.cache;

import com.hanlin.fadp.base.concurrent.ExecutionPool;

public abstract class CacheLine<V> extends ExecutionPool.Pulse {
    protected CacheLine(long loadTimeNanos, long expireTimeNanos) {
        super(loadTimeNanos, expireTimeNanos);
        // FIXME: this seems very odd to me (ARH)
        //if (loadTime <= 0) {
        //    hasExpired = true;
        //}
    }

    abstract CacheLine<V> changeLine(boolean useSoftReference, long expireTimeNanos);
    abstract void remove();
    boolean differentExpireTime(long expireTimeNanos) {
        return this.expireTimeNanos - loadTimeNanos - expireTimeNanos != 0;
    }
    public abstract V getValue();

    void cancel() {
    }

    @Override
    public void run() {
        remove();
    }
}

