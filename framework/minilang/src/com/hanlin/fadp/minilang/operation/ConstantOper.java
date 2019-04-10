
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * <p>
 * <b>Title:</b> A MakeInStringOperation that appends the specified constant string
 */
public class ConstantOper extends MakeInStringOperation {

    String constant;

    public ConstantOper(Element element) {
        super(element);
        constant = UtilXml.elementValue(element);
    }

    @Override
    public String exec(Map<String, Object> inMap, List<Object> messages, Locale locale, ClassLoader loader) {
        return constant;
    }
}
