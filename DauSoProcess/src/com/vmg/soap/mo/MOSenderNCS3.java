package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderNCS3 extends MOSender {

	public MOSenderNCS3() {
	}

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><urn:smsGateway soapenv:encodingStyle\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<User_ID>$User_ID$</User_ID>"
				+ "<Message>$Message$</Message>"
				+ "<Service_ID>$Service_ID$</Service_ID>"
				+ "<Command_Code>$Command_Code$</Command_Code>"
				+ "<Operator>$Operator$</Operator>"
				+ "<Request_ID>$Request_ID$</Request_ID>"
				+"</urn:smsGateway>"
				+ "</soap:Body>" + "</soap:Envelope>";

	}
}
