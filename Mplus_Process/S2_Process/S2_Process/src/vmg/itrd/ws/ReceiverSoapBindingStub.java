/**
 * ReceiverSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package vmg.itrd.ws;

public class ReceiverSoapBindingStub extends org.apache.axis.client.Stub implements vmg.itrd.ws.Receiver {   
	private java.util.Vector cachedSerClasses = new java.util.Vector();
private java.util.Vector cachedSerQNames = new java.util.Vector();
private java.util.Vector cachedSerFactories = new java.util.Vector();
private java.util.Vector cachedDeserFactories = new java.util.Vector();

static org.apache.axis.description.OperationDesc [] _operations;

static {
    _operations = new org.apache.axis.description.OperationDesc[6];
    org.apache.axis.description.OperationDesc oper;
    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("InsertMTQueueVMS");
    oper.addParameter(new javax.xml.namespace.QName("", "USER_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MOBILE_OPERATOR"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMMAND_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INFO"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SUBMIT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DONE_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "PROCESS_RESULT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REQUEST_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "TOTAL_SEGMENTS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RETRIES_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INSERT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "NOTES"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AMOUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CHANNEL_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "InsertMTQueueVMSReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[0] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("InsertVMSChargeOnline");
    oper.addParameter(new javax.xml.namespace.QName("", "USER_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MOBILE_OPERATOR"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMMAND_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INFO"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SUBMIT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DONE_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "PROCESS_RESULT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REQUEST_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "TOTAL_SEGMENTS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RETRIES_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INSERT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "NOTES"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_NAME"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CHANNEL_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AMOUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "InsertVMSChargeOnlineReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[1] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("insertMlist");
    oper.addParameter(new javax.xml.namespace.QName("", "mlistTable"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "USER_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "OPTIONS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "FAILURES"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "LAST_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AUTOTIMESTAMPS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMMAND_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REQUEST_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MOBILE_OPERATOR"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MT_COUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MT_FREE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DURATION"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AMOUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMPANY_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "ACTIVE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CHANNEL_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REG_COUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DATE_RETRY"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "insertMlistReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[2] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("insertToMlistFromMlist");
    oper.addParameter(new javax.xml.namespace.QName("", "toMlistTable"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "fromMlistTable"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "Registry_Again"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "userId"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "commandCode"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "serviceId"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "insertToMlistFromMlistReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[3] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("insertVmsChargePackage");
    oper.addParameter(new javax.xml.namespace.QName("", "USER_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MOBILE_OPERATOR"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMMAND_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INFO"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SUBMIT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DONE_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "PROCESS_RESULT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REQUEST_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "TOTAL_SEGMENTS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RETRIES_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INSERT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "NOTES"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_NAME"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CHANNEL_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AMOUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DAY_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RESULT_CHARGE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "IS_THE_PACKET"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "insertVmsChargePackageReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[4] = oper;

    oper = new org.apache.axis.description.OperationDesc();
    oper.setName("insertVmsReChargePackage");
    oper.addParameter(new javax.xml.namespace.QName("", "USER_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MOBILE_OPERATOR"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "COMMAND_CODE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INFO"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SUBMIT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DONE_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "PROCESS_RESULT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "REQUEST_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "MESSAGE_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "TOTAL_SEGMENTS"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RETRIES_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "INSERT_DATE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "NOTES"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "SERVICE_NAME"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CHANNEL_TYPE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "CONTENT_ID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "AMOUNT"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "DAY_NUM"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "RESULT_CHARGE"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.addParameter(new javax.xml.namespace.QName("", "IS_THE_PACKET"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
    oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    oper.setReturnClass(int.class);
    oper.setReturnQName(new javax.xml.namespace.QName("", "insertVmsReChargePackageReturn"));
    oper.setStyle(org.apache.axis.constants.Style.RPC);
	oper.setUse(org.apache.axis.constants.Use.ENCODED);
    _operations[5] = oper;

}

public ReceiverSoapBindingStub() throws org.apache.axis.AxisFault {
     this(null);
}

public ReceiverSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
     this(service);
     super.cachedEndpoint = endpointURL;
}

public ReceiverSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
    if (service == null) {
        super.service = new org.apache.axis.client.Service();
    } else {
        super.service = service;
    }
}

private org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
    try {
        org.apache.axis.client.Call _call =
                (org.apache.axis.client.Call) super.service.createCall();
        if (super.maintainSessionSet) {
            _call.setMaintainSession(super.maintainSession);
        }
        if (super.cachedUsername != null) {
            _call.setUsername(super.cachedUsername);
        }
        if (super.cachedPassword != null) {
            _call.setPassword(super.cachedPassword);
        }
        if (super.cachedEndpoint != null) {
            _call.setTargetEndpointAddress(super.cachedEndpoint);
        }
        if (super.cachedTimeout != null) {
            _call.setTimeout(super.cachedTimeout);
        }
        if (super.cachedPortName != null) {
            _call.setPortName(super.cachedPortName);
        }
        java.util.Enumeration keys = super.cachedProperties.keys();
        while (keys.hasMoreElements()) {
            java.lang.String key = (java.lang.String) keys.nextElement();
            _call.setProperty(key, super.cachedProperties.get(key));
        }
        return _call;
    }
    catch (java.lang.Throwable t) {
        throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
    }
}

public int insertMTQueueVMS(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR, java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE, java.lang.String INFO, java.lang.String SUBMIT_DATE, java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT, java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID, java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS, java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE, java.lang.String NOTES, java.lang.String AMOUNT, java.lang.String CHANNEL_TYPE) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[0]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "InsertMTQueueVMS"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, AMOUNT, CHANNEL_TYPE});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}

public int insertVMSChargeOnline(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR, java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE, java.lang.String INFO, java.lang.String SUBMIT_DATE, java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT, java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID, java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS, java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE, java.lang.String NOTES, java.lang.String SERVICE_NAME, java.lang.String CHANNEL_TYPE, java.lang.String CONTENT_ID, java.lang.String AMOUNT) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[1]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "InsertVMSChargeOnline"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}

public int insertMlist(java.lang.String mlistTable, java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String DATE, java.lang.String OPTIONS, java.lang.String FAILURES, java.lang.String LAST_CODE, java.lang.String AUTOTIMESTAMPS, java.lang.String COMMAND_CODE, java.lang.String REQUEST_ID, java.lang.String MESSAGE_TYPE, java.lang.String MOBILE_OPERATOR, java.lang.String MT_COUNT, java.lang.String MT_FREE, java.lang.String DURATION, java.lang.String AMOUNT, java.lang.String CONTENT_ID, java.lang.String SERVICE, java.lang.String COMPANY_ID, java.lang.String ACTIVE, java.lang.String CHANNEL_TYPE, java.lang.String REG_COUNT, java.lang.String DATE_RETRY) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[2]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "insertMlist"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {mlistTable, USER_ID, SERVICE_ID, DATE, OPTIONS, FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT, DATE_RETRY});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}

public int insertToMlistFromMlist(java.lang.String toMlistTable, java.lang.String fromMlistTable, java.lang.String registry_Again, java.lang.String userId, java.lang.String commandCode, java.lang.String serviceId) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[3]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "insertToMlistFromMlist"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {toMlistTable, fromMlistTable, registry_Again, userId, commandCode, serviceId});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}

public int insertVmsChargePackage(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR, java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE, java.lang.String INFO, java.lang.String SUBMIT_DATE, java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT, java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID, java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS, java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE, java.lang.String NOTES, java.lang.String SERVICE_NAME, java.lang.String CHANNEL_TYPE, java.lang.String CONTENT_ID, java.lang.String AMOUNT, java.lang.String DAY_NUM, java.lang.String RESULT_CHARGE, java.lang.String IS_THE_PACKET) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[4]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "insertVmsChargePackage"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}

public int insertVmsReChargePackage(java.lang.String USER_ID, java.lang.String SERVICE_ID, java.lang.String MOBILE_OPERATOR, java.lang.String COMMAND_CODE, java.lang.String CONTENT_TYPE, java.lang.String INFO, java.lang.String SUBMIT_DATE, java.lang.String DONE_DATE, java.lang.String PROCESS_RESULT, java.lang.String MESSAGE_TYPE, java.lang.String REQUEST_ID, java.lang.String MESSAGE_ID, java.lang.String TOTAL_SEGMENTS, java.lang.String RETRIES_NUM, java.lang.String INSERT_DATE, java.lang.String NOTES, java.lang.String SERVICE_NAME, java.lang.String CHANNEL_TYPE, java.lang.String CONTENT_ID, java.lang.String AMOUNT, java.lang.String DAY_NUM, java.lang.String RESULT_CHARGE, java.lang.String IS_THE_PACKET) throws java.rmi.RemoteException {
    if (super.cachedEndpoint == null) {
        throw new org.apache.axis.NoEndPointException();
    }
    org.apache.axis.client.Call _call = createCall();
    _call.setOperation(_operations[5]);
    _call.setUseSOAPAction(true);
    _call.setSOAPActionURI("");
    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
    _call.setOperationName(new javax.xml.namespace.QName("http://ws.itrd.vmg", "insertVmsReChargePackage"));

    setRequestHeaders(_call);
    setAttachments(_call);
    java.lang.Object _resp = _call.invoke(new java.lang.Object[] {USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET});

    if (_resp instanceof java.rmi.RemoteException) {
        throw (java.rmi.RemoteException)_resp;
    }
    else {
        extractAttachments(_call);
        try {
            return ((java.lang.Integer) _resp).intValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
        }
    }
}
}
