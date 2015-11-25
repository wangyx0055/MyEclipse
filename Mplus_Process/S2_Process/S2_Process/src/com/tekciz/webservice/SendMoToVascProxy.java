package com.tekciz.webservice;

public class SendMoToVascProxy implements com.tekciz.webservice.SendMoToVasc {
  private String _endpoint = null;
  private com.tekciz.webservice.SendMoToVasc sendMoToVasc = null;
  
  public SendMoToVascProxy() {
    _initSendMoToVascProxy();
  }
  
  public SendMoToVascProxy(String endpoint) {
    _endpoint = endpoint;
    _initSendMoToVascProxy();
  }
  
  private void _initSendMoToVascProxy() {
    try {
      sendMoToVasc = (new com.tekciz.webservice.SendMoToVascServiceLocator()).getSendMoToVasc();
      if (sendMoToVasc != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sendMoToVasc)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sendMoToVasc)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sendMoToVasc != null)
      ((javax.xml.rpc.Stub)sendMoToVasc)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.tekciz.webservice.SendMoToVasc getSendMoToVasc() {
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc;
  }
  
  public int updateStatus(java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException{
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc.updateStatus(sMsisdn, iStatus);
  }
  
  public int sendMo(java.lang.String sUser, java.lang.String sPass, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, java.lang.String sRequestId, java.lang.String sOperator, int iRegisType, int iMulti) throws java.rmi.RemoteException{
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc.sendMo(sUser, sPass, sMsisdn, sServiceNumber, sServiceCode, sInfo, sRequestId, sOperator, iRegisType, iMulti);
  }
  
  
}