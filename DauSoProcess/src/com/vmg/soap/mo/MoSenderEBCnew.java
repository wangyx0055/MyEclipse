package com.vmg.soap.mo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
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

public class MoSenderEBCnew {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("icom@20!!",
				"icom@20!!");

		client.getParams().setAuthenticationPreemptive(true);

		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "http://tempuri.org/InsertMO");
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

	public static String SendXML(String Request_ID, String Phone_Number,
			String Service_ID, String Command_Code, String Content,
			String Operator,String time) throws Exception {
		String url = Constants._prop.getProperty("url_ebc_new");
		String password = Constants._prop.getProperty("password_ebc_new");
		String username = Constants._prop.getProperty("username_ebc_new");
		/*
		 * <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
		 * xmlns:tem="http://tempuri.org/"> <soap:Header/> <soap:Body>
		 * <tem:InsertMO> <tem:RequestId>22111102092159377</tem:RequestId>
		 * <!--Optional:--> <tem:Operator>VIETTEL</tem:Operator>
		 * <!--Optional:--> <tem:UserId>84984328029</tem:UserId>
		 * <!--Optional:--> <tem:ReceiverId>84984328029</tem:ReceiverId>
		 * <!--Optional:--> <tem:ServiceId>8051</tem:ServiceId> <!--Optional:-->
		 * <tem:CommandCode>testebc</tem:CommandCode> <!--Optional:-->
		 * <tem:Message>testebc</tem:Message>
		 * <tem:MessageType>1</tem:MessageType> <!--Optional:-->
		 * <tem:RequestTime>2011-11-02 17:33:00</tem:RequestTime>
		 * <tem:ProcessStatus>1</tem:ProcessStatus> <!--Optional:-->
		 * <tem:Username>icom@20!!</tem:Username> <!--Optional:-->
		 * <tem:Password>icom@20!!</tem:Password> </tem:InsertMO> </soap:Body>
		 * </soap:Envelope>
		 */
		String template = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"

				+ "<soap:Header/>"
				+ "<soap:Body>"
				+ "<tem:InsertMO>"
				+ "<tem:RequestId>"
				+ Request_ID
				+ "</tem:RequestId>"
				+ "<tem:Operator>"
				+ Operator
				+ "</tem:Operator>"
				+ "<tem:UserId>"
				+ Phone_Number
				+ "</tem:UserId>"
				+ "<tem:ReceiverId>"
				+ Phone_Number
				+ "</tem:ReceiverId>"
				+ "<tem:ServiceId>"
				+ Service_ID
				+ "</tem:ServiceId>"
				+ "<tem:CommandCode>"
				+ Command_Code
				+ "</tem:CommandCode>"
				+ "<tem:Message>"
				+ Content
				+ "</tem:Message>"
				+ "<tem:MessageType>1</tem:MessageType>"
				+ "<tem:RequestTime>"
				+ time
				+ "</tem:RequestTime>"
				+ "<tem:ProcessStatus>1</tem:ProcessStatus>"
				+ "<tem:Username>"
				+ username
				+ "</tem:Username>"
				+ "<tem:Password>"
				+ password
				+ "</tem:Password>"
				+ "</tem:InsertMO>"
				+ " </soap:Body>"
				+ "</soap:Envelope>";
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "SenderMsgResult");
		Util.logger.info("SenderMsgResult:" + sReturn);
		return sReturn;
	}

}
