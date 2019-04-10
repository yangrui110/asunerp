package com.hanlin.fadp.webapp.website;

import javax.servlet.http.HttpServletRequest;

import com.hanlin.fadp.base.lang.ThreadSafe;
import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.util.EntityUtilProperties;

/**
 * Web site properties.
 */
/**
 * @ClassName: WebSiteProperties 
 * @Description: TODO
 * @author: 祖国
 * @date: 2015年12月21日 下午12:00:25
 */
@ThreadSafe
public final class WebSiteProperties {


    /**
     * Returns a <code>WebSiteProperties</code> instance initialized to the settings found
     * in the <code>url.properties</code> file.
     */
    public static WebSiteProperties defaults(Delegator delegator) {
        return new WebSiteProperties(delegator);
    }

    /**
     * Returns a <code>WebSiteProperties</code> instance initialized to the settings found
     * in the application's WebSite entity value. If the application does not have a
     * WebSite entity value then the instance is initialized to the settings found
     * in the <code>url.properties</code> file.
     * 
     * @param request
     * @throws GenericEntityException
     */
    /**
     * @Title: from 
     * @Description: 获取WebSiteProperties实例，先从request中通过_WEBSITE_PROPS_属性进行获取，
     * 如果为null，则从缓存中获取attribute为“delegator"的delegator，通过该delegator获取
     * webapp/config/url.properties文件来构建一个WebSiteProperties实例
     * @param request
     * @throws GenericEntityException
     * @return: WebSiteProperties
     */
    public static WebSiteProperties from(HttpServletRequest request) throws GenericEntityException {
        Assert.notNull("request", request);
        WebSiteProperties webSiteProps = (WebSiteProperties) request.getAttribute("_WEBSITE_PROPS_");
        if (webSiteProps == null) {
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            WebSiteProperties defaults = new WebSiteProperties(delegator);
            String httpPort = defaults.getHttpPort();
            String httpHost = defaults.getHttpHost();
            String httpsPort = defaults.getHttpsPort();
            String httpsHost = defaults.getHttpsHost();
            boolean enableHttps = defaults.getEnableHttps();
            if (delegator != null) {
                String webSiteId = WebSiteWorker.getWebSiteId(request);
                if (webSiteId != null) {
                    GenericValue webSiteValue = EntityQuery.use(delegator).from("WebSite").where("webSiteId", webSiteId).cache().queryOne();
                    if (webSiteValue != null) {
                        if (webSiteValue.get("httpPort") != null) {
                            httpPort = webSiteValue.getString("httpPort");
                        }
                        if (webSiteValue.get("httpHost") != null) {
                            httpHost = webSiteValue.getString("httpHost");
                        }
                        if (webSiteValue.get("httpsPort") != null) {
                            httpsPort = webSiteValue.getString("httpsPort");
                        }
                        if (webSiteValue.get("httpsHost") != null) {
                            httpsHost = webSiteValue.getString("httpsHost");
                        }
                        if (webSiteValue.get("enableHttps") != null) {
                            enableHttps = webSiteValue.getBoolean("enableHttps");
                        }
                    }
                }
            }
            if (httpPort.isEmpty() && !request.isSecure()) {
                httpPort = String.valueOf(request.getServerPort());
            }
            if (httpHost.isEmpty()) {
                httpHost = request.getServerName();
            }
            if (httpsPort.isEmpty() && request.isSecure()) {
                httpsPort = String.valueOf(request.getServerPort());
            }
            if (httpsHost.isEmpty()) {
                httpsHost = request.getServerName();
            }
            
            if (Start.getInstance().getConfig().portOffset != 0) {
                Integer httpPortValue = Integer.valueOf(httpPort);
                httpPortValue += Start.getInstance().getConfig().portOffset;
                httpPort = httpPortValue.toString();
                Integer httpsPortValue = Integer.valueOf(httpsPort);
                httpsPortValue += Start.getInstance().getConfig().portOffset;
                httpsPort = httpsPortValue.toString();
            }                
            
            webSiteProps = new WebSiteProperties(httpPort, httpHost, httpsPort, httpsHost, enableHttps);
            request.setAttribute("_WEBSITE_PROPS_", webSiteProps);
        }
        return webSiteProps;
    }

