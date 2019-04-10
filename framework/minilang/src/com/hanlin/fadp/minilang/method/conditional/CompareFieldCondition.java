
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.List;

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
 * Implements the &lt;if-compare-field&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifcomparefield%3E}}">Mini-language Reference</a>
 */
public final class CompareFieldCondition extends MethodOperation implements Conditional {

    // This method is needed only during the v1 to v2 transition
    private static boolean autoCorrect(Element element) {
        // Correct missing to-field attribute
        String toFieldAttr = element.getAttribute("to-field");
        if (toFieldAttr.isEmpty()) {
            element.setAttribute("to-field", element.getAttribute("field"));
            return true;
        }
        return false;
    }

    private final Compare compare;
    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleStringExpander formatFse;
    private final String operator;
    private final FlexibleMapAccessor<Object> toFieldFma;
    private final Class<?> targetClass;
    private final String type;
    // Sub-operations are used only when this is a method operation.
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> subOps;

    public CompareFieldCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "format", "operator", "type", "to-field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "operator", "to-field");
            MiniLangValidate.constantAttributes(simpleMethod, element, "operator", "type");
            MiniLangValidate.constantPlusExpressionAttributes(simpleMethod, element, "format");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field", "to-field");
        }
        boolean elementModified = autoCorrect(element);
        if (elementModified && MiniLangUtil.autoCorrectOn()) {
            MiniLangUtil.flagDocumentAsCorrected(element);
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.formatFse = FlexibleStringExpander.getInstance(element.getAttribute("format"));
        this.operator = element.getAttribute("operator");
        this.compare = Compare.getInstance(this.operator);
        if (this.compare == null) {
            MiniLangValidate.handleError("Invalid operator " + this.operator, simpleMethod, element);
        }
        this.toFieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("to-field"));
        this.type = element.getAttribute("type");
        Class<?> targetClass = null;
        if (!this.type.isEmpty()) {
            try {
                targetClass = ObjectType.loadClass(this.type);
            } catch (ClassNotFoundException e) {
                MiniLangValidate.handleError("Invalid type " + this.type, simpleMethod, element);
            }
        }
        this.targetClass = targetClass;
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
            throw new MiniLangRuntimeException("Invalid operator " + this.operator, this);
        }
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        Object toFieldVal = toFieldFma.get(methodContext.getEnvMap());
        Class<?> targetClass = this.targetClass;
        if (targetClass == null) {
            targetClass = MiniLangUtil.getObjectClassForConversion(fieldVal);
        }
        String format = formatFse.expandString(methodContext.getEnvMap());
        try {
            return this.compare.doCompare(fieldVal, toFieldVal, targetClass, methodContext.getLocale(), methodContext.getTimeZone(), format);
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
        String format = formatFse.expandString(methodContext.getEnvMap());
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        Object toFieldVal = toFieldFma.get(methodContext.getEnvMap());
        messageBuffer.append("[");
        messageBuffer.append(fieldFma);
        messageBuffer.append("=");
        messageBuffer.append(fieldVal);
        messageBuffer.append("] ");
        messageBuffer.append(operator);
        messageBuffer.append(" [");
        messageBuffer.append(toFieldFma);
        messageBuffer.append("=");
        messageBuffer.append(toFieldVal);
        messageBuffer.append("] ");
        messageBuffer.append(" as ");
        messageBuffer.append(type);
        if (UtilValidate.isNotEmpty(format)) {
            messageBuffer.append(":");
            messageBuffer.append(format);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-compare-field ");
        sb.append("field=\"").append(this.fieldFma).append("\" operator=\"").append(operator).append("\" ");
        if (!this.toFieldFma.isEmpty()) {
            sb.append("to-field=\"").append(this.toFieldFma).append("\" ");
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
     * A &lt;if-compare-field&gt; element factory. 
     */
    public static final class CompareFieldConditionFactory extends ConditionalFactory<CompareFieldCondition> implements Factory<CompareFieldCondition> {
        @Override
        public CompareFieldCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CompareFieldCondition(element, simpleMethod);
        }

        public CompareFieldCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new CompareFieldCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-compare-field";
        }
    }
}
