
package com.hanlin.fadp.minilang.method.conditional;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangElement;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;else-if&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Celseif%3E}}">Mini-language Reference</a>
 */
public final class ElseIf extends MiniLangElement {

    private final Conditional condition;
    private final List<MethodOperation> thenSubOps;

    public ElseIf(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.childElements(simpleMethod, element, "condition", "then");
            MiniLangValidate.requiredChildElements(simpleMethod, element, "condition", "then");
        }
        Element conditionElement = UtilXml.firstChildElement(element, "condition");
        Element conditionChildElement = UtilXml.firstChildElement(conditionElement);
        this.condition = ConditionalFactory.makeConditional(conditionChildElement, simpleMethod);
        Element thenElement = UtilXml.firstChildElement(element, "then");
        this.thenSubOps = Collections.unmodifiableList(SimpleMethod.readOperations(thenElement, simpleMethod));
    }

    public boolean checkCondition(MethodContext methodContext) throws MiniLangException {
        return condition.checkCondition(methodContext);
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.thenSubOps) {
            method.gatherArtifactInfo(aic);
        }
    }

    public List<MethodOperation> getThenSubOps() {
        return this.thenSubOps;
    }

    public boolean runSubOps(MethodContext methodContext) throws MiniLangException {
        return SimpleMethod.runSubOps(thenSubOps, methodContext);
    }
}
