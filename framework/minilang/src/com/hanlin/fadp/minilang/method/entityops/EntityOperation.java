
package com.hanlin.fadp.minilang.method.entityops;

import com.hanlin.fadp.base.util.string.FlexibleStringExpander;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.minilang.method.MethodContext;
import com.hanlin.fadp.minilang.method.MethodOperation;
import org.w3c.dom.Element;

/**
 * An abstract base class for entity operations.
 */
public abstract class EntityOperation extends MethodOperation {

    private final FlexibleStringExpander delegatorNameFse;

    public EntityOperation(Element element, SimpleMethod simpleMethod) throws MiniLangException {
        super(element, simpleMethod);
        this.delegatorNameFse = FlexibleStringExpander.getInstance(element.getAttribute("delegator-name"));
    }

    protected final Delegator getDelegator(MethodContext methodContext) {
        String delegatorName = delegatorNameFse.expandString(methodContext.getEnvMap());
        if (!delegatorName.isEmpty()) {
            return DelegatorFactory.getDelegator(delegatorName);
        }
        return methodContext.getDelegator();
    }
}
