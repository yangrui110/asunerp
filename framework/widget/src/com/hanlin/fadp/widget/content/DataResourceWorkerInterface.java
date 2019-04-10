
package com.hanlin.fadp.widget.content;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.entity.Delegator;

/**
 * ContentWorkerInterface
 */
public interface DataResourceWorkerInterface {
    public String renderDataResourceAsTextExt(Delegator delegator, String dataResourceId, Map<String, Object> templateContext,
            Locale locale, String targetMimeTypeId, boolean cache) throws GeneralException, IOException;

    public void renderDataResourceAsTextExt(Delegator delegator, String dataResourceId, Appendable out, Map<String, Object> templateContext,
            Locale locale, String targetMimeTypeId, boolean cache) throws GeneralException, IOException;
}
