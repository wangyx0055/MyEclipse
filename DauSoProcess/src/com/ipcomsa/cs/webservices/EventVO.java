/**
 * EventVO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ipcomsa.cs.webservices;

public class EventVO  implements java.io.Serializable {
    private java.lang.Long accountId;

    private java.lang.Short accountStatus;

    private java.util.Calendar callDate;

    private java.math.BigDecimal chargeAmount;

    private java.util.Calendar chargeDate;

    private java.util.Calendar date;

    private java.lang.String description;

    private java.lang.String forwardPhone;

    private java.lang.Long id;

    private java.lang.Long listId;

    private java.lang.Short listPriority;

    private java.lang.Integer listType;

    private java.lang.String mapHour;

    private java.util.Calendar nextChargeDate;

    private java.lang.String numA;

    private java.lang.String phoneNumber;

    private java.lang.Long planId;

    private java.lang.String planName;

    private java.lang.String resultDesc;

    private java.lang.Long resultId;

    private java.lang.Integer serviceType;

    private int type;

    public EventVO() {
    }

    public EventVO(
           java.lang.Long accountId,
           java.lang.Short accountStatus,
           java.util.Calendar callDate,
           java.math.BigDecimal chargeAmount,
           java.util.Calendar chargeDate,
           java.util.Calendar date,
           java.lang.String description,
           java.lang.String forwardPhone,
           java.lang.Long id,
           java.lang.Long listId,
           java.lang.Short listPriority,
           java.lang.Integer listType,
           java.lang.String mapHour,
           java.util.Calendar nextChargeDate,
           java.lang.String numA,
           java.lang.String phoneNumber,
           java.lang.Long planId,
           java.lang.String planName,
           java.lang.String resultDesc,
           java.lang.Long resultId,
           java.lang.Integer serviceType,
           int type) {
           this.accountId = accountId;
           this.accountStatus = accountStatus;
           this.callDate = callDate;
           this.chargeAmount = chargeAmount;
           this.chargeDate = chargeDate;
           this.date = date;
           this.description = description;
           this.forwardPhone = forwardPhone;
           this.id = id;
           this.listId = listId;
           this.listPriority = listPriority;
           this.listType = listType;
           this.mapHour = mapHour;
           this.nextChargeDate = nextChargeDate;
           this.numA = numA;
           this.phoneNumber = phoneNumber;
           this.planId = planId;
           this.planName = planName;
           this.resultDesc = resultDesc;
           this.resultId = resultId;
           this.serviceType = serviceType;
           this.type = type;
    }


    /**
     * Gets the accountId value for this EventVO.
     * 
     * @return accountId
     */
    public java.lang.Long getAccountId() {
        return accountId;
    }


    /**
     * Sets the accountId value for this EventVO.
     * 
     * @param accountId
     */
    public void setAccountId(java.lang.Long accountId) {
        this.accountId = accountId;
    }


    /**
     * Gets the accountStatus value for this EventVO.
     * 
     * @return accountStatus
     */
    public java.lang.Short getAccountStatus() {
        return accountStatus;
    }


    /**
     * Sets the accountStatus value for this EventVO.
     * 
     * @param accountStatus
     */
    public void setAccountStatus(java.lang.Short accountStatus) {
        this.accountStatus = accountStatus;
    }


    /**
     * Gets the callDate value for this EventVO.
     * 
     * @return callDate
     */
    public java.util.Calendar getCallDate() {
        return callDate;
    }


    /**
     * Sets the callDate value for this EventVO.
     * 
     * @param callDate
     */
    public void setCallDate(java.util.Calendar callDate) {
        this.callDate = callDate;
    }


    /**
     * Gets the chargeAmount value for this EventVO.
     * 
     * @return chargeAmount
     */
    public java.math.BigDecimal getChargeAmount() {
        return chargeAmount;
    }


    /**
     * Sets the chargeAmount value for this EventVO.
     * 
     * @param chargeAmount
     */
    public void setChargeAmount(java.math.BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }


    /**
     * Gets the chargeDate value for this EventVO.
     * 
     * @return chargeDate
     */
    public java.util.Calendar getChargeDate() {
        return chargeDate;
    }


    /**
     * Sets the chargeDate value for this EventVO.
     * 
     * @param chargeDate
     */
    public void setChargeDate(java.util.Calendar chargeDate) {
        this.chargeDate = chargeDate;
    }


    /**
     * Gets the date value for this EventVO.
     * 
     * @return date
     */
    public java.util.Calendar getDate() {
        return date;
    }


    /**
     * Sets the date value for this EventVO.
     * 
     * @param date
     */
    public void setDate(java.util.Calendar date) {
        this.date = date;
    }


    /**
     * Gets the description value for this EventVO.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this EventVO.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the forwardPhone value for this EventVO.
     * 
     * @return forwardPhone
     */
    public java.lang.String getForwardPhone() {
        return forwardPhone;
    }


    /**
     * Sets the forwardPhone value for this EventVO.
     * 
     * @param forwardPhone
     */
    public void setForwardPhone(java.lang.String forwardPhone) {
        this.forwardPhone = forwardPhone;
    }


    /**
     * Gets the id value for this EventVO.
     * 
     * @return id
     */
    public java.lang.Long getId() {
        return id;
    }


    /**
     * Sets the id value for this EventVO.
     * 
     * @param id
     */
    public void setId(java.lang.Long id) {
        this.id = id;
    }


    /**
     * Gets the listId value for this EventVO.
     * 
     * @return listId
     */
    public java.lang.Long getListId() {
        return listId;
    }


    /**
     * Sets the listId value for this EventVO.
     * 
     * @param listId
     */
    public void setListId(java.lang.Long listId) {
        this.listId = listId;
    }


    /**
     * Gets the listPriority value for this EventVO.
     * 
     * @return listPriority
     */
    public java.lang.Short getListPriority() {
        return listPriority;
    }


    /**
     * Sets the listPriority value for this EventVO.
     * 
     * @param listPriority
     */
    public void setListPriority(java.lang.Short listPriority) {
        this.listPriority = listPriority;
    }


    /**
     * Gets the listType value for this EventVO.
     * 
     * @return listType
     */
    public java.lang.Integer getListType() {
        return listType;
    }


    /**
     * Sets the listType value for this EventVO.
     * 
     * @param listType
     */
    public void setListType(java.lang.Integer listType) {
        this.listType = listType;
    }


    /**
     * Gets the mapHour value for this EventVO.
     * 
     * @return mapHour
     */
    public java.lang.String getMapHour() {
        return mapHour;
    }


    /**
     * Sets the mapHour value for this EventVO.
     * 
     * @param mapHour
     */
    public void setMapHour(java.lang.String mapHour) {
        this.mapHour = mapHour;
    }


    /**
     * Gets the nextChargeDate value for this EventVO.
     * 
     * @return nextChargeDate
     */
    public java.util.Calendar getNextChargeDate() {
        return nextChargeDate;
    }


    /**
     * Sets the nextChargeDate value for this EventVO.
     * 
     * @param nextChargeDate
     */
    public void setNextChargeDate(java.util.Calendar nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
    }


    /**
     * Gets the numA value for this EventVO.
     * 
     * @return numA
     */
    public java.lang.String getNumA() {
        return numA;
    }


    /**
     * Sets the numA value for this EventVO.
     * 
     * @param numA
     */
    public void setNumA(java.lang.String numA) {
        this.numA = numA;
    }


    /**
     * Gets the phoneNumber value for this EventVO.
     * 
     * @return phoneNumber
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber value for this EventVO.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the planId value for this EventVO.
     * 
     * @return planId
     */
    public java.lang.Long getPlanId() {
        return planId;
    }


    /**
     * Sets the planId value for this EventVO.
     * 
     * @param planId
     */
    public void setPlanId(java.lang.Long planId) {
        this.planId = planId;
    }


    /**
     * Gets the planName value for this EventVO.
     * 
     * @return planName
     */
    public java.lang.String getPlanName() {
        return planName;
    }


    /**
     * Sets the planName value for this EventVO.
     * 
     * @param planName
     */
    public void setPlanName(java.lang.String planName) {
        this.planName = planName;
    }


    /**
     * Gets the resultDesc value for this EventVO.
     * 
     * @return resultDesc
     */
    public java.lang.String getResultDesc() {
        return resultDesc;
    }


    /**
     * Sets the resultDesc value for this EventVO.
     * 
     * @param resultDesc
     */
    public void setResultDesc(java.lang.String resultDesc) {
        this.resultDesc = resultDesc;
    }


    /**
     * Gets the resultId value for this EventVO.
     * 
     * @return resultId
     */
    public java.lang.Long getResultId() {
        return resultId;
    }


    /**
     * Sets the resultId value for this EventVO.
     * 
     * @param resultId
     */
    public void setResultId(java.lang.Long resultId) {
        this.resultId = resultId;
    }


    /**
     * Gets the serviceType value for this EventVO.
     * 
     * @return serviceType
     */
    public java.lang.Integer getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this EventVO.
     * 
     * @param serviceType
     */
    public void setServiceType(java.lang.Integer serviceType) {
        this.serviceType = serviceType;
    }


    /**
     * Gets the type value for this EventVO.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets the type value for this EventVO.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EventVO)) return false;
        EventVO other = (EventVO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.accountId==null && other.getAccountId()==null) || 
             (this.accountId!=null &&
              this.accountId.equals(other.getAccountId()))) &&
            ((this.accountStatus==null && other.getAccountStatus()==null) || 
             (this.accountStatus!=null &&
              this.accountStatus.equals(other.getAccountStatus()))) &&
            ((this.callDate==null && other.getCallDate()==null) || 
             (this.callDate!=null &&
              this.callDate.equals(other.getCallDate()))) &&
            ((this.chargeAmount==null && other.getChargeAmount()==null) || 
             (this.chargeAmount!=null &&
              this.chargeAmount.equals(other.getChargeAmount()))) &&
            ((this.chargeDate==null && other.getChargeDate()==null) || 
             (this.chargeDate!=null &&
              this.chargeDate.equals(other.getChargeDate()))) &&
            ((this.date==null && other.getDate()==null) || 
             (this.date!=null &&
              this.date.equals(other.getDate()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.forwardPhone==null && other.getForwardPhone()==null) || 
             (this.forwardPhone!=null &&
              this.forwardPhone.equals(other.getForwardPhone()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.listId==null && other.getListId()==null) || 
             (this.listId!=null &&
              this.listId.equals(other.getListId()))) &&
            ((this.listPriority==null && other.getListPriority()==null) || 
             (this.listPriority!=null &&
              this.listPriority.equals(other.getListPriority()))) &&
            ((this.listType==null && other.getListType()==null) || 
             (this.listType!=null &&
              this.listType.equals(other.getListType()))) &&
            ((this.mapHour==null && other.getMapHour()==null) || 
             (this.mapHour!=null &&
              this.mapHour.equals(other.getMapHour()))) &&
            ((this.nextChargeDate==null && other.getNextChargeDate()==null) || 
             (this.nextChargeDate!=null &&
              this.nextChargeDate.equals(other.getNextChargeDate()))) &&
            ((this.numA==null && other.getNumA()==null) || 
             (this.numA!=null &&
              this.numA.equals(other.getNumA()))) &&
            ((this.phoneNumber==null && other.getPhoneNumber()==null) || 
             (this.phoneNumber!=null &&
              this.phoneNumber.equals(other.getPhoneNumber()))) &&
            ((this.planId==null && other.getPlanId()==null) || 
             (this.planId!=null &&
              this.planId.equals(other.getPlanId()))) &&
            ((this.planName==null && other.getPlanName()==null) || 
             (this.planName!=null &&
              this.planName.equals(other.getPlanName()))) &&
            ((this.resultDesc==null && other.getResultDesc()==null) || 
             (this.resultDesc!=null &&
              this.resultDesc.equals(other.getResultDesc()))) &&
            ((this.resultId==null && other.getResultId()==null) || 
             (this.resultId!=null &&
              this.resultId.equals(other.getResultId()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType()))) &&
            this.type == other.getType();
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
        if (getAccountId() != null) {
            _hashCode += getAccountId().hashCode();
        }
        if (getAccountStatus() != null) {
            _hashCode += getAccountStatus().hashCode();
        }
        if (getCallDate() != null) {
            _hashCode += getCallDate().hashCode();
        }
        if (getChargeAmount() != null) {
            _hashCode += getChargeAmount().hashCode();
        }
        if (getChargeDate() != null) {
            _hashCode += getChargeDate().hashCode();
        }
        if (getDate() != null) {
            _hashCode += getDate().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getForwardPhone() != null) {
            _hashCode += getForwardPhone().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getListId() != null) {
            _hashCode += getListId().hashCode();
        }
        if (getListPriority() != null) {
            _hashCode += getListPriority().hashCode();
        }
        if (getListType() != null) {
            _hashCode += getListType().hashCode();
        }
        if (getMapHour() != null) {
            _hashCode += getMapHour().hashCode();
        }
        if (getNextChargeDate() != null) {
            _hashCode += getNextChargeDate().hashCode();
        }
        if (getNumA() != null) {
            _hashCode += getNumA().hashCode();
        }
        if (getPhoneNumber() != null) {
            _hashCode += getPhoneNumber().hashCode();
        }
        if (getPlanId() != null) {
            _hashCode += getPlanId().hashCode();
        }
        if (getPlanName() != null) {
            _hashCode += getPlanName().hashCode();
        }
        if (getResultDesc() != null) {
            _hashCode += getResultDesc().hashCode();
        }
        if (getResultId() != null) {
            _hashCode += getResultId().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EventVO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservices.cs.ipcomsa.com/", "eventVO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "callDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chargeAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chargeDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("date");
        elemField.setXmlName(new javax.xml.namespace.QName("", "date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("forwardPhone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "forwardPhone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listPriority");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listPriority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mapHour");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mapHour"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextChargeDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nextChargeDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("planId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "planId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("planName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "planName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
