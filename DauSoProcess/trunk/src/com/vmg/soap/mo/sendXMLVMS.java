package com.vmg.soap.mo;

import java.math.BigDecimal;
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

public class sendXMLVMS {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("icom",
				"sendmo2vasc");
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "UserLoginRequest");

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
		Util.logger.info("responseCode:" + responseCode);

		return xmlResponse;
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}
	 
	public static String[] SendXML(String url, String command, String user_id,
			BigDecimal request_id, String reqTime,String contents) throws Exception {
		String template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.mc.vasc.com\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ser:processCRBTs soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<msisdn xsi:type=\"xsd:string\">"
				+ user_id
				+ "</msisdn>"	
				+ "<providerId xsi:type=\"xsd:string\">IMZGW</providerId>"
				+ "<serviceId xsi:type=\"xsd:string\">IMZ_ICOM</serviceId>"
				+ "<userName xsi:type=\"xsd:string\">icom</userName>"
				+ "<password xsi:type=\"xsd:string\">icom123$%</password>"
				+ "<amount xsi:type=\"xsd:string\">0</amount>"
				+ "<reqTime xsi:type=\"xsd:string\">"
				+ reqTime
				+ "</reqTime>"
				+ "<command xsi:type=\"xsd:string\">"
				+ command
				+ "</command>"	
				+ "<contents xsi:type=\"xsd:string\">"
				+ contents
				+ "</contents>"
				+ "<amount xsi:type=\"xsd:string\">0</amount>"
				
				+ "</ser:processCRBTs>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String[] sReturn = null ;
		sReturn =new String[2];
		sReturn[0]=getValue(xmlReturn, "ERROR");
		Util.logger.info("sReturn[0]:" + sReturn[0]);
		
		sReturn[1]=getValue(xmlReturn, "ERROR_DESC");
		Util.logger.info("sReturn[1]:" + sReturn[1]);
		
		return sReturn;
	}


}
