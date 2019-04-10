
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * Abstract class defining the interface to a MakeInString operation
 */
public abstract class MakeInStringOperation {

    public MakeInStringOperation(Element element) {
    }

    public abstract String exec(Map<String, Object> inMap, List<Object> messages, Locale locale, ClassLoader loader);
}
