/**
 * SendMoToVasc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.tekciz.webservice;

public interface SendMoToVasc extends java.rmi.Remote {
    public int updateStatus(java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException;
    public int sendMo(java.lang.String sUser, java.lang.String sPass, java.lang.String sMsisdn, java.lang.String sServiceNumber, java.lang.String sServiceCode, java.lang.String sInfo, java.lang.String sRequestId, java.lang.String sOperator, int iRegisType, int iMulti) throws java.rmi.RemoteException;
}
