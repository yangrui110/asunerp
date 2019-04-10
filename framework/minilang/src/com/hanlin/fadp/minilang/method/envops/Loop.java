
package com.hanlin.fadp.minilang.method.envops;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import com.hanlin.fadp.minilang.method.envops.Break.BreakElementException;
import com.hanlin.fadp.minilang.method.envops.Continue.ContinueElementException;
import org.w3c.dom.Element;

/**
 * Implements the &lt;loop&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cloop%3E}}">Mini-language Reference</a>
 */
public final class Loop extends MethodOperation {

    public static final String module = Loop.class.getName();

    private final FlexibleStringExpander countFse;
    private final FlexibleMapAccessor<Integer> fieldFma;
    private final List<MethodOperation> subOps;

    public Loop(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "count", "field");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "count");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "count", "field");
        }
        this.countFse = FlexibleStringExpander.getInstance(element.getAttribute("count"));
        this.fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        this.subOps = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String countStr = this.countFse.expandString(methodContext.getEnvMap());
        int count = 0;
        try {
            count = Double.valueOf(countStr).intValue();
        } catch (NumberFormatException e) {
            throw new MiniLangRuntimeException("Error while converting \"" + countStr + "\" to a number: " + e.getMessage(), this);
        }
        if (count < 0) {
            throw new MiniLangRuntimeException("Unable to execute loop operation because the count is negative: " + countStr, this);
        }
        for (int i = 0; i < count; i++) {
            this.fieldFma.put(methodContext.getEnvMap(), i);
            try {
                for (MethodOperation methodOperation : subOps) {
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
        for (MethodOperation method : this.subOps) {
            method.gatherArtifactInfo(aic);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<loop ");
        if (!this.countFse.isEmpty()) {
            sb.append("count=\"").append(this.countFse).append("\" ");
        }
        if (!this.fieldFma.isEmpty()) {
            sb.append("field=\"").append(this.fieldFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;loop&gt; element.
     */
    public static final class LoopFactory implements Factory<Loop> {
        @Override
        public Loop createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Loop(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "loop";
        }
    }
}
