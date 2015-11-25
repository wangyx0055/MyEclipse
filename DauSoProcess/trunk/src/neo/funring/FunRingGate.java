/**
 * FunRingGate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package neo.funring;

public interface FunRingGate extends javax.xml.rpc.Service {
    public java.lang.String getFunRingGateHttpSoap12EndpointAddress();

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap12Endpoint() throws javax.xml.rpc.ServiceException;

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap12Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getFunRingGateHttpSoap11EndpointAddress();

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap11Endpoint() throws javax.xml.rpc.ServiceException;

    public neo.funring.FunRingGatePortType getFunRingGateHttpSoap11Endpoint(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
