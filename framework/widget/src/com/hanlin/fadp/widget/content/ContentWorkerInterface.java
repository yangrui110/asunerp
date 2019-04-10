
package com.hanlin.fadp.widget.content;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.service.LocalDispatcher;

/**
 * ContentWorkerInterface
 */
public interface ContentWorkerInterface {

    // helper methods
    public GenericValue getCurrentContentExt(Delegator delegator, List<Map<String, ? extends Object>> trail, GenericValue userLogin, Map<String, Object> ctx, Boolean nullThruDatesOnly, String contentAssocPredicateId)  throws GeneralException;
    public GenericValue getWebSitePublishPointExt(Delegator delegator, String contentId, boolean ignoreCache) throws GenericEntityException;
    public String getMimeTypeIdExt(Delegator delegator, GenericValue view, Map<String, Object> ctx);

    // new rendering methods
    public void renderContentAsTextExt(LocalDispatcher dispatcher, Delegator delegator, String contentId, Appendable out, Map<String, Object> templateContext, Locale locale, String mimeTypeId, boolean cache) throws GeneralException, IOException;
    public String renderContentAsTextExt(LocalDispatcher dispatcher, Delegator delegator, String contentId, Map<String, Object> templateContext, Locale locale, String mimeTypeId, boolean cache) throws GeneralException, IOException;

    public void renderSubContentAsTextExt(LocalDispatcher dispatcher, Delegator delegator, String contentId, Appendable out, String mapKey, Map<String, Object> templateContext, Locale locale, String mimeTypeId, boolean cache) throws GeneralException, IOException;
    public String renderSubContentAsTextExt(LocalDispatcher dispatcher, Delegator delegator, String contentId, String mapKey, Map<String, Object> templateContext, Locale locale, String mimeTypeId, boolean cache) throws GeneralException, IOException;
}
