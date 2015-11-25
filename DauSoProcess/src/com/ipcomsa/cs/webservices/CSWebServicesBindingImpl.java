/**
 * CSWebServicesBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ipcomsa.cs.webservices;

public class CSWebServicesBindingImpl implements com.ipcomsa.cs.webservices.CSWebServices{
    public com.ipcomsa.cs.webservices.CsResponse addAccount(java.lang.String phoneNumber, java.lang.String planName) throws java.rmi.RemoteException {
        CSWebServicesBindingStub binding = null;

        try {
            CSWebServicesServiceLocator locator = new CSWebServicesServiceLocator();
            locator.setCSWebServicesPortEndpointAddress("");

             binding = (CSWebServicesBindingStub) locator.getCSWebServicesPort();

             return binding.addAccount(phoneNumber, planName);
             //System.out.println("callScreenReg_REQ_return Set TCSI: " + ret);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse sendsms(java.lang.String phoneNumber, java.lang.String message) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse addBlackListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse addGreyListMember(java.lang.String phoneNumber, java.lang.String numA, java.lang.String forwardPhone, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse addListMember(java.lang.String phoneNumber, java.lang.String numA, java.lang.String forwardPhone, int serviceType, int listType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse addWhiteListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse checkCallScreening(java.lang.String phoneNumber, java.lang.String numA, java.util.Calendar callDate, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse checkPrefix(java.lang.String phoneNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse disableAccount(java.lang.String phoneNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse enableAccount(java.lang.String phoneNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.AccountVO getAccount(java.lang.String phoneNumber) throws java.rmi.RemoteException {
        CSWebServicesBindingStub binding = null;

        try {
            CSWebServicesServiceLocator locator = new CSWebServicesServiceLocator();
            locator.setCSWebServicesPortEndpointAddress("");

             binding = (CSWebServicesBindingStub) locator.getCSWebServicesPort();

             return binding.getAccount(phoneNumber);
             //System.out.println("callScreenReg_REQ_return Set TCSI: " + ret);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }

    public java.lang.String[] getBlackList(java.lang.String phoneNumber, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[][] getGreyList(java.lang.String phoneNumber, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String getListMapHour(java.lang.String phoneNumber, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] getTotalAccountsReport() throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] getWhiteList(java.lang.String phoneNumber, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse modifyForwardPhone(java.lang.String phoneNumber, java.lang.String numA, java.lang.String forwardPhone, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse modifyListMapHour(java.lang.String phoneNumber, java.lang.String mapHour, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse modifyPlanCharge(java.lang.String planName, java.lang.String stCharge) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse removeAccount(java.lang.String phoneNumber) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse removeBlackListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse removeGreyListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse removeListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType, int listType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.CsResponse removeWhiteListMember(java.lang.String phoneNumber, java.lang.String numA, int serviceType) throws java.rmi.RemoteException {
        return null;
    }

    public com.ipcomsa.cs.webservices.PagedListVO reportEventAccount(java.lang.String phoneNumber, java.util.Calendar fromDate, java.util.Calendar toDate, int nextPage, int maxRows) throws java.rmi.RemoteException {
        return null;
    }

}
