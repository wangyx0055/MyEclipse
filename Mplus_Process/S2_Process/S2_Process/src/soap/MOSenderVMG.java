package soap;

public class MOSenderVMG extends MOSender {

	public MOSenderVMG() {
	}

	@Override
	public void setTemplate() {
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ " <soap:Body>"
			+ "<Icom_WSProcessMo xmlns=\"http://tempuri.org/\">"
			+ "   <User_ID>$User_ID$</User_ID>"
			+ "  <Service_ID>$Service_ID$</Service_ID>"
			+ " <Command_Code>$Command_Code$</Command_Code>"
			+ " <Message>$Message$</Message>"
			+ "  <Request_ID>$Request_ID$</Request_ID>"
			+ " <user>icomsms</user>" + "   <password>icomsms</password>"
			+ "<Operator>$Operator$</Operator>" + "  </Icom_WSProcessMo>"
			+ " </soap:Body>" + "</soap:Envelope>";
		


        
	}
}
