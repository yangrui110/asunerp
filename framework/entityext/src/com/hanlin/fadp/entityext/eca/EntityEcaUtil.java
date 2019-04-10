
package com.hanlin.fadp.entityext.eca;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.hanlin.fadp.base.component.ComponentConfig;
import com.hanlin.fadp.base.concurrent.ExecutionPool;
import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.MainResourceHandler;
import com.hanlin.fadp.base.config.ResourceHandler;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityConfException;
import com.hanlin.fadp.entity.config.model.DelegatorElement;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.config.model.EntityEcaReader;
import com.hanlin.fadp.entity.config.model.Resource;
import org.w3c.dom.Element;

/**
 * EntityEcaUtil
 */
public class EntityEcaUtil {

    public static final String module = EntityEcaUtil.class.getName();

    private static final UtilCache<String, Map<String, Map<String, List<EntityEcaRule>>>> entityEcaReaders = UtilCache.createUtilCache("entity.EcaReaders", 0, 0, false);

    public static Map<String, Map<String, List<EntityEcaRule>>> getEntityEcaCache(String entityEcaReaderName) {
        Map<String, Map<String, List<EntityEcaRule>>> ecaCache = entityEcaReaders.get(entityEcaReaderName);
        if (ecaCache == null) {
            // FIXME: Collections are not thread safe
            ecaCache = new HashMap<String, Map<String, List<EntityEcaRule>>>();
            readConfig(entityEcaReaderName, ecaCache);
            ecaCache = entityEcaReaders.putIfAbsentAndGet(entityEcaReaderName, ecaCache);
        }
        return ecaCache;
    }

    public static String getEntityEcaReaderName(String delegatorName) {
        DelegatorElement delegatorInfo = null;
        try {
            delegatorInfo = EntityConfig.getInstance().getDelegator(delegatorName);
        } catch (GenericEntityConfException e) {
            Debug.logWarning(e, "Exception thrown while getting field type config: ", module);
        }
        if (delegatorInfo == null) {
            Debug.logError("BAD ERROR: Could not find delegator config with name: " + delegatorName, module);
            return null;
        }
        return delegatorInfo.getEntityEcaReader();
    }

    protected static void readConfig(String entityEcaReaderName, Map<String, Map<String, List<EntityEcaRule>>> ecaCache) {
        EntityEcaReader entityEcaReaderInfo = null;
        try {
            entityEcaReaderInfo = EntityConfig.getInstance().getEntityEcaReader(entityEcaReaderName);
        } catch (GenericEntityConfException e) {
            Debug.logError(e, "Exception thrown while getting entity-eca-reader config with name: " + entityEcaReaderName, module);
        }
        if (entityEcaReaderInfo == null) {
            Debug.logError("BAD ERROR: Could not find entity-eca-reader config with name: " + entityEcaReaderName, module);
            return;
        }

        List<Future<List<EntityEcaRule>>> futures = new LinkedList<Future<List<EntityEcaRule>>>();
        for (Resource eecaResourceElement : entityEcaReaderInfo.getResourceList()) {
            ResourceHandler handler = new MainResourceHandler(EntityConfig.ENTITY_ENGINE_XML_FILENAME, eecaResourceElement.getLoader(), eecaResourceElement.getLocation());
            futures.add(ExecutionPool.GLOBAL_FORK_JOIN.submit(createEcaLoaderCallable(handler)));
        }

        // get all of the component resource eca stuff, ie specified in each fadp-component.xml file
        for (ComponentConfig.EntityResourceInfo componentResourceInfo: ComponentConfig.getAllEntityResourceInfos("eca")) {
            if (entityEcaReaderName.equals(componentResourceInfo.readerName)) {
                futures.add(ExecutionPool.GLOBAL_FORK_JOIN.submit(createEcaLoaderCallable(componentResourceInfo.createResourceHandler())));
            }
        }

        for (List<EntityEcaRule> oneFileRules: ExecutionPool.getAllFutures(futures)) {
            for (EntityEcaRule rule: oneFileRules) {
                String entityName = rule.getEntityName();
                String eventName = rule.getEventName();
                Map<String, List<EntityEcaRule>> eventMap = ecaCache.get(entityName);
                List<EntityEcaRule> rules = null;
                if (eventMap == null) {
                    eventMap = new HashMap<String, List<EntityEcaRule>>();
                    rules = new LinkedList<EntityEcaRule>();
                    ecaCache.put(entityName, eventMap);
                    eventMap.put(eventName, rules);
                } else {
                    rules = eventMap.get(eventName);
                    if (rules == null) {
                        rules = new LinkedList<EntityEcaRule>();
                        eventMap.put(eventName, rules);
                    }
                }
                rules.add(rule);
            }
        }
    }

    private static List<EntityEcaRule> getEcaDefinitions(ResourceHandler handler) {
        List<EntityEcaRule> rules = new LinkedList<EntityEcaRule>();
        Element rootElement = null;
        try {
            rootElement = handler.getDocument().getDocumentElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
            return rules;
        }
        for (Element e: UtilXml.childElementList(rootElement, "eca")) {
            rules.add(new EntityEcaRule(e));
        }
        try {
            Debug.logInfo("Loaded [" + rules.size() + "] Entity ECA definitions from " + handler.getFullLocation() + " in loader " + handler.getLoaderName(), module);
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
        }
        return rules;
    }

    protected static Callable<List<EntityEcaRule>> createEcaLoaderCallable(final ResourceHandler handler) {
        return new Callable<List<EntityEcaRule>>() {
            public List<EntityEcaRule> call() throws Exception {
                return getEcaDefinitions(handler);
            }
        };
    }

    public static Collection<EntityEcaRule> getEntityEcaRules(Delegator delegator, String entityName, String event) {
        Map<String, Map<String, List<EntityEcaRule>>> ecaCache = EntityEcaUtil.getEntityEcaCache(EntityEcaUtil.getEntityEcaReaderName(delegator.getDelegatorName()));
        Map<String, List<EntityEcaRule>> eventMap = ecaCache.get(entityName);
        if (eventMap != null) {
            if (event != null) {
                return eventMap.get(event);
            }
        }
        return null;
    }
}
