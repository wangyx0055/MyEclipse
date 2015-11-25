
/**
 * SendPushMessageServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service;

    /**
     *  SendPushMessageServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SendPushMessageServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SendPushMessageServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SendPushMessageServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getPushMessageDeliveryStatus method
            * override this method for handling normal response from getPushMessageDeliveryStatus operation
            */
           public void receiveResultgetPushMessageDeliveryStatus(
                    org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.GetPushMessageDeliveryStatusResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPushMessageDeliveryStatus operation
           */
            public void receiveErrorgetPushMessageDeliveryStatus(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for sendPushMessage method
            * override this method for handling normal response from sendPushMessage operation
            */
           public void receiveResultsendPushMessage(
                    org.csapi.www.wsdl.parlayx.wap_push.send.v1_0.service.SendPushMessageServiceStub.SendPushMessageResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from sendPushMessage operation
           */
            public void receiveErrorsendPushMessage(java.lang.Exception e) {
            }
                


    }
    