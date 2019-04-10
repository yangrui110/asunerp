
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilValidate;
import org.w3c.dom.Element;

/**
 * Checks to see if the current field is empty (null or zero length)
 */
public class NotEmpty extends SimpleMapOperation {

    public NotEmpty(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
    }

    @Override
    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        Object obj = inMap.get(fieldName);
        if (obj instanceof java.lang.String) {
            String fieldValue = (java.lang.String) obj;
            if (!UtilValidate.isNotEmpty(fieldValue)) {
                addMessage(messages, loader, locale);
            }
        } else {
            if (obj == null)
                addMessage(messages, loader, locale);
        }
    }
}
