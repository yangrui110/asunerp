
package com.hanlin.fadp.service.group;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.MainResourceHandler;
import com.hanlin.fadp.base.config.ResourceHandler;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.service.config.ServiceConfigUtil;
import com.hanlin.fadp.service.config.model.ServiceGroups;
import org.w3c.dom.Element;

/**
 * ServiceGroupReader.java
 */
public class ServiceGroupReader {

    public static final String module = ServiceGroupReader.class.getName();

    // using a cache is dangerous here because if someone clears it the groups won't work at all: public static UtilCache groupsCache = new UtilCache("service.ServiceGroups", 0, 0, false);
    public static Map<String, GroupModel> groupsCache = new ConcurrentHashMap<String, GroupModel>();

    public static void readConfig() {
        List<ServiceGroups> serviceGroupsList = null;
        try {
            serviceGroupsList = ServiceConfigUtil.getServiceEngine().getServiceGroups();
        } catch (GenericConfigException e) {
            // FIXME: Refactor API so exceptions can be thrown and caught.
            Debug.logError(e, module);
            throw new RuntimeException(e.getMessage());
        }
        for (ServiceGroups serviceGroup : serviceGroupsList) {
            ResourceHandler handler = new MainResourceHandler(ServiceConfigUtil.SERVICE_ENGINE_XML_FILENAME, serviceGroup.getLoader(), serviceGroup.getLocation());
            addGroupDefinitions(handler);
        }

        // get all of the component resource group stuff, ie specified in each fadp-component.xml file
        for (ComponentConfig.ServiceResourceInfo componentResourceInfo: ComponentConfig.getAllServiceResourceInfos("group")) {
            addGroupDefinitions(componentResourceInfo.createResourceHandler());
        }
    }

    public static void addGroupDefinitions(ResourceHandler handler) {
        Element rootElement = null;

        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
            return;
        }
        int numDefs = 0;

        for (Element group: UtilXml.childElementList(rootElement, "group")) {
            String groupName = group.getAttribute("name");
            if (groupName == null || groupName.isEmpty()) {
                Debug.logError("XML Parsing error: <group> element 'name' attribute null or empty", module);
                continue;
            }
            groupsCache.put(groupName, new GroupModel(group));
            numDefs++;
        }
        if (Debug.infoOn()) {
            String resourceLocation = handler.getLocation();
            try {
                resourceLocation = handler.getURL().toExternalForm();
            } catch (GenericConfigException e) {
                Debug.logError(e, "Could not get resource URL", module);
            }
            Debug.logInfo("Loaded [" + numDefs + "] Group definitions from " + resourceLocation, module);
        }
    }

    public static GroupModel getGroupModel(String serviceName) {
        if (groupsCache.size() == 0) {
            ServiceGroupReader.readConfig();
        }
        return groupsCache.get(serviceName);
    }
}
