
package com.hanlin.fadp.minilang.method.entityops;

import java.util.Map;

import com.hanlin.fadp.base.util.collections.FlexibleMapAccessor;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangRuntimeException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;make-value&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cmakevalue%3E}}">Mini-language Reference</a>
 */
public final class MakeValue extends EntityOperation {

    private final FlexibleStringExpander entityNameFse;
    private final FlexibleMapAccessor<Map<String, ? extends Object>> mapFma;
    private final FlexibleMapAccessor<GenericValue> valueFma;

    public MakeValue(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "value-field", "entity-name", "map", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "value-field", "entity-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "map", "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
        valueFma = FlexibleMapAccessor.getInstance(element.getAttribute("value-field"));
        entityNameFse = FlexibleStringExpander.getInstance(element.getAttribute("entity-name"));
        mapFma = FlexibleMapAccessor.getInstance(element.getAttribute("map"));
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        String entityName = entityNameFse.expandString(methodContext.getEnvMap());
        if (entityName.isEmpty()) {
            throw new MiniLangRuntimeException("Entity name not found.", this);
        }
        Delegator delegator = getDelegator(methodContext);
        valueFma.put(methodContext.getEnvMap(), delegator.makeValidValue(entityName, mapFma.get(methodContext.getEnvMap())));
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(entityNameFse.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<make-value ");
        sb.append("entity-name=\"").append(this.entityNameFse).append("\" ");
        sb.append("value-field=\"").append(this.valueFma).append("\" ");
        if (!mapFma.isEmpty()) {
            sb.append("map=\"").append(this.mapFma).append("\" ");
        }
        sb.append("/>");
        return sb.toString();
    }

    /**
     * A factory for the &lt;make-value&gt; element.
     */
    public static final class MakeValueFactory implements Factory<MakeValue> {
        @Override
        public MakeValue createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new MakeValue(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "make-value";
        }
    }
}
