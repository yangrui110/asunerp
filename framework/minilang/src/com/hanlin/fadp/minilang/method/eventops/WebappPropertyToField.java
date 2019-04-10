
package com.hanlin.fadp.minilang.method.eventops;

import java.net.URL;

import javax.servlet.ServletContext;

import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;webapp-property-to-field&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cwebapppropertytofield%3E}}">Mini-language Reference</a>
 */
public final class WebappPropertyToField extends MethodOperation {

    private final FlexibleStringExpander defaultFse;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander propertyFse;
    private final FlexibleStringExpander resourceFse;

    public WebappPropertyToField(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "resource", "property", "default");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "resource", "property");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.resourceFse = FlexibleStringExpander.getInstance(element.getAttribute("resource"));
        this.propertyFse = FlexibleStringExpander.getInstance(element.getAttribute("property"));
        this.defaultFse = FlexibleStringExpander.getInstance(element.getAttribute("default"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (methodContext.getMethodType() == MethodContext.EVENT) {
            String resource = resourceFse.expandString(methodContext.getEnvMap());
            ServletContext servletContext = (ServletContext) methodContext.getRequest().getAttribute("servletContext");
            URL propsUrl = null;
            try {
                propsUrl = servletContext.getResource(resource);
            } catch (java.net.MalformedURLException e) {
                throw new MiniLangRuntimeException("Exception thrown while finding properties file " + resource + ": " + e.getMessage(), this);
            }
            if (propsUrl == null) {
                throw new MiniLangRuntimeException("Properties file " + resource + " not found.", this);
            }
            String property = propertyFse.expandString(methodContext.getEnvMap());
            String fieldVal = UtilProperties.getPropertyValue(propsUrl, property);
            if (fieldVal == null) {
                fieldVal = defaultFse.expandString(methodContext.getEnvMap());
            }
            fieldFma.put(methodContext.getEnvMap(), fieldVal);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<webapp-property-to-field ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        sb.append("resource=\"").append(this.resourceFse).append("\" ");
        sb.append("property=\"").append(this.propertyFse).append("\" ");
        if (!this.defaultFse.isEmpty()) {
            sb.append("default=\"").append(this.defaultFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;webapp-property-to-field&gt; element.
     */
    public static final class WebappPropertyToFieldFactory implements Factory<WebappPropertyToField> {
        @Override
        public WebappPropertyToField createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new WebappPropertyToField(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "webapp-property-to-field";
        }
    }
}
