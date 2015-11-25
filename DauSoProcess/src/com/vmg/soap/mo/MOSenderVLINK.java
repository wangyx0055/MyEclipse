package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderVLINK extends MOSender {

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soapenv:Body>"
				+ "<ns1:GetSmsContent soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://203.162.168.170/smsvietlaw/\">"
				+ "<ns1:user_id xsi:type=\"xsd:string\">$User_ID$</ns1:user_id>"
				+ "<ns1:service_id xsi:type=\"xsd:string\">$Service_ID$</ns1:service_id>"
				+ "<ns1:command_code xsi:type=\"xsd:string\">$Command_Code$</ns1:command_code>"
				+ "<ns1:info xsi:type=\"xsd:string\">$Message$</ns1:info>"
				+ "<ns1:request_id xsi:type=\"xsd:string\">$Request_ID$</ns1:request_id>"
				+ "</ns1:GetSmsContent>" + "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetSmsContent xmlns=\"http://localhost/SmsGatewayPartner/\"><user_id>$User_ID$</user_id><service_id>$Service_ID$</service_id><command_code>$Command_Code$</command_code><info>$Message$</info><request_id>$Request_ID$</request_id></GetSmsContent></soap:Body></soap:Envelope>";

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>" + "<receiverMO xmlns=\"http://tempuri.org/\">"
				+ "<UserId>$User_ID$</UserId>" + "<PrefixId>$Service_ID$</PrefixId>"
				+ "<Command_Code>$Command_Code$</Command_Code>"
				+ "<MessageIn>$Message$</MessageIn>"
				+ "<RequestId>$Request_ID$</RequestId>"
				+ "<TelcoId>$Operator$</TelcoId>"
				+ "<UserName>6x54</UserName>" + "<Password>6x54</Password>"
				+ "</receiverMO>" + "</soap:Body>" + "</soap:Envelope>";

	}

}
