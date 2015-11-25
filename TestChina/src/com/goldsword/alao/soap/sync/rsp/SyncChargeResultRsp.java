/**
 * SyncChargeResultRsp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.goldsword.alao.soap.sync.rsp;

public class SyncChargeResultRsp  implements java.io.Serializable {
    private com.goldsword.alao.soap.sync.common.NamedParameterList extensionInfo;

    private java.lang.String rspResult;

    public SyncChargeResultRsp() {
    }

    public SyncChargeResultRsp(
           com.goldsword.alao.soap.sync.common.NamedParameterList extensionInfo,
           java.lang.String rspResult) {
           this.extensionInfo = extensionInfo;
           this.rspResult = rspResult;
    }


    /**
     * Gets the extensionInfo value for this SyncChargeResultRsp.
     * 
     * @return extensionInfo
     */
    public com.goldsword.alao.soap.sync.common.NamedParameterList getExtensionInfo() {
        return extensionInfo;
    }


    /**
     * Sets the extensionInfo value for this SyncChargeResultRsp.
     * 
     * @param extensionInfo
     */
    public void setExtensionInfo(com.goldsword.alao.soap.sync.common.NamedParameterList extensionInfo) {
        this.extensionInfo = extensionInfo;
    }


    /**
     * Gets the rspResult value for this SyncChargeResultRsp.
     * 
     * @return rspResult
     */
    public java.lang.String getRspResult() {
        return rspResult;
    }


    /**
     * Sets the rspResult value for this SyncChargeResultRsp.
     * 
     * @param rspResult
     */
    public void setRspResult(java.lang.String rspResult) {
        this.rspResult = rspResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SyncChargeResultRsp)) return false;
        SyncChargeResultRsp other = (SyncChargeResultRsp) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensionInfo==null && other.getExtensionInfo()==null) || 
             (this.extensionInfo!=null &&
              this.extensionInfo.equals(other.getExtensionInfo()))) &&
            ((this.rspResult==null && other.getRspResult()==null) || 
             (this.rspResult!=null &&
              this.rspResult.equals(other.getRspResult())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getExtensionInfo() != null) {
            _hashCode += getExtensionInfo().hashCode();
        }
        if (getRspResult() != null) {
            _hashCode += getRspResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SyncChargeResultRsp.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://rsp.sync.soap.alao.goldsword.com", "SyncChargeResultRsp"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rsp.sync.soap.alao.goldsword.com", "extensionInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://common.sync.soap.alao.goldsword.com", "NamedParameterList"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rspResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://rsp.sync.soap.alao.goldsword.com", "rspResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
