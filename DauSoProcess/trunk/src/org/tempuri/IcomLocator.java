/**
 * IcomLocator.java
 *
 * This file was d from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class IcomLocator extends org.apache.axis.client.Service implements org.tempuri.Icom {

    public IcomLocator() {
    }


    public IcomLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    /**
	 * @return the icomSoap_address
	 */
	public java.lang.String getIcomSoap_address() {
		return icomSoap_address;
	}


	/**
	 * @param icomSoap_address the icomSoap_address to set
	 */
	public void setIcomSoap_address(java.lang.String icomSoap_address) {
		this.icomSoap_address = icomSoap_address;
	}


	/**
	 * @return the icomSoapWSDDServiceName
	 */
	public java.lang.String getIcomSoapWSDDServiceName() {
		return icomSoapWSDDServiceName;
	}


	/**
	 * @param icomSoapWSDDServiceName the icomSoapWSDDServiceName to set
	 */
	public void setIcomSoapWSDDServiceName(java.lang.String icomSoapWSDDServiceName) {
		this.icomSoapWSDDServiceName = icomSoapWSDDServiceName;
	}


	/**
	 * @return the icomSoap12_address
	 */
	public java.lang.String getIcomSoap12_address() {
		return icomSoap12_address;
	}


	/**
	 * @param icomSoap12_address the icomSoap12_address to set
	 */
	public void setIcomSoap12_address(java.lang.String icomSoap12_address) {
		this.icomSoap12_address = icomSoap12_address;
	}


	/**
	 * @return the icomSoap12WSDDServiceName
	 */
	public java.lang.String getIcomSoap12WSDDServiceName() {
		return icomSoap12WSDDServiceName;
	}


	/**
	 * @param icomSoap12WSDDServiceName the icomSoap12WSDDServiceName to set
	 */
	public void setIcomSoap12WSDDServiceName(
			java.lang.String icomSoap12WSDDServiceName) {
		this.icomSoap12WSDDServiceName = icomSoap12WSDDServiceName;
	}


	/**
	 * @param ports the ports to set
	 */
	public void setPorts(java.util.HashSet ports) {
		this.ports = ports;
	}


	public IcomLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for icomSoap
    private java.lang.String icomSoap_address = "http://smsservice.maromedia.vn/icom.asmx";

    public java.lang.String geticomSoapAddress() {
        return icomSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String icomSoapWSDDServiceName = "icomSoap";

    public java.lang.String geticomSoapWSDDServiceName() {
        return icomSoapWSDDServiceName;
    }

    public void seticomSoapWSDDServiceName(java.lang.String name) {
        icomSoapWSDDServiceName = name;
    }

    public org.tempuri.IcomSoap geticomSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(icomSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return geticomSoap(endpoint);
    }

    public org.tempuri.IcomSoap geticomSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.IcomSoapStub _stub = new org.tempuri.IcomSoapStub(portAddress, this);
            _stub.setPortName(geticomSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void seticomSoapEndpointAddress(java.lang.String address) {
        icomSoap_address = address;
    }


    // Use to get a proxy class for icomSoap12
    private java.lang.String icomSoap12_address = "http://smsservice.maromedia.vn/icom.asmx";

    public java.lang.String geticomSoap12Address() {
        return icomSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String icomSoap12WSDDServiceName = "icomSoap12";

    public java.lang.String geticomSoap12WSDDServiceName() {
        return icomSoap12WSDDServiceName;
    }

    public void seticomSoap12WSDDServiceName(java.lang.String name) {
        icomSoap12WSDDServiceName = name;
    }

    public org.tempuri.IcomSoap geticomSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(icomSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return geticomSoap12(endpoint);
    }

    public org.tempuri.IcomSoap geticomSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.IcomSoap12Stub _stub = new org.tempuri.IcomSoap12Stub(portAddress, this);
            _stub.setPortName(geticomSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void seticomSoap12EndpointAddress(java.lang.String address) {
        icomSoap12_address = address;
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
            if (org.tempuri.IcomSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.IcomSoapStub _stub = new org.tempuri.IcomSoapStub(new java.net.URL(icomSoap_address), this);
                _stub.setPortName(geticomSoapWSDDServiceName());
                return _stub;
            }
            if (org.tempuri.IcomSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.IcomSoap12Stub _stub = new org.tempuri.IcomSoap12Stub(new java.net.URL(icomSoap12_address), this);
                _stub.setPortName(geticomSoap12WSDDServiceName());
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
        if ("icomSoap".equals(inputPortName)) {
            return geticomSoap();
        }
        else if ("icomSoap12".equals(inputPortName)) {
            return geticomSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "icom");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "icomSoap"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "icomSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("icomSoap".equals(portName)) {
            seticomSoapEndpointAddress(address);
        }
        else 
if ("icomSoap12".equals(portName)) {
            seticomSoap12EndpointAddress(address);
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
