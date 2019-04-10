
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.ObjectType;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if-empty&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cifempty%3E}}">Mini-language Reference</a>
 */
public final class EmptyCondition extends MethodOperation implements Conditional {

    public static final String module = EmptyCondition.class.getName();

    private final FlexibleMapAccessor<Object> fieldFma;
    // Sub-operations are used only when this is a method operation.
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> subOps;

    public EmptyCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field");
        }
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
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
        return ObjectType.isEmpty(fieldFma.get(methodContext.getEnvMap()));
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
        messageBuffer.append("empty[");
        messageBuffer.append(fieldFma);
        messageBuffer.append("=");
        messageBuffer.append(fieldFma.get(methodContext.getEnvMap()));
        messageBuffer.append("]");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<if-empty ");
        sb.append("field=\"").append(this.fieldFma).append("\"/>");
        return sb.toString();
    }

    /**
     * A &lt;if-empty&gt; element factory. 
     */
    public static final class EmptyConditionFactory extends ConditionalFactory<EmptyCondition> implements Factory<EmptyCondition> {
        @Override
        public EmptyCondition createCondition(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EmptyCondition(element, simpleMethod);
        }

        public EmptyCondition createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EmptyCondition(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "if-empty";
        }
    }
}
