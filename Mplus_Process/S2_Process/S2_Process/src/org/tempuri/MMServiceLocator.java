/**
 * MMServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class MMServiceLocator extends org.apache.axis.client.Service implements org.tempuri.MMService {

    public MMServiceLocator() {
    }


    public MMServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MMServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MMServiceSoap
    private java.lang.String MMServiceSoap_address = "http://nicetek.com.vn:8282/mplussrv/service.asmx";

    public java.lang.String getMMServiceSoapAddress() {
        return MMServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MMServiceSoapWSDDServiceName = "MMServiceSoap";

    public java.lang.String getMMServiceSoapWSDDServiceName() {
        return MMServiceSoapWSDDServiceName;
    }

    public void setMMServiceSoapWSDDServiceName(java.lang.String name) {
        MMServiceSoapWSDDServiceName = name;
    }

    public org.tempuri.MMServiceSoap getMMServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MMServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMMServiceSoap(endpoint);
    }

    public org.tempuri.MMServiceSoap getMMServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.MMServiceSoapStub _stub = new org.tempuri.MMServiceSoapStub(portAddress, this);
            _stub.setPortName(getMMServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMMServiceSoapEndpointAddress(java.lang.String address) {
        MMServiceSoap_address = address;
    }


    // Use to get a proxy class for MMServiceSoap12
    private java.lang.String MMServiceSoap12_address = "http://nicetek.com.vn:8282/mplussrv/service.asmx";

    public java.lang.String getMMServiceSoap12Address() {
        return MMServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MMServiceSoap12WSDDServiceName = "MMServiceSoap12";

    public java.lang.String getMMServiceSoap12WSDDServiceName() {
        return MMServiceSoap12WSDDServiceName;
    }

    public void setMMServiceSoap12WSDDServiceName(java.lang.String name) {
        MMServiceSoap12WSDDServiceName = name;
    }

    public org.tempuri.MMServiceSoap getMMServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MMServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMMServiceSoap12(endpoint);
    }

    public org.tempuri.MMServiceSoap getMMServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.MMServiceSoap12Stub _stub = new org.tempuri.MMServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getMMServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMMServiceSoap12EndpointAddress(java.lang.String address) {
        MMServiceSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.MMServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.MMServiceSoapStub _stub = new org.tempuri.MMServiceSoapStub(new java.net.URL(MMServiceSoap_address), this);
                _stub.setPortName(getMMServiceSoapWSDDServiceName());
                return _stub;
            }
            if (org.tempuri.MMServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.MMServiceSoap12Stub _stub = new org.tempuri.MMServiceSoap12Stub(new java.net.URL(MMServiceSoap12_address), this);
                _stub.setPortName(getMMServiceSoap12WSDDServiceName());
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
        if ("MMServiceSoap".equals(inputPortName)) {
            return getMMServiceSoap();
        }
        else if ("MMServiceSoap12".equals(inputPortName)) {
            return getMMServiceSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "MMService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "MMServiceSoap"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "MMServiceSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MMServiceSoap".equals(portName)) {
            setMMServiceSoapEndpointAddress(address);
        }
        else 
if ("MMServiceSoap12".equals(portName)) {
            setMMServiceSoap12EndpointAddress(address);
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
