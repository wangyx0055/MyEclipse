package org.tempuri;

public class SendMOICom2HTSSoapProxy implements org.tempuri.SendMOICom2HTSSoap {
  private String _endpoint = null;
  private org.tempuri.SendMOICom2HTSSoap sendMOICom2HTSSoap = null;
  
  public SendMOICom2HTSSoapProxy() {
    _initSendMOICom2HTSSoapProxy();
  }
  
  public SendMOICom2HTSSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initSendMOICom2HTSSoapProxy();
  }
  
  private void _initSendMOICom2HTSSoapProxy() {
    try {
      sendMOICom2HTSSoap = (new org.tempuri.SendMOICom2HTSLocator()).getSendMOICom2HTSSoap();
      if (sendMOICom2HTSSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sendMOICom2HTSSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sendMOICom2HTSSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sendMOICom2HTSSoap != null)
      ((javax.xml.rpc.Stub)sendMOICom2HTSSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.SendMOICom2HTSSoap getSendMOICom2HTSSoap() {
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap;
  }
  
  public java.lang.String ketQuaDiemTNPT(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matinh, java.lang.String sbd) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDiemTNPT(username, password, requestID, serviceID, mobicode, commandCode, matinh, sbd);
  }
  
  public java.lang.String ketQuaDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDiemDH(username, password, requestID, serviceID, mobicode, commandCode, sbd);
  }
  
  public java.lang.String ketQuaDiemCHuanDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDiemCHuanDH(username, password, requestID, serviceID, mobicode, commandCode, matruong);
  }
  
  public java.lang.String ketQuaViTriDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaViTriDH(username, password, requestID, serviceID, mobicode, commandCode, sbd);
  }
  
  public java.lang.String ketQuaNV2MaTruongDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaNV2MaTruongDH(username, password, requestID, serviceID, mobicode, commandCode, matruong);
  }
  
  public java.lang.String ketQuaNV2MucDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String mucdiem) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaNV2MucDiemDH(username, password, requestID, serviceID, mobicode, commandCode, mucdiem);
  }
  
  public java.lang.String ketQuaDapAnDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String makhoi, java.lang.String mamon, java.lang.String made) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDapAnDH(username, password, requestID, serviceID, mobicode, commandCode, makhoi, mamon, made);
  }
  
  public java.lang.String ketQuaDapAnCD(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String makhoi, java.lang.String mamon, java.lang.String made) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDapAnCD(username, password, requestID, serviceID, mobicode, commandCode, makhoi, mamon, made);
  }
  
  public java.lang.String ketQuaDBDoTruot(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaDBDoTruot(username, password, requestID, serviceID, mobicode, commandCode, sbd);
  }
  
  public java.lang.String ketQuaMucDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong, java.lang.String mucdiem) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaMucDiemDH(username, password, requestID, serviceID, mobicode, commandCode, matruong, mucdiem);
  }
  
  public java.lang.String ketQuaChiTieuTS(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaChiTieuTS(username, password, requestID, serviceID, mobicode, commandCode, matruong);
  }
  
  public java.lang.String ketQuaTYLECHOI(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong, java.lang.String namts) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.ketQuaTYLECHOI(username, password, requestID, serviceID, mobicode, commandCode, matruong, namts);
  }
  
  public int checkMaTruongDH(java.lang.String username, java.lang.String password, java.lang.String matruong) throws java.rmi.RemoteException{
    if (sendMOICom2HTSSoap == null)
      _initSendMOICom2HTSSoapProxy();
    return sendMOICom2HTSSoap.checkMaTruongDH(username, password, matruong);
  }
  
  
}