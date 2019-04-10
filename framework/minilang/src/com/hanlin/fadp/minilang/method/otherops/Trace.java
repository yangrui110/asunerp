
package com.hanlin.fadp.minilang.method.otherops;

import java.util.Collections;
import java.util.List;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;trace&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Ctrace%3E}}">Mini-language Reference</a>
 */
public final class Trace extends MethodOperation {

    public static final String module = Trace.class.getName();

    private final int level;
    private final List<MethodOperation> methodOperations;

    public Trace(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "level");
            MiniLangValidate.constantAttributes(simpleMethod, element, "level");
        }
        String levelAttribute = MiniLangValidate.checkAttribute(element.getAttribute("level"), "info");
        Integer levelInt = Debug.getLevelFromString(levelAttribute);
        if (levelInt == null) {
            MiniLangValidate.handleError("Invalid level attribute", simpleMethod, element);
            this.level = Debug.INFO;
        } else {
            this.level = levelInt.intValue();
        }
        methodOperations = Collections.unmodifiableList(SimpleMethod.readOperations(element, simpleMethod));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        methodContext.setTraceOn(this.level);
        outputTraceMessage(methodContext, "Trace on.");
        try {
            return SimpleMethod.runSubOps(methodOperations, methodContext);
        } finally {
            methodContext.setTraceOff();
            outputTraceMessage(methodContext, "Trace off.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<trace ");
        sb.append("level=\"").append(Log.LEVEL_ARRAY[this.level]).append("\" >");
        return sb.toString();
    }

    /**
     * A factory for the &lt;trace&gt; element.
     */
    public static final class TraceFactory implements Factory<Trace> {
        @Override
        public Trace createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new Trace(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "trace";
        }
    }
}
