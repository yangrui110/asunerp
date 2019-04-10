
package com.hanlin.fadp.service.mail;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.ResourceHandler;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import org.w3c.dom.Element;

public class ServiceMcaUtil {

    public static final String module = ServiceMcaUtil.class.getName();
    private static final UtilCache<String, ServiceMcaRule> mcaCache = UtilCache.createUtilCache("service.ServiceMCAs", 0, 0, false);

    public static void reloadConfig() {
        mcaCache.clear();
        readConfig();
    }

    public static void readConfig() {
        // TODO: Missing in XSD file.

        // get all of the component resource eca stuff, ie specified in each fadp-component.xml file
        for (ComponentConfig.ServiceResourceInfo componentResourceInfo: ComponentConfig.getAllServiceResourceInfos("mca")) {
            addMcaDefinitions(componentResourceInfo.createResourceHandler());
        }
    }

    public static void addMcaDefinitions(ResourceHandler handler) {
        Element rootElement = null;
        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
            return;
        }

        int numDefs = 0;
        for (Element e: UtilXml.childElementList(rootElement, "mca")) {
            String ruleName = e.getAttribute("mail-rule-name");
            mcaCache.put(ruleName, new ServiceMcaRule(e));
            numDefs++;
        }

        if (Debug.importantOn()) {
            String resourceLocation = handler.getLocation();
            try {
                resourceLocation = handler.getURL().toExternalForm();
            } catch (GenericConfigException e) {
                Debug.logError(e, "Could not get resource URL", module);
            }
            Debug.logImportant("Loaded " + numDefs + " Service MCA definitions from " + resourceLocation, module);
        }
    }

    public static Collection<ServiceMcaRule> getServiceMcaRules() {
    if (mcaCache.size() == 0) {
        readConfig();
    }
        return mcaCache.values();
    }

    public static void evalRules(LocalDispatcher dispatcher, MimeMessageWrapper wrapper, GenericValue userLogin) throws GenericServiceException {
        Set<String> actionsRun = new TreeSet<String>();
        for (ServiceMcaRule rule: getServiceMcaRules()) {
            rule.eval(dispatcher, wrapper, actionsRun, userLogin);
        }
    }
}
