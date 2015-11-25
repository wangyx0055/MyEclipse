package com.vmg.soap.mo;

import java.net.Authenticator;
import java.util.Properties;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Logger;
import com.vmg.sms.process.MsgObject;
import org.apache.axis.utils.XMLUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;


public abstract class MTSender {

	protected String template;

	public abstract void setTemplate();

	public String parseResult(String xmlResponse) {
		if (xmlResponse.indexOf(">1<") != -1)
			return "1";

		if (xmlResponse.indexOf(">-1<") != -1)
			return "-1";

		if (xmlResponse.indexOf(">0<") != -1)
			return "0";

		return xmlResponse;
	}

	public String sendMO(String url, String username, String password,
			String action, MsgObject msgObject, String text, String commandcode)
			throws Exception {

		String xmlToSend = parseTemplate(action, msgObject, text, commandcode);
		String[] resultHttpPost = httpPost(url, username, password, action,
				xmlToSend);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];
		
		
		Util.logger.info(this.getClass().getName() + "@"
				+ "@@@xmlResponse:" + xmlResponse);
		

		if (!responseCode.equals("200")) {
			throw new Exception("THE RESPONSE CODE IS " + responseCode
					+ "  THE XML IN ANSWERING IS " + xmlResponse);
		}

		String result = parseResult(xmlResponse);

		return result;
	}

	private String[] httpPost(String url, String username, String password,
			String action, String xml) throws Exception {

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
		Header[] headers = method.getRequestHeaders();

		Util.logger.info(this.getClass().getName() + "@"
				+ "HEADER OF THE REQUEST");
		for (int i = 0; i < headers.length; i++) {
			Header header = headers[i];
			Util.logger.info(this.getClass().getName() + "@" + header.getName()
					+ "=" + header.getValue());
		}

		Util.logger.info(this.getClass().getName() + "@"
				+ "RESULT FO THE CALLING HTML POST REQUEST" + iRes);
		byte[] response = method.getResponseBody();

		String textResponse = new String(response);
		Util.logger.info(this.getClass().getName() + "@" + "MESSAGE:"
				+ textResponse);

		String[] toReturn = { "" + iRes, textResponse };

		return toReturn;
	}

	private String parseTemplate(String action, MsgObject msgObject,
			String text, String commandcode) throws Exception {

		setTemplate();

		String msisdn = msgObject.getUserid();
		if (msisdn.startsWith("+"))
			msisdn = msisdn.substring(1);

		Properties props = new Properties();
	
		
		
		String strTextEncoded = "";
		String strText = "";
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		strTextEncoded = encoder.encode(msgObject.getUsertext().getBytes());
		
		strText = msgObject.getUsertext();
		props.setProperty("Service_ID", msgObject.getServiceid());
		props.setProperty("User_ID", msisdn);
		props.setProperty("Command_Code", commandcode);
		strText = strText.replace("&", "&amp;");
		props.setProperty("Message", strText);
		props.setProperty("Request_ID", msgObject.getRequestid().toString());
		props.setProperty("Message_Type", msgObject.getMsgtype() + "");
		
		props.setProperty("Total_Message", 1 + "");
		props.setProperty("Message_Index", 1 + "");
		props.setProperty("IsMore", 0 + "");
		props.setProperty("Content_Type", msgObject.getContenttype() + "");
		props.setProperty("Operator", msgObject.getMobileoperator());
		
		
		props.setProperty("action", action);

		SimpleParser sp = new SimpleParser(template);
		String xmlToSend = sp.parse(props);

		Util.logger.info(this.getClass().getName() + "@" + "XML TO SEND "
				+ xmlToSend);
		return xmlToSend;
	}

}
