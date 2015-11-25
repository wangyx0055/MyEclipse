/**
 * ReceiverServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vms.to.icom.moqueue;

import icom.Constants;

public class ReceiverServiceLocator extends org.apache.axis.client.Service implements vms.to.icom.moqueue.ReceiverService {

    public ReceiverServiceLocator() {
    }


    public ReceiverServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ReceiverServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Receiver
    private java.lang.String Receiver_address = Constants._prop.getProperty("wsVMS2ICOMMOQUEUE", "");

    public java.lang.String getReceiverAddress() {
        return Receiver_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ReceiverWSDDServiceName = "Receiver";

    public java.lang.String getReceiverWSDDServiceName() {
        return ReceiverWSDDServiceName;
    }

    public void setReceiverWSDDServiceName(java.lang.String name) {
        ReceiverWSDDServiceName = name;
    }

    public vms.to.icom.moqueue.Receiver getReceiver() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Receiver_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getReceiver(endpoint);
    }

    public vms.to.icom.moqueue.Receiver getReceiver(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            vms.to.icom.moqueue.ReceiverSoapBindingStub _stub = new vms.to.icom.moqueue.ReceiverSoapBindingStub(portAddress, this);
            _stub.setPortName(getReceiverWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setReceiverEndpointAddress(java.lang.String address) {
        Receiver_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (vms.to.icom.moqueue.Receiver.class.isAssignableFrom(serviceEndpointInterface)) {
                vms.to.icom.moqueue.ReceiverSoapBindingStub _stub = new vms.to.icom.moqueue.ReceiverSoapBindingStub(new java.net.URL(Receiver_address), this);
                _stub.setPortName(getReceiverWSDDServiceName());
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
    @Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Receiver".equals(inputPortName)) {
            return getReceiver();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://moqueue.icom.to.vms", "ReceiverService");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://moqueue.icom.to.vms", "Receiver"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Receiver".equals(portName)) {
            setReceiverEndpointAddress(address);
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
