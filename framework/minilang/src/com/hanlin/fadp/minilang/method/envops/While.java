
package com.hanlin.fadp.minilang.method.envops;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.minilang.method.conditional.Conditional;
import com.hanlin.fadp.minilang.method.conditional.ConditionalFactory;
import com.hanlin.fadp.minilang.method.envops.Break.BreakElementException;
import com.hanlin.fadp.minilang.method.envops.Continue.ContinueElementException;
import org.w3c.dom.Element;

/**
 * Implements the &lt;while&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cwhile%3E}}">Mini-language Reference</a>
 */
public final class While extends MethodOperation {

    private final Conditional condition;
    private final List<MethodOperation> thenSubOps;

    public While(Element element, SimpleMethod simpleMethod) throws MiniLangException {
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

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        while (condition.checkCondition(methodContext)) {
            try {
                for (MethodOperation methodOperation : thenSubOps) {
                    if (!methodOperation.exec(methodContext)) {
                        return false;
                    }
                }
            } catch (MiniLangException e) {
                if (e instanceof BreakElementException) {
                    break;
                }
                if (e instanceof ContinueElementException) {
                    continue;
                }
                throw e;
            }
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        for (MethodOperation method : this.thenSubOps) {
            method.gatherArtifactInfo(aic);
        }
    }

    @Override
    public String toString() {
        StringBuilder messageBuf = new StringBuilder();
        this.condition.prettyPrint(messageBuf, null);
        return "<while><condition>" + messageBuf + "</condition></while>";
    }

    /**
     * A factory for the &lt;while&gt; element.
     * 
     * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cwhile%3E}}">Mini-language Reference</a>
     */
    public static final class WhileFactory implements Factory<While> {
        @Override
        public While createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new While(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "while";
        }
    }
}
