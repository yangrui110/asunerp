
package com.hanlin.fadp.entity.cache;

import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;

public abstract class AbstractCache<K, V> {

    protected final String delegatorName, id, cacheNamePrefix;

    protected AbstractCache(String delegatorName, String id) {
        this.delegatorName = delegatorName;
        this.id = id;
        this.cacheNamePrefix = "entitycache.".concat(id).concat(".").concat(delegatorName).concat(".");
    }

    public Delegator getDelegator() {
        return DelegatorFactory.getDelegator(this.delegatorName);
    }

    public void remove(String entityName) {
        UtilCache.clearCache(getCacheName(entityName));
    }

    public void clear() {
        UtilCache.clearCachesThatStartWith(getCacheNamePrefix());
    }

    public String getCacheNamePrefix() {
        return cacheNamePrefix;
    }

    public String[] getCacheNamePrefixes() {
        return new String[] {
            "entitycache." + id + ".${delegator-name}.",
            cacheNamePrefix
        };
    }

    public String getCacheName(String entityName) {
        return getCacheNamePrefix() + entityName;
    }

    public String[] getCacheNames(String entityName) {
        String[] prefixes = getCacheNamePrefixes();
        String[] names = new String[prefixes.length * 2];
        for (int i = 0; i < prefixes.length; i++) {
            names[i] = prefixes[i] + "${entity-name}";
        }
        for (int i = prefixes.length, j = 0; j < prefixes.length; i++, j++) {
            names[i] = prefixes[j] + entityName;
        }
        return names;
    }

    protected UtilCache<K, V> getCache(String entityName) {
        return UtilCache.findCache(getCacheName(entityName));
    }

    protected UtilCache<K, V> getOrCreateCache(String entityName) {
        String name = getCacheName(entityName);
        return UtilCache.getOrCreateUtilCache(name, 0, 0, 0, true, false, getCacheNames(entityName));
    }
}
