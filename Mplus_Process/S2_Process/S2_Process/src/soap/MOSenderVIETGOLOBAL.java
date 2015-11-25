package soap;

public class MOSenderVIETGOLOBAL extends MOSender {
	public MOSenderVIETGOLOBAL(){
		
	}
	public void setTemplate() {
		super.template = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">"+
			"<soapenv:Header/>"+
			"<soapenv:Body>"+
			"<tem:MoReveive>"+
			"<tem:sUserName>$UserName$</tem:sUserName>"+
			"<tem:sPassword>$Passwd$</tem:sPassword>"+
			"<tem:sMsisdn>$User_ID$</tem:sMsisdn>"+
			"<tem:sServiceNumber>$Service_ID$</tem:sServiceNumber>"+
			"<tem:sServiceCode>$Command_Code$</tem:sServiceCode>"+
			"<tem:sInfo>$Message$</tem:sInfo>"+
			"<tem:iRequestId>$Request_ID$</tem:iRequestId>"+
			"<tem:sOperator>VMS</tem:sOperator>"+
			"<tem:iChannel_Type>$iMulti$</tem:iChannel_Type>"+
			"</tem:MoReveive>"+	
			"</soapenv:Body>"+
			"</soapenv:Envelope>";
	}
}

