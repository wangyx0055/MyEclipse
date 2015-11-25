/**
 * IMZGWLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.viettel.service;

public class IMZGWLocator extends org.apache.axis.client.Service implements com.viettel.service.IMZGW {

    public IMZGWLocator() {
    }


    public IMZGWLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IMZGWLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IMZGWHttpSoap11Endpoint
    private java.lang.String IMZGWHttpSoap11Endpoint_address = "http://10.58.44.77:8081/process/services/IMZGW.IMZGWHttpSoap11Endpoint/";

    public java.lang.String getIMZGWHttpSoap11EndpointAddress() {
        return IMZGWHttpSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IMZGWHttpSoap11EndpointWSDDServiceName = "IMZGWHttpSoap11Endpoint";

    public java.lang.String getIMZGWHttpSoap11EndpointWSDDServiceName() {
        return IMZGWHttpSoap11EndpointWSDDServiceName;
    }

    public void setIMZGWHttpSoap11EndpointWSDDServiceName(java.lang.String name) {
        IMZGWHttpSoap11EndpointWSDDServiceName = name;
    }

    public com.viettel.service.IMZGWPortType getIMZGWHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IMZGWHttpSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIMZGWHttpSoap11Endpoint(endpoint);
    }

    public com.viettel.service.IMZGWPortType getIMZGWHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.viettel.service.IMZGWSoap11BindingStub _stub = new com.viettel.service.IMZGWSoap11BindingStub(portAddress, this);
            _stub.setPortName(getIMZGWHttpSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIMZGWHttpSoap11EndpointEndpointAddress(java.lang.String address) {
        IMZGWHttpSoap11Endpoint_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.viettel.service.IMZGWPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.viettel.service.IMZGWSoap11BindingStub _stub = new com.viettel.service.IMZGWSoap11BindingStub(new java.net.URL(IMZGWHttpSoap11Endpoint_address), this);
                _stub.setPortName(getIMZGWHttpSoap11EndpointWSDDServiceName());
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
        if ("IMZGWHttpSoap11Endpoint".equals(inputPortName)) {
            return getIMZGWHttpSoap11Endpoint();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.viettel.com", "IMZGW");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.viettel.com", "IMZGWHttpSoap11Endpoint"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IMZGWHttpSoap11Endpoint".equals(portName)) {
            setIMZGWHttpSoap11EndpointEndpointAddress(address);
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
