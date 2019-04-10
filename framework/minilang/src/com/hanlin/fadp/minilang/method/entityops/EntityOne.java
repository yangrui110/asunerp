
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.finder.PrimaryKeyFinder;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.artifact.ArtifactInfoContext;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;entity-one&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Centityone%3E}}">Mini-language Reference</a>
 */
public final class EntityOne extends EntityOperation {

    public static final String module = EntityOne.class.getName();

    private final PrimaryKeyFinder finder;

    public EntityOne(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "entity-name", "use-cache", "auto-field-map", "value-field", "delegator-name");
            MiniLangValidate.requiredAttributes(simpleMethod, element, "entity-name", "value-field");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "value-field", "delegator-name");
            MiniLangValidate.childElements(simpleMethod, element, "field-map", "select-field");
        }
        this.finder = new PrimaryKeyFinder(element);
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
        StringBuilder sb = new StringBuilder("<entity-one ");
        sb.append("entity-name=\"").append(this.finder.getEntityName()).append("\" />");
        return sb.toString();
    }

    /**
     * A factory for the &lt;entity-one&gt; element.
     */
    public static final class EntityOneFactory implements Factory<EntityOne> {
        @Override
        public EntityOne createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new EntityOne(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "entity-one";
        }
    }
}
