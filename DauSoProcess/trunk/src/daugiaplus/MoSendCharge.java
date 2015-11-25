package daugiaplus;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

public class MoSendCharge {

	public static int MAX_SUB = 10;

	private static String[] httpPost(String url, String xml) throws Exception {

		HttpClient client = new HttpClient();

		HttpState state = client.getState();
		client.setState(state);
		PostMethod method = new PostMethod(url);
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

	public static String ReCharge(String url, String partner_UserName, String validString,
			String msisdn, String userName, String amount,
			String promotionAmount, String requestTime) throws Exception {
		
		String template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ " <ReCharge xmlns=\"http://tempuri.org/\">"
				+ "<Partner_UserName>"
				+ partner_UserName
				+ "</Partner_UserName>"
				+ "<ValidString>"
				+ validString
				+ "</ValidString>"
				+ "<MSISDN>"
				+ msisdn
				+ "</MSISDN>"
				+ "<UserName>"
				+ userName
				+ "</UserName>"
				+ "<Amount>"
				+ amount
				+ "</Amount>"
				+ "<PromotionAmount>"
				+ amount
				+ "</PromotionAmount>"
				+ "<RequestTime>"
				+ requestTime
				+ "</RequestTime>"
				+ "</ReCharge>"
				+ "</soap:Body>"
				+ "</soap:Envelope>";
		
	//	Util.logger.info("XML2Send:" + template);
		System.out.println("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
	//	Util.logger.info("xmlReturn:" + xmlReturn);
		System.out.println("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "ReChargeResult");
	//	Util.logger.info("SenderMsgResult:" + sReturn);
		System.out.println("SenderMsgResult:" + sReturn);
		return sReturn;
	}
}
