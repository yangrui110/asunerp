
package com.hanlin.fadp.base.util.collections;

import java.util.Map;

import com.hanlin.fadp.base.util.UtilObject;

/** this class can go away when ofbiz switches to java 1.6, replaced by
 *  AbstractMap.SimpleImmutableEntry
 */
public class ReadOnlyMapEntry<K, V> implements Map.Entry<K, V> {
    protected final K key;
    protected final V value;

    public ReadOnlyMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry<?, ?>)) return false;
        if (this == o) return true;
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) o;
        return UtilObject.equalsHelper(getKey(), other.getKey()) && UtilObject.equalsHelper(getValue(), other.getValue());
    }

    @Override
    public int hashCode() {
        return UtilObject.doHashCode(getKey()) ^ UtilObject.doHashCode(getValue());
    }
}
