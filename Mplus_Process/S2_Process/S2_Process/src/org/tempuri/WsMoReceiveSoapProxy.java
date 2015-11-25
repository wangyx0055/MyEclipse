package org.tempuri;

public class WsMoReceiveSoapProxy implements org.tempuri.WsMoReceiveSoap {
  private String _endpoint = null;
  private org.tempuri.WsMoReceiveSoap wsMoReceiveSoap = null;
  
  public WsMoReceiveSoapProxy() {
    _initWsMoReceiveSoapProxy();
  }
  
  public WsMoReceiveSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initWsMoReceiveSoapProxy();
  }
  
  private void _initWsMoReceiveSoapProxy() {
    try {
      wsMoReceiveSoap = (new org.tempuri.WsMoReceiveLocator()).getWsMoReceiveSoap();
      if (wsMoReceiveSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wsMoReceiveSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wsMoReceiveSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wsMoReceiveSoap != null)
      ((javax.xml.rpc.Stub)wsMoReceiveSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.WsMoReceiveSoap getWsMoReceiveSoap() {
    if (wsMoReceiveSoap == null)
      _initWsMoReceiveSoapProxy();
    return wsMoReceiveSoap;
  }
  
  public int moReveive(java.lang.String sUserName, java.lang.String sPassword, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, int iRequestId, java.lang.String sOperator, int iChannel_Type) throws java.rmi.RemoteException{
    if (wsMoReceiveSoap == null)
      _initWsMoReceiveSoapProxy();
    return wsMoReceiveSoap.moReveive(sUserName, sPassword, sMsisdn, sServiceNumber, sServiceCode, sInfo, iRequestId, sOperator, iChannel_Type);
  }
  
  
}