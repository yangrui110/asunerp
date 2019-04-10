
package com.hanlin.fadp.base.util.collections;

import java.util.Map;

public abstract class GenericMapKeySet<K, V, M extends Map<K, V>> extends GenericMapSet<K, V, M, K> {
    public GenericMapKeySet(M source) {
        super(source);
    }

    public boolean remove(Object item) {
        return source.remove(item) != null;
    }
}

