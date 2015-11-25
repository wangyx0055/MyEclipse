

/**
 * SmsNotificationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service;

    /*
     *  SmsNotificationService java interface
     */

    public interface SmsNotificationService {
          

        /**
          * Auto generated method signature
          * 
                    * @param notifySmsDeliveryReceipt0
                
         */

         
                     public org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE notifySmsDeliveryReceipt(

                        org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE notifySmsDeliveryReceipt0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param notifySmsDeliveryReceipt0
            
          */
        public void startnotifySmsDeliveryReceipt(

            org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE notifySmsDeliveryReceipt0,

            final org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service.SmsNotificationServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param notifySmsReception2
                
         */

         
                     public org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE notifySmsReception(

                        org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE notifySmsReception2)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param notifySmsReception2
            
          */
        public void startnotifySmsReception(

            org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE notifySmsReception2,

            final org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service.SmsNotificationServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    