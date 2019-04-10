

package com.hanlin.fadp.service.rmi.socket.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLServerSocket;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.SSLUtil;

/**
 * RMI SSL Server Socket Factory
 */
@SuppressWarnings("serial")
public class SSLServerSocketFactory implements RMIServerSocketFactory, Serializable {

    public static final String module =  SSLServerSocketFactory.class.getName();
    protected boolean clientAuth = false;
    protected String keystore = null;
    protected String ksType = null;
    protected String ksPass = null;
    protected String alias = null;

    public void setNeedClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
    }

    public void setKeyStore(String location, String type, String password) {
        this.keystore = location;
        this.ksType = type;
        this.ksPass = password;
    }

    public void setKeyStoreAlias(String alias) {
        this.alias = alias;
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        char[] passphrase = null;
        if (ksPass != null) {
            passphrase = ksPass.toCharArray();
        }
        // framework/base/config/fadprmi.jks
        KeyStore ks = null;
        if (keystore != null) {
            try {
                ks = KeyStore.getInstance(ksType);
                ks.load(new FileInputStream(keystore), passphrase);
            } catch (NoSuchAlgorithmException e) {
                Debug.logError(e, module);
                throw new IOException(e.getMessage());
            } catch (CertificateException e) {
                Debug.logError(e, module);
                throw new IOException(e.getMessage());
            } catch (KeyStoreException e) {
                Debug.logError(e, module);
                throw new IOException(e.getMessage());
            }
        }

        if (alias == null) {
            throw new IOException("SSL certificate alias cannot be null; MUST be set for SSLServerSocketFactory!");
        }

        javax.net.ssl.SSLServerSocketFactory factory = null;
        try {
            if (ks != null) {
                factory = SSLUtil.getSSLServerSocketFactory(ks, ksPass, alias);
            } else {
                factory = SSLUtil.getSSLServerSocketFactory(alias);
            }
        } catch (GeneralSecurityException e) {
            Debug.logError(e, "Error getting javax.net.ssl.SSLServerSocketFactory instance for Service Engine RMI calls: " + e.toString(), module);
            throw new IOException(e.toString());
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting javax.net.ssl.SSLServerSocketFactory instance for Service Engine RMI calls: " + e.toString(), module);
        }

        if (factory == null) {
            throw new IOException("Unable to obtain SSLServerSocketFactory for provided KeyStore");
        }

        SSLServerSocket socket = (SSLServerSocket) factory.createServerSocket(port);
        socket.setNeedClientAuth(clientAuth);
        return socket;
    }
}
