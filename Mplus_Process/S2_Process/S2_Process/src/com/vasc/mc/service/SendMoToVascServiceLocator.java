/**
 * SendMoToVascServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.vasc.mc.service;

public class SendMoToVascServiceLocator extends org.apache.axis.client.Service implements com.vasc.mc.service.SendMoToVascService {

    public SendMoToVascServiceLocator() {
    }


    public SendMoToVascServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SendMoToVascServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SendMoToVasc
    private java.lang.String SendMoToVasc_address = "http://10.151.190.36:8080/SendMo/services/SendMoToVasc";

    public java.lang.String getSendMoToVascAddress() {
        return SendMoToVasc_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SendMoToVascWSDDServiceName = "SendMoToVasc";

    public java.lang.String getSendMoToVascWSDDServiceName() {
        return SendMoToVascWSDDServiceName;
    }

    public void setSendMoToVascWSDDServiceName(java.lang.String name) {
        SendMoToVascWSDDServiceName = name;
    }

    public com.vasc.mc.service.SendMoToVasc getSendMoToVasc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SendMoToVasc_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSendMoToVasc(endpoint);
    }

    public com.vasc.mc.service.SendMoToVasc getSendMoToVasc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.vasc.mc.service.SendMoToVascSoapBindingStub _stub = new com.vasc.mc.service.SendMoToVascSoapBindingStub(portAddress, this);
            _stub.setPortName(getSendMoToVascWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSendMoToVascEndpointAddress(java.lang.String address) {
        SendMoToVasc_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.vasc.mc.service.SendMoToVasc.class.isAssignableFrom(serviceEndpointInterface)) {
                com.vasc.mc.service.SendMoToVascSoapBindingStub _stub = new com.vasc.mc.service.SendMoToVascSoapBindingStub(new java.net.URL(SendMoToVasc_address), this);
                _stub.setPortName(getSendMoToVascWSDDServiceName());
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
        if ("SendMoToVasc".equals(inputPortName)) {
            return getSendMoToVasc();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.mc.vasc.com", "SendMoToVascService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.mc.vasc.com", "SendMoToVasc"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SendMoToVasc".equals(portName)) {
            setSendMoToVascEndpointAddress(address);
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
