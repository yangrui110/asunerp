
package com.hanlin.fadp.service.rmi;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import com.hanlin.fadp.service.GenericServiceException;
import com.hanlin.fadp.service.ModelService;
import com.hanlin.fadp.service.ServiceDispatcher;
import com.hanlin.fadp.service.engine.GenericAsyncEngine;

/**
 * RmiServiceEngine.java (感觉像是对应于客户端)
 */
public class RmiServiceEngine extends GenericAsyncEngine {

    public RmiServiceEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        return run(modelService, context);
    }

    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        run(modelService, context);
    }

    protected Map<String, Object> run(ModelService service, Map<String, Object> context) throws GenericServiceException {
        // locate the remote dispatcher
        RemoteDispatcher rd = null;
        try {
            rd = (RemoteDispatcher) Naming.lookup(this.getLocation(service));
        } catch (NotBoundException e) {
            throw new GenericServiceException("RemoteDispatcher not bound to : " + service.location, e);
        } catch (java.net.MalformedURLException e) {
            throw new GenericServiceException("Invalid format for location");
        } catch (RemoteException e) {
            throw new GenericServiceException("RMI Error", e);
        }

        Map<String, Object> result = null;
        if (rd != null) {
            try {
                result = rd.runSync(service.invoke, context);
            } catch (RemoteException e) {
                throw new GenericServiceException("RMI Invocation Error", e);
            }
        } else {
            throw new GenericServiceException("RemoteDispatcher came back as null");
        }

        if (result == null) {
            throw new GenericServiceException("Null result returned");
        }

        return result;
    }
}
