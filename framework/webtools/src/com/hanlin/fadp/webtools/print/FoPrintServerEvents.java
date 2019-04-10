
package com.hanlin.fadp.webtools.print;

import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.widget.renderer.ScreenRenderer;
import com.hanlin.fadp.widget.renderer.html.HtmlScreenRenderer;

/**
 * FoPrintServerEvents
 */

public class FoPrintServerEvents {

    public static final String module = FoPrintServerEvents.class.getName();
    private static HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();

    public static String getXslFo(HttpServletRequest req, HttpServletResponse resp) {
        LocalDispatcher dispatcher = (LocalDispatcher) req.getAttribute("dispatcher");
        Map<String, Object> reqParams = UtilHttp.getParameterMap(req);
        reqParams.put("locale", UtilHttp.getLocale(req));

        String screenUri = (String) reqParams.remove("screenUri");
        if (UtilValidate.isNotEmpty(screenUri)) {
            String base64String = null;
            try {
                byte[] bytes = FoPrintServerEvents.getXslFo(dispatcher.getDispatchContext(), screenUri, reqParams);
                base64String = new String(Base64.encodeBase64(bytes));
            } catch (GeneralException e) {
                Debug.logError(e, module);
                try {
                    resp.sendError(500);
                } catch (IOException e1) {
                    Debug.logError(e1, module);
                }
            }
            if (base64String != null) {
                try {
                    Writer out = resp.getWriter();
                    out.write(base64String);
                } catch (IOException e) {
                    try {
                        resp.sendError(500);
                    } catch (IOException e1) {
                        Debug.logError(e1, module);
                    }
                }
            }
        }

        return null;
    }

    public static byte[] getXslFo(DispatchContext dctx, String screen, Map<String, Object> parameters) throws GeneralException {
        // run as the system user
        GenericValue system = null;
        try {
            system = dctx.getDelegator().findOne("UserLogin", false, "userLoginId", "system");
        } catch (GenericEntityException e) {
            throw new GeneralException(e.getMessage(), e);
        }
        parameters.put("userLogin", system);
        if (!parameters.containsKey("locale")) {
            parameters.put("locale", Locale.getDefault());
        }

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenRenderer screens = new ScreenRenderer(writer, null, htmlScreenRenderer);
            screens.populateContextForService(dctx, parameters);
            screens.render(screen);
        } catch (Throwable t) {
            throw new GeneralException("Problems rendering FOP XSL-FO", t);
        }
        return writer.toString().getBytes();
    }
}
