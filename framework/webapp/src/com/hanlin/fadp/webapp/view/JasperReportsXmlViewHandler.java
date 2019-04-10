
package com.hanlin.fadp.webapp.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.transaction.TransactionFactoryLoader;
import com.hanlin.fadp.webapp.control.ContextFilter;
import com.hanlin.fadp.entity.Delegator;

/**
 * Handles JasperReports PDF view rendering
 */
public class JasperReportsXmlViewHandler extends AbstractViewHandler {

    public static final String module = JasperReportsXmlViewHandler.class.getName();

    protected ServletContext context;

    public void init(ServletContext context) throws ViewHandlerException {
        this.context = context;
    }

    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet

        if (request == null) {
            throw new ViewHandlerException("The HttpServletRequest object was null, how did that happen?");
        }
        if (UtilValidate.isEmpty(page)) {
            throw new ViewHandlerException("View page was null or empty, but must be specified");
        }
        if (UtilValidate.isEmpty(info)) {
            Debug.logWarning("View info string was null or empty, but must be used to specify an Entity that is mapped to the Entity Engine datasource that the report will use.", module);
        }

        // tell the ContextFilter we are forwarding
        request.setAttribute(ContextFilter.FORWARDED_FROM_SERVLET, Boolean.valueOf(true));

        Delegator delegator = (Delegator) request.getAttribute("delegator");

        if (delegator == null) {
            throw new ViewHandlerException("The delegator object was null, how did that happen?");
        }

        try {
            String datasourceName = delegator.getEntityHelperName(info);
            InputStream is = context.getResourceAsStream(page);
            Map parameters = UtilHttp.getParameterMap(request);

            JasperReport report = JasperCompileManager.compileReport(is);

            response.setContentType("text/xml");

            PipedOutputStream fillToPrintOutputStream = new PipedOutputStream();
            PipedInputStream fillToPrintInputStream = new PipedInputStream(fillToPrintOutputStream);

            if (UtilValidate.isNotEmpty(datasourceName)) {
                JasperFillManager.fillReportToStream(report, fillToPrintOutputStream, parameters, TransactionFactoryLoader.getInstance().getConnection(new GenericHelperInfo(null, datasourceName)));
            } else {
                JasperFillManager.fillReportToStream(report, fillToPrintOutputStream, parameters, new JREmptyDataSource());
            }
            JasperExportManager.exportReportToXmlStream(fillToPrintInputStream, response.getOutputStream());
        } catch (IOException ie) {
            throw new ViewHandlerException("IO Error in region", ie);
        } catch (java.sql.SQLException e) {
            throw new ViewHandlerException("Database error while running report", e);
        } catch (Exception e) {
            throw new ViewHandlerException("Error in report", e);
            // } catch (ServletException se) {
            // throw new ViewHandlerException("Error in region", se.getRootCause());
        }
    }
}
