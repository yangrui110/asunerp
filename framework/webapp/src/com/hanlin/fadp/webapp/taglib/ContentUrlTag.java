
package com.hanlin.fadp.webapp.taglib;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.webapp.website.WebSiteWorker;
import com.hanlin.fadp.entity.GenericValue;

/**
 * ContentUrlTag - Creates a URL string prepending the content prefix from url.properties
 */
public class ContentUrlTag {

    public static final String module = ContentUrlTag.class.getName();

    public static void appendContentPrefix(HttpServletRequest request, StringBuilder urlBuffer) {
        try {
            appendContentPrefix(request, (Appendable) urlBuffer);
        } catch (IOException e) {
            throw UtilMisc.initCause(new InternalError(e.getMessage()), e);
        }
    }

    public static void appendContentPrefix(HttpServletRequest request, Appendable urlBuffer) throws IOException {
        if (request == null) {
            Debug.logWarning("Request was null in appendContentPrefix; this probably means this was used where it shouldn't be, like using ofbizContentUrl in a screen rendered through a service; using best-bet behavior: standard prefix from url.properties (no WebSite or security setting known)", module);
            String prefix = UtilProperties.getPropertyValue("url", "content.url.prefix.standard");
            if (prefix != null) {
                urlBuffer.append(prefix.trim());
            }
            return;
        }
        GenericValue webSite = WebSiteWorker.getWebSite(request);
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        boolean isForwardedSecure = UtilValidate.isNotEmpty(forwardedProto) && "HTTPS".equals(forwardedProto.toUpperCase());
        boolean isSecure = request.isSecure() || isForwardedSecure;
        appendContentPrefix(webSite, isSecure, urlBuffer);
    }

    public static void appendContentPrefix(GenericValue webSite, boolean secure, Appendable urlBuffer) throws IOException {
        if (secure) {
            if (webSite != null && UtilValidate.isNotEmpty(webSite.getString("secureContentPrefix"))) {
                urlBuffer.append(webSite.getString("secureContentPrefix").trim());
            } else {
                String prefix = UtilProperties.getPropertyValue("url", "content.url.prefix.secure");
                if (prefix != null) {
                    urlBuffer.append(prefix.trim());
                }
            }
        } else {
            if (webSite != null && UtilValidate.isNotEmpty(webSite.getString("standardContentPrefix"))) {
                urlBuffer.append(webSite.getString("standardContentPrefix").trim());
            } else {
                String prefix = UtilProperties.getPropertyValue("url", "content.url.prefix.standard");
                if (prefix != null) {
                    urlBuffer.append(prefix.trim());
                }
            }
        }
    }

    public static String getContentPrefix(HttpServletRequest request) {
        StringBuilder buf = new StringBuilder();
        ContentUrlTag.appendContentPrefix(request, buf);
        return buf.toString();
    }
}
