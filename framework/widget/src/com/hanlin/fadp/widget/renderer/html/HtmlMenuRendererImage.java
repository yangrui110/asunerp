
package com.hanlin.fadp.widget.renderer.html;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.widget.content.WidgetContentWorker;
import com.hanlin.fadp.widget.model.ModelMenuItem;

/**
 * Widget Library - HTML Menu Renderer implementation
 */

public class HtmlMenuRendererImage extends HtmlMenuRenderer {

    protected HtmlMenuRendererImage() {}

    public HtmlMenuRendererImage(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }


    public String buildDivStr(ModelMenuItem menuItem, Map<String, Object> context) throws IOException {

        StringBuilder imgStr = new StringBuilder("<img src=\"");
        String contentId = menuItem.getAssociatedContentId(context);
        Delegator delegator = (Delegator)request.getAttribute("delegator");
        GenericValue webSitePublishPoint = null;
                //Debug.logInfo("in HtmlMenuRendererImage, contentId:" + contentId,"");
        try {
            if (WidgetContentWorker.contentWorker != null) {
                webSitePublishPoint = WidgetContentWorker.contentWorker.getWebSitePublishPointExt(delegator, contentId, false);
            } else {
                Debug.logError("Not rendering image because can't get WebSitePublishPoint, not ContentWorker found.", module);
            }
        } catch (GenericEntityException e) {
                //Debug.logInfo("in HtmlMenuRendererImage, GEException:" + e.getMessage(),"");
            throw new RuntimeException(e.getMessage());
        }
        String medallionLogoStr = webSitePublishPoint.getString("medallionLogo");
        StringWriter buf = new StringWriter();
        appendContentUrl(buf, medallionLogoStr);
        imgStr.append(buf.toString());
                //Debug.logInfo("in HtmlMenuRendererImage, imgStr:" + imgStr,"");
        String cellWidth = menuItem.getCellWidth();
        imgStr.append("\"");
        if (UtilValidate.isNotEmpty(cellWidth))
            imgStr.append(" width=\"").append(cellWidth).append("\" ");

        imgStr.append(" border=\"0\" />");
        return imgStr.toString();
    }

}
