package soap;

public  class MOSenderThienNgan extends MOSender {
	public MOSenderThienNgan() {

	}

	@Override
	public void setTemplate() {

		super.template = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:aes=\"http://aes\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<aes:moReceiver>"
				+ "<aes:UserName>$UserName$</aes:UserName>"
				+ "<aes:Password>$Passwd$</aes:Password>"
				+ "<aes:Msisdn>$User_ID$</aes:Msisdn>"
				+ "<aes:ServiceNumber>$Service_ID$</aes:ServiceNumber>"
				+ "<aes:ServiceCode>$Command_Code$</aes:ServiceCode>"
				+ "<aes:Info>$Message$</aes:Info>"
				+ "<aes:RequestId>$Request_ID$</aes:RequestId>"
				+ "<aes:Operator>VMS</aes:Operator>"
				+ "<aes:Channel_Type>$ChannelType$</aes:Channel_Type>"
				+ " </aes:moReceiver>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";
	}

}
