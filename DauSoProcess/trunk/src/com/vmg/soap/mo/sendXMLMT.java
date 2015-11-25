package com.vmg.soap.mo;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Timestamp;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.vmg.sms.common.Util;
public class sendXMLMT {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("username", "password");
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "mtReceive");

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

	public static String sendWS(String url, String xml) throws Exception {

		Util.logger.info("");
		String[] resultHttpPost = httpPost(url, xml);

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
	public static String GetResponse(String url, String User_ID,String Message, String Service_ID,String Command_Code,String Message_Type,String Request_ID,String Total_Message,String Message_Index,String IsMore,String Content_Type,String Operator)
			throws Exception {
		String template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.mc.vasc.com\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ser:mtReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<User_ID xsi:type=\"xsd:string\">"
				+ User_ID
				+ "</User_ID>"
				+ "<Message xsi:type=\"xsd:string\">"
				+ Message
				+ "</Message>"
				+ "<Service_ID xsi:type=\"xsd:string\">"
				+ Service_ID
				+ "</Service_ID>"
				+ "<Command_Code xsi:type=\"xsd:string\">"
				+ Command_Code
				+ "</Command_Code>"
			   + "<Message_Type xsi:type=\"xsd:string\">"
				+ Message_Type
				+ "</Message_Type>"
				+ "<Request_ID xsi:type=\"xsd:string\">"
				+ Request_ID
				+ "</Request_ID>"
				+ "<Total_Message xsi:type=\"xsd:string\">"
				+ Total_Message
				+ "</Total_Message>"
				+ "<Message_Index xsi:type=\"xsd:string\">"
				+ Message_Index
				+ "</Message_Index>"
				+ "<IsMore xsi:type=\"xsd:string\">"
				+ IsMore
				+ "</IsMore>"
				+ "<Content_Type xsi:type=\"xsd:string\">"
				+ Content_Type
				+ "</Content_Type>"
				+ "<Operator xsi:type=\"xsd:string\">"
				+ Operator
				+ "</Operator>"
				
				+ "</ser:mtReceiver>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "mtReceiverReturn");
		return sReturn;
	}
}
