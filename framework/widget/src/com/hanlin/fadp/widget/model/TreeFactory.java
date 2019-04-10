
package com.hanlin.fadp.widget.model;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.service.LocalDispatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Widget Library - Tree factory class
 */
public class TreeFactory {

    public static final String module = TreeFactory.class.getName();

    public static final UtilCache<String, Map<String, ModelTree>> treeLocationCache = UtilCache.createUtilCache("widget.tree.locationResource", 0, 0, false);

    public static ModelTree getTreeFromLocation(String resourceName, String treeName, Delegator delegator, LocalDispatcher dispatcher)
            throws IOException, SAXException, ParserConfigurationException {
        Map<String, ModelTree> modelTreeMap = treeLocationCache.get(resourceName);
        if (modelTreeMap == null) {
            URL treeFileUrl = FlexibleLocation.resolveLocation(resourceName);
            Document treeFileDoc = UtilXml.readXmlDocument(treeFileUrl, true, true);
            modelTreeMap = readTreeDocument(treeFileDoc, delegator, dispatcher, resourceName);
            modelTreeMap = treeLocationCache.putIfAbsentAndGet(resourceName, modelTreeMap);
        }
        ModelTree modelTree = modelTreeMap.get(treeName);
        if (modelTree == null) {
            throw new IllegalArgumentException("Could not find tree with name [" + treeName + "] in class resource ["
                    + resourceName + "]");
        }
        return modelTree;
    }

    public static Map<String, ModelTree> readTreeDocument(Document treeFileDoc, Delegator delegator, LocalDispatcher dispatcher, String treeLocation) {
        Map<String, ModelTree> modelTreeMap = new HashMap<String, ModelTree>();
        if (treeFileDoc != null) {
            // read document and construct ModelTree for each tree element
            Element rootElement = treeFileDoc.getDocumentElement();
            for (Element treeElement: UtilXml.childElementList(rootElement, "tree")) {
                ModelTree modelTree = new ModelTree(treeElement, treeLocation);
                modelTreeMap.put(modelTree.getName(), modelTree);
            }
        }
        return modelTreeMap;
    }
}
