
package com.hanlin.fadp.minilang.operation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.ObjectType;
import org.w3c.dom.Element;

/**
 * A string operation that calls a validation method
 */
public class ValidateMethod extends SimpleMapOperation {

    public static final String module = ValidateMethod.class.getName();

    String className;
    String methodName;

    public ValidateMethod(Element element, SimpleMapProcess simpleMapProcess) {
        super(element, simpleMapProcess);
        this.methodName = element.getAttribute("method");
        this.className = element.getAttribute("class");
    }

    @Override
    public void exec(Map<String, Object> inMap, Map<String, Object> results, List<Object> messages, Locale locale, ClassLoader loader) {
        Object obj = inMap.get(fieldName);
        String fieldValue = null;
        try {
            fieldValue = (String) ObjectType.simpleTypeConvert(obj, "String", null, locale);
        } catch (GeneralException e) {
            messages.add("Could not convert field value for comparison: " + e.getMessage());
            return;
        }
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        Class<?>[] paramTypes = new Class<?>[] { String.class };
        Object[] params = new Object[] { fieldValue };
        Class<?> valClass;
        try {
            valClass = loader.loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            String msg = "Could not find validation class: " + className;
            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }
        Method valMethod;
        try {
            valMethod = valClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException cnfe) {
            String msg = "Could not find validation method: " + methodName + " of class " + className;
            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }
        Boolean resultBool = Boolean.FALSE;
        try {
            resultBool = (Boolean) valMethod.invoke(null, params);
        } catch (Exception e) {
            String msg = "Error in validation method " + methodName + " of class " + className + ": " + e.getMessage();

            messages.add(msg);
            Debug.logError("[ValidateMethod.exec] " + msg, module);
            return;
        }
        if (!resultBool.booleanValue()) {
            addMessage(messages, loader, locale);
        }
    }
}
