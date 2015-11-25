/**
 * Account_UpdateSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface Account_UpdateSoap extends java.rmi.Remote {
    public int updateAccount(java.lang.String username, java.lang.String password, java.lang.String phonenumber, java.lang.String nickname, byte[] img, java.lang.String nameOfimage, java.lang.String birthofday, java.lang.String favorite, java.lang.String province, int gender) throws java.rmi.RemoteException;
    public int activeAccount(java.lang.String username, java.lang.String password, java.lang.String phonenumber) throws java.rmi.RemoteException;
    public int updateChat(java.lang.String phonenumber, java.lang.String content, java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public java.lang.String updateVote(java.lang.String username, java.lang.String password, java.lang.String nameofsong, int pointToVote) throws java.rmi.RemoteException;
}
