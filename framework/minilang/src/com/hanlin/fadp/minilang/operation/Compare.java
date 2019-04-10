
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * Compares an in-field to the specified value
 */
public class Compare extends BaseCompare {

    String value;

    public Compare(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        this.value = element.getAttribute("value");
    }

    @Override
    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        Object fieldValue = inMap.get(fieldName);
        doCompare(fieldValue, value, messages, locale, loader, true);
    }
}
