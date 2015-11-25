/**
 * IMZGWSoap11BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.viettel.service;


import com.vmg.sms.common.Util;

public class IMZGWSoap11BindingImpl implements com.viettel.service.IMZGWPortType{
    public java.lang.String processCRBTs(java.lang.String msisdn, java.lang.String providerId, java.lang.String serviceId, java.lang.String userName, java.lang.String password, java.lang.String amount, java.lang.String reqTime, java.lang.String command, java.lang.String contents, java.lang.String sourceType) throws java.rmi.RemoteException {
    
    	IMZGWSoap11BindingStub  binding = null;
    	
    	 try {
    		
    		 IMZGWLocator locator = new IMZGWLocator();
    	      binding = (IMZGWSoap11BindingStub) locator.getIMZGWHttpSoap11Endpoint();
          
             return binding.processCRBTs(msisdn, providerId, serviceId, userName, password, amount, reqTime, command, contents, sourceType);
         	
         }catch (Exception e) {
             e.printStackTrace();
         }
         return "";
    }

}
