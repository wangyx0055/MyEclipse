package icom;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Timestamp;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import app.PPGMain;
import app.Putter;

public class IcomSendClient {
	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials(PPGMain.props.getProperty("cpgCPmt_70usrid"),
				PPGMain.props.getProperty("cpgCPmt_70passwd"));
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "UserLoginRequest");

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

		PPGMain.echo("xmlResponse:" + xmlResponse);

		return xmlResponse;
	}

	private static String getValue(String xml, String tagName) {
		String openTag = "<ns1:" + tagName ;
		
		int st=0,ed=0;
		st =xml.indexOf(openTag);
		if(st>-1)
			st+=openTag.length();
		ed=xml.indexOf(">",st);
		st=ed+1;
		ed=xml.indexOf("<",st);
		if(ed>-1)
			return xml.substring(st,ed);
		else
			return "";
		
	}

	public static void execMT_ACK(Putter p){
		/*******************************************************************
		 * step 3 SMG->icom mtACK
		 * 
		 * WebService
		 */
	
		try {
			String ret=send_ACK(PPGMain.props.getProperty("cpgCPmt_70mt_ack"),p.get("mobile"),p.get("spnum"),
					p.get("keyword"),p.get("content"),p.get("out_linkid"),p.get("oprator"));
			p.set("retcode", ret.equals("1")?"0":ret);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			p.set("retcode","3");
			p.set("retmsg", e.getMessage());
		}
		
		
	}
	public static void execMt_ENC(Putter p) {
		/***********************************************************************
		 * 
		 * mobie : User_ID content: Message shortcode: Service_ID commandcode:
		 * Command_Code messagetype: Message_Type linkid: Request_ID:
		 * totalmessage: Total_Message messageindex: Message_Index ismore:
		 * IsMore contenttype: Content_Type operator: Operator deskey: KeyDES
		 * Step 5: SMG => Icom¡¯s MT WebService
		 */

		try {
			String ret = send_ENC(PPGMain.props.getProperty("cpgCPmt_70mt_enc"), p.get("mobile"),
					p.get("content"), p.get("spnum"), 
					p.get("keyword"), p.get("feetype").equals("") ? "1" : p.get("feetype"), 
					p.get("out_linkid"), p.get("msgt"), p.get("msgi"), 
					p.get("ismore"), p.get("msgtype"), p.get("oprator"),
					p.get("deskey"));
			
				p.set("retcode", ret.equals("1")?"0":ret);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			p.set("retcode","3");
			p.set("retmsg", e.getMessage());
		}

	}
	public static String send_ACK(String url, String User_ID,
			String Service_ID, String Command_Code, String Message,
			String Request_ID, String Operator) throws Exception {
		String template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.mc.vasc.com\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ser:moReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<User_ID xsi:type=\"xsd:string\">"
				+ User_ID
				+ "</User_ID>"
				+ "<Service_ID xsi:type=\"xsd:string\">"
				+ Service_ID
				+ "</Service_ID>"
				+ "<Command_Code xsi:type=\"xsd:string\">"
				+ Command_Code
				+ "</Command_Code>"
				+ "<Message xsi:type=\"xsd:string\">"
				+ Message
				+ "</Message>"
				+ "<Request_ID xsi:type=\"xsd:string\">"
				+ Request_ID
				+ "</Request_ID>"
				+ "<Operator xsi:type=\"xsd:string\">"
				+ Operator
				+ "</Operator>"

				+ "</ser:moReceiver>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		PPGMain.echo("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		PPGMain.echo("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "moReceiverReturn");
		
		return sReturn;
	}

	public static String send_ENC(String url, String User_ID,
			String Message, String Service_ID, String Command_Code,
			String Message_Type, String Request_ID, String Total_Message,
			String Message_Index, String IsMore, String Content_Type,
			String Operator, String KeyDES) throws Exception {
		String template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.mc.vasc.com\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<ser:mtReceiver soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<User_ID xsi:type=\"xsd:string\">"
				+ User_ID
				+ "</User_ID>"
				+ "<Message xsi:type=\"xsd:string\">"
				+ Message
				+ "</Message>"
				+ "<Service_ID xsi:type=\"xsd:string\">"
				+ Service_ID
				+ "</Service_ID>"
				+ "<Command_Code xsi:type=\"xsd:string\">"
				+ Command_Code
				+ "</Command_Code>"
				+ "<Message_Type xsi:type=\"xsd:string\">"
				+ Message_Type
				+ "</Message_Type>"
				+ "<Request_ID xsi:type=\"xsd:string\">"
				+ Request_ID
				+ "</Request_ID>"
				+ "<Total_Message xsi:type=\"xsd:string\">"
				+ Total_Message
				+ "</Total_Message>"
				+ "<Message_Index xsi:type=\"xsd:string\">"
				+ Message_Index
				+ "</Message_Index>"
				+ "<IsMore xsi:type=\"xsd:string\">"
				+ IsMore
				+ "</IsMore>"
				+ "<Content_Type xsi:type=\"xsd:string\">"
				+ Content_Type
				+ "</Content_Type>"
				+ "<Operator xsi:type=\"xsd:string\">"
				+ Operator
				+ "</Operator>"
				+ "<KeyDES xsi:type=\"xsd:string\">"
				+ KeyDES
				+ "</KeyDES>"

				+ "</ser:mtReceiver>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		PPGMain.echo("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		PPGMain.echo("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "mtReceiverReturn");
		return sReturn;
	}
}