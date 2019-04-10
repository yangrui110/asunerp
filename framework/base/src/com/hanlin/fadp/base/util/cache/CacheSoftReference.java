
package com.hanlin.fadp.base.util.cache;

import java.io.Serializable;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ReferenceCleaner;

@SuppressWarnings("serial")
public abstract class CacheSoftReference<V> extends ReferenceCleaner.Soft<V> implements Serializable {

    public static final String module = CacheSoftReference.class.getName();

    public CacheSoftReference(V o) {
        super(o);
    }

    @Override
    public void clear() {
        if (Debug.verboseOn()) {
            Debug.logVerbose(new Exception("UtilCache.CacheSoftRef.clear()"), "Clearing UtilCache SoftReference - " + get(), module);
        }
        super.clear();
    }

    @Override
    public void finalize() throws Throwable {
        if (Debug.verboseOn()) {
            Debug.logVerbose(new Exception("UtilCache.CacheSoftRef.finalize()"), "Finalize UtilCache SoftReference - " + get(), module);
        }
        super.finalize();
    }
}
