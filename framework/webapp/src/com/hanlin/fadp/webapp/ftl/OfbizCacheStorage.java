
package com.hanlin.fadp.webapp.ftl;

import freemarker.cache.CacheStorage;

import com.hanlin.fadp.base.util.cache.UtilCache;

/**
 * A custom cache wrapper for caching FreeMarker templates
 */
public class OfbizCacheStorage implements CacheStorage {
    //can't have global cache because names/keys are relative to the webapp
    protected final UtilCache<Object, Object> localCache;

    public OfbizCacheStorage(String id) {
        this.localCache = UtilCache.createUtilCache("webapp.FreeMarkerCache." + id, 0, 0, false);
    }

    public Object get(Object key) {
        return localCache.get(key);
    }

    public void put(Object key, Object value) {
        localCache.put(key, value);
    }

    public void remove(Object key) {
        localCache.remove(key);
    }

    public void clear() {
        localCache.clear();
    }
}
