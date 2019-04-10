
package com.hanlin.fadp.webapp.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ViewHandler - View Handler Interface
 */
public interface ViewHandler {

    /**
     * Sets the name of the view handler as declared in the controller configuration file.
     * @param name String The name of the view handler as declared in the controller configuration file.
     */
    public void setName(String name);

    /**
     * Gets the name of the view handler as declared in the controller configuration file.
     * @return name String The name of the view handler as declared in the controller configuration file.
     */
    public String getName();

    /**
     * Initializes the handler. Since handlers use the singleton pattern this method should only be called
     * the first time the handler is used.
     *
     * @param context ServletContext This may be needed by the handler in order to lookup properties or XML
     * definition files for rendering pages or handler options.
     * @throws ViewHandlerException
     */
    public void init(ServletContext context) throws ViewHandlerException;

    /**
     * Render the page.
     *
     * @param name The name of the view.
     * @param page The source of the view; could be a page, url, etc depending on the type of handler.
     * @param info An info string attached to this view
     * @param request The HttpServletRequest object used when requesting this page.
     * @param response The HttpServletResponse object to be used to present the page.
     * @throws ViewHandlerException
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException;
}
