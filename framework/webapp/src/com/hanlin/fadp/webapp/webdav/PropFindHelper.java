
package com.hanlin.fadp.webapp.webdav;

import java.util.LinkedList;
import java.util.List;

import com.hanlin.fadp.base.util.UtilValidate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** PROPFIND HTTP method helper class. This class provides helper methods for
 * working with WebDAV PROPFIND requests and responses.*/
public class PropFindHelper extends ResponseHelper {

    protected final Document requestDocument;

    public PropFindHelper(Document requestDocument) {
        this.requestDocument = requestDocument;
    }

    public Element createPropElement(List<Element> propList) {
        Element element = this.responseDocument.createElementNS(DAV_NAMESPACE_URI, "D:prop");
        if (UtilValidate.isNotEmpty(propList)) {
            for (Element propElement : propList) {
                element.appendChild(propElement);
            }
        }
        return element;
    }

    public Element createPropStatElement(Element prop, String stat) {
        Element element = this.responseDocument.createElementNS(DAV_NAMESPACE_URI, "D:propstat");
        element.appendChild(prop);
        element.appendChild(createStatusElement(stat));
        return element;
    }

    public List<Element> getFindPropsList(String nameSpaceUri) {
        List<Element> result = new LinkedList<Element>();
        NodeList nodeList = this.requestDocument.getElementsByTagNameNS(nameSpaceUri == null ? "*" : nameSpaceUri, "prop");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i).getFirstChild();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    result.add((Element) node);
                }
                node = node.getNextSibling();
            }
        }
        return result;
    }

    public boolean isAllProp() {
        NodeList nodeList = this.requestDocument.getElementsByTagNameNS(DAV_NAMESPACE_URI, "allprop");
        return nodeList.getLength() > 0;
    }

    public boolean isPropName() {
        NodeList nodeList = this.requestDocument.getElementsByTagNameNS(DAV_NAMESPACE_URI, "propname");
        return nodeList.getLength() > 0;
    }

}
