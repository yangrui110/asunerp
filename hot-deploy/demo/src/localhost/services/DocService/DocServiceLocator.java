/**
 * DocServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.services.DocService;

public class DocServiceLocator extends org.apache.axis.client.Service implements localhost.services.DocService.DocService {

    public DocServiceLocator() {
    }


    public DocServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DocServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DocServiceHttpPort
    private String DocServiceHttpPort_address = "http://101.132.38.102:8089/services/DocService";

    public String getDocServiceHttpPortAddress() {
        return DocServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String DocServiceHttpPortWSDDServiceName = "DocServiceHttpPort";

    public String getDocServiceHttpPortWSDDServiceName() {
        return DocServiceHttpPortWSDDServiceName;
    }

    public void setDocServiceHttpPortWSDDServiceName(String name) {
        DocServiceHttpPortWSDDServiceName = name;
    }

    public localhost.services.DocService.DocServicePortType getDocServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DocServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDocServiceHttpPort(endpoint);
    }

    public localhost.services.DocService.DocServicePortType getDocServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            localhost.services.DocService.DocServiceHttpBindingStub _stub = new localhost.services.DocService.DocServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getDocServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDocServiceHttpPortEndpointAddress(String address) {
        DocServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (localhost.services.DocService.DocServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                localhost.services.DocService.DocServiceHttpBindingStub _stub = new localhost.services.DocService.DocServiceHttpBindingStub(new java.net.URL(DocServiceHttpPort_address), this);
                _stub.setPortName(getDocServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("DocServiceHttpPort".equals(inputPortName)) {
            return getDocServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost/services/DocService", "DocService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost/services/DocService", "DocServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("DocServiceHttpPort".equals(portName)) {
            setDocServiceHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
