
package com.hanlin.fadp.minilang.method.envops;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.FieldObject;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodObject;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.minilang.method.StringObject;
import org.w3c.dom.Element;

/**
 * Implements the &lt;create-object&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{<createobject>}}">Mini-language Reference</a>
 */
public final class CreateObject extends MethodOperation {

    public static final String module = CreateObject.class.getName();

    private final String className;
    private final Class<?> targetClass;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final List<MethodObject<?>> parameters;

    public CreateObject(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<create-object> element is deprecated (use <script>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "class-name", "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "class-name", "field");
            MiniLangValidate.childElements(simpleMethod, element, "string", "field");
        }
        className = element.getAttribute("class-name");
        Class<?> targetClass = null;
        try {
            targetClass = ObjectType.loadClass(this.className);
        } catch (ClassNotFoundException e) {
            MiniLangValidate.handleError("Class not found with name " + this.className, simpleMethod, element);
        }
        this.targetClass = targetClass;
        fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        List<? extends Element> parameterElements = UtilXml.childElementList(element);
        if (parameterElements.size() > 0) {
            ArrayList<MethodObject<?>> parameterList = new ArrayList<MethodObject<?>>(parameterElements.size());
            for (Element parameterElement : parameterElements) {
                if ("string".equals(parameterElement.getNodeName())) {
                    parameterList.add(new StringObject(parameterElement, simpleMethod));
                } else if ("field".equals(parameterElement.getNodeName())) {
                    parameterList.add(new FieldObject<Object>(parameterElement, simpleMethod));
                }
            }
            parameterList.trimToSize();
            this.parameters = Collections.unmodifiableList(parameterList);
        } else {
            this.parameters = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object[] args = null;
        Class<?>[] parameterTypes = null;
        if (parameters != null) {
            args = new Object[parameters.size()];
            parameterTypes = new Class<?>[parameters.size()];
            int i = 0;
            for (MethodObject<?> methodObjectDef : parameters) {
                args[i] = methodObjectDef.getObject(methodContext);
                Class<?> typeClass = null;
                try {
                    typeClass = methodObjectDef.getTypeClass(methodContext);
                } catch (ClassNotFoundException e) {
                    String errMsg = "ERROR: Could not complete the " + simpleMethod.getShortDescription() + " process [Parameter type not found with name " + methodObjectDef.getTypeName() + "]";
                    Debug.logWarning(e, errMsg, module);
                    simpleMethod.addErrorMessage(methodContext, errMsg);
                    return false;
                }
                parameterTypes[i] = typeClass;
                i++;
            }
        }
        try {
            Constructor<?> constructor = targetClass.getConstructor(parameterTypes);
            fieldFma.put(methodContext.getEnvMap(),constructor.newInstance(args));
        } catch (Exception e) {
            throw new MiniLangRuntimeException(e, this);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<create-object ");
        sb.append("class-name=\"").append(this.className).append("\" ");
        sb.append("field=\"").append(this.fieldFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;create-object&gt; element.
     */
    public static final class CreateObjectFactory implements Factory<CreateObject> {
        @Override
        public CreateObject createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CreateObject(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "create-object";
        }
    }
}
