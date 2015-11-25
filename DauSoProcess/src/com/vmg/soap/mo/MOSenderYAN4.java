package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderYAN4 extends MOSender {

	public MOSenderYAN4() {
	}

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><ProcessRingTone  xmlns=\"http://tempuri.org/\">"
				+ "<messcontent>$Message$</messcontent>"
				+ "<shortcode>$Service_ID$</shortcode>"
				+ "<mobilenumber>$User_ID$</mobilenumber>"
				+ "<request_id>$Request_ID$</request_id>"
				+ "<operatorname>$Operator$</operatorname>"
				+ "<requestsecurityCode>01331679458</requestsecurityCode>" + "</ProcessRingTone>"
				+ "</soap:Body>" + "</soap:Envelope>";
		

		
	}
}
