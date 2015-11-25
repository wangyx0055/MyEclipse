/**
 * GoldSwordService_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.goldsword.alao.soap.sync.server;

public interface GoldSwordService_Service extends javax.xml.rpc.Service {
    public java.lang.String getGoldSwordServiceAddress();

    public com.goldsword.alao.soap.sync.server.GoldSwordService_PortType getGoldSwordService() throws javax.xml.rpc.ServiceException;

    public com.goldsword.alao.soap.sync.server.GoldSwordService_PortType getGoldSwordService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
