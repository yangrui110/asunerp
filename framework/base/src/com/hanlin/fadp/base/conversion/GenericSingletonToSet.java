
package com.hanlin.fadp.base.conversion;

import java.util.HashSet;
import java.util.Set;

public class GenericSingletonToSet<T> extends AbstractConverter<T, Set<T>> {
    public GenericSingletonToSet(Class<T> sourceClass) {
        super(sourceClass, Set.class);
    }

    public Set<T> convert(T obj) throws ConversionException {
        Set<T> tempSet = new HashSet<T>();
        tempSet.add(obj);
        return tempSet;
    }
}
