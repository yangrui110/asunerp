
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.ObjectType;
import org.w3c.dom.Element;

/**
 * A MakeInStringOperation that inserts the value of an in-field
 */
public class InFieldOper extends MakeInStringOperation {

    public static final String module = InFieldOper.class.getName();

    String fieldName;

    public InFieldOper(Element element) {
        super(element);
        fieldName = element.getAttribute("field");
    }

    @Override
    public String exec(Map<String, Object> inMap, List<Object> messages, Locale locale, ClassLoader loader) {
        Object obj = inMap.get(fieldName);
        if (obj == null) {
            Debug.logWarning("[SimpleMapProcessor.InFieldOper.exec] In field " + fieldName + " not found, not appending anything", module);
            return null;
        }
        try {
            return (String) ObjectType.simpleTypeConvert(obj, "String", null, locale);
        } catch (GeneralException e) {
            Debug.logWarning(e, module);
            messages.add("Error converting incoming field \"" + fieldName + "\" in map processor: " + e.getMessage());
            return null;
        }
    }
}
