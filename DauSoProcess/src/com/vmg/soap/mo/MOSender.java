package com.vmg.soap.mo;

import java.io.File;
import java.io.FileInputStream;

import java.util.Properties;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.Logger;
import com.vmg.sms.process.MsgObject;
import org.apache.axis.utils.XMLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class MOSender {

	protected String template;
	public String[] telcol = { "VIETTEL", "GPC", "VMS", "EVN", "BEELINE" };

	public abstract void setTemplate();

	public String parseResult(String xmlResponse) {
		if (xmlResponse.indexOf(">1<") != -1)
			return "1";

		if (xmlResponse.indexOf(">-1<") != -1)
			return "-1";

		if (xmlResponse.indexOf(">0<") != -1)
			return "0";

		if (xmlResponse.indexOf(">2<") != -1)
			return "2";
		if (xmlResponse.indexOf(">3<") != -1)
			return "3";

		if (xmlResponse.indexOf(">4<") != -1)
			return "4";

		if (xmlResponse.indexOf(">5<") != -1)
			return "5";

		if (xmlResponse.indexOf(">6<") != -1)
			return "6";

		return xmlResponse;
	}

	public String sendMO(String url, String username, String password,
			String action, MsgObject msgObject, String text,
			String commandcode, String encode, String telco) throws Exception {

		String xmlToSend = parseTemplate(action, msgObject, text, commandcode,
				encode, telco);

		String[] resultHttpPost = httpPost(url, username, password, action,
				xmlToSend);

		String responseCode = resultHttpPost[0];
		String xmlResponse = resultHttpPost[1];

		if (!responseCode.equals("200")) {
			throw new Exception("THE RESPONSE CODE IS " + responseCode
					+ "  THE XML IN ANSWERING IS " + xmlResponse);
		}

		String result = parseResult(xmlResponse);
		// System.out.println("REsult:" + result);

		return result;
	}

	private String[] httpPost(String url, String username, String password,
			String action, String xml) throws Exception {

		Authenticator cosmoteAuthenticator = new VMGAuthenticator(username,
				password);
		Authenticator.setDefault(cosmoteAuthenticator);

		// /////////////////////////////////////

		DefaultHttpClient httpclient = new DefaultHttpClient();

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		FileInputStream instream = new FileInputStream(new File(
				"smsreceiver.payoo.com.vn.cer"));
		try {
			trustStore.load(instream, "nopassword".toCharArray());
		} finally {
			instream.close();
		}

		// SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory
				.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Scheme sch = new Scheme("https", socketFactory, 443);
		httpclient.getConnectionManager().getSchemeRegistry().register(sch);

		HttpGet httpget = new HttpGet("https://localhost/");

		// System.out.println("executing request" + httpget.getRequestLine());

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(response.getStatusLine());
		if (entity != null) {
			System.out.println("Response content length: "
					+ entity.getContentLength());
		}
		if (entity != null) {
			entity.consumeContent();
		}

		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();

		// //////////////

		/*
		 * 
		 * 
		 * 
		 * 
		 * HttpState state = client.getState(); state.setCredentials(new
		 * AuthScope(null, -1), new UsernamePasswordCredentials(username,
		 * password));
		 * 
		 * client.setState(state);
		 * 
		 * System.err.println("URL:" + url); PostMethod method = new
		 * PostMethod(url); method.setDoAuthentication(true);
		 * 
		 * 
		 * method.getHostAuthState().setAuthAttempted(true);
		 * method.getHostAuthState().setAuthRequested(true);
		 * method.getHostAuthState().setPreemptive();
		 * method.addRequestHeader("Content-Type", "text/xml");
		 * 
		 * method.addRequestHeader("SOAPAction", action); try {
		 * method.setRequestBody(xml);
		 * 
		 * } catch (Exception e) { try { ByteArrayRequestEntity entity = new
		 * ByteArrayRequestEntity(xml .getBytes());
		 * method.setRequestEntity(entity);
		 * 
		 * } catch (Exception e1) { throw new
		 * Exception("Impossible to set the xml in the post"); } }
		 * 
		 * 
		 * 
		 * 
		 * int iRes = client.executeMethod(method);
		 * 
		 * Header[] headers = method.getRequestHeaders();
		 * 
		 * 
		 * Util.logger.info(this.getClass().getName() + "@" +
		 * "HEADER OF THE REQUEST"); for (int i = 0; i < headers.length; i++) {
		 * Header header = headers[i];
		 * Util.logger.info(this.getClass().getName() + "@" + header.getName() +
		 * "=" + header.getValue()); }
		 * 
		 * Util.logger.info(this.getClass().getName() + "@" +
		 * "RESULT FO THE CALLING HTML POST REQUEST" + iRes); byte[] response =
		 * method.getResponseBody();
		 * 
		 * String textResponse = new String(response);
		 * Util.logger.info(this.getClass().getName() + "@" + "MESSAGE" +
		 * textResponse);
		 * 
		 * String[] toReturn = { "" + iRes, textResponse };
		 * 
		 * 
		 * return toReturn;
		 */
		return null;
	}

	private String parseTemplate(String action, MsgObject msgObject,
			String text, String commandcode, String encode, String telco)
			throws Exception {

		String[] arrtelco = telco.split(",");
		setTemplate();

		String msisdn = msgObject.getUserid();
		if (msisdn.startsWith("+"))
			msisdn = msisdn.substring(1);

		String strTextEncoded = "";
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		strTextEncoded = encoder.encode(text.getBytes());

		Properties props = new Properties();
		props.setProperty("Service_ID", msgObject.getServiceid());
		props.setProperty("User_ID", msisdn);
		props.setProperty("Command_Code", commandcode);
		if ("1".equalsIgnoreCase(encode) || "".equalsIgnoreCase(encode)) {
			props.setProperty("Message", strTextEncoded);
		} else {
			props.setProperty("Message", msgObject.getUsertext());
		}

		for (int i = 0; i < 5; i++) {
			if (msgObject.getMobileoperator().equalsIgnoreCase(
					Constants.TELCOS[i])) {
				// props.setProperty("Operator", msgObject.getMobileoperator());
				props.setProperty("Operator", arrtelco[i]);
			}
		}

		props.setProperty("Request_ID", msgObject.getRequestid().toString());
		props.setProperty("action", action);

		SimpleParser sp = new SimpleParser(template);
		String xmlToSend = sp.parse(props);

		Util.logger.info(this.getClass().getName() + "@" + "XML TO SEND "
				+ xmlToSend);
		return xmlToSend;
	}

	// /////////

}
