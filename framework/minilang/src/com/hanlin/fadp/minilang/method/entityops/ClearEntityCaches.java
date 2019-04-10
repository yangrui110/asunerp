
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.MiniLangValidate;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * Implements the &lt;clear-entity-caches&gt; element.
 * 
 * @see <a href="https://cwiki.apache.org/confluence/display/OFBADMIN/Mini-language+Reference#Mini-languageReference-{{%3Cclearentitycaches%3E}}">Mini-language Reference</a>
 */
public final class ClearEntityCaches extends EntityOperation {

    public ClearEntityCaches(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        if (MiniLangValidate.validationOn()) {
            MiniLangValidate.attributeNames(simpleMethod, element, "delegator-name");
            MiniLangValidate.expressionAttributes(simpleMethod, element, "delegator-name");
            MiniLangValidate.noChildElements(simpleMethod, element);
        }
    }

    @Override
    public boolean exec(MethodContext methodContext) throws MiniLangException {
        Delegator delegator = getDelegator(methodContext);
        delegator.clearAllCaches();
        return true;
    }

    @Override
    public String toString() {
        return "<clear-entity-caches/>";
    }

    /**
     * A factory for the &lt;clear-entity-caches&gt; element.
     */
    public static final class ClearEntityCachesFactory implements Factory<ClearEntityCaches> {
        @Override
        public ClearEntityCaches createMethodOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
            return new ClearEntityCaches(element, simpleMethod);
        }

        @Override
        public String getName() {
            return "clear-entity-caches";
        }
    }
}
