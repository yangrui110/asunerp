
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
 * Widget Library - Form factory class
 */
public class FormFactory {

    public static final String module = FormFactory.class.getName();
    private static final UtilCache<String, ModelForm> formLocationCache = UtilCache.createUtilCache("widget.form.locationResource", 0, 0, false);
    private static final UtilCache<String, ModelForm> formWebappCache = UtilCache.createUtilCache("widget.form.webappResource", 0, 0, false);

    public static Map<String, ModelForm> getFormsFromLocation(String resourceName, ModelReader entityModelReader, DispatchContext dispatchContext)
            throws IOException, SAXException, ParserConfigurationException {
        URL formFileUrl = FlexibleLocation.resolveLocation(resourceName);
        Document formFileDoc = UtilXml.readXmlDocument(formFileUrl, true, true);
        return readFormDocument(formFileDoc, entityModelReader, dispatchContext, resourceName);
    }

    public static ModelForm getFormFromLocation(String resourceName, String formName, ModelReader entityModelReader, DispatchContext dispatchContext)
            throws IOException, SAXException, ParserConfigurationException {
        StringBuilder sb = new StringBuilder(dispatchContext.getDelegator().getDelegatorName());
        sb.append(":").append(resourceName).append("#").append(formName);
        String cacheKey = sb.toString();
        ModelForm modelForm = formLocationCache.get(cacheKey);
        if (modelForm == null) {
            URL formFileUrl = FlexibleLocation.resolveLocation(resourceName);
            Document formFileDoc = UtilXml.readXmlDocument(formFileUrl, true, true);
            if (formFileDoc == null) {
                throw new IllegalArgumentException("Could not find resource [" + resourceName + "]");
            }
            modelForm = createModelForm(formFileDoc, entityModelReader, dispatchContext, resourceName, formName);
            modelForm = formLocationCache.putIfAbsentAndGet(cacheKey, modelForm);
        }
        if (modelForm == null) {
            throw new IllegalArgumentException("Could not find form with name [" + formName + "] in class resource [" + resourceName + "]");
        }
        return modelForm;
    }

    public static ModelForm getFormFromWebappContext(String resourceName, String formName, HttpServletRequest request)
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName + "::" + formName;
        ModelForm modelForm = formWebappCache.get(cacheKey);
        if (modelForm == null) {
            ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
            URL formFileUrl = servletContext.getResource(resourceName);
            Document formFileDoc = UtilXml.readXmlDocument(formFileUrl, true, true);
            Element formElement = UtilXml.firstChildElement(formFileDoc.getDocumentElement(), "form", "name", formName);
            modelForm = createModelForm(formElement, delegator.getModelReader(), dispatcher.getDispatchContext(), resourceName, formName);
            modelForm = formWebappCache.putIfAbsentAndGet(cacheKey, modelForm);
        }
        if (modelForm == null) {
            throw new IllegalArgumentException("Could not find form with name [" + formName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelForm;
    }

    public static Map<String, ModelForm> readFormDocument(Document formFileDoc, ModelReader entityModelReader, DispatchContext dispatchContext, String formLocation) {
        Map<String, ModelForm> modelFormMap = new HashMap<String, ModelForm>();
        if (formFileDoc != null) {
            // read document and construct ModelForm for each form element
            Element rootElement = formFileDoc.getDocumentElement();
            List<? extends Element> formElements = UtilXml.childElementList(rootElement, "form");
            for (Element formElement : formElements) {
                String formName = formElement.getAttribute("name");
                String cacheKey = formLocation + "#" + formName;
                ModelForm modelForm = formLocationCache.get(cacheKey);
                if (modelForm == null) {
                    modelForm = createModelForm(formElement, entityModelReader, dispatchContext, formLocation, formName);
                    modelForm = formLocationCache.putIfAbsentAndGet(cacheKey, modelForm);
                }
                modelFormMap.put(formName, modelForm);
            }
        }
        return modelFormMap;
    }

    public static ModelForm createModelForm(Document formFileDoc, ModelReader entityModelReader, DispatchContext dispatchContext, String formLocation, String formName) {
        Element formElement = UtilXml.firstChildElement(formFileDoc.getDocumentElement(), "form", "name", formName);
        return createModelForm(formElement, entityModelReader, dispatchContext, formLocation, formName);
    }

    public static ModelForm createModelForm(Element formElement, ModelReader entityModelReader, DispatchContext dispatchContext, String formLocation, String formName) {
        String formType = formElement.getAttribute("type");
        if (formType.isEmpty() || "single".equals(formType) || "upload".equals(formType)) {
            return new ModelSingleForm(formElement, formLocation, entityModelReader, dispatchContext);
        } else {
            return new ModelGrid(formElement, formLocation, entityModelReader, dispatchContext);
        }
    }
}
