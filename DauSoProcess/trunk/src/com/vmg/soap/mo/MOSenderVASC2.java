package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderVASC2 extends MOSender {

	public MOSenderVASC2() {
	}

	public void setTemplate() {
	
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<messageReceiver xmlns=\"http://tempuri.org/\">"
					+ "<UserID>$User_ID$</UserID>"
					+ "<ServiceID>$Service_ID$</ServiceID>"
					+ "<CommandCode>$Command_Code$</CommandCode>"
					+ "<Message>$Message$</Message>"
					+ "<RequestID>$Request_ID$</RequestID>"
					+ "</messageReceiver>"
					+ "</soap:Body>"
					+ "</soap:Envelope>";


	}
}
