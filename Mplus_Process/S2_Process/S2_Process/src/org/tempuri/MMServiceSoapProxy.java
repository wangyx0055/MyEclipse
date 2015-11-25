package org.tempuri;

public class MMServiceSoapProxy implements org.tempuri.MMServiceSoap {
  private String _endpoint = null;
  private org.tempuri.MMServiceSoap mMServiceSoap = null;
  
  public MMServiceSoapProxy() {
    _initMMServiceSoapProxy();
  }
  
  public MMServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initMMServiceSoapProxy();
  }
  
  private void _initMMServiceSoapProxy() {
    try {
      mMServiceSoap = (new org.tempuri.MMServiceLocator()).getMMServiceSoap();
      if (mMServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)mMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)mMServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (mMServiceSoap != null)
      ((javax.xml.rpc.Stub)mMServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.MMServiceSoap getMMServiceSoap() {
    if (mMServiceSoap == null)
      _initMMServiceSoapProxy();
    return mMServiceSoap;
  }
  
  public int moReceiver(java.lang.String userName, java.lang.String passWord, java.lang.String msIsdn, java.lang.String serviceNumber, java.lang.String serviceCode, java.lang.String msgInfo, java.lang.String requestId, java.lang.String oper, int channelType) throws java.rmi.RemoteException{
    if (mMServiceSoap == null)
      _initMMServiceSoapProxy();
    return mMServiceSoap.moReceiver(userName, passWord, msIsdn, serviceNumber, serviceCode, msgInfo, requestId, oper, channelType);
  }
  
  public int cpSynstatus(java.lang.String userName, java.lang.String passWord, java.lang.String msIsdn, int iStatus) throws java.rmi.RemoteException{
    if (mMServiceSoap == null)
      _initMMServiceSoapProxy();
    return mMServiceSoap.cpSynstatus(userName, passWord, msIsdn, iStatus);
  }
  
  
}