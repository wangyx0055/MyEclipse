package com.vmg.soap.mo;



import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;

public class SoapMVcorpSMS {
	
	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		client.getParams().setAuthenticationPreemptive(true);

		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
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

		String[] resultHttpPost = httpPost(url, xml);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];

		//Utils.logger.info("xmlResponse:" + xmlResponse);

		return xmlResponse;
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}

	public static String SendXML(Long mo_id, Long request_id, String mobile_operator,
			String user_id, String service_id, String command_code,
			String message, int message_type, String request_time, int process_status) throws Exception {
		String url = Constants._prop.getProperty("url_MVcorp");
		String userName = Constants._prop.getProperty("username_MVcorp");
		String passwords = Constants._prop.getProperty("pass_MVcorp");
		
		String template = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">"

				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertMO>"
				+ "<tem:MO_ID>"
				+ mo_id
				+ "</tem:MO_ID>"
				+ "<tem:REQUEST_ID>"
				+ request_id
				+ "</tem:REQUEST_ID>"
				+ "<tem:MOBILE_OPERATOR>"
				+ mobile_operator
				+ "</tem:MOBILE_OPERATOR>"
				+ "<tem:USER_ID>"
				+ user_id
				+ "</tem:USER_ID>"
				+ "<tem:SERVICE_ID>"
				+ service_id
				+ "</tem:SERVICE_ID>"
				+ "<tem:COMMAND_CODE>"
				+ command_code
				+ "</tem:COMMAND_CODE>"
				+ " <tem:MESSAGE>"
				+ message
				+ "</tem:MESSAGE>"
				+ "<tem:MESSAGE_TYPE>"
				+ message_type
				+ "</tem:MESSAGE_TYPE>"
				+ " <tem:REQUEST_TIME>"
				+ request_time
				+ "</tem:REQUEST_TIME>"
				+ " <tem:PROCESS_STATUS>"
				+ process_status
				+ "</tem:PROCESS_STATUS>"
				+ " <tem:USER_NAME>"
				+ userName
				+ "</tem:USER_NAME>"
				+ " <tem:PASSWORD>"
				+ passwords
				+ "</tem:PASSWORD>"
				+ "</tem:InsertMO>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";
		
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "InsertMOResult");
		Util.logger.info("SenderMsgResult:" + sReturn);
		return sReturn;
	}
}
