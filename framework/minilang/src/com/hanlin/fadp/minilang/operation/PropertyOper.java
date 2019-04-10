
package com.hanlin.fadp.minilang.operation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.base.util.UtilValidate;
import org.w3c.dom.Element;

/**
 * A MakeInStringOperation that insert the value of a property from a properties file
 */
public class PropertyOper extends MakeInStringOperation {

    public static final String module = PropertyOper.class.getName();

    String property;
    String resource;

    public PropertyOper(Element element) {
        super(element);
        resource = element.getAttribute("resource");
        property = element.getAttribute("property");
    }

    @Override
    public String exec(Map<String, Object> inMap, List<Object> messages, Locale locale, ClassLoader loader) {
        String propStr = UtilProperties.getPropertyValue(UtilURL.fromResource(resource, loader), property);

        if (UtilValidate.isEmpty(propStr)) {
            Debug.logWarning("[SimpleMapProcessor.PropertyOper.exec] Property " + property + " in resource " + resource + " not found, not appending anything", module);
            return null;
        } else {
            return propStr;
        }
    }
}
