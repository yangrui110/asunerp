
package com.hanlin.fadp.widget.portal;

import java.io.IOException;
import java.util.Map;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;

/**
 * PortalPageWorkerInterface
 */
public interface PortalPageWorkerInterface {
    public String renderPortalPageAsTextExt(Delegator delegator, String portalPageId, Map<String, Object> templateContext,
            boolean cache) throws GeneralException, IOException;
}
