/**
 * MMServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface MMServiceSoap extends java.rmi.Remote {
    public int moReceiver(java.lang.String userName, java.lang.String passWord, java.lang.String msIsdn, java.lang.String serviceNumber, java.lang.String serviceCode, java.lang.String msgInfo, java.lang.String requestId, java.lang.String oper, int channelType) throws java.rmi.RemoteException;
    public int cpSynstatus(java.lang.String userName, java.lang.String passWord, java.lang.String msIsdn, int iStatus) throws java.rmi.RemoteException;
}
