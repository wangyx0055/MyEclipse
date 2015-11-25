package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderKRAZE extends MOSender {

	public MOSenderKRAZE() {
	}

	public void setTemplate() {
				
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><Receiver xmlns=\"http://tempuri.org/\">"
			+ "<operatorID>$OperatorID$</operatorID>"
			+ "<username>NaiscorpMusic2010</username>"
			+ "</Receiver>"
			+ "</soap:Body>" + "</soap:Envelope>";
		
	
        
	}
}
