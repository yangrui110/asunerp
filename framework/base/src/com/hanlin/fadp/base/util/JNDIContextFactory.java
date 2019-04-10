
package com.hanlin.fadp.base.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.config.JNDIConfigUtil;
import com.hanlin.fadp.base.util.cache.UtilCache;

/**
 * JNDIContextFactory - central source for JNDI Contexts by helper name
 *
 */
public class JNDIContextFactory {

    public static final String module = JNDIContextFactory.class.getName();
    // FIXME: InitialContext instances are not thread-safe! They should not be cached.
    private static final UtilCache<String, InitialContext> contexts = UtilCache.createUtilCache("entity.JNDIContexts", 0, 0);

    /**
     * Return the initial context according to the entityengine.xml parameters that correspond to the given prefix
     * @return the JNDI initial context
     */
    public static InitialContext getInitialContext(String jndiServerName) throws GenericConfigException {
        InitialContext ic = contexts.get(jndiServerName);

        if (ic == null) {
            JNDIConfigUtil.JndiServerInfo jndiServerInfo = JNDIConfigUtil.getJndiServerInfo(jndiServerName);

            if (jndiServerInfo == null) {
                throw new GenericConfigException("ERROR: no jndi-server definition was found with the name " + jndiServerName + " in jndiservers.xml");
            }

            try {
                if (UtilValidate.isEmpty(jndiServerInfo.contextProviderUrl)) {
                    ic = new InitialContext();
                } else {
                    Hashtable<String, Object> h = new Hashtable<String, Object>();

                    h.put(Context.INITIAL_CONTEXT_FACTORY, jndiServerInfo.initialContextFactory);
                    h.put(Context.PROVIDER_URL, jndiServerInfo.contextProviderUrl);
                    if (UtilValidate.isNotEmpty(jndiServerInfo.urlPkgPrefixes))
                        h.put(Context.URL_PKG_PREFIXES, jndiServerInfo.urlPkgPrefixes);

                    if (UtilValidate.isNotEmpty(jndiServerInfo.securityPrincipal))
                        h.put(Context.SECURITY_PRINCIPAL, jndiServerInfo.securityPrincipal);
                    if (UtilValidate.isNotEmpty(jndiServerInfo.securityCredentials))
                        h.put(Context.SECURITY_CREDENTIALS, jndiServerInfo.securityCredentials);

                    ic = new InitialContext(h);
                }
            } catch (Exception e) {
                String errorMsg = "Error getting JNDI initial context for server name " + jndiServerName;

                Debug.logError(e, errorMsg, module);
                throw new GenericConfigException(errorMsg, e);
            }

            if (ic != null) {
                ic = contexts.putIfAbsentAndGet(jndiServerName, ic);
            }
        }

        return ic;
    }
    /**
     * Removes an entry from the JNDI cache.
     * @param jndiServerName
     */
    public static void clearInitialContext(String jndiServerName) {
        contexts.remove(jndiServerName);
    }

}
