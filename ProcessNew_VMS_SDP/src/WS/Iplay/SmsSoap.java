package WS.Iplay;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "SmsSoap", targetNamespace = "http://iplay.vn/Webservice/Sms")
public interface SmsSoap
{

	/**
	 * 
	 * @param commandCode
	 * @param userID
	 * @param receiveDate
	 * @param requestID
	 * @param serviceID
	 * @param userName
	 * @param password
	 * @param operator
	 * @param info
	 * @return returns java.lang.String
	 */
	@WebMethod(operationName = "MOForward", action = "http://iplay.vn/Webservice/Sms/MOForward")
	@WebResult(name = "MOForwardResult", targetNamespace = "http://iplay.vn/Webservice/Sms")
	@RequestWrapper(localName = "MOForward", targetNamespace = "http://iplay.vn/Webservice/Sms", className = "WS.Iplay.MOForward")
	@ResponseWrapper(localName = "MOForwardResponse", targetNamespace = "http://iplay.vn/Webservice/Sms", className = "WS.Iplay.MOForwardResponse")
	public String moForward(@WebParam(name = "User_ID", targetNamespace = "http://iplay.vn/Webservice/Sms") String userID,
			@WebParam(name = "Service_ID", targetNamespace = "http://iplay.vn/Webservice/Sms") String serviceID,
			@WebParam(name = "Command_Code", targetNamespace = "http://iplay.vn/Webservice/Sms") String commandCode,
			@WebParam(name = "Info", targetNamespace = "http://iplay.vn/Webservice/Sms") String info,
			@WebParam(name = "Request_ID", targetNamespace = "http://iplay.vn/Webservice/Sms") String requestID,
			@WebParam(name = "Receive_Date", targetNamespace = "http://iplay.vn/Webservice/Sms") String receiveDate,
			@WebParam(name = "Operator", targetNamespace = "http://iplay.vn/Webservice/Sms") String operator,
			@WebParam(name = "UserName", targetNamespace = "http://iplay.vn/Webservice/Sms") String userName,
			@WebParam(name = "Password", targetNamespace = "http://iplay.vn/Webservice/Sms") String password);

}
