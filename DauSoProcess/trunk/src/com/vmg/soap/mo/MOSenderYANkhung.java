package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderYANkhung extends MOSender {

	public MOSenderYANkhung() {
	}

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">"
			+" <soapenv:Header/>"
			+"   <soapenv:Body>"
				+ "<tem:PhotoFrame>"
				+ "<tem:messcontent>$Message$</tem:messcontent>"
				+ "<tem:shortcode>$Service_ID$</tem:shortcode>"
				+ "<tem:mobilenumber>$User_ID$</tem:mobilenumber>"
				+ "<tem:request_id>$Request_ID$</tem:request_id>"
				+ "<tem:operatorname>$Operator$</tem:operatorname>"
				+ "<tem:requestsecurityCode>123</tem:requestsecurityCode>" 
				+ "</tem:PhotoFrame>"
		  + "</soapenv:Body>"		
		+"</soapenv:Envelope>";
		  
		

	}
}
