/**
 * ModeDateServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package localhost.services.ModeDateService;

public interface ModeDateServicePortType extends java.rmi.Remote {
    public String getModeDataByID(int in0, int in1, int in2, String in3, String in4) throws java.rmi.RemoteException;
    public String deleteModeDataById(int in0, int in1, int in2, String in3) throws java.rmi.RemoteException;
    public String getAllModeDataList(int in0, int in1, int in2, int in3, int in4, String in5, String in6, String in7) throws java.rmi.RemoteException;
    public String saveModeData(String in0) throws java.rmi.RemoteException;
    public int getAllModeDataCount(int in0, int in1, String in2, String in3) throws java.rmi.RemoteException;
}
