/**
 * MPlus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package MPlusServiceDitech;

public interface MPlus extends javax.xml.rpc.Service {
    public java.lang.String getMPlusPortAddress();

    public MPlusServiceDitech.MPlusPort getMPlusPort() throws javax.xml.rpc.ServiceException;

    public MPlusServiceDitech.MPlusPort getMPlusPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
