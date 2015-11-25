/**
 * ReceiverService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vms.to.gamefarm.moqueue;

public interface ReceiverService extends javax.xml.rpc.Service{
    public java.lang.String getReceiverAddress();

    public vms.to.gamefarm.moqueue.Receiver getReceiver() throws javax.xml.rpc.ServiceException;

    public vms.to.gamefarm.moqueue.Receiver getReceiver(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
