package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderNCS extends MOSender {

	public MOSenderNCS() {
	}

	public void setTemplate() {


		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><ReceiveMO xmlns=\"http://tempuri.org/\">"
			+ "<moid>$Request_ID$</moid>"
			+ "<moseq>$Request_ID$</moseq>"
			+ "<src>$User_ID$</src>"
			+ "<dest>$Service_ID$</dest>"
			+ "<cmdcode>$Command_Code$</cmdcode>"
			+ "<msgbody>$Message$</msgbody>"
			+ "<username>ws_authe_user2</username>"
			+ "<password>uS3rl_Ws2o1l</password>"
			+ "</ReceiveMO>"
			+ "</soap:Body>" + "</soap:Envelope>";
       
	}
}
