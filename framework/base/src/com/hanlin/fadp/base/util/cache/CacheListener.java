
package com.hanlin.fadp.base.util.cache;

public interface CacheListener<K, V> {

    public void noteKeyRemoval(UtilCache<K, V> cache, K key, V oldValue);

    public void noteKeyAddition(UtilCache<K, V> cache, K key, V newValue);

    public void noteKeyUpdate(UtilCache<K, V> cache, K key, V newValue, V oldValue);
}
