package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderVTM extends MOSender {

	public MOSenderVTM() {
	}

	public void setTemplate() {
	
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><ReceiveMO xmlns=\"http://tempuri.org/\">"
			
			+ "<moid>$Request_ID$</moid>"
			+ "<moseq></moseq>"
			+ "<src>$User_ID$</src>"
			+ "<dest>$Service_ID$</dest>"
			+ "<cmdcode>$Command_Code$</cmdcode>"
			+ "<msgbody>$Message$</msgbody>"
			+ "<opid>$Operator$</opid>"
			+ "<username>icom@vtm</username>"
			+ "<password>*qas^w@ed!</password>"
			+ "</ReceiveMO>"
			+ "</soap:Body>" + "</soap:Envelope>";
       
	}
}
