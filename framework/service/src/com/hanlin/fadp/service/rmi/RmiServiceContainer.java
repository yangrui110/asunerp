
package com.hanlin.fadp.service.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hanlin.fadp.base.container.Container;
import com.hanlin.fadp.base.container.ContainerConfig;
import com.hanlin.fadp.base.container.ContainerException;
import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ServiceContainer;

/**
 * RMI Service Engine Container / Dispatcher(目前还不知道这个是做什么的)
 */
public class RmiServiceContainer implements Container {

    public static final String module = RmiServiceContainer.class.getName();

    protected RemoteDispatcherImpl remote = null;
    protected String configFile = null;
    protected String name = null;
    private String containerName;
    // Container methods

    @Override
    public void init(String[] args, String name, String configFile) {
        this.containerName = name;
        this.configFile = configFile;
    }

    public boolean start() throws ContainerException {
        // get the container config
        ContainerConfig.Container cfg = ContainerConfig.getContainer(containerName, configFile);
        ContainerConfig.Container.Property initialCtxProp = cfg.getProperty("use-initial-context");
        ContainerConfig.Container.Property lookupHostProp = cfg.getProperty("bound-host");
        ContainerConfig.Container.Property lookupPortProp = cfg.getProperty("bound-port");
        ContainerConfig.Container.Property lookupNameProp = cfg.getProperty("bound-name");
        ContainerConfig.Container.Property delegatorProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property clientProp = cfg.getProperty("client-factory");
        ContainerConfig.Container.Property serverProp = cfg.getProperty("server-factory");

        // check the required lookup-name property
        if (lookupNameProp == null || UtilValidate.isEmpty(lookupNameProp.value)) {
            throw new ContainerException("Invalid lookup-name defined in container configuration");
        } else {
            this.name = lookupNameProp.value;
        }

        // check the required delegator-name property
        if (delegatorProp == null || UtilValidate.isEmpty(delegatorProp.value)) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        }

        String useCtx = initialCtxProp == null || initialCtxProp.value == null ? "false" : initialCtxProp.value;
        String host = lookupHostProp == null || lookupHostProp.value == null ? "localhost" : lookupHostProp.value;
        String port = lookupPortProp == null || lookupPortProp.value == null ? "1100" : lookupPortProp.value;
        if (Start.getInstance().getConfig().portOffset != 0) {
            Integer portValue = Integer.valueOf(port);
            portValue += Start.getInstance().getConfig().portOffset;
            port = portValue.toString();
        }                
        String keystore = ContainerConfig.getPropertyValue(cfg, "ssl-keystore", null);
        String ksType = ContainerConfig.getPropertyValue(cfg, "ssl-keystore-type", "JKS");
        String ksPass = ContainerConfig.getPropertyValue(cfg, "ssl-keystore-pass", null);
        String ksAlias = ContainerConfig.getPropertyValue(cfg, "ssl-keystore-alias", null);
        boolean clientAuth = ContainerConfig.getPropertyValue(cfg, "ssl-client-auth", false);

        // setup the factories
        RMIClientSocketFactory csf = null;
        RMIServerSocketFactory ssf = null;

        // get the classloader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // load the factories
        if (clientProp != null && UtilValidate.isNotEmpty(clientProp.value)) {
            try {
                Class<?> c = loader.loadClass(clientProp.value);
                csf = (RMIClientSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }
        if (serverProp != null && UtilValidate.isNotEmpty(serverProp.value)) {
            try {
                Class<?> c = loader.loadClass(serverProp.value);
                ssf = (RMIServerSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }

        // set the client auth flag on our custom SSL socket factory
        if (ssf != null && ssf instanceof com.hanlin.fadp.service.rmi.socket.ssl.SSLServerSocketFactory) {
            ((com.hanlin.fadp.service.rmi.socket.ssl.SSLServerSocketFactory) ssf).setNeedClientAuth(clientAuth);
            ((com.hanlin.fadp.service.rmi.socket.ssl.SSLServerSocketFactory) ssf).setKeyStoreAlias(ksAlias);
            if (keystore != null) {
                ((com.hanlin.fadp.service.rmi.socket.ssl.SSLServerSocketFactory) ssf).setKeyStore(keystore, ksType, ksPass);
            }
        }

        // get the delegator for this container
        Delegator delegator = DelegatorFactory.getDelegator(delegatorProp.value);

        // create the LocalDispatcher
        LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(name, delegator);

        // create the RemoteDispatcher
        try {
            remote = new RemoteDispatcherImpl(dispatcher, csf, ssf);
        } catch (RemoteException e) {
            throw new ContainerException("Unable to start the RMI dispatcher", e);
        }

        if (!useCtx.equalsIgnoreCase("true")) {
            // bind RMIDispatcher to RMI Naming (Must be JRMP protocol)
            try {
                Naming.rebind("//" + host + ":" + port + "/" + name, remote);
            } catch (RemoteException e) {
                throw new ContainerException("Unable to bind RMIDispatcher to RMI on " + "//host[" + host + "]:port[" + port + "]/name[" + name + "] - with remote=" + remote , e);
            } catch (java.net.MalformedURLException e) {
                throw new ContainerException("Invalid URL for binding", e);
            }
        } else {
            // bind RMIDispatcher to InitialContext (must be RMI protocol not IIOP)
            try {
            	System.out.println(System.getProperties());
                InitialContext ic = new InitialContext();
                ic.rebind(name, remote);
            } catch (NamingException e) {
                throw new ContainerException("Unable to bind RMIDispatcher to JNDI", e);
            }

            // check JNDI
            try {
                InitialContext ic = new InitialContext();
                Object o = ic.lookup(name);
                if (o == null) {
                    throw new NamingException("Object came back null");
                }
            } catch (NamingException e) {
                throw new ContainerException("Unable to lookup bound objects", e);
            }
        }

        return true;
    }

    public void stop() throws ContainerException {
        remote.deregister();
    }

    public String getName() {
        return containerName;
    }
}
