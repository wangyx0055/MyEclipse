package com.vmg.soap.mo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.Timestamp;
import com.vmg.sms.process.Constants;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.vmg.sms.common.Util;

public class sendXMLRING {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml, String username, String password, String getFunction) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();
		//"ICom_6x54","DFws#$234"
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);
		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", getFunction);
		//"http://tempuri.org/SetRingback"
		try {
			method.setRequestBody(xml);

		} catch (Exception e) {
			try {
				ByteArrayRequestEntity entity = new ByteArrayRequestEntity(xml
						.getBytes());
				method.setRequestEntity(entity);

			} catch (Exception e1) {
				throw new Exception("Impossible to set the xml in the post");
			}
		}

		int iRes = client.executeMethod(method);

		byte[] response = method.getResponseBody();

		String textResponse = new String(response);

		String[] toReturn = { "" + iRes, textResponse };

		return toReturn;
	}

	public static String sendWS(String url, String xml,String username, String password, String getFunction ) throws Exception {

		Util.logger.info("");

		String[] resultHttpPost = httpPost(url, xml,username, password,getFunction);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];

		Util.logger.info("xmlResponse:" + xmlResponse);

		return xmlResponse;
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}

	public static String SendXML(String url, String Command, String MSISDN,
			String ValidCode, String RingBackCode, String RequestTime)
			throws Exception {
		
		String username ="ICom_6x54";
		String password="DFws#$234";
		String getFunction="http://tempuri.org/SetRingback";
		
		String template =
		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
				+ "<soap:Header/>"
				+ "   <soap:Body>"
				+ "    <tem:SetRingback>"
				+ "  <tem:Partner_UserName>ICom_6x54</tem:Partner_UserName>"
				+ "  <tem:Partner_Password>DFws#$234</tem:Partner_Password>"
				+ " <tem:Command>"
				+ Command
				+ "</tem:Command>"
				+ "   <tem:MSISDN>"
				+ MSISDN
				+ "</tem:MSISDN>"
				+ "  <tem:ValidCode>"
				+ ValidCode
				+ "</tem:ValidCode>"
				+ "  <tem:RingBackCode>"
				+ RingBackCode
				+ "</tem:RingBackCode>"
				+ "  <tem:RequestTime>"
				+ RequestTime
				+ "</tem:RequestTime>"
				+ "</tem:SetRingback>" + " </soap:Body>" + "</soap:Envelope>";

		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template, username,password, getFunction);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "SetRingbackResult");
		Util.logger.info("SetRingbackResult:" + sReturn);

		return sReturn;
	}
public static String sendMTXML(String url, String userID, 
				String keywords,	String serviceID, String message,int MessageType, BigDecimal RequestID,
		int TotalMessage,int MessageIndex, int IsMore,int ContentType)throws Exception {		
		
	// 
		String usernameAVG ="icom";
		String passwordAVG="R3o11v0rP1110cI";
		usernameAVG = Constants._prop.getProperty("usernameAVG" +"", usernameAVG);
		passwordAVG = Constants._prop.getProperty("passwordAVG" +"", passwordAVG);
		String getFunction="mtReceive";
		String template ="<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.itrd.vmg\"> " +
							"<soapenv:Header/> "+
							"<soapenv:Body>"+
							" <ws:mtReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+
		   					"<User_ID>" + userID + "</User_ID>"+
		   					"<Message>" + message + "</Message>"+
		   					"<Service_ID>" + serviceID + "</Service_ID>"+
		   					"<Command_Code>" + keywords + "</Command_Code>"+
		   					"<Message_Type>" + MessageType + "</Message_Type>"+
		   					"<Request_ID>" + RequestID + "</Request_ID>"+
		   					"<Total_Message>" + TotalMessage + "</Total_Message>"+
		   					"<Message_Index>" + MessageIndex + "</Message_Index>"+
		   					"<IsMore>" + IsMore + "</IsMore>"+
		   					"<Content_Type>" + ContentType + "</Content_Type>"+
		   					"</ws:mtReceiver>"+
		   					"</soapenv:Body>" +
		   					"</soapenv:Envelope>";
		
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template,usernameAVG,passwordAVG,getFunction);
		Util.logger.info("xmlReturn:" + xmlReturn);
		//String sReturn = getValue(xmlReturn, "mtReceiverReturn");		
		return xmlReturn;
	}

public static String Http_get(String surl) throws IOException {
	URL url = new URL(surl);
	Util.logger.info("urldk:   " + surl);
	
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setRequestMethod("GET");

	BufferedReader reader = new BufferedReader(new InputStreamReader(
			connection.getInputStream()));
	Util.logger.info("getResponseCode : " + connection.getResponseCode());
	String line = null;
	String data = "";
	while ((line = reader.readLine()) != null) {
		data += line;
	}
	Util.logger.info("data :   " + data);
	return data;
}
public static String parseResult(String xmlResponse) {
	if (xmlResponse.indexOf(">1<") != -1)
		return "1";

	if (xmlResponse.indexOf(">-1<") != -1)
		return "-1";

	if (xmlResponse.indexOf(">0<") != -1)
		return "0";

	if (xmlResponse.indexOf(">2<") != -1)
		return "2";
	if (xmlResponse.indexOf(">3<") != -1)
		return "3";

	if (xmlResponse.indexOf(">4<") != -1)
		return "4";

	if (xmlResponse.indexOf(">5<") != -1)
		return "5";

	if (xmlResponse.indexOf(">6<") != -1)
		return "6";

	return xmlResponse;
}

}
