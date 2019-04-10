
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * Compares the current field to another field
 */
public class CompareField extends BaseCompare {

    String compareName;

    public CompareField(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        this.compareName = element.getAttribute("field");
    }

    @Override
    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        Object compareValue = inMap.get(compareName);
        Object fieldValue = inMap.get(fieldName);

        doCompare(fieldValue, compareValue, messages, locale, loader, false);
    }
}
