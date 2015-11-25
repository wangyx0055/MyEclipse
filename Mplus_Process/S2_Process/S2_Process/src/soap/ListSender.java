package soap;

import icom.MsgObject;
import icom.common.Util;

import java.net.Authenticator;
import java.util.Properties;

import org.apache.axis.utils.XMLUtils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

public abstract class ListSender {

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
			String action, MsgObject msgObject, String commandcode,
			String encode, String sHours) throws Exception {

		Util.logger.info("Send MO from ICOM to VMS");

		String xmlToSend = parseTemplate(action, msgObject, commandcode,
				encode, sHours);
		Util.logger.info("xml To Send: " + xmlToSend);
		String[] resultHttpPost = httpPost(url, username, password, action,
				xmlToSend);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];

		if (!responseCode.equals("200")) {
			throw new Exception("THE RESPONSE CODE IS " + responseCode
					+ "  THE XML IN ANSWERING IS " + xmlResponse);
		}
		
		String result = parseResult(xmlResponse);
		Util.logger.info("sendMO @@ result:"+result);
		return result;
	}

	private String[] httpPost(String url, String username, String password,
			String action, String xml) throws Exception {

		Authenticator cosmoteAuthenticator = new ICOMAuthenticator(username,
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
		Util.logger.info(this.getClass().getName() + "@" + "MESSAGE"
				+ textResponse);

		String[] toReturn = { "" + iRes, textResponse };

		return toReturn;
	}

	private String parseTemplate(String action, MsgObject msgObject,
			String commandcode, String encode, String sHours) throws Exception {

		setTemplate();

		String msisdn = msgObject.getUserid();
		if (msisdn.startsWith("+"))
			msisdn = msisdn.substring(1);

		Properties props = new Properties();
		String strTextEncoded = "";
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		strTextEncoded = encoder.encode(msgObject.getUsertext().getBytes());

		props.setProperty("Service_ID", msgObject.getServiceid());

		if ("0".equalsIgnoreCase(encode)) {
			props.setProperty("Message", XMLUtils.xmlEncodeString(msgObject
					.getUsertext()));
			Util.logger.info(this.getClass().getName() + "@" + "Message" + "="
					+ msgObject.getUsertext() + "@textxmlendcode:"
					+ XMLUtils.xmlEncodeString(msgObject.getUsertext()));
		} else {
			props.setProperty("Message", strTextEncoded);
			Util.logger.info(this.getClass().getName() + "@" + "Message:"
					+ msgObject.getUsertext() + "textencode=" + strTextEncoded);
		}

		props.setProperty("User_ID", msisdn);
		props.setProperty("Command_Code", commandcode);
		props.setProperty("Request_ID", msgObject.getRequestid().toString());
		props.setProperty("Message_Type", msgObject.getMsgtype() + "");
		props.setProperty("Service_Name", msgObject.getServiceName());
		props.setProperty("Content_ID", msgObject.getContentId() + "");
		props.setProperty("Amount", msgObject.getAmount() + "");
		props.setProperty("Time_Delivery", sHours);
		props.setProperty("Last_Code", msgObject.getLast_code());
		props.setProperty("Company_ID", msgObject.getCompany_id());
		///////////

		props.setProperty("Total_Message", 1 + "");
		props.setProperty("Message_Index", 1 + "");
		props.setProperty("IsMore", 0 + "");
		props.setProperty("Content_Type", msgObject.getContenttype() + "");
		props.setProperty("Operator", msgObject.getMobileoperator());

		// /////////////////

		props.setProperty("action", action);

		SimpleParser sp = new SimpleParser(template);
		String xmlToSend = sp.parse(props);

		Util.logger.info(this.getClass().getName() + "@" + "XML TO SEND "
				+ xmlToSend);

		return xmlToSend;
	}

}
