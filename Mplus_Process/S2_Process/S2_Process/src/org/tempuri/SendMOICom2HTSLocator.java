/**
 * SendMOICom2HTSLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class SendMOICom2HTSLocator extends org.apache.axis.client.Service implements org.tempuri.SendMOICom2HTS {

    public SendMOICom2HTSLocator() {
    }


    public SendMOICom2HTSLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SendMOICom2HTSLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SendMOICom2HTSSoap
    private java.lang.String SendMOICom2HTSSoap_address = "http://123.30.172.183:92/SendMOICom2HTS.asmx?WSDL";

    public java.lang.String getSendMOICom2HTSSoapAddress() {
        return SendMOICom2HTSSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SendMOICom2HTSSoapWSDDServiceName = "SendMOICom2HTSSoap";

    public java.lang.String getSendMOICom2HTSSoapWSDDServiceName() {
        return SendMOICom2HTSSoapWSDDServiceName;
    }

    public void setSendMOICom2HTSSoapWSDDServiceName(java.lang.String name) {
        SendMOICom2HTSSoapWSDDServiceName = name;
    }

    public org.tempuri.SendMOICom2HTSSoap getSendMOICom2HTSSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SendMOICom2HTSSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSendMOICom2HTSSoap(endpoint);
    }

    public org.tempuri.SendMOICom2HTSSoap getSendMOICom2HTSSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.SendMOICom2HTSSoapStub _stub = new org.tempuri.SendMOICom2HTSSoapStub(portAddress, this);
            _stub.setPortName(getSendMOICom2HTSSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSendMOICom2HTSSoapEndpointAddress(java.lang.String address) {
        SendMOICom2HTSSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.SendMOICom2HTSSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.SendMOICom2HTSSoapStub _stub = new org.tempuri.SendMOICom2HTSSoapStub(new java.net.URL(SendMOICom2HTSSoap_address), this);
                _stub.setPortName(getSendMOICom2HTSSoapWSDDServiceName());
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
        if ("SendMOICom2HTSSoap".equals(inputPortName)) {
            return getSendMOICom2HTSSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "SendMOICom2HTS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "SendMOICom2HTSSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SendMOICom2HTSSoap".equals(portName)) {
            setSendMOICom2HTSSoapEndpointAddress(address);
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
