
package com.hanlin.fadp.minilang.method.entityops;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;find-by-and&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cfindbyand%3E}}">Mini-language Reference</a>
 */
public final class FindByAnd extends EntityOperation {

    public static final String module = FindByAnd.class.getName();

    private final FlexibleStringExpander entityNameFse;
    private final FlexibleMapAccessor<Collection<String>> fieldsToSelectListFma;
    private final FlexibleMapAccessor<Object> listFma;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleMapAccessor<List<String>> orderByListFma;
    private final FlexibleStringExpander useCacheFse;
    private final FlexibleStringExpander useIteratorFse;

    public FindByAnd(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "fields-to-select-list", "use-iterator", "list", "map", "order-by-list", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "list", "map");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list", "map", "fields-to-select-list", "order-by-list", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        listFma = FlexibleMapAccessor.getInstance(element.getAttribute("list"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
        orderByListFma = FlexibleMapAccessor.getInstance(element.getAttribute("order-by-list"));
        fieldsToSelectListFma = FlexibleMapAccessor.getInstance(element.getAttribute("fields-to-select-list"));
        useCacheFse = FlexibleStringExpander.getInstance(element.getAttribute("use-cache"));
        useIteratorFse = FlexibleStringExpander.getInstance(element.getAttribute("use-iterator"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String entityName = entityNameFse.expandString(methodContext.getEnvMap());
        boolean useCache = "true".equals(useCacheFse.expandString(methodContext.getEnvMap()));
        boolean useIterator = "true".equals(useIteratorFse.expandString(methodContext.getEnvMap()));
        List<String> orderByNames = orderByListFma.get(methodContext.getEnvMap());
        Delegator delegator = getDelegator(methodContext);
        try {
            EntityCondition whereCond = null;
            Map<String, ? extends Object> fieldMap = mapFma.get(methodContext.getEnvMap());
            if (fieldMap != null) {
                whereCond = EntityCondition.makeCondition(fieldMap);
            }
            if (useIterator) {
                listFma.put(methodContext.getEnvMap(), EntityQuery.use(delegator)
                                                                  .select(UtilMisc.toSet(fieldsToSelectListFma.get(methodContext.getEnvMap())))
                                                                  .from(entityName)
                                                                  .where(whereCond)
                                                                  .orderBy(orderByNames)
                                                                  .queryList());
            } else {
                listFma.put(methodContext.getEnvMap(), EntityQuery.use(delegator)
                                                                  .select(UtilMisc.toSet(fieldsToSelectListFma.get(methodContext.getEnvMap())))
                                                                  .from(entityName)
                                                                  .where(whereCond)
                                                                  .orderBy(orderByNames)
                                                                  .cache(useCache)
                                                                  .queryList());
            }
        } catch (GenericEntityException e) {
            String errMsg = "Exception thrown while performing entity find: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(entityNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<find-by-and ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        sb.append("list=\"").append(this.listFma).append("\" ");
        sb.append("map=\"").append(this.mapFma).append("\" ");
        if (!orderByListFma.isEmpty()) {
            sb.append("order-by-list=\"").append(this.orderByListFma).append("\" ");
        }
        if (!fieldsToSelectListFma.isEmpty()) {
            sb.append("fields-to-select-list=\"").append(this.fieldsToSelectListFma).append("\" ");
        }
        if (!useCacheFse.isEmpty()) {
            sb.append("use-cache=\"").append(this.useCacheFse).append("\" ");
        }
        if (!useIteratorFse.isEmpty()) {
            sb.append("use-iterator=\"").append(this.useIteratorFse).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;find-by-and&gt; element.
     */
    public static final class FindByAndFactory implements Factory<FindByAnd> {
        @Override
        public FindByAnd createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new FindByAnd(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "find-by-and";
        }
    }
}
