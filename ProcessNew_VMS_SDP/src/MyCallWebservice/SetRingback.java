package MyCallWebservice;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import MyProcessServer.LocalConfig;
import MyUtility.MyLogger;

public class SetRingback
{
	@SuppressWarnings("deprecation")
	public static String CallWebservice(String MSISDN, String Command, String RingbackCode, String ValidCode)
	{
		MyLogger mLog = new MyLogger(SetRingback.class.getName());

		String Template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>" 
				+ "<SetRingback xmlns=\"http://tempuri.org/\">" 
				+ "<Partner_UserName>%s</Partner_UserName>"
				+ "<Partner_Password>%s</Partner_Password>" 
				+ "<Command>%s</Command>" 
				+ "<MSISDN>%s</MSISDN>" 
				+ "<ValidCode>%s</ValidCode>"
				+ "<RingBackCode>%s</RingBackCode>" 
				+ "<RequestTime>%s</RequestTime>" 
				+ "</SetRingback>" 
				+ "</soap:Body>" 
				+ "</soap:Envelope>";

		String Result = "1";

		String RequestTime = "";
		String XML = String.format(Template, new Object[] { "HB6x83", "HB6x83DFF3424", Command, MSISDN, ValidCode, RingbackCode, RequestTime });

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		client.setState(state);
		PostMethod method = new PostMethod(LocalConfig.URLSetRingback);
		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");

		method.addRequestHeader("SOAPAction", "http://tempuri.org/SetRingback");
		try
		{
			method.setRequestBody(XML);

		}
		catch (Exception e)
		{
			mLog.log.error(e);
			try
			{
				ByteArrayRequestEntity entity = new ByteArrayRequestEntity(XML.getBytes());
				method.setRequestEntity(entity);

			}
			catch (Exception e1)
			{
				mLog.log.error(e1);
			}
		}

		try
		{
			client.executeMethod(method);

			byte[] response = method.getResponseBody();

			String textResponse = new String(response);

			String xmlRecords = textResponse;

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("SetRingbackResult");

			Element line = (Element) nodes.item(0);
			Result = getCharacterDataFromElement(line);

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			Result = "-1";
		}
		finally
		{
			mLog.log.debug("Call_WS_SetRingback------->MSISDN:" + MSISDN + " || Command:" + Command + " || RingbackCode:" + RingbackCode + " || RequestTime:"
					+ RequestTime + " || Result:" + Result);
		}
		return Result;
	}

	public static String getCharacterDataFromElement(Element e)
	{
		Node child = e.getFirstChild();
		if (child instanceof CharacterData)
		{
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

}
