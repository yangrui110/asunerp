
package com.hanlin.fadp.minilang.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * The container of MakeInString operations to make a new input String
 */
public class MakeInString {

    public static final String module = MakeInString.class.getName();

    String fieldName;
    List<MakeInStringOperation> operations = new ArrayList<MakeInStringOperation>();

    public MakeInString(Element makeInStringElement) {
        fieldName = makeInStringElement.getAttribute("field");
        List<? extends Element> operationElements = UtilXml.childElementList(makeInStringElement);
        if (UtilValidate.isNotEmpty(operationElements)) {
            for (Element curOperElem : operationElements) {
                String nodeName = curOperElem.getNodeName();
                if ("in-field".equals(nodeName)) {
                    operations.add(new InFieldOper(curOperElem));
                } else if ("property".equals(nodeName)) {
                    operations.add(new PropertyOper(curOperElem));
                } else if ("constant".equals(nodeName)) {
                    operations.add(new ConstantOper(curOperElem));
                } else {
                    Debug.logWarning("[SimpleMapProcessor.MakeInString.MakeInString] Operation element \"" + nodeName + "\" not recognized", module);
                }
            }
        }
    }

    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        StringBuilder buffer = new StringBuilder();
        for (MakeInStringOperation oper : operations) {
            String curStr = oper.exec(inMap, messages, locale, loader);
            if (curStr != null)
                buffer.append(curStr);
        }
        inMap.put(fieldName, buffer.toString());
    }
}
