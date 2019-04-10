
package com.hanlin.fadp.webapp.view;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.webapp.control.ContextFilter;

/**
 * JspViewHandler - Java Server Pages View Handler
 */
public class JspViewHandler extends AbstractViewHandler {

    public static final String module = JspViewHandler.class.getName();

    protected ServletContext context;

    public void init(ServletContext context) throws ViewHandlerException {
        this.context = context;
    }

    public void render(String name, String page, String contentType, String encoding, String info, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet

        if (request == null) {
            throw new ViewHandlerException("Null HttpServletRequest object");
        }
        if (UtilValidate.isEmpty(page)) {
            throw new ViewHandlerException("Null or empty source");
        }

        //Debug.logInfo("Requested Page : " + page, module);
        //Debug.logInfo("Physical Path  : " + context.getRealPath(page));

        // tell the ContextFilter we are forwarding
        request.setAttribute(ContextFilter.FORWARDED_FROM_SERVLET, Boolean.TRUE);
        RequestDispatcher rd = request.getRequestDispatcher(page);

        if (rd == null) {
            Debug.logInfo("HttpServletRequest.getRequestDispatcher() failed; trying ServletContext", module);
            rd = context.getRequestDispatcher(page);
            if (rd == null) {
                Debug.logInfo("ServletContext.getRequestDispatcher() failed; trying ServletContext.getNamedDispatcher(\"jsp\")", module);
                rd = context.getNamedDispatcher("jsp");
                if (rd == null) {
                    throw new ViewHandlerException("Source returned a null dispatcher (" + page + ")");
                }
            }
        }

        try {
            rd.include(request, response);
        } catch (IOException ie) {
            throw new ViewHandlerException("IO Error in view", ie);
        } catch (ServletException e) {
            Throwable throwable = e.getRootCause() != null ? e.getRootCause() : e;

            if (throwable instanceof JspException) {
                JspException jspe = (JspException) throwable;

                throwable = jspe.getCause() != null ? jspe.getCause() : jspe;
            }
            Debug.logError(throwable, "ServletException rendering JSP view", module);
            throw new ViewHandlerException(e.getMessage(), throwable);
        }
    }
}
