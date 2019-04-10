
package com.hanlin.fadp.entityext;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ServiceContainer;

/**
 * EntityEcaUtil
 */
public class EntityServiceFactory {

    public static final String module = EntityServiceFactory.class.getName();

    public static LocalDispatcher getLocalDispatcher(Delegator delegator) {
        LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher("entity-" + delegator.getDelegatorName(), delegator);
        return dispatcher;
    }

    public static DispatchContext getDispatchContext(Delegator delegator) {
        LocalDispatcher dispatcher = getLocalDispatcher(delegator);
        if (dispatcher == null) return null;
        return dispatcher.getDispatchContext();
    }
}
