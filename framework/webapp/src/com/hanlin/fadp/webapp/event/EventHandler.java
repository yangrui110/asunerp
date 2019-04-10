
package com.hanlin.fadp.webapp.event;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.webapp.control.ConfigXMLReader.Event;
import com.hanlin.fadp.webapp.control.ConfigXMLReader.RequestMap;

/**
 * EventHandler - Event Handler Interface
 */

public interface EventHandler {

    /**
     * Initializes the handler. Since handlers use the singleton pattern this method should only be called
     * the first time the handler is used.
     *
     * @param context ServletContext This may be needed by the handler in order to lookup properties or XML
     * definition files for rendering pages or handler options.
     * @throws EventHandlerException
     */
    public void init(ServletContext context) throws EventHandlerException;

    /**
     * @Title: invoke 
     * @Description: 调用web的event处理方法
     * @param event Event对象
     * @param requestMap 请求map，为controller.xml中调用事件所在的request-map节点的解析对象
     * @param request HttpServeletRequset对象，即servlet请求对象
     * @param response HttpServletResponse对象，即servlet应答对象
     * @throws EventHandlerException
     * @return: String
     */
    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException;
}
