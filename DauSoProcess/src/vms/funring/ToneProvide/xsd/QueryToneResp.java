/**
 * QueryToneResp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package vms.funring.ToneProvide.xsd;

public class QueryToneResp  extends vms.funring.ToneProvide.xsd.BaseQueryResp  implements java.io.Serializable {
    private vms.funring.ToneProvide.xsd.ToneInfo[] queryToneInfos;

    public QueryToneResp() {
    }

    public QueryToneResp(
           java.lang.String eventClassName,
           java.lang.Long operationID,
           java.lang.Integer resultCode,
           java.lang.String resultInfo,
           java.lang.String returnCode,
           java.lang.String recordSum,
           vms.funring.ToneProvide.xsd.ToneInfo[] queryToneInfos) {
        super(
            eventClassName,
            operationID,
            resultCode,
            resultInfo,
            returnCode,
            recordSum);
        this.queryToneInfos = queryToneInfos;
    }


    /**
     * Gets the queryToneInfos value for this QueryToneResp.
     * 
     * @return queryToneInfos
     */
    public vms.funring.ToneProvide.xsd.ToneInfo[] getQueryToneInfos() {
        return queryToneInfos;
    }


    /**
     * Sets the queryToneInfos value for this QueryToneResp.
     * 
     * @param queryToneInfos
     */
    public void setQueryToneInfos(vms.funring.ToneProvide.xsd.ToneInfo[] queryToneInfos) {
        this.queryToneInfos = queryToneInfos;
    }

    public vms.funring.ToneProvide.xsd.ToneInfo getQueryToneInfos(int i) {
        return this.queryToneInfos[i];
    }

    public void setQueryToneInfos(int i, vms.funring.ToneProvide.xsd.ToneInfo _value) {
        this.queryToneInfos[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryToneResp)) return false;
        QueryToneResp other = (QueryToneResp) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.queryToneInfos==null && other.getQueryToneInfos()==null) || 
             (this.queryToneInfos!=null &&
              java.util.Arrays.equals(this.queryToneInfos, other.getQueryToneInfos())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getQueryToneInfos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getQueryToneInfos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getQueryToneInfos(), i);
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
        new org.apache.axis.description.TypeDesc(QueryToneResp.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ToneProvide.funring.vms/xsd", "QueryToneResp"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryToneInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ToneProvide.funring.vms/xsd", "queryToneInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ToneProvide.funring.vms/xsd", "ToneInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
