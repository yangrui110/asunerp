
package com.hanlin.fadp.minilang.method.envops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.collections.MapComparator;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;order-map-list&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cordermaplist%3E}}">Mini-language Reference</a>
 */
public final class OrderMapList extends MethodOperation {

    private final FlexibleMapAccessor<List<Map<Object, Object>>> listFma;
    private final MapComparator mc;

    public OrderMapList(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list");
            MiniLangValidate.childElements(simpleMethod, element, "order-by");
            MiniLangValidate.requiredChildElements(simpleMethod, element, "order-by");
        }
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        List<? extends Element> orderByElements = UtilXml.childElementList(element, "order-by");
        if (orderByElements.size() > 0) {
            ArrayList<FlexibleMapAccessor<String>> orderByList = new ArrayList<FlexibleMapAccessor<String>>(orderByElements.size());
            for (Element orderByElement : orderByElements) {
                FlexibleMapAccessor<String> fma = FlexibleMapAccessor.getInstance(orderByElement.getAttribute("field"));
                orderByList.add(fma);
            }
            mc = new MapComparator(orderByList);
        } else {
            mc = null;
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (mc == null) {
            throw new MiniLangRuntimeException("order-by sub-elements not found.", this);
        }
        List<Map<Object, Object>> orderList = listFma.get(methodContext.getEnvMap());
        if (orderList != null) {
            Collections.sort(orderList, mc);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<order-map-list ");
        sb.append("list=\"").append(this.listFma).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;order-map-list&gt; element.
     */
    public static final class OrderMapListFactory implements Factory<OrderMapList> {
        @Override
        public OrderMapList createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new OrderMapList(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "order-map-list";
        }
    }
}
