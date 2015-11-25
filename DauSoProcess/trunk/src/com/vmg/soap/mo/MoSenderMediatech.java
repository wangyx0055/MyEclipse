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

public class MoSenderMediatech {
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
		method.addRequestHeader("SOAPAction", "http://tempuri.org/SenderMsg");
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

	public static String SendXML( 
			String Request_ID, String Phone_Number, String Service_ID ,String Command_Code,String Content,String Operator)
			throws Exception {
		String url=Constants._prop.getProperty("url_mediatech");
		String password=Constants._prop.getProperty("password_mediatech");
		String username=Constants._prop.getProperty("username_mediatech");

		String template =
			"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"

				+ "<soap:Header/>"
				+ "<soap:Body>"
				+ "<tem:SenderMsg>"			
				+ "<tem:UserName>" +username + "</tem:UserName>"
				+ "<tem:Password>" +password + "</tem:Password>"
				+ "<tem:User_ID>" + Phone_Number + "</tem:User_ID>"			
				+ "<tem:Message>"+Content+"</tem:Message>"
				+ "<tem:Service_ID>" + Service_ID + "</tem:Service_ID>"
				+ "<tem:Command_Code>" + Command_Code + "</tem:Command_Code>"
				+ "<tem:Request_ID>" +Request_ID + "</tem:Request_ID>"
				+ "<tem:Operator>" +Operator + "</tem:Operator>"
				+ "</tem:SenderMsg>" + " </soap:Body>" + "</soap:Envelope>";
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "SenderMsgResult");
		Util.logger.info("SenderMsgResult:" + sReturn);
		return sReturn;
	}
	
}
