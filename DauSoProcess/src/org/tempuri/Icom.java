/**
 * Icom.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface Icom extends javax.xml.rpc.Service {
    public java.lang.String geticomSoapAddress();

    public org.tempuri.IcomSoap geticomSoap() throws javax.xml.rpc.ServiceException;

    public org.tempuri.IcomSoap geticomSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String geticomSoap12Address();

    public org.tempuri.IcomSoap geticomSoap12() throws javax.xml.rpc.ServiceException;

    public org.tempuri.IcomSoap geticomSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
