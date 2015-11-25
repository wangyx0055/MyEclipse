
/**
 * SmsNotificationServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service;

    /**
     *  SmsNotificationServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SmsNotificationServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SmsNotificationServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SmsNotificationServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for notifySmsDeliveryReceipt method
            * override this method for handling normal response from notifySmsDeliveryReceipt operation
            */
           public void receiveResultnotifySmsDeliveryReceipt(
                    org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from notifySmsDeliveryReceipt operation
           */
            public void receiveErrornotifySmsDeliveryReceipt(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for notifySmsReception method
            * override this method for handling normal response from notifySmsReception operation
            */
           public void receiveResultnotifySmsReception(
                    org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from notifySmsReception operation
           */
            public void receiveErrornotifySmsReception(java.lang.Exception e) {
            }
                


    }
    