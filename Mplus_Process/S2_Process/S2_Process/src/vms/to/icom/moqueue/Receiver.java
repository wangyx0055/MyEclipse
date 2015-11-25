/**
 * Receiver.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vms.to.icom.moqueue;

public interface Receiver extends java.rmi.Remote {
    public int insertMOQueueICOM(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR, java.lang.String COMMAND_CODE, java.lang.String INFO, java.lang.String RECEIVE_DATE, java.lang.String RESPONDED, java.lang.String REQUEST_ID, java.lang.String CHANNEL_TYPE) throws java.rmi.RemoteException;
}
