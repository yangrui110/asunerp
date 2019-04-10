
package com.hanlin.fadp.base.container;

import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.util.RMIExtendedSocketFactory;

/**
 * NamingServiceContainer
 *
 */

public class NamingServiceContainer implements Container {

    public static final String module = NamingServiceContainer.class.getName();

    protected String configFileLocation = null;
    protected boolean isRunning = false;
    protected Registry registry = null;
    protected int namingPort = 1100;
    protected String namingHost = null;

    protected RMIExtendedSocketFactory rmiSocketFactory;

    private String name;

    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name =name;
        this.configFileLocation = configFile;

        ContainerConfig.Container cfg = ContainerConfig.getContainer(name, configFileLocation);

        // get the naming (JNDI) port
        
        ContainerConfig.Container.Property port = cfg.getProperty("port");
        if (port.value != null) {
            try {
                this.namingPort = Integer.parseInt(port.value) + Start.getInstance().getConfig().portOffset;
            } catch (Exception e) {
                throw new ContainerException("Invalid port defined in container [naming-container] configuration or as portOffset; not a valid int");
            }
        }

        // get the naming (JNDI) server
        ContainerConfig.Container.Property host = cfg.getProperty("host");
        if (host != null && host.value != null) {
            this.namingHost =  host.value ;
        }

        try {
            rmiSocketFactory = new RMIExtendedSocketFactory( namingHost );
        } catch ( UnknownHostException uhEx ) {
            throw new ContainerException("Invalid host defined in container [naming-container] configuration; not a valid IP address", uhEx);
        }

    }

    public boolean start() throws ContainerException {
        try {
            registry = LocateRegistry.createRegistry(namingPort, rmiSocketFactory, rmiSocketFactory);
        } catch (RemoteException e) {
            throw new ContainerException("Unable to locate naming service", e);
        }

        isRunning = true;
        return isRunning;
    }

    public void stop() throws ContainerException {
        if (isRunning) {
            try {
                isRunning = !UnicastRemoteObject.unexportObject(registry, true);
            } catch (NoSuchObjectException e) {
                throw new ContainerException("Unable to shutdown naming registry");
            }
        }
    }

    public String getName() {
        return name;
    }
}
