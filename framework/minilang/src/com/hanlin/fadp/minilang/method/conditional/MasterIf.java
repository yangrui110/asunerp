
package com.hanlin.fadp.minilang.method.conditional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;if&gt; element.
 */
public final class MasterIf extends MethodOperation {

    private final Conditional condition;
    private final List<ElseIf> elseIfs;
    private final List<MethodOperation> elseSubOps;
    private final List<MethodOperation> thenSubOps;

    public MasterIf(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.childElements(simpleMethod, element, "condition", "then", "else-if", "else");
            MiniLangValidate.requiredChildElements(simpleMethod, element, "condition", "then");
        }
        Element conditionElement = UtilXml.firstChildElement(element, "condition");
        Element conditionChildElement = UtilXml.firstChildElement(conditionElement);
        this.condition = ConditionalFactory.makeConditional(conditionChildElement, simpleMethod);
        Element thenElement = UtilXml.firstChildElement(element, "then");
        this.thenSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(thenElement, simpleMethod));
        List<? extends Element> elseIfElements = UtilXml.childElementList(element, "else-if");
        if (elseIfElements.isEmpty()) {
            this.elseIfs = null;
        } else {
            List<ElseIf> elseIfs = new ArrayList<ElseIf>(elseIfElements.size());
            for (Element elseIfElement : elseIfElements) {
                elseIfs.add(new ElseIf(elseIfElement, simpleMethod));
            }
            this.elseIfs = Collections.unmodifiableList(elseIfs);
        }
        Element elseElement = UtilXml.firstChildElement(element, "else");
        if (elseElement == null) {
            this.elseSubOps = null;
        } else {
            this.elseSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(elseElement, simpleMethod));
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        // if conditions fails, always return true; if a sub-op returns false
        // return false and stop, otherwise return true
        // return true;
        // only run subOps if element is empty/null
        boolean runSubOps = condition.checkCondition(methodContext);
        if (runSubOps) {
            return SimpleMethod.runSubOps(thenSubOps, methodContext);
        } else {
            // try the else-ifs
            if (elseIfs != null) {
                for (ElseIf elseIf : elseIfs) {
                    if (elseIf.checkCondition(methodContext)) {
                        return elseIf.runSubOps(methodContext);
                    }
                }
            }
            if (elseSubOps != null) {
                return SimpleMethod.runSubOps(elseSubOps, methodContext);
            } else {
                return true;
            }
        }
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.thenSubOps) {
            method.gatherArtifactInfo(aic);
        }
        if (this.elseSubOps != null) {
            for (MethodOperation method : this.elseSubOps) {
                method.gatherArtifactInfo(aic);
            }
        }
        if (this.elseIfs != null) {
            for (ElseIf elseIf : elseIfs) {
                elseIf.gatherArtifactInfo(aic);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder messageBuf = new StringBuilder();
        this.condition.prettyPrint(messageBuf, null);
        return "<if><condition>" + messageBuf + "</condition></if>";
    }

    /**
     * A &lt;if&gt; element factory. 
     */
    public static final class MasterIfFactory implements Factory<MasterIf> {
        public MasterIf createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new MasterIf(element, simpleMethod);
        }

        public String getName() {
            return "if";
        }
    }
}
