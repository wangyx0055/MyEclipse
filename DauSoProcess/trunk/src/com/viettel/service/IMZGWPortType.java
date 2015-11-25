/**
 * IMZGWPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.viettel.service;

public interface IMZGWPortType extends java.rmi.Remote {
    public java.lang.String processCRBTs(java.lang.String msisdn, java.lang.String providerId, java.lang.String serviceId, java.lang.String userName, java.lang.String password, java.lang.String amount, java.lang.String reqTime, java.lang.String command, java.lang.String contents, java.lang.String sourceType) throws java.rmi.RemoteException;
}
