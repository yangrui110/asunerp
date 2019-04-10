package com.hanlin.fadp.webapp.control;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.hanlin.fadp.base.component.ComponentConfig.WebappInfo;
import com.hanlin.fadp.base.location.FlexibleLocation;
import com.hanlin.fadp.base.metrics.Metrics;
import com.hanlin.fadp.base.metrics.MetricsFactory;
import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.GeneralException;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.base.util.collections.MapContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @ClassName: ConfigXMLReader 
 * @Description: 处理每个web应用下面的controller.xml的操作，controller.xml为站点配置文件。
 * 本类含五个内置内部类，是controller.xml文件的解析，为相关操作数据，所有类和方法均为static，
 * @author: 祖国
 * @date: 2015年12月17日 下午5:35:07
 */
public class ConfigXMLReader {

    public static final String module = ConfigXMLReader.class.getName();
    public static final String controllerXmlFileName = "/WEB-INF/controller.xml";
    private static final UtilCache<URL, ControllerConfig> controllerCache = UtilCache.createUtilCache("webapp.ControllerConfig");
    private static final UtilCache<String, List<ControllerConfig>> controllerSearchResultsCache = UtilCache.createUtilCache("webapp.ControllerSearchResults");
    public static final RequestResponse emptyNoneRequestResponse = RequestResponse.createEmptyNoneRequestResponse();

    public static Set<String> findControllerFilesWithRequest(String requestUri, String controllerPartialPath) throws GeneralException {
        Set<String> allControllerRequestSet = new HashSet<String>();
        if (UtilValidate.isEmpty(requestUri)) {
            return allControllerRequestSet;
        }
        String cacheId = controllerPartialPath != null ? controllerPartialPath : "NOPARTIALPATH";
        List<ControllerConfig> controllerConfigs = controllerSearchResultsCache.get(cacheId);
        if (controllerConfigs == null) {
            try {
                // find controller.xml file with webappMountPoint + "/WEB-INF" in the path
                List<File> controllerFiles = FileUtil.findXmlFiles(null, controllerPartialPath, "site-conf", "site-conf.xsd");
                controllerConfigs = new LinkedList<ControllerConfig>();
                for (File controllerFile : controllerFiles) {
                    URL controllerUrl = null;
                    try {
                        controllerUrl = controllerFile.toURI().toURL();
                    } catch (MalformedURLException mue) {
                        throw new GeneralException(mue);
                    }
                    ControllerConfig cc = ConfigXMLReader.getControllerConfig(controllerUrl);
                    controllerConfigs.add(cc);
                }
                controllerConfigs = controllerSearchResultsCache.putIfAbsentAndGet(cacheId, controllerConfigs);
            } catch (IOException e) {
                throw new GeneralException("Error finding controller XML files to lookup request references: " + e.toString(), e);
            }
        }
        if (controllerConfigs != null) {
            for (ControllerConfig cc : controllerConfigs) {
                // make sure it has the named request in it
                if (cc.requestMapMap.get(requestUri) != null) {
                    String requestUniqueId = cc.url.toExternalForm() + "#" + requestUri;
                    allControllerRequestSet.add(requestUniqueId);
                    // Debug.logInfo("========== In findControllerFilesWithRequest found controller with request here [" + requestUniqueId + "]", module);
                }
            }
        }
        return allControllerRequestSet;
    }

