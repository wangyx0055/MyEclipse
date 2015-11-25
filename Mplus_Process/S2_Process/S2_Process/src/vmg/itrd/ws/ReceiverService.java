/**
 * ReceiverService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package vmg.itrd.ws;

public interface ReceiverService extends javax.xml.rpc.Service {
    public java.lang.String getReceiverAddress();

    public vmg.itrd.ws.Receiver getReceiver() throws javax.xml.rpc.ServiceException;

    public vmg.itrd.ws.Receiver getReceiver(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
