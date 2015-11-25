package soap;

public class ListSenderVMS extends ListSender {

	public ListSenderVMS() {
	}

	@Override
	public void setTemplate() {
		super.template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.itrd.vmg\" >"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ws:mtReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<USER_ID xsi:type=\"xsd:string\">$User_ID$</USER_ID>"
				+ "<SERVICE_ID xsi:type=\"xsd:string\">$Service_ID$</SERVICE_ID>"
				+ "<COMMAND_CODE xsi:type=\"xsd:string\">$Command_Code$</COMMAND_CODE>"
				+ "<MESSAGE_TYPE xsi:type=\"xsd:string\">$Message_Type$</MESSAGE_TYPE>"
				+ "<REQUEST_ID xsi:type=\"xsd:string\">$Request_ID$</REQUEST_ID>"
				+ "<MESSAGE_ID xsi:type=\"xsd:string\">0</MESSAGE_ID>"
				+ "<SERVICE_NAME xsi:type=\"xsd:string\">$Service_Name$</SERVICE_NAME>"
				+ "<CHANNEL_TYPE xsi:type=\"xsd:string\">$Channel_Type$</CHANNEL_TYPE>"
				+ "<CONTENT_ID xsi:type=\"xsd:string\">$Content_ID$</CONTENT_ID>"
				+ "<AMOUNT xsi:type=\"xsd:string\">$Amount$</AMOUNT>"
				+ "<TIME_DELIVERY xsi:type=\"xsd:string\">$Time_Delivery$</TIME_DELIVERY>"
				+ "<COMPANY_ID xsi:type=\"xsd:string\">$Company_ID$</COMPANY_ID>"
				+ "<CPID xsi:type=\"xsd:string\">-1</CPID>"
				+ "</ws:mtReceiver>" + "</soapenv:Body>"
				+ "</soapenv:Envelope>";
	}
}
