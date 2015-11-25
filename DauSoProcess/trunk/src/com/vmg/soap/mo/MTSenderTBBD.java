package com.vmg.soap.mo;

public class MTSenderTBBD extends MOSender {

	public MTSenderTBBD() {
	}

	public void setTemplate() {
		/*
		 * super.template = "<?xml version='1.0' encoding='UTF-8'?>" +"<SOAP-ENV:Envelope
		 * xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"
		 * xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		 * xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +"<SOAP-ENV:Body>" +"<ns1:mtReceiver
		 * xmlns:ns1=\"Receiver\"
		 * SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +"<User_ID
		 * xsi:type=\"xsd:string\">$User_ID$</User_ID>" +"<Message
		 * xsi:type=\"xsd:string\">$Message$</Message>" +"<Service_ID
		 * xsi:type=\"xsd:string\">$Service_ID$</Service_ID>" +"<Command_Code
		 * xsi:type=\"xsd:string\">$Command_Code$</Command_Code>" +"<Message_Type
		 * xsi:type=\"xsd:string\">$Message_Type$</Message_Type>" +"<Request_ID
		 * xsi:type=\"xsd:string\">$Request_ID$</Request_ID>" +"<Total_Message
		 * xsi:type=\"xsd:string\">$Total_Message$</Total_Message>" +"<Message_Index
		 * xsi:type=\"xsd:string\">$Message_Index$</Message_Index>" +"<IsMore
		 * xsi:type=\"xsd:string\">$IsMore$</IsMore>" +"<Content_Type
		 * xsi:type=\"xsd:string\">$Content_Type$</Content_Type>" +"<Operator
		 * xsi:type=\"xsd:string\">$Operator$</Operator>" +"</ns1:mtReceiver>" +"</SOAP-ENV:Body>" +"</SOAP-ENV:Envelope>";
		 */

		/*
		 * <?xml version="1.0" encoding="utf-8"?> <soap:Envelope
		 * xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"> <soap:Body>
		 * <SentMTBase64 xmlns="http://tempuri.org/SMS/SMSServices">
		 * <MsgSequence>0</MsgSequence><ReceiverNumber>84983801346</ReceiverNumber>
		 * <ServiceNumber>8081</ServiceNumber>
		 * <SmsContentBase64>ZW0gZ3VpIGxhaSBkYXkgbmhlIQ==</SmsContentBase64>
		 * <SMSType>0</SMSType> <Data /> <isBilling>0</isBilling>
		 * <PartnerCode>908HUK785DHF357BVI9630KDE</PartnerCode> <TelcoCode>0</TelcoCode>
		 * <CommandCode>XSTD</CommandCode></SentMTBase64></soap:Body></soap:Envelope>
		 */
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<soap:Body>" +
		"<SentMT xmlns=\"http://tempuri.org/SMS/SMSServices\">" +
		"<MsgSequence>$Request_ID$</MsgSequence>" +
		"<ReceiverNumber>$User_ID$</ReceiverNumber>" +
		"<ServiceNumber>$Service_ID$</ServiceNumber>" +
		"<SmsContent>$Message$</SmsContent>" +
		"<SMSType>$Content_Type$</SMSType>" +
		"<Data>$Message$</Data><isBilling>$Message_Type$</isBilling><PartnerName>908HUK785DHF357BVI9630KDE</PartnerName></SentMT></soap:Body></soap:Envelope>";

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<SentMT xmlns=\"http://tempuri.org/SMS/SMSServices\">"
				+ "<MsgSequence>$Request_ID$</MsgSequence>"
				+ "<ReceiverNumber>$User_ID$</ReceiverNumber>"
				+ "<ServiceNumber>$Service_ID$</ServiceNumber>"
				+ "<SmsContent>$Message$</SmsContent>"
				+ "<SMSType>$Content_Type$</SMSType>"
				+ "<Data>$Message$</Data>"
				+ "<isBilling>$Message_Type$</isBilling>"
				+ "<PartnerName>908HUK785DHF357BVI9630KDE</PartnerName>"
				+ "</SentMT>" + "</soap:Body>" + "</soap:Envelope>";

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<SentMTBase64 xmlns=\"http://tempuri.org/SMS/SMSServices\">"
				+ "<MsgSequence>$Request_ID$</MsgSequence>"
				+ "<ReceiverNumber>$User_ID$</ReceiverNumber>"
				+ "<ServiceNumber>$Service_ID$</ServiceNumber>"
				+ "<SmsContentBase64>$Message$</SmsContentBase64>"
				+ "<SMSType>$Content_Type$</SMSType>"
				+ "<DataBase64>$Message$</DataBase64>"
				+ "<isBilling>$Message_Type$</isBilling>"
				+ "<PartnerCode>908HUK785DHF357BVI9630KDE</PartnerCode>"
				+ "<TelcoCode>0</TelcoCode>"
				+ "<CommandCode>$Command_Code$</CommandCode>"
				+ "</SentMTBase64>" + "</soap:Body>" + "</soap:Envelope>";
		

		 
		super.template =  "<?xml version='1.0' encoding='UTF-8'?>"
			+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
			+ "<SOAP-ENV:Body>"
			+ "<ns1:mtReceiver xmlns:ns1=\"Receiver\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
			+ "<User_ID xsi:type=\"xsd:string\">$User_ID$</User_ID>"
			+ "<Message xsi:type=\"xsd:string\">$Message$</Message>"
			+ "<Service_ID xsi:type=\"xsd:string\">$Service_ID$</Service_ID>"
			+ "<Command_Code xsi:type=\"xsd:string\">$Command_Code$</Command_Code>"
			+ "<Message_Type xsi:type=\"xsd:string\">$Message_Type$</Message_Type>"
			+ "<Request_ID xsi:type=\"xsd:string\">$Request_ID$</Request_ID>"
			+ "<Total_Message xsi:type=\"xsd:string\">$Total_Message$</Total_Message>"
			+ "<Message_Index xsi:type=\"xsd:string\">$Message_Index$</Message_Index>"
			+ "<IsMore xsi:type=\"xsd:string\">$IsMore$</IsMore>"
			+ "<Content_Type xsi:type=\"xsd:string\">$Content_Type$</Content_Type>"
			+ "<Operator xsi:type=\"xsd:string\">Operator</Operator>"
			+ "</ns1:mtReceiver>" + "</SOAP-ENV:Body>"
			+ "</SOAP-ENV:Envelope>";
		

        
	}
}
