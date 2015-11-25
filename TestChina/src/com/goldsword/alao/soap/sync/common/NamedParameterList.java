/**
 * NamedParameterList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.goldsword.alao.soap.sync.common;

public class NamedParameterList  implements java.io.Serializable {
    private com.goldsword.alao.soap.sync.common.NamedParameter[] namedParameters;

    public NamedParameterList() {
    }

    public NamedParameterList(
           com.goldsword.alao.soap.sync.common.NamedParameter[] namedParameters) {
           this.namedParameters = namedParameters;
    }


    /**
     * Gets the namedParameters value for this NamedParameterList.
     * 
     * @return namedParameters
     */
    public com.goldsword.alao.soap.sync.common.NamedParameter[] getNamedParameters() {
        return namedParameters;
    }


    /**
     * Sets the namedParameters value for this NamedParameterList.
     * 
     * @param namedParameters
     */
    public void setNamedParameters(com.goldsword.alao.soap.sync.common.NamedParameter[] namedParameters) {
        this.namedParameters = namedParameters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamedParameterList)) return false;
        NamedParameterList other = (NamedParameterList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.namedParameters==null && other.getNamedParameters()==null) || 
             (this.namedParameters!=null &&
              java.util.Arrays.equals(this.namedParameters, other.getNamedParameters())));
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
        if (getNamedParameters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNamedParameters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNamedParameters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NamedParameterList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://common.sync.soap.alao.goldsword.com", "NamedParameterList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("namedParameters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://common.sync.soap.alao.goldsword.com", "namedParameters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://common.sync.soap.alao.goldsword.com", "NamedParameter"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://server.sync.soap.alao.goldsword.com", "item"));
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
