/**
 * GoldSwordService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.goldsword.alao.soap.sync.server;

public class GoldSwordService_ServiceLocator extends org.apache.axis.client.Service implements com.goldsword.alao.soap.sync.server.GoldSwordService_Service {

    public GoldSwordService_ServiceLocator() {
    }


    public GoldSwordService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GoldSwordService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GoldSwordService
    private java.lang.String GoldSwordService_address = "http://localhost:8080/management_game/services/GoldSwordService";

    public java.lang.String getGoldSwordServiceAddress() {
        return GoldSwordService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GoldSwordServiceWSDDServiceName = "GoldSwordService";

    public java.lang.String getGoldSwordServiceWSDDServiceName() {
        return GoldSwordServiceWSDDServiceName;
    }

    public void setGoldSwordServiceWSDDServiceName(java.lang.String name) {
        GoldSwordServiceWSDDServiceName = name;
    }

    public com.goldsword.alao.soap.sync.server.GoldSwordService_PortType getGoldSwordService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GoldSwordService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGoldSwordService(endpoint);
    }

    public com.goldsword.alao.soap.sync.server.GoldSwordService_PortType getGoldSwordService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.goldsword.alao.soap.sync.server.GoldSwordServiceSoapBindingStub _stub = new com.goldsword.alao.soap.sync.server.GoldSwordServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getGoldSwordServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGoldSwordServiceEndpointAddress(java.lang.String address) {
        GoldSwordService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.goldsword.alao.soap.sync.server.GoldSwordService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.goldsword.alao.soap.sync.server.GoldSwordServiceSoapBindingStub _stub = new com.goldsword.alao.soap.sync.server.GoldSwordServiceSoapBindingStub(new java.net.URL(GoldSwordService_address), this);
                _stub.setPortName(getGoldSwordServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GoldSwordService".equals(inputPortName)) {
            return getGoldSwordService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server.sync.soap.alao.goldsword.com", "GoldSwordService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server.sync.soap.alao.goldsword.com", "GoldSwordService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GoldSwordService".equals(portName)) {
            setGoldSwordServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
