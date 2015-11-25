package com.vmg.soap.mo;

public class MOSenderVietUnion1 extends MOSender {

	public void setTemplate() {

		super.template = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<soap:Body>"
				+ "<ns1:moreceiver xmlns:ns1=\"http://ws.sms.com\">"
				+ "<ns1:in0>$User_ID$</ns1:in0>"
				+ "<ns1:in1>$Service_ID$</ns1:in1>"
				+ "<ns1:in2>$Command_Code$</ns1:in2>"
				+ "<ns1:in3>$Message$</ns1:in3>"
				+ "<ns1:in4>$Request_ID$</ns1:in4>"
				+ "<ns1:in5>$Operator$</ns1:in5>"
				+ "</ns1:moreceiver>"
				+ "</soap:Body>" + "</soap:Envelope>";

		

		
		
		
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
			+ "<soap:Body><GetMOData xmlns=\"http://tempuri.org/\">"
			+ "<UserID>$User_ID$</UserID>"
			+ "<ServiceID>$Service_ID$</ServiceID>"
			+ "<CommandCode>$Command_Code$</CommandCode>"
			+ "<Content>$Message$</Content>"
			+ "<RequestID>$Request_ID$</RequestID>"
			+ "<Operator>$Operator$</Operator>" + "</GetMOData>"
			+ "</soap:Body>" + "</soap:Envelope>";
		
		
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Header>"
			+ "<SMSReceiverCredential xmlns=\"http://smsreceiver.payoo.com.vn/\">"
			+ "<Username>Basic TU9TZW5zb3I6WTB1QHJlUjFnaHQ=</Username>"
			+ "<Password></Password>"
			+ "</SMSReceiverCredential>" + "</soap:Header>" + "<soap:Body>"
			+ "<receiverMO xmlns=\"http://smsreceiver.payoo.com.vn/\">"
			+ "<User_ID>$User_ID$</User_ID>"
			+ "<Service_ID>$Service_ID$</Service_ID>"
			+ "<Command_Code>$Command_Code$</Command_Code>"
			+ "<Message>$Message$</Message>"
			+ "<Request_ID>$Request_ID$</Request_ID>"
			+ "<Operator>$Operator$</Operator>" + "</receiverMO>"
			+ "</soap:Body>" + "</soap:Envelope>";
		
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		    + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
		    + " <soap:Header>"
		    + " <SMSReceiverCredential xmlns=\"http://smsreceiver.payoo.com.vn\">"
		    + " <Username>VnnICOM</Username>"
		    + " <Password>DovvJ0neS&P500Nsdq</Password>"
		    + " </SMSReceiverCredential>" + " </soap:Header>"
		    + " <soap:Body>"
		    + "<receiverMO xmlns=\"http://smsreceiver.payoo.com.vn\">"
		    + "  <User_ID>$User_ID$</User_ID>"
		    + "  <Service_ID>$Service_ID$</Service_ID>"
		    + " <Command_Code>$Command_Code$</Command_Code>"
		    + " <Message>$Message$</Message>"
		    + " <Request_ID>$Request_ID$</Request_ID>"
		    + " <Operator>$Operator$</Operator>" + " </receiverMO>"
		    + " </soap:Body>" + "</soap:Envelope>";

	}
}
