
package com.hanlin.fadp.base.util.collections;

import java.util.Locale;

/**
 * A simple interface to facilitate the retreival of values based on a Locale.
 *
 */
public interface LocalizedMap<V> {
    public V get(String name, Locale locale);
}
