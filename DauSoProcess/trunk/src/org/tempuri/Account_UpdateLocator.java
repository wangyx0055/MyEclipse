/**
 * Account_UpdateLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Account_UpdateLocator extends org.apache.axis.client.Service implements org.tempuri.Account_Update {

    public Account_UpdateLocator() {
    }


    public Account_UpdateLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Account_UpdateLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Account_UpdateSoap
    private java.lang.String Account_UpdateSoap_address = "http://210.245.80.190:8091/sms/Account_Update.asmx";

    public java.lang.String getAccount_UpdateSoapAddress() {
        return Account_UpdateSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String Account_UpdateSoapWSDDServiceName = "Account_UpdateSoap";

    public java.lang.String getAccount_UpdateSoapWSDDServiceName() {
        return Account_UpdateSoapWSDDServiceName;
    }

    public void setAccount_UpdateSoapWSDDServiceName(java.lang.String name) {
        Account_UpdateSoapWSDDServiceName = name;
    }

    public org.tempuri.Account_UpdateSoap getAccount_UpdateSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Account_UpdateSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccount_UpdateSoap(endpoint);
    }

    public org.tempuri.Account_UpdateSoap getAccount_UpdateSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.Account_UpdateSoapStub _stub = new org.tempuri.Account_UpdateSoapStub(portAddress, this);
            _stub.setPortName(getAccount_UpdateSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccount_UpdateSoapEndpointAddress(java.lang.String address) {
        Account_UpdateSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.Account_UpdateSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.Account_UpdateSoapStub _stub = new org.tempuri.Account_UpdateSoapStub(new java.net.URL(Account_UpdateSoap_address), this);
                _stub.setPortName(getAccount_UpdateSoapWSDDServiceName());
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
        if ("Account_UpdateSoap".equals(inputPortName)) {
            return getAccount_UpdateSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "Account_Update");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "Account_UpdateSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Account_UpdateSoap".equals(portName)) {
            setAccount_UpdateSoapEndpointAddress(address);
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
