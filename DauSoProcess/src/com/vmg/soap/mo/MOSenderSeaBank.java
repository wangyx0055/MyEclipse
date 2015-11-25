package com.vmg.soap.mo;

public class MOSenderSeaBank extends MOSender {

	public void setTemplate() {

		super.template = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soap:Body>"
				+ "<ns1:moreceiver xmlns:ns1=\"http://ws.sms.com\">"
				+ "<ns1:in0>$User_ID$</ns1:in0>"
				+ "<ns1:in1>$Service_ID$</ns1:in1>"
				+ "<ns1:in2>$Command_Code$</ns1:in2>"
				+ "<ns1:in3>$Message$</ns1:in3>"
				+ "<ns1:in4>$Request_ID$</ns1:in4>"
				+ "<ns1:in5>$Operator$</ns1:in5>"
				+ "</ns1:moreceiver>"
				+ "</soap:Body>" + "</soap:Envelope>";
		
		
		super.template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "<soapenv:Body><messageReceiver xmlns=\"http://deploy.vmg.vn\">"
			+ "<User_ID>$User_ID$</User_ID>"
			+ "<Service_ID>$Service_ID$</Service_ID>"
			+ "<Command_Code>$Command_Code$</Command_Code>"
			+ "<Message>$Message$</Message>" + "<Request_ID>$Request_ID$</Request_ID>"
			+ "</messageReceiver></soapenv:Body></soapenv:Envelope>";
		
		super.template = "<?xml version='1.0' encoding='UTF-8'?>"
			+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
			+ "<SOAP-ENV:Body>"
			+ "<ns1:moReceiver xmlns:ns1=\"Receiver\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
			+ "<User_ID xsi:type=\"xsd:string\">$User_ID$</User_ID>"
			+ "<Service_ID xsi:type=\"xsd:string\">$Service_ID$</Service_ID>"
			+ "<Command_Code xsi:type=\"xsd:string\">$Command_Code$</Command_Code>"
			+ "<Message xsi:type=\"xsd:string\">$Message$</Message>"
			+ "<Request_ID xsi:type=\"xsd:string\">$Request_ID$</Request_ID>"
			+ "</ns1:moReceiver>" + "</SOAP-ENV:Body>"
			+ "</SOAP-ENV:Envelope>";
		
		super.template = "<?xml version='1.0' encoding='UTF-8'?>"
			+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
			+ "<SOAP-ENV:Body>"
			+ "<ns1:moReceiverStandard xmlns:ns1=\"Receiver\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
			+ "<User_ID xsi:type=\"xsd:string\">$User_ID$</User_ID>"
			+ "<Service_ID xsi:type=\"xsd:string\">$Service_ID$</Service_ID>"
			+ "<Command_Code xsi:type=\"xsd:string\">$Command_Code$</Command_Code>"
			+ "<Message xsi:type=\"xsd:string\">$Message$</Message>"
			+ "<Request_ID xsi:type=\"xsd:string\">$Request_ID$</Request_ID>"
			+ "<Operator xsi:type=\"xsd:string\">$Operator$</Operator>"
			+ "</ns1:moReceiverStandard>" + "</SOAP-ENV:Body>"
			+ "</SOAP-ENV:Envelope>";
	}
}
