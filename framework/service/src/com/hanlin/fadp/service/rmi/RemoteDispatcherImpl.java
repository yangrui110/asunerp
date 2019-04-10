
package com.hanlin.fadp.service.rmi;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.util.EntityUtilProperties;
import com.hanlin.fadp.service.GenericRequester;
import com.hanlin.fadp.service.GenericResultWaiter;
import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.LocalDispatcher;
import com.hanlin.fadp.service.ModelService;

/**
 * Generic Services Remote Dispatcher Implementation(这个类就对应于 Interface 的实现类)
 */
@SuppressWarnings("serial")
public class RemoteDispatcherImpl extends UnicastRemoteObject implements RemoteDispatcher {

    public static final String module = RemoteDispatcherImpl.class.getName();
    private static boolean exportAll = false;

    protected LocalDispatcher dispatcher = null;

    public RemoteDispatcherImpl(LocalDispatcher dispatcher, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(0, csf, ssf);
        this.dispatcher = dispatcher;
        Delegator delegator = dispatcher.getDelegator();
        exportAll = "true".equals(EntityUtilProperties.getPropertyValue("service", "remotedispatcher.exportall", "false", delegator));
    }

    // RemoteDispatcher methods

    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> context) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        return dispatcher.runSync(serviceName, context);
    }

    public Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> context, int transactionTimeout, boolean requireNewTransaction) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        return dispatcher.runSync(serviceName, context, transactionTimeout, requireNewTransaction);
    }

    public void runSyncIgnore(String serviceName, Map<String, ? extends Object> context) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runSyncIgnore(serviceName, context);
    }

    public void runSyncIgnore(String serviceName, Map<String, ? extends Object> context, int transactionTimeout, boolean requireNewTransaction) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runSyncIgnore(serviceName, context, transactionTimeout, requireNewTransaction);
    }

    public void runAsync(String serviceName, Map<String, ? extends Object> context, GenericRequester requester, boolean persist, int transactionTimeout, boolean requireNewTransaction) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runAsync(serviceName, context, requester, persist, transactionTimeout, requireNewTransaction);
    }

    public void runAsync(String serviceName, Map<String, ? extends Object> context, GenericRequester requester, boolean persist) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runAsync(serviceName, context, requester, persist);
    }

    public void runAsync(String serviceName, Map<String, ? extends Object> context, GenericRequester requester) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runAsync(serviceName, context, requester);
    }

    public void runAsync(String serviceName, Map<String, ? extends Object> context, boolean persist) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runAsync(serviceName, context, persist);
    }

    public void runAsync(String serviceName, Map<String, ? extends Object> context) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.runAsync(serviceName, context);
    }

    public GenericResultWaiter runAsyncWait(String serviceName, Map<String, ? extends Object> context, boolean persist) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        return dispatcher.runAsyncWait(serviceName, context, persist);
    }

    public GenericResultWaiter runAsyncWait(String serviceName, Map<String, ? extends Object> context) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        return dispatcher.runAsyncWait(serviceName, context);
    }

    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, count, endTime);
    }

    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, count);
    }

    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, endTime);
    }

    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime) throws GenericServiceException, RemoteException {
        this.checkExportFlag(serviceName);
        dispatcher.schedule(serviceName, context, startTime);
    }

    public void deregister() {
        dispatcher.deregister();
    }

    protected void checkExportFlag(String serviceName) throws GenericServiceException {
        ModelService model = dispatcher.getDispatchContext().getModelService(serviceName);
        if (!model.export && !exportAll) {
            // TODO: make this log on the server rather than the client
            //Debug.logWarning("Attempt to invoke a non-exported service: " + serviceName, module);
            throw new GenericServiceException("Cannot find requested service");
        }
    }

}
