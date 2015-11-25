/**
 * FunRingGateLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package neo.funring;

public class FunRingGateLocator extends org.apache.axis.client.Service implements neo.funring.FunRingGate {

    public FunRingGateLocator() {
    }


    public FunRingGateLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FunRingGateLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FunRingGateHttpSoap12Endpoint
    private java.lang.String FunRingGateHttpSoap12Endpoint_address = "http://127.0.0.1:8080/FunRingGate/services/FunRingGate.FunRingGateHttpSoap12Endpoint/";

    public java.lang.String getFunRingGateHttpSoap12EndpointAddress() {
        return FunRingGateHttpSoap12Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FunRingGateHttpSoap12EndpointWSDDServiceName = "FunRingGateHttpSoap12Endpoint";

    public java.lang.String getFunRingGateHttpSoap12EndpointWSDDServiceName() {
        return FunRingGateHttpSoap12EndpointWSDDServiceName;
    }

    public void setFunRingGateHttpSoap12EndpointWSDDServiceName(java.lang.String name) {
        FunRingGateHttpSoap12EndpointWSDDServiceName = name;
    }

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FunRingGateHttpSoap12Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFunRingGateHttpSoap12Endpoint(endpoint);
    }

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            neo.funring.FunRingGateSoap12BindingStub _stub = new neo.funring.FunRingGateSoap12BindingStub(portAddress, this);
            _stub.setPortName(getFunRingGateHttpSoap12EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFunRingGateHttpSoap12EndpointEndpointAddress(java.lang.String address) {
        FunRingGateHttpSoap12Endpoint_address = address;
    }


    // Use to get a proxy class for FunRingGateHttpSoap11Endpoint
    private java.lang.String FunRingGateHttpSoap11Endpoint_address = "http://127.0.0.1:8080/FunRingGate/services/FunRingGate.FunRingGateHttpSoap11Endpoint/";

    public java.lang.String getFunRingGateHttpSoap11EndpointAddress() {
        return FunRingGateHttpSoap11Endpoint_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FunRingGateHttpSoap11EndpointWSDDServiceName = "FunRingGateHttpSoap11Endpoint";

    public java.lang.String getFunRingGateHttpSoap11EndpointWSDDServiceName() {
        return FunRingGateHttpSoap11EndpointWSDDServiceName;
    }

    public void setFunRingGateHttpSoap11EndpointWSDDServiceName(java.lang.String name) {
        FunRingGateHttpSoap11EndpointWSDDServiceName = name;
    }

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FunRingGateHttpSoap11Endpoint_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFunRingGateHttpSoap11Endpoint(endpoint);
    }

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            neo.funring.FunRingGateSoap11BindingStub _stub = new neo.funring.FunRingGateSoap11BindingStub(portAddress, this);
            _stub.setPortName(getFunRingGateHttpSoap11EndpointWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFunRingGateHttpSoap11EndpointEndpointAddress(java.lang.String address) {
        FunRingGateHttpSoap11Endpoint_address = address;
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
            if (neo.funring.FunRingGatePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                neo.funring.FunRingGateSoap12BindingStub _stub = new neo.funring.FunRingGateSoap12BindingStub(new java.net.URL(FunRingGateHttpSoap12Endpoint_address), this);
                _stub.setPortName(getFunRingGateHttpSoap12EndpointWSDDServiceName());
                return _stub;
            }
            if (neo.funring.FunRingGatePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                neo.funring.FunRingGateSoap11BindingStub _stub = new neo.funring.FunRingGateSoap11BindingStub(new java.net.URL(FunRingGateHttpSoap11Endpoint_address), this);
                _stub.setPortName(getFunRingGateHttpSoap11EndpointWSDDServiceName());
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
        if ("FunRingGateHttpSoap12Endpoint".equals(inputPortName)) {
            return getFunRingGateHttpSoap12Endpoint();
        }
        else if ("FunRingGateHttpSoap11Endpoint".equals(inputPortName)) {
            return getFunRingGateHttpSoap11Endpoint();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://funring.neo", "FunRingGate");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://funring.neo", "FunRingGateHttpSoap12Endpoint"));
            ports.add(new javax.xml.namespace.QName("http://funring.neo", "FunRingGateHttpSoap11Endpoint"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FunRingGateHttpSoap12Endpoint".equals(portName)) {
            setFunRingGateHttpSoap12EndpointEndpointAddress(address);
        }
        else 
if ("FunRingGateHttpSoap11Endpoint".equals(portName)) {
            setFunRingGateHttpSoap11EndpointEndpointAddress(address);
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
