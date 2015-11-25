package com.goldsword.alao.soap.sync.server;

public class GoldSwordServiceProxy implements com.goldsword.alao.soap.sync.server.GoldSwordService_PortType {
  private String _endpoint = null;
  private com.goldsword.alao.soap.sync.server.GoldSwordService_PortType goldSwordService_PortType = null;
  
  public GoldSwordServiceProxy() {
    _initGoldSwordServiceProxy();
  }
  
  public GoldSwordServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initGoldSwordServiceProxy();
  }
  
  private void _initGoldSwordServiceProxy() {
    try {
      goldSwordService_PortType = (new com.goldsword.alao.soap.sync.server.GoldSwordService_ServiceLocator()).getGoldSwordService();
      if (goldSwordService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)goldSwordService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)goldSwordService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (goldSwordService_PortType != null)
      ((javax.xml.rpc.Stub)goldSwordService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.goldsword.alao.soap.sync.server.GoldSwordService_PortType getGoldSwordService_PortType() {
    if (goldSwordService_PortType == null)
      _initGoldSwordServiceProxy();
    return goldSwordService_PortType;
  }
  
  public com.goldsword.alao.soap.sync.rsp.SyncChargeResultRsp syncChargeResult(com.goldsword.alao.soap.sync.req.SyncChargeResultReq syncChargeResultReq) throws java.rmi.RemoteException{
    if (goldSwordService_PortType == null)
      _initGoldSwordServiceProxy();
    return goldSwordService_PortType.syncChargeResult(syncChargeResultReq);
  }
  
  public java.lang.String syncCharge(java.lang.String userId, java.lang.String serviceId, java.lang.String commandCode, java.lang.String info, java.lang.String requestId, java.lang.String receiveDate, java.lang.String operator, java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException{
    if (goldSwordService_PortType == null)
      _initGoldSwordServiceProxy();
    return goldSwordService_PortType.syncCharge(userId, serviceId, commandCode, info, requestId, receiveDate, operator, userName, password);
  }
  
  
}