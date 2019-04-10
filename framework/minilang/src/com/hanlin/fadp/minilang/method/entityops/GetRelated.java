
package com.hanlin.fadp.minilang.method.entityops;

import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * Implements the &lt;get-related&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cgetrelated%3E}}">Mini-language Reference</a>
 */
public final class GetRelated extends MethodOperation {

    public static final String module = GetRelated.class.getName();

    private final FlexibleMapAccessor<Object> listFma;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleMapAccessor<List<String>> orderByListFma;
    private final FlexibleStringExpander relationNameFse;
    private final FlexibleStringExpander useCacheFse;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public GetRelated(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "relation-name", "list", "map", "order-by-list", "use-cache");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "relation-name", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "list", "map", "order-by-list");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        relationNameFse = FlexibleStringExpander.getInstance(element.getAttribute("relation-name"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
        orderByListFma = FlexibleMapAccessor.getInstance(element.getAttribute("order-by-list"));
        useCacheFse = FlexibleStringExpander.getInstance(element.getAttribute("use-cache"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        GenericValue value = valueFma.get(methodContext.getEnvMap());
        if (value == null) {
            throw new MiniLangRuntimeException("Entity value not found with name: " + valueFma, this);
        }
        String relationName = relationNameFse.expandString(methodContext.getEnvMap());
        boolean useCache = "true".equals(useCacheFse.expandString(methodContext.getEnvMap()));
        List<String> orderByNames = orderByListFma.get(methodContext.getEnvMap());
        Map<String, ? extends Object> constraintMap = mapFma.get(methodContext.getEnvMap());
        try {
            listFma.put(methodContext.getEnvMap(), value.getRelated(relationName, constraintMap, orderByNames, useCache));
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while finding related values: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(relationNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<get-related ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        sb.append("relation-name=\"").append(this.relationNameFse).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        if (!mapFma.isEmpty()) {
            sb.append("map=\"").append(this.mapFma).append("\" ");
        }
        if (!orderByListFma.isEmpty()) {
            sb.append("order-by-list=\"").append(this.orderByListFma).append("\" ");
        }
        if (!useCacheFse.isEmpty()) {
            sb.append("use-cache=\"").append(this.useCacheFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;get-related&gt; element.
     */
    public static final class GetRelatedFactory implements Factory<GetRelated> {
        @Override
        public GetRelated createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new GetRelated(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "get-related";
        }
    }
}
