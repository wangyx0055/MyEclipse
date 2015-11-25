/**
 * SendMOICom2HTSSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface SendMOICom2HTSSoap extends java.rmi.Remote {

    /**
     * ket qua diem tot nghiep ptth
     */
    public java.lang.String ketQuaDiemTNPT(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matinh, java.lang.String sbd) throws java.rmi.RemoteException;

    /**
     * ket qua diem thi đại học 2012
     */
    public java.lang.String ketQuaDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException;

    /**
     * ket qua diem chuan đại học 2012
     */
    public java.lang.String ketQuaDiemCHuanDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException;

    /**
     * Vi tri cua thi sinh du thi trong truong
     */
    public java.lang.String ketQuaViTriDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException;

    /**
     * Nguyen vong 2 ma truong
     */
    public java.lang.String ketQuaNV2MaTruongDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException;

    /**
     * Nguyen vong 2 muc diem
     */
    public java.lang.String ketQuaNV2MucDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String mucdiem) throws java.rmi.RemoteException;

    /**
     * Ket qua dap an dai hoc
     */
    public java.lang.String ketQuaDapAnDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String makhoi, java.lang.String mamon, java.lang.String made) throws java.rmi.RemoteException;

    /**
     * Ket qua dap an cao dang
     */
    public java.lang.String ketQuaDapAnCD(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String makhoi, java.lang.String mamon, java.lang.String made) throws java.rmi.RemoteException;

    /**
     * Ket qua du bao do truot
     */
    public java.lang.String ketQuaDBDoTruot(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String sbd) throws java.rmi.RemoteException;

    /**
     * Ket qua muc diem dai hoc
     */
    public java.lang.String ketQuaMucDiemDH(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong, java.lang.String mucdiem) throws java.rmi.RemoteException;

    /**
     * Ket qua chi tieu tuyen sinh
     */
    public java.lang.String ketQuaChiTieuTS(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong) throws java.rmi.RemoteException;

    /**
     * Ket ty le choi dai hoc
     */
    public java.lang.String ketQuaTYLECHOI(java.lang.String username, java.lang.String password, java.lang.String requestID, java.lang.String serviceID, java.lang.String mobicode, java.lang.String commandCode, java.lang.String matruong, java.lang.String namts) throws java.rmi.RemoteException;

    /**
     * Kiem tra ma truong dai hoc
     */
    public int checkMaTruongDH(java.lang.String username, java.lang.String password, java.lang.String matruong) throws java.rmi.RemoteException;
}
