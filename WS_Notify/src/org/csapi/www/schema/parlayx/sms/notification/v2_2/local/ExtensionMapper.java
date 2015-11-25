
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

        
            package org.csapi.www.schema.parlayx.sms.notification.v2_2.local;
        
            /**
            *  ExtensionMapper class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ExtensionMapper{

          public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                       java.lang.String typeName,
                                                       javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/notification/v2_2/local".equals(namespaceURI) &&
                  "notifySmsDeliveryReceipt".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceipt.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/v2_2".equals(namespaceURI) &&
                  "DeliveryStatus".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.v2_2.DeliveryStatus.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI) &&
                  "ServiceException".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.common.v2_1.ServiceException.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/notification/v2_2/local".equals(namespaceURI) &&
                  "notifySmsReceptionResponse".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReceptionResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/notification/v2_2/local".equals(namespaceURI) &&
                  "notifySmsDeliveryReceiptResponse".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsDeliveryReceiptResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/common/v2_1".equals(namespaceURI) &&
                  "PolicyException".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.common.v2_1.PolicyException.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/v2_2".equals(namespaceURI) &&
                  "SmsMessage".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.v2_2.SmsMessage.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/v2_2".equals(namespaceURI) &&
                  "DeliveryInformation".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.v2_2.DeliveryInformation.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.csapi.org/schema/parlayx/sms/notification/v2_2/local".equals(namespaceURI) &&
                  "notifySmsReception".equals(typeName)){
                   
                            return  org.csapi.www.schema.parlayx.sms.notification.v2_2.local.NotifySmsReception.Factory.parse(reader);
                        

                  }

              
             throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    