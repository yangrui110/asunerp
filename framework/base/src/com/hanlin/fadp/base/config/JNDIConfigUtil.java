
package com.hanlin.fadp.base.config;

import java.util.concurrent.ConcurrentHashMap;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * JNDIConfigUtil
 *
 */
public class JNDIConfigUtil {

    public static final String module = JNDIConfigUtil.class.getName();
    public static final String JNDI_CONFIG_XML_FILENAME = "jndiservers.xml";
    private static final ConcurrentHashMap<String, JndiServerInfo> jndiServerInfos = new ConcurrentHashMap<String, JndiServerInfo>();

    private static Element getXmlRootElement() throws GenericConfigException {
        try {
            return ResourceLoader.getXmlRootElement(JNDIConfigUtil.JNDI_CONFIG_XML_FILENAME);
        } catch (GenericConfigException e) {
            throw new GenericConfigException("Could not get JNDI XML root element", e);
        }
    }

    static {
        try {
            initialize(getXmlRootElement());
        } catch (Exception e) {
            Debug.logError(e, "Error loading JNDI config XML file " + JNDI_CONFIG_XML_FILENAME, module);
        }
    }
    public static void initialize(Element rootElement) throws GenericConfigException {
        // jndi-server - jndiServerInfos
        for (Element curElement: UtilXml.childElementList(rootElement, "jndi-server")) {
            JndiServerInfo jndiServerInfo = new JndiServerInfo(curElement);

            jndiServerInfos.putIfAbsent(jndiServerInfo.name, jndiServerInfo);
        }
    }

    public static JndiServerInfo getJndiServerInfo(String name) {
        return jndiServerInfos.get(name);
    }

    public static final class JndiServerInfo {
        public final String name;
        public final String contextProviderUrl;
        public final String initialContextFactory;
        public final String urlPkgPrefixes;
        public final String securityPrincipal;
        public final String securityCredentials;

        public JndiServerInfo(Element element) {
            this.name = element.getAttribute("name");
            this.contextProviderUrl = element.getAttribute("context-provider-url");
            this.initialContextFactory = element.getAttribute("initial-context-factory");
            this.urlPkgPrefixes = element.getAttribute("url-pkg-prefixes");
            this.securityPrincipal = element.getAttribute("security-principal");
            this.securityCredentials = element.getAttribute("security-credentials");
        }
    }
}
