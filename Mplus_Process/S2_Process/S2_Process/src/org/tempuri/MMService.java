/**
 * MMService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface MMService extends javax.xml.rpc.Service {
    public java.lang.String getMMServiceSoapAddress();

    public org.tempuri.MMServiceSoap getMMServiceSoap() throws javax.xml.rpc.ServiceException;

    public org.tempuri.MMServiceSoap getMMServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getMMServiceSoap12Address();

    public org.tempuri.MMServiceSoap getMMServiceSoap12() throws javax.xml.rpc.ServiceException;

    public org.tempuri.MMServiceSoap getMMServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
