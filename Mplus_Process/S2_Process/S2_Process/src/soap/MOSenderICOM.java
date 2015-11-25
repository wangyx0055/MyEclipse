package soap;

public class MOSenderICOM extends MOSender {

	public MOSenderICOM() {
	}

	@Override
	public void setTemplate() {
		
		super.template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "<soapenv:Body>"
			+ "<ns1:MOReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"http://tempuri.org/GetMOData/MOReceiver\">"
			+ "<ns1:User_ID xsi:type=\"xsd:string\">$User_ID$</ns1:User_ID>"
			+ "<ns1:Service_ID xsi:type=\"xsd:string\">$Service_ID$</ns1:Service_ID>"
			+ "<ns1:Command_Code xsi:type=\"xsd:string\">$Command_Code$</ns1:Command_Code>"
			+ "<ns1:Message xsi:type=\"xsd:string\">$Message$</ns1:Message>"
			+ "<ns1:Request_ID xsi:type=\"xsd:string\">$Request_ID$</ns1:Request_ID>"
			+ "</ns1:MOReceiver>" + "</soapenv:Body>"
			+ "</soapenv:Envelope>";
	}
}
