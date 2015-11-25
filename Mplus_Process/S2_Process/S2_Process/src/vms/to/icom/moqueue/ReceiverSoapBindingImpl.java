/**
 * ReceiverSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vms.to.icom.moqueue;


import icom.Constants;


public class ReceiverSoapBindingImpl implements vms.to.icom.moqueue.Receiver{
    public int insertMOQueueICOM(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR
    		, java.lang.String COMMAND_CODE, java.lang.String INFO, java.lang.String RECEIVE_DATE, java.lang.String RESPONDED
    		, java.lang.String REQUEST_ID, java.lang.String CHANNEL_TYPE) throws java.rmi.RemoteException {
    	ReceiverSoapBindingStub binding = null;
        try {
            ReceiverServiceLocator locator = new ReceiverServiceLocator();
           // locator.setReceiverEndpointAddress("");
            binding = (ReceiverSoapBindingStub) locator.getReceiver();
            
            binding.setUsername(Constants._prop.getProperty("wsVMS2ICOMMOQUEUE_UserName", ""));
            binding.setPassword(Constants._prop.getProperty("wsVMS2ICOMMOQUEUE_PassWord", ""));
     
            return binding.insertMOQueueICOM(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, RECEIVE_DATE, RESPONDED, REQUEST_ID, CHANNEL_TYPE);
             
        }catch (Exception e) {
            e.printStackTrace();
        }
        return -3;
    }

}
