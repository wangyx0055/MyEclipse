package com.vmg.soap.mo;

public class MOSenderFIBO extends MOSender {

	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Header>"
				+ "<SMSReceiverCredential xmlns=\"http://smsreceiver.payoo.com.vn/\">"
				+ "<Username>Basic TU9TZW5zb3I6WTB1QHJlUjFnaHQ=</Username>"
				+ "<Password></Password>" + "</SMSReceiverCredential>"
				+ "</soap:Header>" + "<soap:Body>"
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
				+ "<soap:Body>"
				+ "<messageReceiver xmlns=\"http://tempuri.org/\">"
				+ "<User_ID>$User_ID$</User_ID>"
				+ "<Service_ID>$Service_ID$</Service_ID>"
				+ "<Command_Code>$Command_Code$</Command_Code>"
				+ "<Message>$Message$</Message>"
				+ "<Request_ID>$Request_ID$</Request_ID>"
				+ "<MobiOperator>$Operator$</MobiOperator>"
				+ "</messageReceiver>" + "</soap:Body>" + "</soap:Envelope>";

	}
}
