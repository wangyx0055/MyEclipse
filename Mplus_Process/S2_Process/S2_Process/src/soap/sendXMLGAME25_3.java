package soap;

import icom.Constants;
import icom.common.Util;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;


public class sendXMLGAME25_3 {
	public static int MAX_SUB = 10;
	
	public static String url="http://203.162.71.168:6888/GetMedia.asmx";
	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		Credentials credentials = new UsernamePasswordCredentials("S2VMS",
				"S2VMS!Q@W#E");
		state.setCredentials(null, null, credentials);
		client.setState(state);
		PostMethod method = new PostMethod(url);

		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");
		method.addRequestHeader("SOAPAction", "http://tempuri.org/GetLinkMediaByCate");

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

	public static String SendXML( String MSISDN, int MediaType,
			int MediaID, int ChannelType, double Price, String RequestTime)
			throws Exception {
		String template =

		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"

			+ "<soap:Header/>"
			+ "   <soap:Body>"
			+ "    <tem:GetLinkMedia>"
			+ "  <tem:UserName>S2VMS</tem:UserName>"
			+ "  <tem:Password>S2VMS!Q@W#E</tem:Password>"
			+ "   <tem:MSISDN>" + MSISDN + "</tem:MSISDN>"
			+ " <tem:MediaType>" + MediaType + "</tem:MediaType>"
			+ "  <tem:MediaID>" + MediaID + "</tem:MediaID>"
			+ "  <tem:ChannelType>" +ChannelType + "</tem:ChannelType>"
			+ "  <tem:Price>" +Price + "</tem:Price>"
				+ "  <tem:RequestTime>" + RequestTime +"</tem:RequestTime>"
			+ "</tem:GetLinkMedia>" + " </soap:Body>" + "</soap:Envelope>";
		Util.logger.info("XML2Send:" + template);
		url = Constants._prop.getProperty("urlStore",url);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "GetLinkMediaResult");
		Util.logger.info("GetLinkMediaResult:" + sReturn);

		return sReturn;
	}
	public static String SendXML2( String MSISDN, int MediaType,int CateID,
			int MediaID, int ChannelType, double Price, String RequestTime)
			throws Exception {
		String template =

		"<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"

			+ "<soap:Header/>"
			+ "   <soap:Body>"
			+ "    <tem:GetLinkMediaByCate>"
			+ "  <tem:UserName>S2VMS</tem:UserName>"
			+ "  <tem:Password>S2VMS!Q@W#E</tem:Password>"
			+ "   <tem:MSISDN>" + MSISDN + "</tem:MSISDN>"
			+ " <tem:MediaType>" + MediaType + "</tem:MediaType>"
			+ " <tem:CateID>" + CateID + "</tem:CateID>"
			+ "  <tem:MediaID>" + MediaID + "</tem:MediaID>"
			+ "  <tem:ChannelType>" +ChannelType + "</tem:ChannelType>"
			+ "  <tem:Price>" +Price + "</tem:Price>"
				+ "  <tem:RequestTime>" + RequestTime +"</tem:RequestTime>"
			+ "</tem:GetLinkMediaByCate>" + " </soap:Body>" + "</soap:Envelope>";
		Util.logger.info("XML2Send:" + template);
		url = Constants._prop.getProperty("urlStore",url);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "GetLinkMediaByCateResult");
		Util.logger.info("GetLinkMediaByCateResult:" + sReturn);

		return sReturn;
	}

}
