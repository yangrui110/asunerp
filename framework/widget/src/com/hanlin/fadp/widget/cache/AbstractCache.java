
package com.hanlin.fadp.widget.cache;

import com.hanlin.fadp.base.util.cache.UtilCache;

public abstract class AbstractCache {

    protected String id;

    protected AbstractCache(String id) {
        this.id = id;
    }

    public void remove(String widgetName) {
        UtilCache.clearCache(getCacheName(widgetName));
    }

    public void clear() {
        UtilCache.clearCachesThatStartWith(getCacheNamePrefix());
    }

    public String getCacheNamePrefix() {
        return "widgetcache." + id + ".";
    }

    public String getCacheName(String widgetName) {
        return getCacheNamePrefix() + widgetName;
    }

    protected <K,V> UtilCache<K,V> getCache(String widgetName) {
        return UtilCache.findCache(getCacheName(widgetName));
    }

    protected UtilCache<WidgetContextCacheKey, GenericWidgetOutput> getOrCreateCache(String widgetName) {
        String name = getCacheName(widgetName);
        return UtilCache.getOrCreateUtilCache(name, 0, 0, 0, true, false, name);
    }
}
