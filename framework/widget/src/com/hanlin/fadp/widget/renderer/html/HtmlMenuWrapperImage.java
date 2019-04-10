
package com.hanlin.fadp.widget.renderer.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.widget.model.ModelMenuItem;
import com.hanlin.fadp.widget.renderer.MenuStringRenderer;
import org.xml.sax.SAXException;

/**
 * Widget Library - HTML Menu Wrapper class - makes it easy to do the setup and render of a menu
 */
public class HtmlMenuWrapperImage extends HtmlMenuWrapper {

    public static final String module = HtmlMenuWrapperImage.class.getName();

    protected HtmlMenuWrapperImage() {}

    public HtmlMenuWrapperImage(String resourceName, String menuName, HttpServletRequest request, HttpServletResponse response)
            throws IOException, SAXException, ParserConfigurationException {
        super(resourceName, menuName, request, response);
    }

    @Override
    public MenuStringRenderer getMenuRenderer() {
        return new HtmlMenuRendererImage(request, response);
    }

    @Override
    public void init(String resourceName, String menuName, HttpServletRequest request, HttpServletResponse response)
            throws IOException, SAXException, ParserConfigurationException {

        super.init(resourceName, menuName, request, response);
        //String pubPt = (String)request.getAttribute("pubPt");
        //if (Debug.infoOn()) Debug.logInfo("in init, pubPt:" + pubPt, module);
        Map<String, Object> dummyMap = new HashMap<String, Object>();
        Delegator delegator = (Delegator)request.getAttribute("delegator");
        //if (Debug.infoOn()) Debug.logInfo("in init, delegator:" + delegator, module);
        try {
            for (ModelMenuItem menuItem : modelMenu.getMenuItemList()) {
               String contentId = menuItem.getAssociatedContentId(dummyMap);
               //if (Debug.infoOn()) Debug.logInfo("in init, contentId:" + contentId, module);
               GenericValue webSitePublishPoint = EntityQuery.use(delegator).from("WebSitePublishPoint").where("contentId", contentId).cache().queryOne();
               String menuItemName = menuItem.getName();
               //if (Debug.infoOn()) Debug.logInfo("in init, menuItemName:" + menuItemName, module);
               //if (Debug.infoOn()) Debug.logInfo("in init, webSitePublishPoint:" + webSitePublishPoint, module);
               putInContext(menuItemName, "WebSitePublishPoint", webSitePublishPoint);
            }
        } catch (GenericEntityException e) {
            return;
        }
    }
}
