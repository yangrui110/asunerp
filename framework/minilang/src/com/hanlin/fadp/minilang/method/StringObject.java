
package com.hanlin.fadp.minilang.method;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.SimpleMethod;
import org.w3c.dom.Element;

/**
 * Specifies a <code>java.lang.String</code> to be passed as an argument to a method call.
 */
public final class StringObject extends MethodObject<String> {

    private final FlexibleStringExpander cdataValueFse;
    private final FlexibleStringExpander valueFse;

    public StringObject(Element element, SimpleMethod simpleMethod) {
        super(element, simpleMethod);
        this.cdataValueFse = FlexibleStringExpander.getInstance(UtilXml.elementValue(element));
        this.valueFse = FlexibleStringExpander.getInstance(element.getAttribute("value"));
    }

    @Override
    public String getObject(MethodContext methodContext) {
        String value = this.valueFse.expandString(methodContext.getEnvMap());
        String cdataValue = this.cdataValueFse.expandString(methodContext.getEnvMap());
        return value.concat(cdataValue);
    }

    @Override
    public Class<String> getTypeClass(MethodContext methodContext) throws ClassNotFoundException {
        return java.lang.String.class;
    }

    /** Get the name for the type of the object */
    @Override
    public String getTypeName() {
        return "java.lang.String";
    }
}
