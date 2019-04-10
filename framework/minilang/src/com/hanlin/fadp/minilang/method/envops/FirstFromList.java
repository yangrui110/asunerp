
package com.hanlin.fadp.minilang.method.envops;

import java.util.List;

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;first-from-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfirstfromlist%3E}}">Mini-language Reference</a>
 */
public final class FirstFromList extends MethodOperation {

    private final FlexibleMapAccessor<Object> entryFma;
    private final FlexibleMapAccessor<List<Object>> listFma;

    public FirstFromList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<first-from-list> element is deprecated (use <set>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "entry", "list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entry", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "entry", "list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        entryFma = FlexibleMapAccessor.getInstance(element.getAttribute("entry"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        List<? extends Object> theList = listFma.get(methodContext.getEnvMap());
        if (UtilValidate.isEmpty(theList)) {
            entryFma.put(methodContext.getEnvMap(), null);
        } else {
            entryFma.put(methodContext.getEnvMap(), theList.get(0));
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<first-from-list ");
        sb.append("entry=\"").append(this.entryFma).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;first-from-list&gt; element.
     */
    public static final class FirstFromListFactory implements Factory<FirstFromList> {
        @Override
        public FirstFromList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FirstFromList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "first-from-list";
        }
    }
}
