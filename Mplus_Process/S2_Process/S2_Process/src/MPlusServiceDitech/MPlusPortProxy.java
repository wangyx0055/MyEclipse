package MPlusServiceDitech;

public class MPlusPortProxy implements MPlusServiceDitech.MPlusPort {
  private String _endpoint = null;
  private MPlusServiceDitech.MPlusPort mPlusPort = null;
  
  public MPlusPortProxy() {
    _initMPlusPortProxy();
  }
  
  public MPlusPortProxy(String endpoint) {
    _endpoint = endpoint;
    _initMPlusPortProxy();
  }
  
  private void _initMPlusPortProxy() {
    try {
      mPlusPort = (new MPlusServiceDitech.MPlusLocator()).getMPlusPort();
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
  
  public MPlusServiceDitech.MPlusPort getMPlusPort() {
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort;
  }
  
  public int sendMo(java.lang.String sUser, java.lang.String sPass, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, java.lang.String sRequestId, java.lang.String sOperator, int sRegisType, int iMulti) throws java.rmi.RemoteException{
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort.sendMo(sUser, sPass, sMsisdn, sServiceNumber, sServiceCode, sInfo, sRequestId, sOperator, sRegisType, iMulti);
  }
  
  public int updateStatus(java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException{
    if (mPlusPort == null)
      _initMPlusPortProxy();
    return mPlusPort.updateStatus(sMsisdn, iStatus);
  }
  
  
}