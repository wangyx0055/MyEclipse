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

import MyUtility.MyLogger;

public class ThuDoForwardMO
{
	public static String CallWebservice(String User_ID, String Service_ID, String Command_Code, String Info, String Request_ID, String Receive_Date,
			String Operator, String UserName, String Password)
	{
		MyLogger mLog = new MyLogger(SetRingback.class.getName());

		String Template = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<soapenv:ReceiveMO soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "<User_ID xsi:type=\"xsd:string\">%s</User_ID>"
				+ "<Service_ID xsi:type=\"xsd:string\">%s</Service_ID>"
				+ "<Command_Code xsi:type=\"xsd:string\">%s</Command_Code>"
				+ "<Info xsi:type=\"xsd:string\">%s</Info>"
				+ "<Request_ID xsi:type=\"xsd:string\">%s</Request_ID>"
				+ "<Receive_Date xsi:type=\"xsd:string\">%s</Receive_Date>"
				+ "<Operator xsi:type=\"xsd:string\">%s</Operator>"
				+ "<UserName xsi:type=\"xsd:string\">%s</UserName>"
				+ "<Password xsi:type=\"xsd:string\">%s</Password>" + "</soapenv:ReceiveMO>" + "</soapenv:Body>" + "</soapenv:Envelope>";

		String Result = "1";

		String TextResponse = "";
		String XML = String.format(Template, new Object[] { User_ID, Service_ID, Command_Code, Info, Request_ID, Receive_Date, Operator, UserName, Password });

		HttpClient client = new HttpClient();

		HttpState state = client.getState();

		client.setState(state);
		PostMethod method = new PostMethod("http://gate.gviet.vn/HBCom/MOReceiver.php?wsdl");
		method.setDoAuthentication(true);

		method.getHostAuthState().setAuthAttempted(true);
		method.getHostAuthState().setAuthRequested(true);
		method.getHostAuthState().setPreemptive();
		method.addRequestHeader("Content-Type", "text/xml");

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

			TextResponse = new String(response);

			if (TextResponse.equals("0") || TextResponse.equals("1") || TextResponse.equals("-1"))
			{
				Result = TextResponse;
			}
			else
			{
				String xmlRecords = TextResponse;

				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xmlRecords));

				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("ReceiveMOResult");

				Element line = (Element) nodes.item(0);
				Result = getCharacterDataFromElement(line);
			}

		}
		catch (Exception ex)
		{
			mLog.log.error(ex);
			Result = "-1";
		}
		finally
		{
			mLog.log.info("Call_WS_ForwordMO_ThuDO------->TextResponse:" + TextResponse);
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
