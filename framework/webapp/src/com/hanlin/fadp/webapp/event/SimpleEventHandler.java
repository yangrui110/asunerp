
package com.hanlin.fadp.webapp.event;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.minilang.MiniLangException;
import com.hanlin.fadp.minilang.SimpleMethod;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.RequestMap;

/**
 * SimpleEventHandler - Simple Event Mini-Lang Handler
 */
public class SimpleEventHandler implements EventHandler {

    public static final String module = SimpleEventHandler.class.getName();
    /** Contains the property file name for translation of error messages. */
    public static final String err_resource = "WebappUiLabels";

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws EventHandlerException {
    }

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#invoke(ConfigXMLReader.Event, ConfigXMLReader.RequestMap, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        String xmlResource = event.path;
        String eventName = event.invoke;
        Locale locale = UtilHttp.getLocale(request);

        if (Debug.verboseOn()) Debug.logVerbose("[Set path/method]: " + xmlResource + " / " + eventName, module);

        if (xmlResource == null) {
            throw new EventHandlerException("XML Resource (eventPath) cannot be null");
        }
        if (eventName == null) {
            throw new EventHandlerException("Event Name (eventMethod) cannot be null");
        }

        Debug.logVerbose("[Processing]: SIMPLE Event", module);
        try {
            String eventReturn = SimpleMethod.runSimpleEvent(xmlResource, eventName, request, response);
            if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + eventReturn, module);
            return eventReturn;
        } catch (MiniLangException e) {
            Debug.logError(e, module);
            String errMsg = UtilProperties.getMessage(SimpleEventHandler.err_resource, "simpleEventHandler.event_not_completed", (locale != null ? locale : Locale.getDefault())) + ": ";
            request.setAttribute("_ERROR_MESSAGE_", errMsg + e.getMessage());
            return "error";
        }
    }
}
