/**
 * Tv_updateLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Tv_updateLocator extends org.apache.axis.client.Service implements org.tempuri.Tv_update {

    public Tv_updateLocator() {
    }


    public Tv_updateLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Tv_updateLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for tv_updateSoap
    private java.lang.String tv_updateSoap_address = "http://10.21.0.42:8090/sms/tv_update.asmx";

    public java.lang.String gettv_updateSoapAddress() {
        return tv_updateSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String tv_updateSoapWSDDServiceName = "tv_updateSoap";

    public java.lang.String gettv_updateSoapWSDDServiceName() {
        return tv_updateSoapWSDDServiceName;
    }

    public void settv_updateSoapWSDDServiceName(java.lang.String name) {
        tv_updateSoapWSDDServiceName = name;
    }

    public org.tempuri.Tv_updateSoap gettv_updateSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(tv_updateSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return gettv_updateSoap(endpoint);
    }

    public org.tempuri.Tv_updateSoap gettv_updateSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.Tv_updateSoapStub _stub = new org.tempuri.Tv_updateSoapStub(portAddress, this);
            _stub.setPortName(gettv_updateSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void settv_updateSoapEndpointAddress(java.lang.String address) {
        tv_updateSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.Tv_updateSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.Tv_updateSoapStub _stub = new org.tempuri.Tv_updateSoapStub(new java.net.URL(tv_updateSoap_address), this);
                _stub.setPortName(gettv_updateSoapWSDDServiceName());
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
        if ("tv_updateSoap".equals(inputPortName)) {
            return gettv_updateSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "tv_update");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "tv_updateSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("tv_updateSoap".equals(portName)) {
            settv_updateSoapEndpointAddress(address);
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
