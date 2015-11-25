package soap;

public class MOSenderKRAZE extends MOSender {

	public MOSenderKRAZE() {
	}

	@Override
	public void setTemplate() {
				
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		  "<soap:Body>" +
		    "<sendMT xmlns=\"http://impl.services.gateway.vht.com\">" +
		      "<serviceNum>$Service_ID$</serviceNum>" +
		      "<phoneNumber>$User_ID$</phoneNumber>" +
		      "<message>$Message$</message>" +
		      "<requestId>$Request_ID$</requestId>" +
		      "<provider>icom</provider>" +
		      "<service>$Command_Code$</service>" +
		      "<secretKey>ic0m2oo9</secretKey>" +
		    "</sendMT>" +
		  "</soap:Body>" +
		"</soap:Envelope>";
		
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soap:Body><GetMOData xmlns=\"http://tempuri.org/\">"
			+ "<UserID>$User_ID$</UserID>"
			+ "<ServiceID>$Service_ID$</ServiceID>"
			+ "<CommandCode>$Command_Code$</CommandCode>"
			+ "<GameCode>$Message$</GameCode>"
			+ "<RequestID>$Request_ID$</RequestID>"
			+ "<Operator>$Operator$</Operator>" 
			+ "<UserName>ICOM2010</UserName>"
			+ "<Password>ICOM2010!@#</Password>"
			+ "</GetMOData>"
			+ "</soap:Body>" + "</soap:Envelope>";
		
	
        
	}
}
