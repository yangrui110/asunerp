
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
 * Implements the &lt;field-to-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfieldtolist%3E}}">Mini-language Reference</a>
*/
public final class FieldToList extends MethodOperation {

    private final FlexibleMapAccessor<Object> fieldFma;
    private final FlexibleMapAccessor<List<Object>> listFma;

    public FieldToList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.handleError("<field-to-list> element is deprecated (use <set>)", simpleMethod, element);
            MiniLangValidate.attributeNames(simpleMethod, element, "field", "list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "field", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "field", "list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        fieldFma = FlexibleMapAccessor.getInstance(element.getAttribute("field"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Object fieldVal = fieldFma.get(methodContext.getEnvMap());
        if (fieldVal != null) {
            List<Object> toList = listFma.get(methodContext.getEnvMap());
            if (toList == null) {
                toList = new LinkedList<Object>();
                listFma.put(methodContext.getEnvMap(), toList);
            }
            toList.add(fieldVal);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<field-to-list ");
        sb.append("field=\"").append(this.fieldFma).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;field-to-list&gt; element.
     */
    public static final class FieldToListFactory implements Factory<FieldToList> {
        @Override
        public FieldToList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FieldToList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "field-to-list";
        }
    }
}
