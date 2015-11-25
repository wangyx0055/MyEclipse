/**
 * GoldSwordService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.goldsword.alao.soap.sync.server;

public interface GoldSwordService_PortType extends java.rmi.Remote {
    public com.goldsword.alao.soap.sync.rsp.SyncChargeResultRsp syncChargeResult(com.goldsword.alao.soap.sync.req.SyncChargeResultReq syncChargeResultReq) throws java.rmi.RemoteException;
    public java.lang.String syncCharge(java.lang.String userId, java.lang.String serviceId, java.lang.String commandCode, java.lang.String info, java.lang.String requestId, java.lang.String receiveDate, java.lang.String operator, java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException;
}
