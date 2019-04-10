

package com.hanlin.fadp.minilang.method.callops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.FieldObject;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodObject;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.minilang.method.StringObject;
import org.w3c.dom.Element;

/**
 * Implements the &lt;call-class-method&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ccallclassmethod%3E}}">Mini-language Reference</a>
 */
public final class CallClassMethod extends MethodOperation {

    public static final String module = CallClassMethod.class.getName();

    private final String className;
    private final Class<?> methodClass;
    private final String methodName;
    private final List<MethodObject<?>> parameters;
    private final FlexibleMapAccessor<Object> retFieldFma;

    public CallClassMethod(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<call-class-method> element is deprecated (use <script>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "class-name", "method-name", "ret-field");
            MiniLangValidate.constantAttributes(simpleMethod, element, "class-name", "method-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "class-name", "method-name");
            MiniLangValidate.childElements(simpleMethod, element, "string", "field");
        }
        this.className = element.getAttribute("class-name");
        Class<?> methodClass = null;
        try {
            methodClass = ObjectType.loadClass(this.className);
        } catch (ClassNotFoundException e) {
            MiniLangValidate.handleError("Class not found with name " + this.className, simpleMethod, element);
        }
        this.methodClass = methodClass;
        this.methodName = element.getAttribute("method-name");
        this.retFieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("ret-field"));
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
        MiniLangUtil.callMethod(this, methodContext, parameters, methodClass, null, methodName, retFieldFma);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<call-class-method ");
        if (!this.className.isEmpty()) {
            sb.append("class-name=\"").append(this.className).append("\" ");
        }
        if (!this.methodName.isEmpty()) {
            sb.append("method-name=\"").append(this.methodName).append("\" ");
        }
        if (!this.retFieldFma.isEmpty()) {
            sb.append("ret-field=\"").append(this.retFieldFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;call-class-method&gt; element.
     */
    public static final class CallClassMethodFactory implements Factory<CallClassMethod> {
        @Override
        public CallClassMethod createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CallClassMethod(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "call-class-method";
        }
    }
}
