package org.tempuri;

public class Tv_updateSoapProxy implements org.tempuri.Tv_updateSoap {
  private String _endpoint = null;
  private org.tempuri.Tv_updateSoap tv_updateSoap = null;
  
  public Tv_updateSoapProxy() {
    _initTv_updateSoapProxy();
  }
  
  public Tv_updateSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initTv_updateSoapProxy();
  }
  
  private void _initTv_updateSoapProxy() {
    try {
      tv_updateSoap = (new org.tempuri.Tv_updateLocator()).gettv_updateSoap();
      if (tv_updateSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tv_updateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tv_updateSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tv_updateSoap != null)
      ((javax.xml.rpc.Stub)tv_updateSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.Tv_updateSoap getTv_updateSoap() {
    if (tv_updateSoap == null)
      _initTv_updateSoapProxy();
    return tv_updateSoap;
  }
  
  public java.lang.String voteSongById(java.lang.String username, java.lang.String password, java.lang.String keyword, java.lang.String tel, java.lang.String poinToVote) throws java.rmi.RemoteException{
    if (tv_updateSoap == null)
      _initTv_updateSoapProxy();
    return tv_updateSoap.voteSongById(username, password, keyword, tel, poinToVote);
  }
  
  public java.lang.String voteSongByName(java.lang.String username, java.lang.String password, java.lang.String keyword, java.lang.String tel, java.lang.String poinToVote) throws java.rmi.RemoteException{
    if (tv_updateSoap == null)
      _initTv_updateSoapProxy();
    return tv_updateSoap.voteSongByName(username, password, keyword, tel, poinToVote);
  }
  
  public java.lang.String searchSongHot(java.lang.String username, java.lang.String password, java.lang.String keyword, int numerOfSong) throws java.rmi.RemoteException{
    if (tv_updateSoap == null)
      _initTv_updateSoapProxy();
    return tv_updateSoap.searchSongHot(username, password, keyword, numerOfSong);
  }
  
  public java.lang.String searchSongName(java.lang.String username, java.lang.String password, java.lang.String keyword, int numerOfSong) throws java.rmi.RemoteException{
    if (tv_updateSoap == null)
      _initTv_updateSoapProxy();
    return tv_updateSoap.searchSongName(username, password, keyword, numerOfSong);
  }
  
  
}