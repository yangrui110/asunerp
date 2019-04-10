
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.finder.ByAndFinder;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;entity-and&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Centityand%3E}}">Mini-language Reference</a>
 */
public final class EntityAnd extends EntityOperation {

    public static final String module = EntityAnd.class.getName();

    private final ByAndFinder finder;

    public EntityAnd(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "filter-by-date", "list", "distinct", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "list");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "list");
            MiniLangValidate.childElements(simpleMethod, element, "field-map", "order-by", "limit-range", "limit-view", "use-iterator");
            MiniLangValidate.requiredChildElements(simpleMethod, element, "field-map");
        }
        this.finder = new ByAndFinder(element);
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        try {
            Delegator delegator = getDelegator(methodContext);
            this.finder.runFind(methodContext.getEnvMap(), delegator);
        } catch (GeneralException e) {
            String errMsg = "Exception thrown while performing entity find: " + e.getMessage();
            Debug.logWarning(e, errMsg, module);
            simpleMethod.addErrorMessage(methodContext, errMsg);
            return false;
        }
        return true;
    }

    @Override
    public void gatherArtifactInfo(ArtifactInfoContext aic) {
        aic.addEntityName(this.finder.getEntityName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<entity-and ");
        sb.append("entity-name=\"").append(this.finder.getEntityName()).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;entity-and&gt; element.
     */
    public static final class EntityAndFactory implements Factory<EntityAnd> {
        @Override
        public EntityAnd createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EntityAnd(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "entity-and";
        }
    }
}
