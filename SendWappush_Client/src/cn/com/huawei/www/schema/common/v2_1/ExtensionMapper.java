
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

        
            package cn.com.huawei.www.schema.common.v2_1;
        
            /**
            *  ExtensionMapper class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ExtensionMapper{

          public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                       java.lang.String typeName,
                                                       javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

              
                  if (
                  "http://www.huawei.com.cn/schema/common/v2_1".equals(namespaceURI) &&
                  "RequestSOAPHeader".equals(typeName)){
                   
                            return  cn.com.huawei.www.schema.common.v2_1.RequestSOAPHeader.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://www.huawei.com.cn/schema/common/v2_1".equals(namespaceURI) &&
                  "NotifySOAPHeader".equals(typeName)){
                   
                            return  cn.com.huawei.www.schema.common.v2_1.NotifySOAPHeader.Factory.parse(reader);
                        

                  }

              
             throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    