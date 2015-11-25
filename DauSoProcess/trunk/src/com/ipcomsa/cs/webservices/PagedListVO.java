/**
 * PagedListVO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ipcomsa.cs.webservices;

public class PagedListVO  implements java.io.Serializable {
    private long currentPage;

    private com.ipcomsa.cs.webservices.EventVO[] resultArray;

    private long totalPages;

    public PagedListVO() {
    }

    public PagedListVO(
           long currentPage,
           com.ipcomsa.cs.webservices.EventVO[] resultArray,
           long totalPages) {
           this.currentPage = currentPage;
           this.resultArray = resultArray;
           this.totalPages = totalPages;
    }


    /**
     * Gets the currentPage value for this PagedListVO.
     * 
     * @return currentPage
     */
    public long getCurrentPage() {
        return currentPage;
    }


    /**
     * Sets the currentPage value for this PagedListVO.
     * 
     * @param currentPage
     */
    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }


    /**
     * Gets the resultArray value for this PagedListVO.
     * 
     * @return resultArray
     */
    public com.ipcomsa.cs.webservices.EventVO[] getResultArray() {
        return resultArray;
    }


    /**
     * Sets the resultArray value for this PagedListVO.
     * 
     * @param resultArray
     */
    public void setResultArray(com.ipcomsa.cs.webservices.EventVO[] resultArray) {
        this.resultArray = resultArray;
    }

    public com.ipcomsa.cs.webservices.EventVO getResultArray(int i) {
        return this.resultArray[i];
    }

    public void setResultArray(int i, com.ipcomsa.cs.webservices.EventVO _value) {
        this.resultArray[i] = _value;
    }


    /**
     * Gets the totalPages value for this PagedListVO.
     * 
     * @return totalPages
     */
    public long getTotalPages() {
        return totalPages;
    }


    /**
     * Sets the totalPages value for this PagedListVO.
     * 
     * @param totalPages
     */
    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PagedListVO)) return false;
        PagedListVO other = (PagedListVO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.currentPage == other.getCurrentPage() &&
            ((this.resultArray==null && other.getResultArray()==null) || 
             (this.resultArray!=null &&
              java.util.Arrays.equals(this.resultArray, other.getResultArray()))) &&
            this.totalPages == other.getTotalPages();
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
        _hashCode += new Long(getCurrentPage()).hashCode();
        if (getResultArray() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResultArray());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResultArray(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getTotalPages()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PagedListVO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservices.cs.ipcomsa.com/", "pagedListVO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentPage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentPage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservices.cs.ipcomsa.com/", "eventVO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalPages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
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
