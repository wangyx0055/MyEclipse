/**
 * CSWebServicesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ipcomsa.cs.webservices;

public class CSWebServicesServiceLocator extends org.apache.axis.client.Service implements com.ipcomsa.cs.webservices.CSWebServicesService {

    public CSWebServicesServiceLocator() {
    }


    public CSWebServicesServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CSWebServicesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CSWebServicesPort
    private java.lang.String CSWebServicesPort_address = "http://CS-DB01:8080/CSWebServices/CSWebServices";

    public java.lang.String getCSWebServicesPortAddress() {
        return CSWebServicesPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CSWebServicesPortWSDDServiceName = "CSWebServicesPort";

    public java.lang.String getCSWebServicesPortWSDDServiceName() {
        return CSWebServicesPortWSDDServiceName;
    }

    public void setCSWebServicesPortWSDDServiceName(java.lang.String name) {
        CSWebServicesPortWSDDServiceName = name;
    }

    public com.ipcomsa.cs.webservices.CSWebServices getCSWebServicesPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CSWebServicesPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCSWebServicesPort(endpoint);
    }

    public com.ipcomsa.cs.webservices.CSWebServices getCSWebServicesPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.ipcomsa.cs.webservices.CSWebServicesBindingStub _stub = new com.ipcomsa.cs.webservices.CSWebServicesBindingStub(portAddress, this);
            _stub.setPortName(getCSWebServicesPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCSWebServicesPortEndpointAddress(java.lang.String address) {
        CSWebServicesPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.ipcomsa.cs.webservices.CSWebServices.class.isAssignableFrom(serviceEndpointInterface)) {
                com.ipcomsa.cs.webservices.CSWebServicesBindingStub _stub = new com.ipcomsa.cs.webservices.CSWebServicesBindingStub(new java.net.URL(CSWebServicesPort_address), this);
                _stub.setPortName(getCSWebServicesPortWSDDServiceName());
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
        if ("CSWebServicesPort".equals(inputPortName)) {
            return getCSWebServicesPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservices.cs.ipcomsa.com/", "CSWebServicesService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservices.cs.ipcomsa.com/", "CSWebServicesPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CSWebServicesPort".equals(portName)) {
            setCSWebServicesPortEndpointAddress(address);
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
