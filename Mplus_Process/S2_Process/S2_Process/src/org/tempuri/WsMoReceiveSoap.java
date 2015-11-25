/**
 * WsMoReceiveSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface WsMoReceiveSoap extends java.rmi.Remote {
    public int moReveive(java.lang.String sUserName, java.lang.String sPassword, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, int iRequestId, java.lang.String sOperator, int iChannel_Type) throws java.rmi.RemoteException;
}
