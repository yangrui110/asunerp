
package com.hanlin.fadp.minilang.method.entityops;

import java.sql.Timestamp;
import java.util.List;

import com.hanlin.fadp.base.util.UtilDateTime;
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
 * Implements the &lt;filter-list-by-date&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfilterlistbydate%3E}}">Mini-language Reference</a>
 */
public final class FilterListByDate extends MethodOperation {

    private final FlexibleMapAccessor<List<GenericEntity>> listFma;
    private final FlexibleMapAccessor<List<GenericEntity>> toListFma;
    private final FlexibleMapAccessor<Timestamp> validDateFma;
    private final String fromFieldName;
    private final String thruFieldName;

    public FilterListByDate(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "list", "to-list", "valid-date", "fromDate", "thruDate");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "to-list", "valid-date");
            MiniLangValidate.constantAttributes(simpleMethod, element, "fromDate", "thruDate");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        String toListAttribute = element.getAttribute("to-list");
        if (toListAttribute.isEmpty()) {
            toListFma = listFma;
        } else {
            toListFma = FlexibleMapAccessor.getInstance(toListAttribute);
        }
        validDateFma = FlexibleMapAccessor.getInstance(element.getAttribute("valid-date"));
        fromFieldName = MiniLangValidate.checkAttribute(element.getAttribute("from-field-name"), "fromDate");
        thruFieldName = MiniLangValidate.checkAttribute(element.getAttribute("thru-field-name"), "thruDate");
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        if (!validDateFma.isEmpty()) {
            toListFma.put(methodContext.getEnvMap(), EntityUtil.filterByDate(listFma.get(methodContext.getEnvMap()), validDateFma.get(methodContext.getEnvMap()), fromFieldName, thruFieldName, true));
        } else {
            toListFma.put(methodContext.getEnvMap(), EntityUtil.filterByDate(listFma.get(methodContext.getEnvMap()), UtilDateTime.nowTimestamp(), fromFieldName, thruFieldName, true));
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<filter-list-by-date ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        sb.append("to-list=\"").append(this.toListFma).append("\" ");
        sb.append("valid-date=\"").append(this.validDateFma).append("\" ");
        sb.append("from-field-name=\"").append(this.fromFieldName).append("\" ");
        sb.append("thru-field-name=\"").append(this.thruFieldName).append("\" ");
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;filter-list-by-date&gt; element.
     */
    public static final class FilterListByDateFactory implements Factory<FilterListByDate> {
        @Override
        public FilterListByDate createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FilterListByDate(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "filter-list-by-date";
        }
    }
}
