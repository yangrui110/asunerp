
package com.hanlin.fadp.minilang.method.entityops;

import java.util.Map;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;clear-cache-line&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cclearcacheline%3E}}">Mini-language Reference</a>
 */
public final class ClearCacheLine extends EntityOperation {

    private final FlexibleStringExpander entityNameFse;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;

    public ClearCacheLine(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "map", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "map", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Delegator delegator = getDelegator(methodContext);
        String entityName = entityNameFse.expandString(methodContext.getEnvMap());
        Map<String, ? extends Object> fieldsMap = mapFma.get(methodContext.getEnvMap());
        if (fieldsMap == null) {
            delegator.clearCacheLine(entityName);
        } else {
            delegator.clearCacheLine(entityName, fieldsMap);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<clear-cache-line ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        if (!this.mapFma.isEmpty()) {
            sb.append("map=\"").append(this.mapFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;clear-cache-line&gt; element.
     */
    public static final class ClearCacheLineFactory implements Factory<ClearCacheLine> {
        @Override
        public ClearCacheLine createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ClearCacheLine(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "clear-cache-line";
        }
    }
}
