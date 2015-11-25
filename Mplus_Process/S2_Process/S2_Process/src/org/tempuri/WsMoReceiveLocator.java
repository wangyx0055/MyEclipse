/**
 * WsMoReceiveLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class WsMoReceiveLocator extends org.apache.axis.client.Service implements org.tempuri.WsMoReceive {

    public WsMoReceiveLocator() {
    }


    public WsMoReceiveLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WsMoReceiveLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WsMoReceiveSoap12
    private java.lang.String WsMoReceiveSoap12_address = "http://sms.vgl.com.vn:8888/WsMoReceive.asmx";

    public java.lang.String getWsMoReceiveSoap12Address() {
        return WsMoReceiveSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WsMoReceiveSoap12WSDDServiceName = "WsMoReceiveSoap12";

    public java.lang.String getWsMoReceiveSoap12WSDDServiceName() {
        return WsMoReceiveSoap12WSDDServiceName;
    }

    public void setWsMoReceiveSoap12WSDDServiceName(java.lang.String name) {
        WsMoReceiveSoap12WSDDServiceName = name;
    }

    public org.tempuri.WsMoReceiveSoap getWsMoReceiveSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WsMoReceiveSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWsMoReceiveSoap12(endpoint);
    }

    public org.tempuri.WsMoReceiveSoap getWsMoReceiveSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.WsMoReceiveSoap12Stub _stub = new org.tempuri.WsMoReceiveSoap12Stub(portAddress, this);
            _stub.setPortName(getWsMoReceiveSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWsMoReceiveSoap12EndpointAddress(java.lang.String address) {
        WsMoReceiveSoap12_address = address;
    }


    // Use to get a proxy class for WsMoReceiveSoap
    private java.lang.String WsMoReceiveSoap_address = "http://sms.vgl.com.vn:8888/WsMoReceive.asmx";

    public java.lang.String getWsMoReceiveSoapAddress() {
        return WsMoReceiveSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WsMoReceiveSoapWSDDServiceName = "WsMoReceiveSoap";

    public java.lang.String getWsMoReceiveSoapWSDDServiceName() {
        return WsMoReceiveSoapWSDDServiceName;
    }

    public void setWsMoReceiveSoapWSDDServiceName(java.lang.String name) {
        WsMoReceiveSoapWSDDServiceName = name;
    }

    public org.tempuri.WsMoReceiveSoap getWsMoReceiveSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WsMoReceiveSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWsMoReceiveSoap(endpoint);
    }

    public org.tempuri.WsMoReceiveSoap getWsMoReceiveSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.WsMoReceiveSoapStub _stub = new org.tempuri.WsMoReceiveSoapStub(portAddress, this);
            _stub.setPortName(getWsMoReceiveSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWsMoReceiveSoapEndpointAddress(java.lang.String address) {
        WsMoReceiveSoap_address = address;
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
            if (org.tempuri.WsMoReceiveSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.WsMoReceiveSoap12Stub _stub = new org.tempuri.WsMoReceiveSoap12Stub(new java.net.URL(WsMoReceiveSoap12_address), this);
                _stub.setPortName(getWsMoReceiveSoap12WSDDServiceName());
                return _stub;
            }
            if (org.tempuri.WsMoReceiveSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.WsMoReceiveSoapStub _stub = new org.tempuri.WsMoReceiveSoapStub(new java.net.URL(WsMoReceiveSoap_address), this);
                _stub.setPortName(getWsMoReceiveSoapWSDDServiceName());
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
        if ("WsMoReceiveSoap12".equals(inputPortName)) {
            return getWsMoReceiveSoap12();
        }
        else if ("WsMoReceiveSoap".equals(inputPortName)) {
            return getWsMoReceiveSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "WsMoReceive");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "WsMoReceiveSoap12"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "WsMoReceiveSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WsMoReceiveSoap12".equals(portName)) {
            setWsMoReceiveSoap12EndpointAddress(address);
        }
        else 
if ("WsMoReceiveSoap".equals(portName)) {
            setWsMoReceiveSoapEndpointAddress(address);
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
