
package com.hanlin.fadp.service.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.hanlin.fadp.service.GenericServiceException;

/** An example of how to remotely access the Service Engine's RemoteDispatcher.
 *
 * The following files from OFBiz need to be on the client's classpath:
 * cache.properties
 * debug.properties
 * jsse.properties
 * ofbiz-base.jar
 * ofbiz-service-rmi.jar (copied and renamed from "ofbiz/framework/service/build/lib/ofbiz-service-rmi.raj" from an OFBiz build)
 *
 * The following third-party libraries (can be found in OFBiz) also need to be on the client's classpath:
 * commons-collections.jar
 * javolution.jar
 * jdbm.jar
 * log4j.jar
 *
 * Copy the truststore file framework/base/config/ofbizrmi-truststore.jks to the client
 *
 * Run the client specifying the path to the client truststore: -Djavax.net.ssl.trustStore=ofbizrmi-truststore.jks
 *
 */
public class ExampleRemoteClient {

    protected final static String RMI_URL = "rmi://localhost:1099/RMIDispatcher"; // change to match the remote server
    public static String getRMI_URL() {
		return RMI_URL;
	}
	


	protected RemoteDispatcher rd = null;

    public ExampleRemoteClient() {
        try {
            rd = (RemoteDispatcher) Naming.lookup(RMI_URL);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> runTestService() throws RemoteException, GenericServiceException {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("message", "Remote Service Test");
        return rd.runSync("testScv", context);
    }

    public static void main(String[] args) throws Exception {
    	
//    	Properties systemProps = System.getProperties();  
//    	systemProps.put( "javax.net.ssl.trustStore", "fadprmi-truststore.jks");  
//    	systemProps.put( "javax.net.ssl.trustStorePassword", "changeit");  
//    	System.setProperties(systemProps);
//    	
        ExampleRemoteClient rm = new ExampleRemoteClient();
        Map<String, Object> result = rm.runTestService();
        System.out.println("Service Result Map: " + result);
    }
}
