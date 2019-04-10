
package com.hanlin.fadp.webapp.webdav;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
    /** Called by the the WebDAV servlet to handle a WebDAV request.
     * <p>The <code>HttpServletRequest</code> contains the following attributes:<br/>
     * <table cellspacing="0" cellpadding="2" border="1">
     *   <tr><td>delegator</td><td>A <code>GenericDelgator</code> instance</td></tr>
     *   <tr><td>dispatcher</td><td>A <code>LocalDispatcher</code> instance</td></tr>
     *   <tr><td>security</td><td>A <code>Security</code> instance</td></tr>
     * </table></p>
     */
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ServletException, IOException;
}
