
package com.hanlin.fadp.base.util.collections;

import java.util.Map;

public abstract class GenericMapEntrySet<K, V, M extends GenericMap<K, V>> extends GenericMapSet<K, V, M, Map.Entry<K, V>> {
    public GenericMapEntrySet(M source) {
        super(source);
    }

    public boolean contains(Object item) {
        if (item == null) return false;
        if (!(item instanceof Map.Entry<?, ?>)) return false;
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) item;
        return contains(other.getKey(), other.getValue());
    }

    protected abstract boolean contains(Object key, Object value);

    public boolean remove(Object item) {
        if (item == null) return false;
        if (!(item instanceof Map.Entry<?, ?>)) return false;
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) item;
        Object key = other.getKey();
        if (!source.containsKey(key)) return false;
        source.remove(key);
        return true;
    }
}

