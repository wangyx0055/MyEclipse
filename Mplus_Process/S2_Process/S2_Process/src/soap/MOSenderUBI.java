package soap;

public class MOSenderUBI extends MOSender {

	public MOSenderUBI() {
	}

	@Override
	public void setTemplate() {

		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<messageReceiver xmlns=\"http://tempuri.org/\">"
				+ "<User_ID>$User_ID$</User_ID>"
				+ "<Service_ID>$Service_ID$</Service_ID>"
				+ "<Command_code>$Command_Code$</Command_code>"
				+ "<Message>$Message$</Message>"
				+ "<Request_ID>$Request_ID$</Request_ID>"
				+ "<Operator>$Operator$</Operator>" + "</messageReceiver>"
				+ "</soap:Body>" + "</soap:Envelope>";
	}
}
