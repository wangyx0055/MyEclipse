package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderINET extends MOSender {

	public void setTemplate() {



		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
				+ "<soap:Body>" +
				"<mo xmlns=\"http://servlet.smsgw.mbridge/\">"
				+ "<User_ID xmlns=\"\">$User_ID$</User_ID>"
				+ "<Service_ID xmlns=\"\">$Service_ID$</Service_ID>"
				+ "<Command_Code xmlns=\"\">$Command_Code$</Command_Code>"
				+ "<Message xmlns=\"\">$Message$</Message>"
				+ "<Request_ID xmlns=\"\">$Request_ID$</Request_ID>"
				+ "<Operator xmlns=\"\">$Operator$</Operator>"
				+ "</mo>" +
				"</soap:Body>" +
				"</soap:Envelope>";
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
			+"<soap:Body>"
			+"<receiverMO xmlns=\"http://hanoihtv.inet\">"
			+"<User_Id>$User_ID$</User_Id><Service_Id>$Service_ID$</Service_Id><Mobile_Operator>$Operator$</Mobile_Operator><Command_Code>$Command_Code$</Command_Code><Message>$Message$</Message><Request_Id>$Request_ID$</Request_Id></receiverMO></soap:Body></soap:Envelope>";
		

	}

}
