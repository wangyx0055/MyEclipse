package com.vmg.soap.mo;

import com.vmg.soap.mo.MOSender;

public class MOSenderDDS extends MOSender {

	public MOSenderDDS() {
	}

	public void setTemplate() {
	
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><ReceiveMO xmlns=\"http://tempuri.org/\">"
			+ "<Username>icomphuongbac</Username>"
			+ "<Password>phuongbac123#@!</Password>"
			
			+ "<Sender>$User_ID$</Sender>"
			+ "<ServiceNumber>$Service_ID$</ServiceNumber>"
			+ "<MessageID>$Request_ID$</MessageID>"
			
			+ "<CODE>$Command_Code$</CODE>"
			+ "<TextContent>$Message$</TextContent>"
			
			+ "</ReceiveMO>"
			+ "</soap:Body>" + "</soap:Envelope>";
		  
	}
}
