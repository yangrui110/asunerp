
package com.hanlin.fadp.webapp.event;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Factory class that provides the proper <code>RequestBodyMapHandler</code> based on the content type of the <code>ServletRequest</code> */
public class RequestBodyMapHandlerFactory {
    private final static Map<String, RequestBodyMapHandler> requestBodyMapHandlers = new HashMap<String, RequestBodyMapHandler>();
    static {
        requestBodyMapHandlers.put("application/json", new JSONRequestBodyMapHandler());
    }

    public static RequestBodyMapHandler getRequestBodyMapHandler(ServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.indexOf(";") != -1) {
            contentType = contentType.substring(0, contentType.indexOf(";"));
        }
        return requestBodyMapHandlers.get(contentType);
    }

    public static Map<String, Object> extractMapFromRequestBody(ServletRequest request) throws IOException {
        Map<String, Object> outputMap = null;
        RequestBodyMapHandler handler = getRequestBodyMapHandler(request);
        if (handler != null) {
            outputMap = handler.extractMapFromRequestBody(request);
        }
        return outputMap;
    }
}
