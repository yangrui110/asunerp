
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
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
 * Implements the &lt;if-compare&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifcompare%3E}}">Mini-language Reference</a>
 */
public final class CompareCondition extends MethodOperation implements Conditional {

    private final Compare compare;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander formatFse;
    private final String operator;
    private final Class<?> targetClass;
    private final String type;
    private final FlexibleStringExpander valueFse;
    // Sub-operations are used only when this is a method operation.
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> subOps;

    public CompareCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "format", "operator", "type", "value");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "operator", "value");
            MiniLangValidate.constantAttributes(simpleMethod, element, "operator", "type");
            MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "value");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.formatFse = FlexibleStringExpander.getInstance(element.getAttribute("format"));
        this.operator = element.getAttribute("operator");
        this.compare = Compare.getInstance(this.operator);
        if (this.compare == null) {
            MiniLangValidate.handleError("Invalid operator " + this.operator, simpleMethod, element);
        }
        this.type = element.getAttribute("type");
        Class<?> targetClass = null;
        if (!this.type.isEmpty()) {
            if ("contains".equals(this.operator)) {
                MiniLangValidate.handleError("Operator \"contains\" does not support type conversions (remove the type attribute).", simpleMethod, element);
                targetClass = Object.class;
            } else {
                try {
                    targetClass = ObjectType.loadClass(this.type);
                } catch (ClassNotFoundException e) {
                    MiniLangValidate.handleError("Invalid type " + this.type, simpleMethod, element);
                }
            }
        }
        this.targetClass = targetClass;
        this.valueFse = FlexibleStringExpander.getInstance(element.getAttribute("value"));
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
        if (this.compare == null) {
            throw new MiniLangRuntimeException("Invalid operator \"" + this.operator + "\"", this);
        }
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        Class<?> targetClass = this.targetClass;
        if (targetClass == null) {
            targetClass = MiniLangUtil.getObjectClassForConversion(fieldVal);
        }
        String value = valueFse.expandString(methodContext.getEnvMap());
        String format = formatFse.expandString(methodContext.getEnvMap());
        try {
            // We use en locale here so constant (literal) values are converted properly.
            return this.compare.doCompare(fieldVal, value, targetClass, Locale.ENGLISH, methodContext.getTimeZone(), format);
        } catch (Exception e) {
            simpleMethod.addErrorMessage(methodContext, e.getMessage());
        }
        return false;
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
        String value = valueFse.expandString(methodContext.getEnvMap());
        String format = formatFse.expandString(methodContext.getEnvMap());
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        messageBuffer.append("[");
        messageBuffer.append(this.fieldFma);
        messageBuffer.append("=");
        messageBuffer.append(fieldVal);
        messageBuffer.append("] ");
        messageBuffer.append(operator);
        messageBuffer.append(" ");
        messageBuffer.append(value);
        messageBuffer.append(" as ");
        messageBuffer.append(type);
        if (UtilValidate.isNotEmpty(format)) {
            messageBuffer.append(":");
            messageBuffer.append(format);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-compare ");
        sb.append("field=\"").append(this.fieldFma).append("\" operator=\"").append(operator).append("\" ");
        if (!this.valueFse.isEmpty()) {
            sb.append("value=\"").append(this.valueFse).append("\" ");
        }
        if (!this.type.isEmpty()) {
            sb.append("type=\"").append(this.type).append("\" ");
        }
        if (!this.formatFse.isEmpty()) {
            sb.append("format=\"").append(this.formatFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A &lt;if-compare&gt; element factory. 
     */
    public static final class CompareConditionFactory extends ConditionalFactory<CompareCondition> implements Factory<CompareCondition> {
        @Override
        public CompareCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CompareCondition(element, simpleMethod);
        }

        public CompareCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CompareCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-compare";
        }
    }
}
