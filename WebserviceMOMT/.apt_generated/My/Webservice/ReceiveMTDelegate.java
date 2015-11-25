package My.Webservice;

import MyGateway.SMSReceiveForward;
import MyGateway.SMSSendQueue;
import MyUtility.*;
import MyUtility.MyConfig.Telco;

@javax.jws.WebService(targetNamespace = "http://Webservice.My/", serviceName = "ReceiveMTService", portName = "ReceiveMT", wsdlLocation = "WEB-INF/wsdl/ReceiveMTService.wsdl")
public class ReceiveMTDelegate
{

	My.Webservice.ReceiveMT receiveMT = new My.Webservice.ReceiveMT();

	public String SendMT(String User_ID, String Message, String Service_ID, String Command_Code, String Message_Type, String Request_ID, String Total_Message,
			String Content_Type, String Operator, String UserName, String Password) throws Exception
	{
		return receiveMT
				.SendMT(User_ID, Message, Service_ID, Command_Code, Message_Type, Request_ID, Total_Message, Content_Type, Operator, UserName, Password);
	}

}