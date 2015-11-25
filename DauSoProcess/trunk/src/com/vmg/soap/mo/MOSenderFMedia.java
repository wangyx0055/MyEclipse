package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderFMedia extends MOSender {

	public MOSenderFMedia() {
	}

	public void setTemplate() {
	
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><ReceiveMO xmlns=\"http://tempuri.org/\">"
			+ "<mobileno>$User_ID$</mobileno>"
			+ "<gameid>$Message$</gameid>"
			+ "<username>ICOM</username>"
			+ "<password>j4c7s1</password>"
			+ "</ReceiveMO>"
			+ "</soap:Body>" + "</soap:Envelope>";
       
	}
}
