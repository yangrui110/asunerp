
package com.hanlin.fadp.base.conversion;

import java.util.LinkedList;
import java.util.List;

public class GenericSingletonToList<T> extends AbstractConverter<T, List<T>> {
    public GenericSingletonToList(Class<T> sourceClass) {
        super(sourceClass, List.class);
    }

    public List<T> convert(T obj) throws ConversionException {
        List<T> tempList = new LinkedList<T>();
        tempList.add(obj);
        return tempList;
    }
}
