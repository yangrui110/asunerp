
package com.hanlin.fadp.minilang.method.conditional;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangUtil;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-validate-method&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifvalidatemethod%3E}}">Mini-language Reference</a>
 */
public final class ValidateMethodCondition extends MethodOperation implements Conditional {

    public static final String module = ValidateMethodCondition.class.getName();
    private static final Class<?>[] paramTypes = new Class<?>[] { String.class };

    private final String className;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final String methodName;
    // Sub-operations are used only when this is a method operation.
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> subOps;

    public ValidateMethodCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "method", "class");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "method");
            MiniLangValidate.constantAttributes(simpleMethod, element, "method", "class");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.methodName = element.getAttribute("method");
        this.className = MiniLangValidate.checkAttribute(element.getAttribute("class"), "com.hanlin.fadp.base.util.UtilValidate");
        Element childElement = UtilXml.firstChildElement(element);
        if (childElement != null && !"else".equals(childElement.getTagName())) {
            this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
        } else {
            this.subOps = null;
        }
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement != null) {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        } else {
            this.elseSubOps = null;
        }
    }

    @Override
    public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        if (fieldVal == null) {
            fieldVal = "";
        } else if (!(fieldVal instanceof String)) {
            try {
                fieldVal = MiniLangUtil.convertType(fieldVal, String.class, methodContext.getLocale(), methodContext.getTimeZone(), null);
            } catch (Exception e) {
                throw new MiniLangRuntimeException(e, this);
            }
        }
        Object[] params = new Object[] { fieldVal };
        try {
            Class<?> valClass = methodContext.getLoader().loadClass(className);
            Method valMethod = valClass.getMethod(methodName, paramTypes);
            Boolean resultBool = (Boolean) valMethod.invoke(null, params);
            return resultBool.booleanValue();
        } catch (Exception e) {
            throw new MiniLangRuntimeException(e, this);
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (checkCondition(methodContext)) {
            if (this.subOps != null) {
                return SimpleMethod.runSubOps(subOps, methodContext);
            }
        } else {
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            }
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        if (this.subOps != null) {
            for (MethodOperation method : this.subOps) {
                method.gatherArtifactInfo(aic);
            }
        }
        if (this.elseSubOps != null) {
            for (MethodOperation method : this.elseSubOps) {
                method.gatherArtifactInfo(aic);
            }
        }
    }

    public void prettyPrint(StringBuilder messageBuffer, MethodContext methodContext) {
        messageBuffer.append("validate-method[");
        messageBuffer.append(className);
        messageBuffer.append(".");
        messageBuffer.append(methodName);
        messageBuffer.append("(");
        messageBuffer.append(this.fieldFma);
        if (methodContext != null) {
            messageBuffer.append("=");
            messageBuffer.append(fieldFma.get(methodContext.getEnvMap()));
        }
        messageBuffer.append(")]");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-validate-method ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        if (!this.methodName.isEmpty()) {
            sb.append("methodName=\"").append(this.methodName).append("\" ");
        }
        if (!"com.hanlin.fadp.base.util.UtilValidate".equals(this.className)) {
            sb.append("class=\"").append(this.className).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A &lt;if-validate-method&gt; element factory. 
     */
    public static final class ValidateMethodConditionFactory extends ConditionalFactory<ValidateMethodCondition> implements Factory<ValidateMethodCondition> {
        @Override
        public ValidateMethodCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ValidateMethodCondition(element, simpleMethod);
        }

        @Override
        public ValidateMethodCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ValidateMethodCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-validate-method";
        }
    }
}