    public static Set<String> findControllerRequestUniqueForTargetType(String target, String urlMode) throws GeneralException {
        if (UtilValidate.isEmpty(urlMode)) {
            urlMode = "intra-app";
        }
        int indexOfDollarSignCurlyBrace = target.indexOf("${");
        int indexOfQuestionMark = target.indexOf("?");
        if (indexOfDollarSignCurlyBrace >= 0 && (indexOfQuestionMark < 0 || indexOfQuestionMark > indexOfDollarSignCurlyBrace)) {
            // we have an expanded string in the requestUri part of the target, not much we can do about that...
            return null;
        }
        if ("intra-app".equals(urlMode)) {
            // look through all controller.xml files and find those with the request-uri referred to by the target
            String requestUri = UtilHttp.getRequestUriFromTarget(target);
            Set<String> controllerLocAndRequestSet = ConfigXMLReader.findControllerFilesWithRequest(requestUri, null);
            // if (controllerLocAndRequestSet.size() > 0) Debug.logInfo("============== In findRequestNamesLinkedtoInWidget, controllerLocAndRequestSet: " + controllerLocAndRequestSet, module);
            return controllerLocAndRequestSet;
        } else if ("inter-app".equals(urlMode)) {
            String webappMountPoint = UtilHttp.getWebappMountPointFromTarget(target);
            if (webappMountPoint != null)
                webappMountPoint += "/WEB-INF";
            String requestUri = UtilHttp.getRequestUriFromTarget(target);

            Set<String> controllerLocAndRequestSet = ConfigXMLReader.findControllerFilesWithRequest(requestUri, webappMountPoint);
            // if (controllerLocAndRequestSet.size() > 0) Debug.logInfo("============== In findRequestNamesLinkedtoInWidget, controllerLocAndRequestSet: " + controllerLocAndRequestSet, module);
            return controllerLocAndRequestSet;
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * @Title: getControllerConfig 
     * @Description: 通过fadp-component.xml中webapp节点的配置信息获取controller.xml解析类ControllerConfig实例。
     * @param webAppInfo
     * @throws WebAppConfigurationException
     * @throws MalformedURLException
     * @return: ControllerConfig
     */
    public static ControllerConfig getControllerConfig(WebappInfo webAppInfo) throws WebAppConfigurationException, MalformedURLException {
        Assert.notNull("webAppInfo", webAppInfo);
        //二级应用目录+webapp/二级应用目录名+/WEB-INF/controller.xml
        String filePath = webAppInfo.getLocation().concat(controllerXmlFileName);
        File configFile = new File(filePath);
        return getControllerConfig(configFile.toURI().toURL());
    }

    /**
     * @Title: getControllerConfig 
     * @Description: 将controller.xml的解析类实例放到ConfigXMLReader的缓存controllerCache中，
     * 该缓存是一个map，key=controller.xml的url，value=解析类ControllerConfig实例，返回该实例。
     * @param url
     * @throws WebAppConfigurationException
     * @return: ControllerConfig 返回一个controller.xml解析类ControllerConfig实例
     */
    public static ControllerConfig getControllerConfig(URL url) throws WebAppConfigurationException {
        ControllerConfig controllerConfig = controllerCache.get(url);
        if (controllerConfig == null) {
            controllerConfig = controllerCache.putIfAbsentAndGet(url, new ControllerConfig(url));
        }
        return controllerConfig;
    }

    /**
     * @Title: getControllerConfigURL 
     * @Description: 从上下文中获取cotroller.xml文件的url
     * @param context
     * @return: URL
     */
    public static URL getControllerConfigURL(ServletContext context) {
        try {
            return context.getResource(controllerXmlFileName);
        } catch (MalformedURLException e) {
            Debug.logError(e, "Error Finding XML Config File: " + controllerXmlFileName, module);
            return null;
        }
    }

    /** Loads the XML file and returns the root element 
     * @throws WebAppConfigurationException */
    private static Element loadDocument(URL location) throws WebAppConfigurationException {
        try {
//            System.out.println(location);
            Document document = UtilXml.readXmlDocument(location, true);
            Element rootElement = document.getDocumentElement();
            if (Debug.verboseOn())
                Debug.logVerbose("Loaded XML Config - " + location, module);
            return rootElement;
        } catch (Exception e) {
            Debug.logError(e, module);
            throw new WebAppConfigurationException(e);
        }
    }

    /**
     * @ClassName: ControllerConfig 
     * @Description: controller.xml解析类，特别注意其中事件分六类：
     * 预处理、请求后处理、登陆前处理、登陆后处理、第一次访问处理和request-map中的event事件
     * @author: 祖国
     * @date: 2015年12月17日 下午9:14:02
     */
    public static class ControllerConfig {
        public URL url;
        private String errorpage;
        private String protectView;
        private String owner;
        private String securityClass;
        private String defaultRequest;
        private String statusCode;
        /** include解析数据 */
        private List<URL> includes = new ArrayList<URL>();
        /**第一次访问时处理的事件，对应firstvisit子节点  */
        private Map<String, Event> firstVisitEventList = new HashMap<String, Event>();
        /** 请求前处理的事件，对应preprocessor子节点 */
        private Map<String, Event> preprocessorEventList = new HashMap<String, Event>();
        /** 每次请求后处理的事件，对应postprocessor子节点 */
        private Map<String, Event> postprocessorEventList = new HashMap<String, Event>();
        /** 登陆后处理的事件，对应after-login子节点 */
        private Map<String, Event> afterLoginEventList = new HashMap<String, Event>();
        /**退出登录处理的事件 ，对于bofore-logout子节点 */
        private Map<String, Event> beforeLogoutEventList = new HashMap<String, Event>();
        /**handler节点类型未指定为view时的解析数据  */
        private Map<String, String> eventHandlerMap = new HashMap<String, String>();
        /** handler节点类型指定为view时的解析数据 */
        private Map<String, String> viewHandlerMap = new HashMap<String, String>();
        /** request-map解析数据 ，key=requst-map节点的uri属性值，value=request-map节点解析类RequestMap的实例*/
        private Map<String, RequestMap> requestMapMap = new HashMap<String, RequestMap>();
        /**view-map节点解析数据map，key=name属性，value=ViewMap实例  */
        private Map<String, ViewMap> viewMapMap = new HashMap<String, ViewMap>();

        public ControllerConfig(URL url) throws WebAppConfigurationException {
            this.url = url;
            Element rootElement = loadDocument(url);
            if (rootElement != null) {
                long startTime = System.currentTimeMillis();
                loadIncludes(rootElement);//加载inlude数据
                loadGeneralConfig(rootElement);//加载通用配置数据
                loadHandlerMap(rootElement);//加载handler配置数据
                loadRequestMap(rootElement);//加载request-map配置数据
                loadViewMap(rootElement);//加载view-map配置数据
                if (Debug.infoOn()) {
                    double totalSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                    String locString = this.url.toExternalForm();
                    Debug.logInfo("controller loaded: " + totalSeconds + "s, " + this.requestMapMap.size() + " requests, " + this.viewMapMap.size() + " views in " + locString, module);
                }
            }
        }

        public Map<String, Event> getAfterLoginEventList() throws WebAppConfigurationException {
            MapContext<String, Event> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getAfterLoginEventList());
            }
            result.push(afterLoginEventList);
            return result;
        }

        public Map<String, Event> getBeforeLogoutEventList() throws WebAppConfigurationException {
            MapContext<String, Event> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getBeforeLogoutEventList());
            }
            result.push(beforeLogoutEventList);
            return result;
        }

        public String getDefaultRequest() throws WebAppConfigurationException {
            if (defaultRequest != null) {
                return defaultRequest;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String defaultRequest = controllerConfig.getDefaultRequest();
                if (defaultRequest != null) {
                    return defaultRequest;
                }
            }
            return null;
        }

        public String getErrorpage() throws WebAppConfigurationException {
            if (errorpage != null) {
                return errorpage;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String errorpage = controllerConfig.getErrorpage();
                if (errorpage != null) {
                    return errorpage;
                }
            }
            return null;
        }

        /**
         * @Title: getEventHandlerMap 
         * @Description: 加载每一个include节点中配置的xxx-controller.xml文件解析的handler数据中
         * 关于type非view的数据map，递归获取，key=handler的name属性值，value=class属性值，为EventHandler实现类
         * @throws WebAppConfigurationException
         * @return: Map<String,String>
         */
        public Map<String, String> getEventHandlerMap() throws WebAppConfigurationException {
            MapContext<String, String> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getEventHandlerMap());
            }
            result.push(eventHandlerMap);
            return result;
        }

        public Map<String, Event> getFirstVisitEventList() throws WebAppConfigurationException {
            MapContext<String, Event> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getFirstVisitEventList());
            }
            result.push(firstVisitEventList);
            return result;
        }

        public String getOwner() throws WebAppConfigurationException {
            if (owner != null) {
                return owner;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String owner = controllerConfig.getOwner();
                if (owner != null) {
                    return owner;
                }
            }
            return null;
        }

        public Map<String, Event> getPostprocessorEventList() throws WebAppConfigurationException {
            MapContext<String, Event> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getPostprocessorEventList());
            }
            result.push(postprocessorEventList);
            return result;
        }

        public Map<String, Event> getPreprocessorEventList() throws WebAppConfigurationException {
            MapContext<String, Event> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getPreprocessorEventList());
            }
            result.push(preprocessorEventList);
            return result;
        }

        public String getProtectView() throws WebAppConfigurationException {
            if (protectView != null) {
                return protectView;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String protectView = controllerConfig.getProtectView();
                if (protectView != null) {
                    return protectView;
                }
            }
            return null;
        }

        /**
         * @Title: getRequestMapMap 
         * @Description: 获取controller.xml和其include的xxx-controller.xml文件中request-map节点的解析数据map，
         * key=requst-map节点的uri属性值，value=request-map节点解析类RequestMap的实例
         * @throws WebAppConfigurationException
         * @return: Map<String,RequestMap>
         */
        public Map<String, RequestMap> getRequestMapMap() throws WebAppConfigurationException {
            MapContext<String, RequestMap> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getRequestMapMap());
            }
            result.push(requestMapMap);
            return result;
        }

        public String getSecurityClass() throws WebAppConfigurationException {
            if (securityClass != null) {
                return securityClass;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String securityClass = controllerConfig.getSecurityClass();
                if (securityClass != null) {
                    return securityClass;
                }
            }
            return null;
        }

        public String getStatusCode() throws WebAppConfigurationException {
            if (statusCode != null) {
                return statusCode;
            }
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                String statusCode = controllerConfig.getStatusCode();
                if (statusCode != null) {
                    return statusCode;
                }
            }
            return null;
        }

        /**
         * @Title: getViewHandlerMap 
         * @Description: 加载每一个include节点中配置的xxx-controller.xml文件解析
         * 的handler数据中关于type为view的数据，递归获取
         * @throws WebAppConfigurationException
         * @return: Map<String,String>
         */
        public Map<String, String> getViewHandlerMap() throws WebAppConfigurationException {
            MapContext<String, String> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {//遍历controller.xml中include节点
            	//获取include节点中配置的xml文件对应的ControllerConfig实例
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                //获取ControllerConfig中
                result.push(controllerConfig.getViewHandlerMap());
            }
            result.push(viewHandlerMap);
            return result;
        }

        /**
         * @Title: getViewMapMap 
         * @Description: controller.xml和其include的xxx-controller.xml中view-map的解析数据map,
         * key=name属性，value=ViewMap实例
         * @throws WebAppConfigurationException
         * @return: Map<String,ViewMap>
         */
        public Map<String, ViewMap> getViewMapMap() throws WebAppConfigurationException {
            MapContext<String, ViewMap> result = MapContext.getMapContext();
            for (URL includeLocation : includes) {
                ControllerConfig controllerConfig = getControllerConfig(includeLocation);
                result.push(controllerConfig.getViewMapMap());
            }
            result.push(viewMapMap);
            return result;
        }

        /**
         * @Title: loadGeneralConfig 
         * @Description: 加载controller.xml中通用配置，非include、handler、request-map、view-map
         * @param rootElement
         * @return: void
         */
        private void loadGeneralConfig(Element rootElement) {
            this.errorpage = UtilXml.childElementValue(rootElement, "errorpage");
            this.statusCode = UtilXml.childElementValue(rootElement, "status-code");
            Element protectElement = UtilXml.firstChildElement(rootElement, "protect");
            if (protectElement != null) {
                this.protectView = protectElement.getAttribute("view");
            }
            this.owner = UtilXml.childElementValue(rootElement, "owner");
            this.securityClass = UtilXml.childElementValue(rootElement, "security-class");
            Element defaultRequestElement = UtilXml.firstChildElement(rootElement, "default-request");
            if (defaultRequestElement != null) {
                this.defaultRequest = defaultRequestElement.getAttribute("request-uri");
            }
            // first visit event
            Element firstvisitElement = UtilXml.firstChildElement(rootElement, "firstvisit");
            if (firstvisitElement != null) {
                for (Element eventElement : UtilXml.childElementList(firstvisitElement, "event")) {
                    String eventName = eventElement.getAttribute("name");
                    if (UtilValidate.isEmpty(eventName)) {
                        eventName = eventElement.getAttribute("type") + "::" + eventElement.getAttribute("path") + "::" + eventElement.getAttribute("invoke");
                    }
                    this.firstVisitEventList.put(eventName, new Event(eventElement));
                }
            }
            // preprocessor events
            Element preprocessorElement = UtilXml.firstChildElement(rootElement, "preprocessor");
            if (preprocessorElement != null) {
                for (Element eventElement : UtilXml.childElementList(preprocessorElement, "event")) {
                    String eventName = eventElement.getAttribute("name");
                    if (UtilValidate.isEmpty(eventName)) {
                        eventName = eventElement.getAttribute("type") + "::" + eventElement.getAttribute("path") + "::" + eventElement.getAttribute("invoke");
                    }
                    this.preprocessorEventList.put(eventName, new Event(eventElement));
                }
            }
            // postprocessor events
            Element postprocessorElement = UtilXml.firstChildElement(rootElement, "postprocessor");
            if (postprocessorElement != null) {
                for (Element eventElement : UtilXml.childElementList(postprocessorElement, "event")) {
                    String eventName = eventElement.getAttribute("name");
                    if (UtilValidate.isEmpty(eventName)) {
                        eventName = eventElement.getAttribute("type") + "::" + eventElement.getAttribute("path") + "::" + eventElement.getAttribute("invoke");
                    }
                    this.postprocessorEventList.put(eventName, new Event(eventElement));
                }
            }
            // after-login events
            Element afterLoginElement = UtilXml.firstChildElement(rootElement, "after-login");
            if (afterLoginElement != null) {
                for (Element eventElement : UtilXml.childElementList(afterLoginElement, "event")) {
                    String eventName = eventElement.getAttribute("name");
                    if (UtilValidate.isEmpty(eventName)) {
                        eventName = eventElement.getAttribute("type") + "::" + eventElement.getAttribute("path") + "::" + eventElement.getAttribute("invoke");
                    }
                    this.afterLoginEventList.put(eventName, new Event(eventElement));
                }
            }
            // before-logout events
            Element beforeLogoutElement = UtilXml.firstChildElement(rootElement, "before-logout");
            if (beforeLogoutElement != null) {
                for (Element eventElement : UtilXml.childElementList(beforeLogoutElement, "event")) {
                    String eventName = eventElement.getAttribute("name");
                    if (UtilValidate.isEmpty(eventName)) {
                        eventName = eventElement.getAttribute("type") + "::" + eventElement.getAttribute("path") + "::" + eventElement.getAttribute("invoke");
                    }
                    this.beforeLogoutEventList.put(eventName, new Event(eventElement));
                }
            }
        }

        /**
         * @Title: loadHandlerMap 
         * @Description: 加载handler解析数据，如果type为view，则加入到viewHandlerMap中，
         * key=name属性值，value=class属性值，且value中的对应的class属性值为ViewHandler实现类
         * 否则加入到eventHandlerMap中
         * @param rootElement
         * @return: void
         */
        private void loadHandlerMap(Element rootElement) {
            for (Element handlerElement : UtilXml.childElementList(rootElement, "handler")) {
                String name = handlerElement.getAttribute("name");
                String type = handlerElement.getAttribute("type");
                String className = handlerElement.getAttribute("class");

                if ("view".equals(type)) {
                    this.viewHandlerMap.put(name, className);
                } else {
                    this.eventHandlerMap.put(name, className);
                }
            }
        }

        /**
         * @Title: loadIncludes 
         * @Description: 加载解析controller.xml中include元素到list<url>中
         * @param rootElement
         * @return: void
         */
        protected void loadIncludes(Element rootElement) {
            for (Element includeElement : UtilXml.childElementList(rootElement, "include")) {
                String includeLocation = includeElement.getAttribute("location");
                if (UtilValidate.isNotEmpty(includeLocation)) {
                    try {
                        URL urlLocation = FlexibleLocation.resolveLocation(includeLocation);
                        includes.add(urlLocation);
                    } catch (MalformedURLException mue) {
                        Debug.logError(mue, "Error processing include at [" + includeLocation + "]:" + mue.toString(), module);
                    }
                }
            }
        }

        /**
         * @Title: loadRequestMap 
         * @Description: 加载解析request-map节点数据，key=uri属性，value=RequestMap实例
         * @param root
         * @return: void
         */
        private void loadRequestMap(Element root) {
            for (Element requestMapElement : UtilXml.childElementList(root, "request-map")) {
                RequestMap requestMap = new RequestMap(requestMapElement);
                this.requestMapMap.put(requestMap.uri, requestMap);
            }
        }

        /**
         * @Title: loadViewMap 
         * @Description: 加载view-map节点解析数据，key=name属性，value=ViewMap实例
         * @param rootElement
         * @return: void
         */
        private void loadViewMap(Element rootElement) {
            for (Element viewMapElement : UtilXml.childElementList(rootElement, "view-map")) {
                ViewMap viewMap = new ViewMap(viewMapElement);
                this.viewMapMap.put(viewMap.name, viewMap);
            }
        }

    }

    /**
     * @ClassName: Event 
     * @Description: request-map元素的event子元素解析
     * @author: 祖国
     * @date: 2015年12月17日 下午6:06:35
     */
    public static class Event {
        public String type;
        public String path;
        public String invoke;
        public boolean globalTransaction = true;
        /** 服务的度量*/
        public Metrics metrics = null;

        public Event(Element eventElement) {
            this.type = eventElement.getAttribute("type");
            this.path = eventElement.getAttribute("path");
            this.invoke = eventElement.getAttribute("invoke");
            this.globalTransaction = !"false".equals(eventElement.getAttribute("global-transaction"));
            // Get metrics.
            Element metricsElement = UtilXml.firstChildElement(eventElement, "metric");
            if (metricsElement != null) {
                this.metrics = MetricsFactory.getInstance(metricsElement);
            }
        }

        public Event(String type, String path, String invoke, boolean globalTransaction) {
            this.type = type;
            this.path = path;
            this.invoke = invoke;
            this.globalTransaction = globalTransaction;
        }
    }

    /**
     * @ClassName: RequestMap 
     * @Description: request-map元素解析，包括event、response（为RequestReponse的map）、metrics子元素
     * @author: 祖国
     * @date: 2015年12月17日 下午6:13:22
     */
    public static class RequestMap {
    	/**request-map节点的uri属性值 */
        public String uri;
        /**request-map节点的edit属性值 */
        public boolean edit = true;
        /**request-map节点的track-visit属性值 */
        public boolean trackVisit = true;
        /**request-map节点的track-serverhit属性值 */
        public boolean trackServerHit = true;
        /**request-map节点的description子节点 */
        public String description;
        /**request-map节点的event子节点 */
        public Event event;
        /**request-map节点的security子节点https属性值 */
        public boolean securityHttps = false;
        /**request-map节点的security子节点auth属性值 */
        public boolean securityAuth = false;
        /**request-map节点的security子节点cert属性值 */
        public boolean securityCert = false;
        /**request-map节点的security子节点external-view属性值 */
        public boolean securityExternalView = true;
        /**request-map节点的security子节点direct-request属性值 */
        public boolean securityDirectRequest = true;
        /**controller.xml中response节点解析map，key=response子节点的name属性值，value=response子节点解析类RequestResponse */
        public Map<String, RequestResponse> requestResponseMap = new HashMap<String, RequestResponse>();
        /**request-map节点的metrics子节点 */
        public Metrics metrics = null;

        public RequestMap(Element requestMapElement) {
            // Get the URI info
        	/** request-map节点的uri元素 */
            this.uri = requestMapElement.getAttribute("uri");
            this.edit = !"false".equals(requestMapElement.getAttribute("edit"));
            this.trackServerHit = !"false".equals(requestMapElement.getAttribute("track-serverhit"));
            this.trackVisit = !"false".equals(requestMapElement.getAttribute("track-visit"));
            // Check for security
            Element securityElement = UtilXml.firstChildElement(requestMapElement, "security");
            if (securityElement != null) {
                this.securityHttps = "true".equals(securityElement.getAttribute("https"));
                this.securityAuth = "true".equals(securityElement.getAttribute("auth"));
                this.securityCert = "true".equals(securityElement.getAttribute("cert"));
                this.securityExternalView = !"false".equals(securityElement.getAttribute("external-view"));
                this.securityDirectRequest = !"false".equals(securityElement.getAttribute("direct-request"));
            }
            // Check for event
            Element eventElement = UtilXml.firstChildElement(requestMapElement, "event");
            if (eventElement != null) {
                this.event = new Event(eventElement);
            }
            // Check for description
            this.description = UtilXml.childElementValue(requestMapElement, "description");
            // Get the response(s)
            for (Element responseElement : UtilXml.childElementList(requestMapElement, "response")) {
                RequestResponse response = new RequestResponse(responseElement);
                requestResponseMap.put(response.name, response);
            }
            // Get metrics.
            Element metricsElement = UtilXml.firstChildElement(requestMapElement, "metric");
            if (metricsElement != null) {
                this.metrics = MetricsFactory.getInstance(metricsElement);
            }
        }
    }

    /**
     * @ClassName: RequestResponse 
     * @Description: request-map元素的response子元素解析
     * @author: 祖国
     * @date: 2015年12月17日 下午6:12:20
     */
    public static class RequestResponse {

        public static RequestResponse createEmptyNoneRequestResponse() {
            RequestResponse requestResponse = new RequestResponse();
            requestResponse.name = "empty-none";
            requestResponse.type = "none";
            requestResponse.value = null;
            return requestResponse;
        }

        /** response子节点的name属性，为success或error */
        public String name;
        /**response子节点的type属性，为url、view、none  */
        public String type;
        /**response子节点的value属性，其值会作为view-map的查找  */
        public String value;
        /** response子节点的status-code属性 */
        public String statusCode;
        /**response子节点的save-last-view属性  */
        public boolean saveLastView = false;
        /** response子节点的save-current-view属性 */
        public boolean saveCurrentView = false;
        /** response子节点的save-home-view属性 */
        public boolean saveHomeView = false;
        /** response子节点的redirect-parameter子节点map，
         * 如果value为空时，key=name属性值，value=form属性值，构成redirectParameterMap
         * 如果value属性值非空，key=name属性值，value=value属性值 ，构成redirectParameterValueMap*/
        public Map<String, String> redirectParameterMap = new HashMap<String, String>();
        /** response子节点的redirect-parameter子节点map，其中的value非空时，key=name属性值，value=value属性值 */
        public Map<String, String> redirectParameterValueMap = new HashMap<String, String>();

        public RequestResponse() {
        }

        public RequestResponse(Element responseElement) {
            this.name = responseElement.getAttribute("name");
            this.type = responseElement.getAttribute("type");
            this.value = responseElement.getAttribute("value");
            this.statusCode = responseElement.getAttribute("status-code");
            this.saveLastView = "true".equals(responseElement.getAttribute("save-last-view"));
            this.saveCurrentView = "true".equals(responseElement.getAttribute("save-current-view"));
            this.saveHomeView = "true".equals(responseElement.getAttribute("save-home-view"));
            for (Element redirectParameterElement : UtilXml.childElementList(responseElement, "redirect-parameter")) {
                if (UtilValidate.isNotEmpty(redirectParameterElement.getAttribute("value"))) {
                    this.redirectParameterValueMap.put(redirectParameterElement.getAttribute("name"), redirectParameterElement.getAttribute("value"));
                } else {
                    String from = redirectParameterElement.getAttribute("from");
                    if (UtilValidate.isEmpty(from))
                        from = redirectParameterElement.getAttribute("name");
                    this.redirectParameterMap.put(redirectParameterElement.getAttribute("name"), from);
                }
            }
        }
    }

    /**
     * @ClassName: ViewMap 
     * @Description: view-map元素解析，没有子元素
     * @author: 祖国
     * @date: 2015年12月17日 下午6:14:32
     */
    public static class ViewMap {
        public String viewMap;
        public String name;
        public String page;
        public String type;
        public String info;
        public String contentType;
        public String encoding;
        public String description;
        public boolean noCache = false;

        public ViewMap(Element viewMapElement) {
            this.name = viewMapElement.getAttribute("name");
            this.page = viewMapElement.getAttribute("page");
            this.type = viewMapElement.getAttribute("type");
            this.info = viewMapElement.getAttribute("info");
            this.contentType = viewMapElement.getAttribute("content-type");
            this.noCache = "true".equals(viewMapElement.getAttribute("no-cache"));
            this.encoding = viewMapElement.getAttribute("encoding");
            this.description = UtilXml.childElementValue(viewMapElement, "description");
            if (UtilValidate.isEmpty(this.page)) {
                this.page = this.name;
            }
        }
    }
}