    /**
     * Returns a <code>WebSiteProperties</code> instance initialized to the settings found
     * in the WebSite entity value.
     * 
     * @param webSiteValue
     */
    public static WebSiteProperties from(GenericValue webSiteValue) {
        Assert.notNull("webSiteValue", webSiteValue);
        if (!"WebSite".equals(webSiteValue.getEntityName())) {
            throw new IllegalArgumentException("webSiteValue is not a WebSite entity value");
        }
        WebSiteProperties defaults = new WebSiteProperties(webSiteValue.getDelegator());
        String httpPort = (webSiteValue.get("httpPort") != null) ? webSiteValue.getString("httpPort") : defaults.getHttpPort();
        String httpHost = (webSiteValue.get("httpHost") != null) ? webSiteValue.getString("httpHost") : defaults.getHttpHost();
        String httpsPort = (webSiteValue.get("httpsPort") != null) ? webSiteValue.getString("httpsPort") : defaults.getHttpsPort();
        String httpsHost = (webSiteValue.get("httpsHost") != null) ? webSiteValue.getString("httpsHost") : defaults.getHttpsHost();
        boolean enableHttps = (webSiteValue.get("enableHttps") != null) ? webSiteValue.getBoolean("enableHttps") : defaults.getEnableHttps();

        if (Start.getInstance().getConfig().portOffset != 0) {
            Integer httpPortValue = Integer.valueOf(httpPort);
            httpPortValue += Start.getInstance().getConfig().portOffset;
            httpPort = httpPortValue.toString();
            Integer httpsPortValue = Integer.valueOf(httpsPort);
            httpsPortValue += Start.getInstance().getConfig().portOffset;
            httpsPort = httpsPortValue.toString();
        }                
        
        return new WebSiteProperties(httpPort, httpHost, httpsPort, httpsHost, enableHttps);
    }

    private final String httpPort;
    private final String httpHost;
    private final String httpsPort;
    private final String httpsHost;
    private final boolean enableHttps;

    private WebSiteProperties(Delegator delegator) {
        this.httpPort = EntityUtilProperties.getPropertyValue("url.properties", "port.http", delegator);
        this.httpHost = EntityUtilProperties.getPropertyValue("url.properties", "force.http.host", delegator);
        this.httpsPort = EntityUtilProperties.getPropertyValue("url.properties", "port.https", delegator);
        this.httpsHost = EntityUtilProperties.getPropertyValue("url.properties", "force.https.host", delegator);
        this.enableHttps = EntityUtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y", delegator);
    }

    private WebSiteProperties(String httpPort, String httpHost, String httpsPort, String httpsHost, boolean enableHttps) {
        this.httpPort = httpPort;
        this.httpHost = httpHost;
        this.httpsPort = httpsPort;
        this.httpsHost = httpsHost;
        this.enableHttps = enableHttps;
    }

    /**
     * Returns the configured http port, or an empty <code>String</code> if not configured.
     */
    public String getHttpPort() {
        return httpPort;
    }

    /**
     * Returns the configured http host, or an empty <code>String</code> if not configured.
     */
    public String getHttpHost() {
        return httpHost;
    }

    /**
     * Returns the configured https port, or an empty <code>String</code> if not configured.
     */
    public String getHttpsPort() {
        return httpsPort;
    }

    /**
     * Returns the configured https host, or an empty <code>String</code> if not configured.
     */
    public String getHttpsHost() {
        return httpsHost;
    }

    /**
     * Returns <code>true</code> if https is enabled.
     */
    public boolean getEnableHttps() {
        return enableHttps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{httpPort=");
        sb.append(httpPort).append(", ");
        sb.append("httpHost=").append(httpHost).append(", ");
        sb.append("httpsPort=").append(httpsPort).append(", ");
        sb.append("httpsHost=").append(httpsHost).append(", ");
        sb.append("enableHttps=").append(enableHttps).append("}");
        return sb.toString();
    }
}
