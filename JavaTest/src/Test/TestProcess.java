package Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.omg.CORBA.DATA_CONVERSION;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import MyUtility.MyFile;
import MyUtility.MyLogger;
import MyUtility.MySeccurity;

public class TestProcess
{
	static String LogDataFolder = ".//LogFile//";

	public static void main(String[] args)
	{
		try
		{
			
			
			/*
			 * for(int i = 2001; i <=8000; i++) {
			 * MyFile.WriteToFile(".\\FileMSISDN.txt", "8484888"+i);
			 * System.out.println("8484888"+i); }
			 */
			RunTest();
			//CallWS();
			/*
			 * String MSISDN ="84919438389"; String PRICE="1000"; String
			 * REASON="RENEW_DAILY"; String ORIGINALPRICE="1000"; String
			 * PROMOTION="0"; String NOTE=""; String CHANNEL="SMS";
			 * Charge(MSISDN, PRICE, REASON, ORIGINALPRICE, PROMOTION, NOTE,
			 * CHANNEL);
			 */

		}
		catch (Exception ex)
		{
			System.out.print(ex.getMessage());
		}
	}

	
	public static void RunTest()
	{
		Properties properties = new Properties();
		FileInputStream fin;
		try
		{
			fin = new FileInputStream("config.properties");

			properties.load(fin);

			fin.close();

			Integer NUM_THREAD = Integer.parseInt(properties.getProperty("NUM_THREAD", "10"));
			String Template = properties.getProperty("Template", "");
			String URL = properties.getProperty("URL", "");
			String FileMSISDN = properties.getProperty("FileMSISDN", "");

			Vector<String> mListMSISDN = MyUtility.MyFile.ReadFileToList(FileMSISDN);

			Integer Pagesize = mListMSISDN.size() / NUM_THREAD;

			for (int i = 0; i < NUM_THREAD; i++)
			{
				ThreadCalWS mThread = new ThreadCalWS();
				mThread.Template = Template;
				mThread.URL = URL;
				Integer Begin = i * Pagesize;
				Integer End = Begin + Pagesize;
				mThread.ThreadIndex = i;
				if (End + Pagesize >= mListMSISDN.size())
					End = mListMSISDN.size() - 1;

				mThread.mListMSISDN = mListMSISDN.subList(Begin, End).toArray();
				mThread.setPriority(Thread.MAX_PRIORITY);
				mThread.start();
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void CallWS()
	{
		String Localhost = "http://10.10.0.58:8088/MTrafficAPI/";
		//String Localhost="http://192.168.0.107:8088/MTrafficAPI/";
		// String Template =
		// "<?xml version=\"1.0\" encoding=\"utf-8\" ?><API><requestid>1235</requestid><msisdn>84919438389</msisdn><packagename>HUY KP</packagename><promotion>0</promotion><trial>0</trial><bundle>0</bundle><note>Test</note><application>Vinaphone</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></API>";

		// Get info Sub

		String Template = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdn>84848882661</msisdn><packagename>DN</packagename><application>VASDEALER</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>";
		String URL = Localhost + "servlet/GetInfoSub";

		// Get info All Sub

		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdn>841234091674</msisdn><application>VASDEALER</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>"
		 * ; String URL = Localhost + "servlet/GetInfoAllSub";
		 */

		// ChangeMSISDN
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdnA>841234091674</msisdnA><msisdnB>841234091675</msisdnB><reason>Test</reason><application>VASDEALER</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>"
		 * ; String URL =
		 * "http://192.168.0.150:8088/MTrafficAPI/servlet/ChangeMSISDN";
		 */

		// Dang ky thue bao
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdn>841234091674</msisdn><packagename>GTHCM</packagename><promotion>7c</promotion><trial>0</trial><bundle>0</bundle><note>Test</note><application>CCOS</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>"
		 * ; String URL = Localhost+"servlet/Register";
		 */

		// Huy dang ky
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdn>841234091674</msisdn><packagename>GTHN</packagename><promotion>0</promotion><trial>0</trial><bundle>0</bundle><note>Test</note><application>VASDEALER</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>"
		 * ; String URL =
		 * "http://192.168.0.150:8088/MTrafficAPI/servlet/Deregister";
		 */

		// Huy dang ky ALL
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>1235</requestid><msisdn>841234091674</msisdn><reason>Test</reason><application>VASDEALER</application><channel>SMS</channel><username>chilinh</username><userip>192.168.1.1</userip></REQUEST>"
		 * ; String URL =
		 * "http://192.168.0.150:8088/MTrafficAPI/servlet/DeregisterAll";
		 */

		// Get Transaction
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>21656</requestid><msisdn>841234091674</msisdn><fromdate>20130801000000</fromdate><todate>20131022164530</todate><pagesize>30</pagesize><pageindex>0</pageindex><application>TEST</application><channel>API</channel><username>namnt</username><userip>10.149.57.140</userip></REQUEST>"
		 * ; String URL =
		 * "http://192.168.0.107:8088/MTrafficAPI/servlet/GetTransaction";
		 */

		// Get Interation
		/*
		 * String Template =
		 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><REQUEST><requestid>21656</requestid><msisdn>841234091674</msisdn><packagename>HS</packagename><fromdate>20130801000000</fromdate><todate>20141022164530</todate><application>TEST</application><channel>API</channel><username>namnt</username><userip>10.149.57.140</userip></REQUEST>"
		 * ; String URL = Localhost + "servlet/GetInteraction";
		 */

		String Result = "1";

		String TextResponse = "";

		String XMLReqeust = Template;

		HttpPost mPost = new HttpPost(URL);

		StringEntity mEntity = new StringEntity(XMLReqeust, ContentType.create("text/xml", "UTF-8"));

		mPost.setEntity(mEntity);
		HttpClient mClient = new DefaultHttpClient();
		try
		{
			HttpResponse response = mClient.execute(mPost);

			HttpEntity entity = response.getEntity();
			TextResponse = EntityUtils.toString(entity);

			System.out.println(TextResponse);

			EntityUtils.consume(entity);
		}
		catch (Exception ex)
		{
			@SuppressWarnings("unused")
			String s = ex.getMessage();
		}

	}

	public static String GetResult(String xmlRecords) throws Exception
	{
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("ERRORID");

			Element line = (Element) nodes.item(0);
			return getCharacterDataFromElement(line);
		}
		catch (Exception ex)
		{
			// TODO: handle exception
			throw ex;
		}
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

	public static void Charge(String MSISDN, String PRICE, String REASON, String ORIGINALPRICE, String PROMOTION, String NOTE, String CHANNEL)
	{
		try
		{
			String XMLResponse = "";
			String XMLReqeust = "";
			String RequestID = Long.toString(System.currentTimeMillis());
			String UserName = "trieuphu";
			String Password = "trieuphuvnp#123";
			String CPName = "TRIEUPHUTT";
			String ChargeURL = "";
			// http://10.10.0.66:8080/billing/billing
			try
			{
				String Template = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>" + "<VAS request_id=\"%s\">"
						+ "<REQ name=\"%s\" user=\"%s\" password=\"%s\">" + "<SUBSCRIBER>" + "<SUBID>%s</SUBID>" + "<PRICE>%s</PRICE>" + "<REASON>%s</REASON>"
						+ "<ORIGINALPRICE>%s</ORIGINALPRICE>" + "<PROMOTION>%s</PROMOTION>" + "<NOTE>%s</NOTE>" + "<CHANNEL>%s</CHANNEL>" + "</SUBSCRIBER>"
						+ "</REQ>" + "</VAS>";
				XMLReqeust = String.format(Template, new Object[] { RequestID, CPName, UserName, Password, MSISDN, PRICE, REASON, ORIGINALPRICE, PROMOTION,
						NOTE, CHANNEL });

				System.out.println(XMLReqeust);

				HttpPost mPost = new HttpPost(ChargeURL);

				StringEntity mEntity = new StringEntity(XMLReqeust, ContentType.create("text/xml", "UTF-8"));
				mPost.setEntity(mEntity);

				HttpClient mClient = new DefaultHttpClient();
				try
				{
					HttpResponse response = mClient.execute(mPost);
					HttpEntity entity = response.getEntity();
					XMLResponse = EntityUtils.toString(entity);
					System.out.println(XMLResponse);
					EntityUtils.consume(entity);
				}
				catch (Exception ex)
				{
					throw ex;
				}
				finally
				{
					mClient.getConnectionManager().shutdown();
				}

			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
			finally
			{
				MyLogger.WriteDataLog(LogDataFolder, "_Charge_VNP", "REQUEST CHARGE --> " + XMLReqeust);
				MyLogger.WriteDataLog(LogDataFolder, "_Charge_VNP", "RESPONSE CHARGE --> " + XMLResponse);
			}

		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

}
