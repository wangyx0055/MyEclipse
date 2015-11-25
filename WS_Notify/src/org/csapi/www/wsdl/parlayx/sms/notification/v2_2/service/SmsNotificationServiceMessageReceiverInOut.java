
/**
 * SmsNotificationServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
        package org.csapi.www.wsdl.parlayx.sms.notification.v2_2.service;

        /**
        *  SmsNotificationServiceMessageReceiverInOut message receiver
        */

        public class SmsNotificationServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        SmsNotificationServiceSkeleton skel = (SmsNotificationServiceSkeleton)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("notifySmsDeliveryReceipt".equals(methodName)){
                
                org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE notifySmsDeliveryReceiptResponse1 = null;
	                        org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE wrappedParam =
                                                             (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               notifySmsDeliveryReceiptResponse1 =
                                                   
                                                   
                                                         skel.notifySmsDeliveryReceipt(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), notifySmsDeliveryReceiptResponse1, false, new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/sms/notification/v2_2/interface",
                                                    "notifySmsDeliveryReceipt"));
                                    } else 

            if("notifySmsReception".equals(methodName)){
                
                org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE notifySmsReceptionResponse3 = null;
	                        org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE wrappedParam =
                                                             (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               notifySmsReceptionResponse3 =
                                                   
                                                   
                                                         skel.notifySmsReception(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), notifySmsReceptionResponse3, false, new javax.xml.namespace.QName("http://www.csapi.org/wsdl/parlayx/sms/notification/v2_2/interface",
                                                    "notifySmsReception"));
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
            private  org.apache.axiom.om.OMElement  toOM(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE wrapnotifySmsReception(){
                                org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE wrappedElement = new org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE wrapnotifySmsDeliveryReceipt(){
                                org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE wrappedElement = new org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE();
                                return wrappedElement;
                         }
                    


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE.class.equals(type)){
                
                           return org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE.class.equals(type)){
                
                           return org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE.class.equals(type)){
                
                           return org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE.class.equals(type)){
                
                           return org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
        }

        private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

        }//end of class
    