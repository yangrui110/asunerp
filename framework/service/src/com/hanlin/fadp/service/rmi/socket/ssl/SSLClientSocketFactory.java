

package com.hanlin.fadp.service.rmi.socket.ssl;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocketFactory;

import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.SSLUtil;
import com.hanlin.fadp.base.config.GenericConfigException;

/**
 * RMI SSL Client Socket Factory
 */
@SuppressWarnings("serial")
public class SSLClientSocketFactory implements RMIClientSocketFactory, Serializable {

    public static final String module = SSLClientSocketFactory.class.getName();

    public Socket createSocket(String host, int port) throws IOException {
        try {
            SSLSocketFactory factory = SSLUtil.getSSLSocketFactory();
            return factory.createSocket(host, port);
        } catch (GeneralSecurityException e) {
            Debug.logError(e, module);
            throw new IOException(e.getMessage());
        } catch (GenericConfigException e) {
            throw new IOException(e.getMessage());
        }
    }
}
