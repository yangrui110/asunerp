
package com.hanlin.fadp.webapp.website;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;

/**
 * WebSiteWorker - Worker class for web site related functionality
 */
public class WebSiteWorker {

    public static final String module = WebSiteWorker.class.getName();

    public static String getWebSiteId(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));

        if (application == null) return null;
        return application.getInitParameter("webSiteId");
    }

    public static GenericValue getWebSite(ServletRequest request) {
        String webSiteId = getWebSiteId(request);
        if (webSiteId == null) {
            return null;
        }

        return findWebSite((Delegator) request.getAttribute("delegator"), webSiteId);
    }

    /**
     * returns a WebSite-GenericValue (using entityCache)
     *
     * @param delegator
     * @param webSiteId
     * @return
     */
    public static GenericValue findWebSite(Delegator delegator, String webSiteId) {
        return findWebSite(delegator, webSiteId, true);
    }

    /**
     * returns a WebSite-GenericValue
     *
     * @param delegator
     * @param webSiteId
     * @param useCache
     * @return
     */
    public static GenericValue findWebSite(Delegator delegator, String webSiteId, boolean useCache) {
        GenericValue result = null;
        try {
            result = EntityQuery.use(delegator).from("WebSite").where("webSiteId", webSiteId).cache(useCache).queryOne();
        }
        catch (GenericEntityException e) {
            Debug.logError("Error looking up website with id " + webSiteId, module);
        }
        return result;
    }
}
