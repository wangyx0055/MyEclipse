/**
 * MPlusLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package MPlusServiceDitech;

public class MPlusLocator extends org.apache.axis.client.Service implements MPlusServiceDitech.MPlus {

    public MPlusLocator() {
    }


    public MPlusLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MPlusLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MPlusPort
    private java.lang.String MPlusPort_address = "http://10.151.190.155:8000/mplus-services/mplus";

    public java.lang.String getMPlusPortAddress() {
        return MPlusPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MPlusPortWSDDServiceName = "MPlusPort";

    public java.lang.String getMPlusPortWSDDServiceName() {
        return MPlusPortWSDDServiceName;
    }

    public void setMPlusPortWSDDServiceName(java.lang.String name) {
        MPlusPortWSDDServiceName = name;
    }

    public MPlusServiceDitech.MPlusPort getMPlusPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MPlusPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMPlusPort(endpoint);
    }

    public MPlusServiceDitech.MPlusPort getMPlusPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            MPlusServiceDitech.MPlusPortStub _stub = new MPlusServiceDitech.MPlusPortStub(portAddress, this);
            _stub.setPortName(getMPlusPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMPlusPortEndpointAddress(java.lang.String address) {
        MPlusPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (MPlusServiceDitech.MPlusPort.class.isAssignableFrom(serviceEndpointInterface)) {
                MPlusServiceDitech.MPlusPortStub _stub = new MPlusServiceDitech.MPlusPortStub(new java.net.URL(MPlusPort_address), this);
                _stub.setPortName(getMPlusPortWSDDServiceName());
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
        if ("MPlusPort".equals(inputPortName)) {
            return getMPlusPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("MPlusService", "MPlus");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("MPlusService", "MPlusPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MPlusPort".equals(portName)) {
            setMPlusPortEndpointAddress(address);
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
