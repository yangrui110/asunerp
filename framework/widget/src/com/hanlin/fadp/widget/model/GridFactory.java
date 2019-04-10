
package com.hanlin.fadp.widget.model;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.model.ModelReader;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.LocalDispatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Widget Library - Grid factory class
 */
public class GridFactory {

    public static final String module = GridFactory.class.getName();
    private static final UtilCache<String, ModelGrid> gridLocationCache = UtilCache.createUtilCache("widget.grid.locationResource", 0, 0, false);
    private static final UtilCache<String, ModelGrid> gridWebappCache = UtilCache.createUtilCache("widget.grid.webappResource", 0, 0, false);

    public static Map<String, ModelGrid> getGridsFromLocation(String resourceName, ModelReader entityModelReader, DispatchContext dispatchContext)
            throws IOException, SAXException, ParserConfigurationException {
        URL gridFileUrl = FlexibleLocation.resolveLocation(resourceName);
        Document gridFileDoc = UtilXml.readXmlDocument(gridFileUrl, true, true);
        return readGridDocument(gridFileDoc, entityModelReader, dispatchContext, resourceName);
    }

    public static ModelGrid getGridFromLocation(String resourceName, String gridName, ModelReader entityModelReader, DispatchContext dispatchContext)
            throws IOException, SAXException, ParserConfigurationException {
        StringBuilder sb = new StringBuilder(dispatchContext.getDelegator().getDelegatorName());
        sb.append(":").append(resourceName).append("#").append(gridName);
        String cacheKey = sb.toString();
        ModelGrid modelGrid = gridLocationCache.get(cacheKey);
        if (modelGrid == null) {
            URL gridFileUrl = FlexibleLocation.resolveLocation(resourceName);
            Document gridFileDoc = UtilXml.readXmlDocument(gridFileUrl, true, true);
            if (gridFileDoc == null) {
                throw new IllegalArgumentException("Could not find resource [" + resourceName + "]");
            }
            modelGrid = createModelGrid(gridFileDoc, entityModelReader, dispatchContext, resourceName, gridName);
            modelGrid = gridLocationCache.putIfAbsentAndGet(cacheKey, modelGrid);
        }
        if (modelGrid == null) {
            throw new IllegalArgumentException("Could not find grid with name [" + gridName + "] in class resource [" + resourceName + "]");
        }
        return modelGrid;
    }

    public static ModelGrid getGridFromWebappContext(String resourceName, String gridName, HttpServletRequest request)
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName + "::" + gridName;
        ModelGrid modelGrid = gridWebappCache.get(cacheKey);
        if (modelGrid == null) {
            ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            URL gridFileUrl = servletContext.getResource(resourceName);
            Document gridFileDoc = UtilXml.readXmlDocument(gridFileUrl, true, true);
            Element gridElement = UtilXml.firstChildElement(gridFileDoc.getDocumentElement(), "grid", "name", gridName);
            modelGrid = createModelGrid(gridElement, delegator.getModelReader(), dispatcher.getDispatchContext(), resourceName, gridName);
            modelGrid = gridWebappCache.putIfAbsentAndGet(cacheKey, modelGrid);
        }
        if (modelGrid == null) {
            throw new IllegalArgumentException("Could not find grid with name [" + gridName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelGrid;
    }

    public static Map<String, ModelGrid> readGridDocument(Document gridFileDoc, ModelReader entityModelReader, DispatchContext dispatchContext, String gridLocation) {
        Map<String, ModelGrid> modelGridMap = new HashMap<String, ModelGrid>();
        if (gridFileDoc != null) {
            // read document and construct ModelGrid for each grid element
            Element rootElement = gridFileDoc.getDocumentElement();
            List<? extends Element> gridElements = UtilXml.childElementList(rootElement, "grid");
            for (Element gridElement : gridElements) {
                String gridName = gridElement.getAttribute("name");
                String cacheKey = gridLocation + "#" + gridName;
                ModelGrid modelGrid = gridLocationCache.get(cacheKey);
                if (modelGrid == null) {
                    modelGrid = createModelGrid(gridElement, entityModelReader, dispatchContext, gridLocation, gridName);
                    modelGrid = gridLocationCache.putIfAbsentAndGet(cacheKey, modelGrid);
                }
                modelGridMap.put(gridName, modelGrid);
            }
        }
        return modelGridMap;
    }

    public static ModelGrid createModelGrid(Document gridFileDoc, ModelReader entityModelReader, DispatchContext dispatchContext, String gridLocation, String gridName) {
        Element gridElement = UtilXml.firstChildElement(gridFileDoc.getDocumentElement(), "grid", "name", gridName);
        if (gridElement == null) {
            // Backwards compatibility - look for form definition
            gridElement = UtilXml.firstChildElement(gridFileDoc.getDocumentElement(), "form", "name", gridName);
        }
        return createModelGrid(gridElement, entityModelReader, dispatchContext, gridLocation, gridName);
    }

    public static ModelGrid createModelGrid(Element gridElement, ModelReader entityModelReader, DispatchContext dispatchContext, String gridLocation, String gridName) {
        return new ModelGrid(gridElement, gridLocation, entityModelReader, dispatchContext);
    }
}
