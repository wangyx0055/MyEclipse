package com.vmg.soap.mo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Authenticator;
import java.security.MessageDigest;
import java.security.Timestamp;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;

public class sendXMLVIETTEL {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml,String username,String password,String action) throws Exception {

	
		Authenticator cosmoteAuthenticator = new VMGAuthenticator(username,
				password);
		Authenticator.setDefault(cosmoteAuthenticator);

		HttpClient client = new HttpClient();

		HttpState state = client.getState();
		state.setCredentials(new AuthScope(null, -1),
				new UsernamePasswordCredentials(username, password));

		client.setState(state);
		PostMethod method = new PostMethod(url);
		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");

		method.addRequestHeader("SOAPAction", action);

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

	public static String sendWS(String url, String xml,String username, String password,String action) throws Exception {

		Util.logger.info("");
		String[] resultHttpPost = httpPost(url, xml,username,password,action);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];

		Util.logger.info("responseCode:" + responseCode);
		Util.logger.info("xmlResponse:" + xmlResponse);

		return responseCode;
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}
	 
	public static String SendXML(String partner, String command, String user_id,
			BigDecimal request_id, String reqTime,String contents) throws Exception {
		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
	
		String template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.viettel.com\">"
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
				+ "<sourceType xsi:type=\"xsd:string\">2</sourceType>"
				+ "</ser:processCRBTs>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		Util.logger.info("XML2Send:" + template);
		String Return = sendWS(url, template,username,password,action);
		Util.logger.info("xmlReturn:" + Return);
	
		return Return;
	}


}
