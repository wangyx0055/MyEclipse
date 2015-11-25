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
import com.vmg.sms.process.Constants;

public class sendXMLMaromedia {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("icom",
				"icommaromedia");
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "http://tempuri.org/ReceiveMO");

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
	public static String SendXML(String moid, String src, String dest,
			String cmdcode, String msgbody, String username,String password) throws Exception {
		String template = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"

				+ "<soap:Header/>"
				+ "<soap:Body>"
				+ "<tem:ReceiveMO>"
				+ "<tem:moid>"
				+ moid
				+ "</tem:moid>"
				+ "<tem:moseq></tem:moseq>"
				+ "<tem:src>"
				+ src
				+ "</tem:src>"
				+ " <tem:dest>"
				+ dest
				+ "</tem:dest>"
				+ "  <tem:cmdcode>"
				+ cmdcode
				+ "</tem:cmdcode>"
				+ "  <tem:msgbody>"
				+ msgbody
				+ "</tem:msgbody>"
				+ "  <tem:username>"
				+ username
				+ "</tem:username>"
				+ "  <tem:password>"
				+ password
				+ "</tem:password>"
				+ "</tem:ReceiveMO>" + " </soap:Body>" + "</soap:Envelope>";
		String url = "http://smsservice.maromedia.vn/icom.asmx?WSDL";
		url = Constants._prop.getProperty("url_Maromedia", url);

		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "ReceiveMOResult");
		Util.logger.info("ReceiveMOResult:" + sReturn);

		return sReturn;
	}

	

}
