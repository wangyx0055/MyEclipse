/**
 * MPlusPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package MPlusServiceMobiTech;

public interface MPlusPort extends java.rmi.Remote {
    public int moReceiver(java.lang.String userName, java.lang.String password, java.lang.String msisdn, java.lang.String serviceNumber, java.lang.String serviceCode, java.lang.String info, java.lang.String requestId, java.lang.String operator, int channel_Type) throws java.rmi.RemoteException;
    public int cpSynstatus(java.lang.String userName, java.lang.String password, java.lang.String sMsisdn, int iStatus) throws java.rmi.RemoteException;
}
