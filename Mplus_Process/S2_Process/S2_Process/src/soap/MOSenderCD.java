package soap;

public class MOSenderCD extends MOSender {
	public MOSenderCD(){
		
	}
	public void setTemplate() {
		/*super.template = 
			"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mpl=\"MPlusService\">"
			+ "<soapenv:Header/>"
			+ "<soapenv:Body>"
			+ "<mpl:sendMo soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
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
			+ "</mpl:sendMo>" 
			+ "</soapenv:Body>" 
			+ "</soapenv:Envelope>";*/
		
		super.template = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:api=\"http://api.icom.messaging.cdialogues.com\">"
			   +"<soapenv:Header/>"
			   +"<soapenv:Body>"
			   +"   <api:moReceiver>"
			   +"      <api:sUser_ID>$User_ID$</api:sUser_ID>"
			   +"      <api:sService_ID>$Service_ID$</api:sService_ID>"
			   +"      <api:sCommand_Code>$Command_Code$</api:sCommand_Code>"
			   +"      <api:sMessage>$Message$</api:sMessage>"
			   +"      <api:sRequest_ID>$Request_ID$</api:sRequest_ID>"
			   +"      <api:sOperator>VMS</api:sOperator>"
			   +"      <api:sChannel_Type>$ChannelType$</api:sChannel_Type>"
			   +"   </api:moReceiver>"
			   +"</soapenv:Body>"
			   +"</soapenv:Envelope>";
				
	}
}

