
package com.hanlin.fadp.minilang.method.entityops;

import java.util.List;
import java.util.Map;

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
 * Implements the &lt;filter-list-by-and&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfilterlistbyand%3E}}">Mini-language Reference</a>
 */
public final class FilterListByAnd extends MethodOperation {

    private final FlexibleMapAccessor<List<GenericEntity>> listFma;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleMapAccessor<List<GenericEntity>> toListFma;

    public FilterListByAnd(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "map", "to-list");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list", "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "map", "to-list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        String toListAttribute = element.getAttribute("to-list");
        if (toListAttribute.isEmpty()) {
            toListFma = listFma;
        } else {
            toListFma = FlexibleMapAccessor.getInstance(toListAttribute);
        }
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Map<String, ? extends Object> theMap = mapFma.get(methodContext.getEnvMap());
        toListFma.put(methodContext.getEnvMap(), EntityUtil.filterByAnd(listFma.get(methodContext.getEnvMap()), theMap));
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<filter-list-by-and ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        sb.append("to-list=\"").append(this.toListFma).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;filter-list-by-and&gt; element.
     */
    public static final class FilterListByAndFactory implements Factory<FilterListByAnd> {
        @Override
        public FilterListByAnd createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FilterListByAnd(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "filter-list-by-and";
        }
    }
}
