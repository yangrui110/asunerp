/**
 * DocServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.services.DocService;

public interface DocServicePortType extends java.rmi.Remote {
    public int deleteDoc(int in0, String in1) throws java.rmi.RemoteException;
    public weaver.docs.webservices.FileInfo[] getFilesByDocs(String in0, String in1) throws java.rmi.RemoteException;
    public int createDoc(weaver.docs.webservices.DocInfo in0, String in1) throws java.rmi.RemoteException;
    public weaver.docs.webservices.DocInfo[] getList(String in0, int in1, int in2) throws java.rmi.RemoteException;
    public weaver.docs.webservices.DocInfo getDoc(int in0, String in1) throws java.rmi.RemoteException;
    public int getDocCount(String in0) throws java.rmi.RemoteException;
    public String login(String in0, String in1, int in2, String in3) throws java.rmi.RemoteException;
    public int updateDoc(weaver.docs.webservices.DocInfo in0, String in1) throws java.rmi.RemoteException;
}
