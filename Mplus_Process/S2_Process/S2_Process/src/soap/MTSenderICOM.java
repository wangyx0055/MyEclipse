package soap;

public class MTSenderICOM extends MOSender {

	public MTSenderICOM() {
	}

	@Override
	public void setTemplate() {
		
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
			+ "<Operator xsi:type=\"xsd:string\">VMS</Operator>"
			+ "</ns1:mtReceiver>" + "</SOAP-ENV:Body>"
			+ "</SOAP-ENV:Envelope>";
		


        
	}
}
