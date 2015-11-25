package com.vmg.soap.mo;

import com.vmg.soap.mo.SenderVHT;

public class MOSenderVHT extends SenderVHT {

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<receiveMO xmlns=\"http://impl.service.gateway.sms.vht.com\">"
				+ "<operator>$Operator$</operator>"
				+ "<serviceId>$Service_ID$</serviceId>"
				+ "<userId>$User_ID$</userId>"
				+ "<requestId>$Request_ID$</requestId>"
				+ "<message>$Message$</message>" + "<username>icom</username>"
				+ "<commandCode>$Command_Code$</commandCode>"
				+ "<password>!c0m@2o1o</password>" + "</receiveMO>"
				+ "</soap:Body>" + "</soap:Envelope>";

	}

}
