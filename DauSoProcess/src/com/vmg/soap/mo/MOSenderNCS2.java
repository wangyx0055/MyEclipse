package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderNCS2 extends MOSender {

	public MOSenderNCS2() {
	}

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body><moReceiver xmlns=\"http://gofficemo.vgate.vn:1111/\">"
				+ "<User_ID>$User_ID$</User_ID>"
				+ "<Service_ID>$Service_ID$</Service_ID>"
				+ "<Command_Code>$Command_Code$</Command_Code>"
				+ "<Message>$Message$</Message>"
				+ "<Request_ID>$Request_ID$</Request_ID>"
				+ "<MobiOperator>$Operator$</MobiOperator>" + "</moReceiver>"
				+ "</soap:Body>" + "</soap:Envelope>";

	}
}
