package com.vmg.soap.mo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;

public class MoSenderLotus {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("icom",
				"icom121");

		client.getParams().setAuthenticationPreemptive(true);

		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "http://6x60.vn/Getmonkey");
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

	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String stringToHex(String base) {
		StringBuffer buffer = new StringBuffer();
		int intValue;
		for (int x = 0; x < base.length(); x++) {
			int cursor = 0;
			intValue = base.charAt(x);
			String binaryChar = new String(Integer.toBinaryString(base
					.charAt(x)));
			for (int i = 0; i < binaryChar.length(); i++) {
				if (binaryChar.charAt(i) == '1') {
					cursor += 1;
				}
			}
			if ((cursor % 2) > 0) {
				intValue += 128;
			}
			buffer.append(Integer.toHexString(intValue));
		}
		return buffer.toString();
	}


	
	public static String SendXML(String Request_ID, String Phone_Number,
			String Service_ID, String Command_Code, String Content,
			String Operator) throws Exception {
		String url = Constants._prop.getProperty("url_lotus");
		String password = Constants._prop.getProperty("password_lotus");
		String username = Constants._prop.getProperty("username_lotus");
		/*
		 * <soapenv:Envelope
		 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:x60="http://6x60.vn/"> <soapenv:Header/> <soapenv:Body>
		 * <x60:Getmonkey> <!--Optional:--> <x60:userid>?</x60:userid>
		 * <!--Optional:--> <x60:message>?</x60:message> <!--Optional:-->
		 * <x60:serviceid>?</x60:serviceid> <!--Optional:-->
		 * <x60:commandcode>?</x60:commandcode> <!--Optional:-->
		 * <x60:requestid>?</x60:requestid> <!--Optional:-->
		 * <x60:operatorid>?</x60:operatorid> <!--Optional:-->
		 * <x60:partner>?</x60:partner> <!--Optional:--> <x60:sign>?</x60:sign>
		 * </x60:Getmonkey> </soapenv:Body> </soapenv:Envelope>
		 */
		String template = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:x60=\"http://6x60.vn/\">"

				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<x60:Getmonkey>"
				+ "<x60:userid>"
				+ Phone_Number
				+ "</x60:userid>"
				+ "<x60:message>"
				+ URLEncoder.encode(Content.toString(), "UTF-8")
				+ "</x60:message>"
				+ "<x60:serviceid>"
				+ Service_ID
				+ "</x60:serviceid>"
				+ "<x60:commandcode>"
				+ Command_Code
				+ "</x60:commandcode>"
				+ "<x60:requestid>"
				+ Request_ID
				+ "</x60:requestid>"
				+ "<x60:operatorid>"
				+ Operator
				+ "</x60:operatorid>"
				+ "<x60:partner>vietnamnet</x60:partner>"
				+ "<x60:sign>"
				+ getMD5(Phone_Number +  URLEncoder.encode(Content.toString(), "UTF-8") + Service_ID
						+ Command_Code + Request_ID + Operator
						+ "vietnamnetvietnamneticom68$")
				+ "</x60:sign>"
				+ "</x60:Getmonkey>"
				+ " </soapenv:Body>"
				+ "</soapenv:Envelope>";
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "GetmonkeyResult");
		Util.logger.info("GetmonkeyResult:" + sReturn);
		return sReturn;
	}

}
