
package com.hanlin.fadp.webapp.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;
import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.webapp.control.ConfigXMLReader;

/**
 * BsfEventHandler - BSF Event Handler
 */
public class BsfEventHandler implements EventHandler {

    public static final String module = BsfEventHandler.class.getName();
    private static final UtilCache<String, String> eventCache = UtilCache.createUtilCache("webapp.BsfEvents");

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws EventHandlerException {
    }

    /**
     * @see com.hanlin.fadp.webapp.event.EventHandler#invoke(ConfigXMLReader.Event, ConfigXMLReader.RequestMap, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(ConfigXMLReader.Event event, ConfigXMLReader.RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null)
            cl = this.getClass().getClassLoader();

        if (context == null) {
            throw new EventHandlerException("Problem getting ServletContext");
        }

        try {
            // create the BSF manager
            BSFManager bsfManager = new BSFManager();
            bsfManager.setClassLoader(cl);

            // expose the event objects to the script
            bsfManager.declareBean("request", request, HttpServletRequest.class);
            bsfManager.declareBean("response", response, HttpServletResponse.class);

            // get the script type
            String scriptType = BSFManager.getLangFromFilename(event.invoke);

            // load the script
            InputStream scriptStream = null;
            String scriptString = null;
            String cacheName = null;
            if (UtilValidate.isEmpty(event.path)) {
                // we are a resource to be loaded off the classpath
                cacheName = event.invoke;
                scriptString = eventCache.get(cacheName);
                if (scriptString == null) {
                    if (Debug.verboseOn()) {
                        Debug.logVerbose("Loading BSF Script at location: " + cacheName, module);
                    }
                    URL scriptUrl = FlexibleLocation.resolveLocation(cacheName);
                    if (scriptUrl == null) {
                        throw new EventHandlerException("BSF script not found at location [" + cacheName + "]");
                    }
                    scriptStream = scriptUrl.openStream();
                    scriptString = IOUtils.getStringFromReader(new InputStreamReader(scriptStream));
                    scriptStream.close();
                    scriptString = eventCache.putIfAbsentAndGet(cacheName, scriptString);
                }
            } else {
                // we are a script in the webapp - load by resource
                cacheName = context.getServletContextName() + ":" + event.path + event.invoke;
                scriptString = eventCache.get(cacheName);
                if (scriptString == null) {
                    scriptStream = context.getResourceAsStream(event.path + event.invoke);
                    if (scriptStream == null) {
                        throw new EventHandlerException("Could not find BSF script file in webapp context: " + event.path + event.invoke);
                    }
                    scriptString = IOUtils.getStringFromReader(new InputStreamReader(scriptStream));
                    scriptStream.close();
                    scriptString = eventCache.putIfAbsentAndGet(cacheName, scriptString);
                }
            }

            // execute the script
            Object result = bsfManager.eval(scriptType, cacheName, 0, 0, scriptString);

            // check the result
            if (result != null && !(result instanceof String)) {
                throw new EventHandlerException("Event did not return a String result, it returned a " + result.getClass().getName());
            }

            return (String) result;
        } catch (BSFException e) {
            throw new EventHandlerException("BSF Error", e);
        } catch (IOException e) {
            throw new EventHandlerException("Problems reading script", e);
        }
    }
}
