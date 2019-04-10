
package com.hanlin.fadp.service.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigException;
import org.w3c.dom.Element;

/**
 * An object that models the <code>&lt;engine&gt;</code> element.
 */
@ThreadSafe
public final class Engine {

    private final String className;
    private final String name;
    private final List<Parameter> parameters;
    private final Map<String, Parameter> parameterMap;

    Engine(Element engineElement) throws ServiceConfigException {
        String name = engineElement.getAttribute("name").intern();
        if (name.isEmpty()) {
            throw new ServiceConfigException("<engine> element name attribute is empty");
        }
        this.name = name;
        String className = engineElement.getAttribute("class").intern();
        if (className.isEmpty()) {
            throw new ServiceConfigException("<engine> element class attribute is empty");
        }
        this.className = className;
        List<? extends Element> parameterElementList = UtilXml.childElementList(engineElement, "parameter");
        if (parameterElementList.isEmpty()) {
            this.parameters = Collections.emptyList();
            this.parameterMap = Collections.emptyMap();
        } else {
            List<Parameter> parameters = new ArrayList<Parameter>(parameterElementList.size());
            Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();
            for (Element parameterElement : parameterElementList) {
                Parameter parameter = new Parameter(parameterElement);
                parameters.add(parameter);
                parameterMap.put(parameter.getName(), parameter);
            }
            this.parameters = Collections.unmodifiableList(parameters);
            this.parameterMap = Collections.unmodifiableMap(parameterMap);
        }
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public Parameter getParameter(String parameterName) {
        return parameterMap.get(parameterName);
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public String getParameterValue(String parameterName) {
        Parameter parameter = parameterMap.get(parameterName);
        if (parameter != null) {
            return parameter.getValue();
        }
        return null;
    }
}
