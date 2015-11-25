package MPlusServiceMobiTech;

public class MPlusPortProxy implements MPlusServiceMobiTech.MPlusPort {
  private String _endpoint = null;
  private MPlusServiceMobiTech.MPlusPort mPlusPort = null;
  
  public MPlusPortProxy() {
    _initMPlusPortProxy();
  }
  
  public MPlusPortProxy(String endpoint) {
    _endpoint = endpoint;
    _initMPlusPortProxy();
  }
  
  private void _initMPlusPortProxy() {
    try {
      mPlusPort = (new MPlusServiceMobiTech.MPlusLocator()).getMPlusPort();
      if (mPlusPort != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)mPlusPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)mPlusPort)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (mPlusPort != null)
      ((javax.xml.rpc.Stub)mPlusPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public MPlusServiceMobiTech.MPlusPort getMPlusPort() {
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort;
  }
  
  public int moReceiver(java.lang.String userName, java.lang.String password, java.lang.String msisdn, java.lang.String serviceNumber, java.lang.String serviceCode, java.lang.String info, java.lang.String requestId, java.lang.String operator, int channel_Type) throws java.rmi.RemoteException{
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort.moReceiver(userName, password, msisdn, serviceNumber, serviceCode, info, requestId, operator, channel_Type);
  }
  
  public int cpSynstatus(java.lang.String userName, java.lang.String password, java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException{
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort.cpSynstatus(userName, password, sMsisdn, iStatus);
  }
  
  
}