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

public class sendXMLRING {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("ICom_6x54",
				"DFws#$234");
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);
		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "http://tempuri.org/SetRingback");

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

	public static String SendXML(String url, String Command, String MSISDN,
			String ValidCode, String RingBackCode, String RequestTime)
			throws Exception {
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
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "SetRingbackResult");
		Util.logger.info("SetRingbackResult:" + sReturn);

		return sReturn;
	}

}
