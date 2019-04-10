
package com.hanlin.fadp.widget.renderer.fo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.pdf.PDFEncryptionParams;
import org.apache.fop.render.pdf.PDFEncryptionOption;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilCodec;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.util.EntityUtilProperties;
import com.hanlin.fadp.webapp.view.AbstractViewHandler;
import com.hanlin.fadp.webapp.view.ApacheFopWorker;
import com.hanlin.fadp.webapp.view.ViewHandlerException;
import com.hanlin.fadp.widget.renderer.FormStringRenderer;
import com.hanlin.fadp.widget.renderer.ScreenRenderer;
import com.hanlin.fadp.widget.renderer.ScreenStringRenderer;
import com.hanlin.fadp.widget.renderer.html.HtmlScreenRenderer;
import com.hanlin.fadp.widget.renderer.macro.MacroFormRenderer;
import com.hanlin.fadp.widget.renderer.macro.MacroScreenRenderer;

/**
 * Uses XSL-FO formatted templates to generate PDF, PCL, POSTSCRIPT etc.  views
 * This handler will use JPublish to generate the XSL-FO
 */
public class ScreenFopViewHandler extends AbstractViewHandler {
    public static final String module = ScreenFopViewHandler.class.getName();
    protected static final String DEFAULT_ERROR_TEMPLATE = "component://common/widget/CommonScreens.xml#FoError";

    protected ServletContext servletContext = null;

