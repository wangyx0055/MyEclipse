package soap;

public class MOSenderVASC extends MOSender {
	public MOSenderVASC() {
	}

	@Override
	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.mc.vasc.com\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ser:sendMo soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<sUser xsi:type=\"xsd:string\">$UserName$</sUser>"
				+ "<sPass xsi:type=\"xsd:string\">$Passwd$</sPass>"
				+ "<sMsisdn xsi:type=\"xsd:string\">$User_ID$</sMsisdn>"
				+ "<sServiceNumber xsi:type=\"xsd:string\">$Service_ID$</sServiceNumber>"
				+ "<sServiceCode xsi:type=\"xsd:string\">$Command_Code$</sServiceCode>"
				+ "<sInfo xsi:type=\"xsd:string\">$Message$</sInfo>"
				+ "<sRequestId xsi:type=\"xsd:string\">$Request_ID$</sRequestId>"
				+ "<sOperator xsi:type=\"xsd:string\">VMS</sOperator>"
				+ "<sRegisType xsi:type=\"xsd:int\">0</sRegisType>"
				+ "<iMulti xsi:type=\"xsd:int\">$iMulti$</iMulti>"
				+ "</ser:sendMo>" + "</soapenv:Body>" + "</soapenv:Envelope>";

	}
}
