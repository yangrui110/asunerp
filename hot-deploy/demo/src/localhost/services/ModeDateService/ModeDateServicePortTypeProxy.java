package localhost.services.ModeDateService;

public class ModeDateServicePortTypeProxy implements localhost.services.ModeDateService.ModeDateServicePortType {
  private String _endpoint = null;
  private localhost.services.ModeDateService.ModeDateServicePortType modeDateServicePortType = null;
  
  public ModeDateServicePortTypeProxy() {
    _initModeDateServicePortTypeProxy();
  }
  
  public ModeDateServicePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initModeDateServicePortTypeProxy();
  }
  
  private void _initModeDateServicePortTypeProxy() {
    try {
      modeDateServicePortType = (new ModeDateServiceLocator()).getModeDateServiceHttpPort();
      if (modeDateServicePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)modeDateServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)modeDateServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (modeDateServicePortType != null)
      ((javax.xml.rpc.Stub)modeDateServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public localhost.services.ModeDateService.ModeDateServicePortType getModeDateServicePortType() {
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType;
  }
  
  public String getModeDataByID(int in0, int in1, int in2, String in3, String in4) throws java.rmi.RemoteException{
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType.getModeDataByID(in0, in1, in2, in3, in4);
  }
  
  public String deleteModeDataById(int in0, int in1, int in2, String in3) throws java.rmi.RemoteException{
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType.deleteModeDataById(in0, in1, in2, in3);
  }
  
  public String getAllModeDataList(int in0, int in1, int in2, int in3, int in4, String in5, String in6, String in7) throws java.rmi.RemoteException{
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType.getAllModeDataList(in0, in1, in2, in3, in4, in5, in6, in7);
  }
  
  public String saveModeData(String in0) throws java.rmi.RemoteException{
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType.saveModeData(in0);
  }
  
  public int getAllModeDataCount(int in0, int in1, String in2, String in3) throws java.rmi.RemoteException{
    if (modeDateServicePortType == null)
      _initModeDateServicePortTypeProxy();
    return modeDateServicePortType.getAllModeDataCount(in0, in1, in2, in3);
  }
  
  
}