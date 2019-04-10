
package com.hanlin.fadp.webapp.view;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.HttpClient;
import com.hanlin.fadp.base.util.HttpClientException;
import com.hanlin.fadp.base.util.UtilValidate;

/**
 * ViewHandlerException - View Handler Exception
 */
public class HttpViewHandler extends AbstractViewHandler {

    public static final String module = HttpViewHandler.class.getName();

    public void init(ServletContext context) throws ViewHandlerException {
    }

    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet

        if (request == null)
            throw new ViewHandlerException("Null HttpServletRequest object");
        if (UtilValidate.isEmpty(page))
            throw new ViewHandlerException("Null or empty source");

        if (Debug.infoOn()) Debug.logInfo("Retreiving HTTP resource at: " + page, module);
        try {
            HttpClient httpClient = new HttpClient(page);
            String pageText = httpClient.get();

            // TODO: parse page and remove harmful tags like <HTML>, <HEAD>, <BASE>, etc - look into the OpenSymphony piece for an example
            response.getWriter().print(pageText);
        } catch (IOException e) {
            throw new ViewHandlerException("IO Error in view", e);
        } catch (HttpClientException e) {
            throw new ViewHandlerException(e.getNonNestedMessage(), e.getNested());
        }
    }
}
