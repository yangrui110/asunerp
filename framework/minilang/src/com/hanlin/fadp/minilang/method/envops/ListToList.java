
package com.hanlin.fadp.minilang.method.envops;

import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;list-to-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Clisttolist%3E}}">Mini-language Reference</a>
 */
public final class ListToList extends MethodOperation {

    private final FlexibleMapAccessor<List<Object>> listFma;
    private final FlexibleMapAccessor<List<Object>> toListFma;

    public ListToList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "to-list", "list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "to-list", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "to-list", "list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        toListFma = FlexibleMapAccessor.getInstance(element.getAttribute("to-list"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        List<Object> fromList = listFma.get(methodContext.getEnvMap());
        if (fromList != null) {
            List<Object> toList = toListFma.get(methodContext.getEnvMap());
            if (toList == null) {
                toList = new LinkedList<Object>();
                toListFma.put(methodContext.getEnvMap(), toList);
            }
            toList.addAll(fromList);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<list-to-list ");
        sb.append("to-list=\"").append(this.toListFma).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;list-to-list&gt; element.
     */
    public static final class ListToListFactory implements Factory<ListToList> {
        @Override
        public ListToList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ListToList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "list-to-list";
        }
    }
}
