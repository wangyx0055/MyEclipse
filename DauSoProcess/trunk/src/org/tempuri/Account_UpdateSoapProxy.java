package org.tempuri;

public class Account_UpdateSoapProxy implements org.tempuri.Account_UpdateSoap {
  private String _endpoint = null;
  private org.tempuri.Account_UpdateSoap account_UpdateSoap = null;
  
  public Account_UpdateSoapProxy() {
    _initAccount_UpdateSoapProxy();
  }
  
  public Account_UpdateSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initAccount_UpdateSoapProxy();
  }
  
  private void _initAccount_UpdateSoapProxy() {
    try {
      account_UpdateSoap = (new org.tempuri.Account_UpdateLocator()).getAccount_UpdateSoap();
      if (account_UpdateSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)account_UpdateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)account_UpdateSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (account_UpdateSoap != null)
      ((javax.xml.rpc.Stub)account_UpdateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.Account_UpdateSoap getAccount_UpdateSoap() {
    if (account_UpdateSoap == null)
      _initAccount_UpdateSoapProxy();
    return account_UpdateSoap;
  }
  
  public int updateAccount(java.lang.String username, java.lang.String password, java.lang.String phonenumber, java.lang.String nickname, byte[] img, java.lang.String nameOfimage, java.lang.String birthofday, java.lang.String favorite, java.lang.String province, int gender) throws java.rmi.RemoteException{
    if (account_UpdateSoap == null)
      _initAccount_UpdateSoapProxy();
    return account_UpdateSoap.updateAccount(username, password, phonenumber, nickname, img, nameOfimage, birthofday, favorite, province, gender);
  }
  
  public int activeAccount(java.lang.String username, java.lang.String password, java.lang.String phonenumber) throws java.rmi.RemoteException{
    if (account_UpdateSoap == null)
      _initAccount_UpdateSoapProxy();
    return account_UpdateSoap.activeAccount(username, password, phonenumber);
  }
  
  public int updateChat(java.lang.String phonenumber, java.lang.String content, java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (account_UpdateSoap == null)
      _initAccount_UpdateSoapProxy();
    return account_UpdateSoap.updateChat(phonenumber, content, username, password);
  }
  
  public java.lang.String updateVote(java.lang.String username, java.lang.String password, java.lang.String nameofsong, int pointToVote) throws java.rmi.RemoteException{
    if (account_UpdateSoap == null)
      _initAccount_UpdateSoapProxy();
    return account_UpdateSoap.updateVote(username, password, nameofsong, pointToVote);
  }
  
  
}