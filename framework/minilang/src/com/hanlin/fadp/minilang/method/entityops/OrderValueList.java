
package com.hanlin.fadp.minilang.method.entityops;

import java.util.List;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.entity.util.EntityUtil;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;order-value-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cordervaluelist%3E}}">Mini-language Reference</a>
 */
public final class OrderValueList extends MethodOperation {

    private final FlexibleMapAccessor<List<? extends GenericEntity>> listFma;
    private final FlexibleMapAccessor<List<String>> orderByListFma;
    private final FlexibleMapAccessor<List<? extends GenericEntity>> toListFma;

    public OrderValueList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "order-by-list", "to-list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list", "order-by-list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "order-by-list", "to-list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        orderByListFma = FlexibleMapAccessor.getInstance(element.getAttribute("order-by-list"));
        String toListAttribute = element.getAttribute("to-list");
        if (toListAttribute.isEmpty()) {
            toListFma = listFma;
        } else {
            toListFma = FlexibleMapAccessor.getInstance(toListAttribute);
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        List<String> orderByList = orderByListFma.get(methodContext.getEnvMap());
        toListFma.put(methodContext.getEnvMap(), EntityUtil.orderBy(listFma.get(methodContext.getEnvMap()), orderByList));
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<order-value-list ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        sb.append("order-by-list=\"").append(this.orderByListFma).append("\" ");
        sb.append("to-list=\"").append(this.toListFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;order-value-list&gt; element.
     */
    public static final class OrderValueListFactory implements Factory<OrderValueList> {
        @Override
        public OrderValueList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new OrderValueList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "order-value-list";
        }
    }
}
