
package com.hanlin.fadp.minilang.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * Map Processor Main Class
 */
public class MapProcessor {

    List<MakeInString> makeInStrings = new ArrayList<MakeInString>();
    String name;
    List<SimpleMapProcess> simpleMapProcesses = new ArrayList<SimpleMapProcess>();

    public MapProcessor(Element simpleMapProcessorElement) {
        name = simpleMapProcessorElement.getAttribute("name");
        for (Element makeInStringElement : UtilXml.childElementList(simpleMapProcessorElement, "make-in-string")) {
            MakeInString makeInString = new MakeInString(makeInStringElement);
            makeInStrings.add(makeInString);
        }
        for (Element simpleMapProcessElement : UtilXml.childElementList(simpleMapProcessorElement, "process")) {
            SimpleMapProcess strProc = new SimpleMapProcess(simpleMapProcessElement);
            simpleMapProcesses.add(strProc);
        }
    }

    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        if (UtilValidate.isNotEmpty(makeInStrings)) {
            for (MakeInString makeInString : makeInStrings) {
                makeInString.exec(inMap, results, messages, locale, loader);
            }
        }
        if (UtilValidate.isNotEmpty(simpleMapProcesses)) {
            for (SimpleMapProcess simpleMapProcess : simpleMapProcesses) {
                simpleMapProcess.exec(inMap, results, messages, locale, loader);
            }
        }
    }

    public String getName() {
        return name;
    }
}
