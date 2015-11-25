package com.vasc.mc.service;

public class SendMoToVascProxy implements com.vasc.mc.service.SendMoToVasc {
  private String _endpoint = null;
  private com.vasc.mc.service.SendMoToVasc sendMoToVasc = null;
  
  public SendMoToVascProxy() {
    _initSendMoToVascProxy();
  }
  
  public SendMoToVascProxy(String endpoint) {
    _endpoint = endpoint;
    _initSendMoToVascProxy();
  }
  
  private void _initSendMoToVascProxy() {
    try {
      sendMoToVasc = (new com.vasc.mc.service.SendMoToVascServiceLocator()).getSendMoToVasc();
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
  
  public com.vasc.mc.service.SendMoToVasc getSendMoToVasc() {
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc;
  }
  
  public void main(java.lang.String[] args) throws java.rmi.RemoteException{
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    sendMoToVasc.main(args);
  }
  
  public int sendMo(java.lang.String sUser, java.lang.String sPass, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, java.lang.String sRequestId, java.lang.String sOperator, int sRegisType, int iMulti) throws java.rmi.RemoteException{
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc.sendMo(sUser, sPass, sMsisdn, sServiceNumber, sServiceCode, sInfo, sRequestId, sOperator, sRegisType, iMulti);
  }
  
  public int updateStatus(java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException{
    if (sendMoToVasc == null)
      _initSendMoToVascProxy();
    return sendMoToVasc.updateStatus(sMsisdn, iStatus);
  }
  
  
}