    /**
     * @see com.hanlin.fadp.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    @Override
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    /**
     * @see com.hanlin.fadp.webapp.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenStringRenderer screenStringRenderer = new MacroScreenRenderer(EntityUtilProperties.getPropertyValue("widget", getName() + ".name", delegator), EntityUtilProperties.getPropertyValue("widget", getName() + ".screenrenderer", delegator));
            FormStringRenderer formStringRenderer = new MacroFormRenderer(EntityUtilProperties.getPropertyValue("widget", getName() + ".formrenderer", delegator), request, response);
            // TODO: uncomment these lines when the renderers are implemented
            //TreeStringRenderer treeStringRenderer = new MacroTreeRenderer(UtilProperties.getPropertyValue("widget", getName() + ".treerenderer"), writer);
            //MenuStringRenderer menuStringRenderer = new MacroMenuRenderer(UtilProperties.getPropertyValue("widget", getName() + ".menurenderer"), writer);
            ScreenRenderer screens = new ScreenRenderer(writer, null, screenStringRenderer);
            screens.populateContextForRequest(request, response, servletContext);

            // this is the object used to render forms from their definitions
            screens.getContext().put("formStringRenderer", formStringRenderer);
            screens.getContext().put("simpleEncoder", UtilCodec.getEncoder(EntityUtilProperties.getPropertyValue("widget", getName() + ".encoder", delegator)));
            screens.render(page);
        } catch (Exception e) {
            renderError("Problems with the response writer/output stream", e, "[Not Yet Rendered]", request, response);
            return;
        }

        // set the input source (XSL-FO) and generate the output stream of contentType
        String screenOutString = writer.toString();
        if (!screenOutString.startsWith("<?xml")) {
            screenOutString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + screenOutString;
        }
        if (Debug.verboseOn()) Debug.logVerbose("XSL:FO Screen Output: " + screenOutString, module);

        if (UtilValidate.isEmpty(contentType)) {
            contentType = UtilProperties.getPropertyValue("widget", getName() + ".default.contenttype");
        }
        
        // get encryption related parameters
        FOUserAgent foUserAgent = null;
        String userPassword = request.getParameter("userPassword");
        String ownerPassword = request.getParameter("ownerPassword");
        boolean allowPrint = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowPrint")) ? ApacheFopWorker.allowPrintDefault : request.getParameter("allowPrint"));
        boolean allowCopyContent = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowCopyContent")) ? ApacheFopWorker.allowCopyContentDefault : request.getParameter("allowCopyContent"));
        boolean allowEditContent = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowEditContent")) ? ApacheFopWorker.allowEditContentDefault : request.getParameter("allowEditContent"));
        boolean allowEditAnnotations = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowEditAnnotations")) ? ApacheFopWorker.allowEditAnnotationsDefault : request.getParameter("allowEditAnnotations"));
        if (UtilValidate.isNotEmpty(userPassword) || UtilValidate.isNotEmpty(ownerPassword) || !allowPrint || !allowCopyContent || allowEditContent || !allowEditAnnotations) {
            int encryptionLength = 128;
            try {
                encryptionLength = Integer.parseInt(request.getParameter("encryption-length"));
            } catch (NumberFormatException e) {
                try {
                    encryptionLength = Integer.parseInt(ApacheFopWorker.encryptionLengthDefault);
                } catch (NumberFormatException e1) {
                    // ignore
                }
            }
            
            boolean encryptMetadata = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("encrypt-metadata")) ? ApacheFopWorker.encryptMetadataDefault : request.getParameter("encrypt-metadata"));
            boolean allowFillInForms = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowFillInForms")) ? ApacheFopWorker.allowFillInFormsDefault : request.getParameter("allowFillInForms"));
            boolean allowAccessContent = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowAccessContent")) ? ApacheFopWorker.allowAccessContentDefault : request.getParameter("allowAccessContent"));
            boolean allowAssembleDocument = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowAssembleDocument")) ? ApacheFopWorker.allowAssembleDocumentDefault : request.getParameter("allowAssembleDocument"));
            boolean allowPrintHq = Boolean.parseBoolean(UtilValidate.isEmpty(request.getParameter("allowPrintHq")) ? ApacheFopWorker.allowPrintHqDefault : request.getParameter("allowPrintHq"));
            
            FopFactory fopFactory = ApacheFopWorker.getFactoryInstance();
            foUserAgent = fopFactory.newFOUserAgent();
            PDFEncryptionParams pdfEncryptionParams = new PDFEncryptionParams(userPassword, ownerPassword, allowPrint, allowCopyContent, allowEditContent, allowEditAnnotations, encryptMetadata);
            pdfEncryptionParams.setAllowFillInForms(allowFillInForms);
            pdfEncryptionParams.setAllowAccessContent(allowAccessContent);
            pdfEncryptionParams.setAllowAssembleDocument(allowAssembleDocument);
            pdfEncryptionParams.setAllowPrintHq(allowPrintHq);
            pdfEncryptionParams.setEncryptionLengthInBits(encryptionLength);
            foUserAgent.getRendererOptions().put(PDFEncryptionOption.ENCRYPTION_PARAMS, pdfEncryptionParams);
        }
        
        Reader reader = new StringReader(screenOutString);
        StreamSource src = new StreamSource(reader);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Fop fop = ApacheFopWorker.createFopInstance(out, contentType, foUserAgent);
            ApacheFopWorker.transform(src, null, fop);
        } catch (Exception e) {
            renderError("Unable to transform FO file", e, screenOutString, request, response);
            return;
        }
        // set the content type and length
        response.setContentType(contentType);
        response.setContentLength(out.size());

        // write to the browser
        try {
            out.writeTo(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            renderError("Unable to write to OutputStream", e, screenOutString, request, response);
        }
    }

    protected void renderError(String msg, Exception e, String screenOutString, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Debug.logError(msg + ": " + e + "; Screen XSL:FO text was:\n" + screenOutString, module);
        try {
            Writer writer = new StringWriter();
            ScreenRenderer screens = new ScreenRenderer(writer, null, new HtmlScreenRenderer());
            screens.populateContextForRequest(request, response, servletContext);
            screens.getContext().put("errorMessage", msg + ": " + e);
            screens.render(DEFAULT_ERROR_TEMPLATE);
            response.setContentType("text/html");
            response.getWriter().write(writer.toString());
            writer.close();
        } catch (Exception x) {
            Debug.logError("Multiple errors rendering FOP", module);
            throw new ViewHandlerException("Multiple errors rendering FOP", x);
        }
    }
}
