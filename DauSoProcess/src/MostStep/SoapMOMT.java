package MostStep;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import com.vmg.sms.process.MsgObject_MostStep;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.net.URLEncoder;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

public class SoapMOMT {

	public static String SendMO(MsgObject msgObject)
			throws InterruptedException {
		int iRetries = 1;
		int iTimeout = 1;
		String username = "icom";
		String password ="5C944B3C3B5CDA17CB59ECB4138CF139";
		String url_add = "http://api.cobill.net/MO.php?";
		String result = "";
		String xmlReturn="";
		while (iRetries > 0) {
			try {				
				
				String userID =  msgObject.getUserid().substring(2);
				String info = msgObject.getUsertext();			
				String serviceID = msgObject.getServiceid();
				String command_code = msgObject.getKeyword(); 
				String telcoid = msgObject.getMobileoperator();			
				BigDecimal rsqID = msgObject.getRequestid();
				String plmn = GetPLMN(telcoid);
				String toEnc = username + password+  rsqID +userID + info + serviceID +  command_code + telcoid;
				Util.logger.info("toEnc___: " + toEnc);
				MessageDigest mdEnc = MessageDigest.getInstance("MD5");
				//Date date = new Date();
				//Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				//String time = formatter.format(date);	
				
				//Timestamp GetNow = Timestamp.valueOf(time);
				mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
				String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
				info = info.replace(" ", "%20");
				String price=GetPrice(serviceID);
				long timestamp = System.currentTimeMillis()/1000;
				String url = "msisdn="+userID+"&" +
				"shortcode="+serviceID+"&content="+info+"&moid="+rsqID+"&telco="+telcoid+"&price="+price+"" +
				"&timestamp="+timestamp+"&area=Ha Noi&mcc=452&currency=VND&plmn="+plmn+"&ver=1&"+
				"keyword="+command_code+"&username="+username+"&password="+md5+"";
				//String encodedString = URLEncoder.encode(s, "UTF-8");
				url = url.replace(" ","%20");
				url = url_add + url;
				Util.logger.info("XML2Send:" + url);
				 xmlReturn = sendWS(url, url);
				Util.logger.info("xmlReturn:" + xmlReturn);
				//String sReturn = getValue(xmlReturn, "rsCreateSub");
				return xmlReturn;

			} catch (Exception e) {
				Util.logger.error("Some Exception..!! Got " + result
						+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
						+ msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ msgObject.getKeyword() + " Online Retry countdown: "
						+ iRetries);

				Util.logger.info("Exception: " + e.toString());

				Util.logger.printStackTrace(e);
				iRetries--;
				Thread.sleep(iTimeout * 1000);
				continue;
			}

		}
		add2SMSSendFailed(msgObject);
		return xmlReturn;

	}
	
public static String SendMT(MsgObject msgObject)
	throws InterruptedException {
	
int iRetries = 1;
int iTimeout = 1;
String username = "icom";
String password ="5C944B3C3B5CDA17CB59ECB4138CF139";
String url_add = "http://api.cobill.net/MT.php?";
String result = "";
String xmlReturn="";
while (iRetries > 0) {
	try {				
		
		String userID =  msgObject.getUserid().substring(2);
		String info = msgObject.getUsertext();	
		info = info.replace(".;", ".");
		info = info.replace(";", ".");
		info = info.replace("+", "va");
		String [] sInfo = info.split("###");
		String serviceID = msgObject.getServiceid();
		String command_code = msgObject.getKeyword(); 
		String telcoid = msgObject.getMobileoperator();			
		BigDecimal rsqID = msgObject.getRequestid();
		String plmn = GetPLMN(telcoid);
		String toEnc = username + password+  rsqID + rsqID +userID + sInfo[0] + serviceID +  command_code + telcoid;
		Util.logger.info("toEnc___: " + toEnc);
		MessageDigest mdEnc = MessageDigest.getInstance("MD5");
		//Date date = new Date();
		//Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//String time = formatter.format(date);	
		
		//Timestamp GetNow = Timestamp.valueOf(time);
		mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
		String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
		info = info.replace(" ", "%20");
		String price=GetPrice(serviceID);
		long timestamp = System.currentTimeMillis()/1000;
		String url = "msisdn="+userID+"&" +
		"shortcode="+serviceID+"&content="+info+"&moid="+rsqID+"&mtid="+rsqID+"&telco="+telcoid+"&price="+price+"" +
		"&timestamp="+timestamp+"&area=Ha Noi&mcc=452&currency=VND&plmn="+plmn+"&ver=1&"+
		"keyword="+command_code+"&username="+username+"&password="+md5+"";
		//String encodedString = URLEncoder.encode(s, "UTF-8");
		url = url.replace(" ","%20");
		url = url_add + url;
		//Http_Post(url);
		iRetries--;
		
		Util.logger.info("XML2Send:" + url);
		 xmlReturn = sendWS(url, url);
		Util.logger.info("xmlReturn:" + xmlReturn);
		//String sReturn = getValue(xmlReturn, "rsCreateSub");
		return xmlReturn;
		//return true;

	} catch (Exception e) {
		Util.logger.error("Some Exception..!! Got " + result
				+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
				+ msgObject.getUserid() + " Shortcode: "
				+ msgObject.getServiceid() + " Keyword: "
				+ msgObject.getKeyword() + " RequestID: "
				+ msgObject.getRequestid() + "CommandCode: "
				+ msgObject.getKeyword() + " Online Retry countdown: "
				+ iRetries);

		Util.logger.info("Exception: " + e.toString());

		Util.logger.printStackTrace(e);
		iRetries--;
		Thread.sleep(iTimeout * 1000);
		continue;
	}

}
	add2SMSSendFailed(msgObject);
	return xmlReturn;

}


public static String SendMT2(MsgObject msgObject, String mtcount)
throws InterruptedException {

int iRetries = 1;
int iTimeout = 1;
String username = "icom";
String password ="5C944B3C3B5CDA17CB59ECB4138CF139";
String url_add = "http://api.cobill.net/MT.php?";
String result = "";
String xmlReturn="";
while (iRetries > 0) {
try {				
	
	String userID =  msgObject.getUserid().substring(2);
	String info = msgObject.getUsertext();	
	info = info.replace(".;", ".");
	info = info.replace(";", ".");
	info = info.replace("+", "va");
	String [] sInfo = info.split("&"); 	
	String serviceID = msgObject.getServiceid();
	String command_code = msgObject.getKeyword(); 
	String telcoid = msgObject.getMobileoperator();			
	BigDecimal rsqID = msgObject.getRequestid();
	String mtid = rsqID + mtcount;
	String plmn = GetPLMN(telcoid);
	String toEnc = username + password+  mtid + mtid +userID + sInfo[0] + serviceID +  command_code + telcoid;
	Util.logger.info("toEnc___: " + toEnc);
	MessageDigest mdEnc = MessageDigest.getInstance("MD5");
	//Date date = new Date();
	//Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	//String time = formatter.format(date);	
	
	//Timestamp GetNow = Timestamp.valueOf(time);
	mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
	String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
	info = info.replace(" ", "%20");
	String price=GetPrice(serviceID);
	long timestamp = System.currentTimeMillis()/1000;
	String url = "msisdn="+userID+"&" +
	"shortcode="+serviceID+"&content="+info+"&moid="+mtid+"&mtid="+mtid+"&telco="+telcoid+"&price="+price+"" +
	"&timestamp="+timestamp+"&area=Ha Noi&mcc=452&currency=VND&plmn="+plmn+"&ver=1&"+
	"keyword="+command_code+"&username="+username+"&password="+md5+"";
	//String encodedString = URLEncoder.encode(s, "UTF-8");
	url = url.replace(" ","%20");
	url = url_add + url;
	//Http_Post(url);
	iRetries--;
	
	Util.logger.info("XML2Send:" + url);
	 xmlReturn = sendWS(url, url);
	Util.logger.info("xmlReturn:" + xmlReturn);
	//String sReturn = getValue(xmlReturn, "rsCreateSub");
	return xmlReturn;
	//return true;

} catch (Exception e) {
	Util.logger.error("Some Exception..!! Got " + result
			+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
			+ msgObject.getUserid() + " Shortcode: "
			+ msgObject.getServiceid() + " Keyword: "
			+ msgObject.getKeyword() + " RequestID: "
			+ msgObject.getRequestid() + "CommandCode: "
			+ msgObject.getKeyword() + " Online Retry countdown: "
			+ iRetries);

	Util.logger.info("Exception: " + e.toString());

	Util.logger.printStackTrace(e);
	iRetries--;
	Thread.sleep(iTimeout * 1000);
	continue;
}

}
add2SMSSendFailed(msgObject);
return xmlReturn;

}

	public static String GetPrice(String service_id) {
		String amount = "0";
		if (service_id.equalsIgnoreCase("8751")) {
			amount = "15000";
		}
		if (service_id.equalsIgnoreCase("8651")) {
			amount = "10000";
		}
		if (service_id.equalsIgnoreCase("8551")) {
			amount = "5000";
		}
		if (service_id.equalsIgnoreCase("8451")) {
			amount = "4000";
		}
		if (service_id.equalsIgnoreCase("8351")) {
			amount = "3000";
		}
		if (service_id.equalsIgnoreCase("8251")) {
			amount = "2000";
		}
		if (service_id.equalsIgnoreCase("8151")) {
			amount = "1000";
		}

		if (service_id.equalsIgnoreCase("8051")) {
			amount = "500";
		}
		return amount;
	}
	
	public static String GetPLMN(String operator) {
		String PLMN = "";
		if (operator.equalsIgnoreCase("VIETTEL")) {
			PLMN = "45204";
		}
		if (operator.equalsIgnoreCase("VMS"))  {
			PLMN = "45201";
		}
		if (operator.equalsIgnoreCase("GPC"))  {
			PLMN = "45202";
		}
		if (operator.equalsIgnoreCase("Beeline"))  {
			PLMN = "45207";
		}
		if (operator.equalsIgnoreCase("Sfone"))  {
			PLMN = "45203";
		}
		if (operator.equalsIgnoreCase("Vietnammobi"))  {
			PLMN = "45205";
		}
				return PLMN;
	}


	public static void Http_Post(String url) {
		try {
			Util.logger.info("url:" + url);
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(url);

			// Execute the POST method
			int statusCode = client.executeMethod(method);
			Util.logger.info("statusCode:" + statusCode);

			if (statusCode != 200) {
				Util.logger.info("Post Fales.statusCode:" + statusCode);

				String contents = method.getResponseBodyAsString();
				method.releaseConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static BigDecimal add2SMSSendFailed(MsgObject msgObject) {

		Util.logger.info("add2SMSSendFailed:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSSendFailed:" + msgObject.getUserid()
					+ ":" + msgObject.getUsertext()
					+ ":Error add row from sms receive error:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
	public static String SendXML(int MediaType, String MSISDN, String MediaID,
			int ChannelType, String Price, String RequestTime) throws Exception {
		String template = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
				+ "<soap:Header/>"
				+ "   <soap:Body>"
				+ "    <tem:GetLinkMedia>"
				+ "  <tem:UserName>MostStepuSer</tem:UserName>"
				+ "  <tem:Password>m0s75@!GHs@0s9</tem:Password>"
				+ "   <tem:MSISDN>"
				+ MSISDN
				+ "</tem:MSISDN>"
				+ " <tem:MediaType>"
				+ MediaType
				+ "</tem:MediaType>"
				+ "  <tem:MediaID>"
				+ MediaID
				+ "</tem:MediaID>"
				+ "  <tem:ChannelType>"
				+ ChannelType
				+ "</tem:ChannelType>"
				+ "  <tem:Price>"
				+ Price
				+ "</tem:Price>"
				+ "  <tem:RequestTime>"
				+ RequestTime
				+ "</tem:RequestTime>"
				+ "</tem:GetLinkMedia>" + " </soap:Body>" + "</soap:Envelope>";
		String url = "http://192.168.168.6:13124/GetMedia.asmx";
		url = Constants._prop.getProperty("url_store", url);
		
		Util.logger.info("XML2Send:" + template);
		String xmlReturn = sendWS(url, template);
		Util.logger.info("xmlReturn:" + xmlReturn);
		String sReturn = getValue(xmlReturn, "GetLinkMediaResult");
		Util.logger.info("GetLinkMediaResult:" + sReturn);

		return sReturn;
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
		method
				.addRequestHeader("SOAPAction",
						"http://tempuri.org/GetLinkMedia");

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


	public static String encode(String s)
	  {
	    StringBuffer sbuf = new StringBuffer();
	    int len = s.length();
	    for (int i = 0; i < len; i++) {
	      int ch = s.charAt(i);
	      if ('A' <= ch && ch <= 'Z') {		// 'A'..'Z'
	        sbuf.append((char)ch);
	      } else if ('a' <= ch && ch <= 'z') {	// 'a'..'z'
		       sbuf.append((char)ch);
	      } else if ('0' <= ch && ch <= '9') {	// '0'..'9'
		       sbuf.append((char)ch);
	      } else if (ch == ' ') {			// space
		       sbuf.append('+');
	      } else if (ch == '-' || ch == '_'		// unreserved
	          || ch == '.' || ch == '!'
	          || ch == '~' || ch == '*'
	          || ch == '\'' || ch == '('
	          || ch == ')') {
	        sbuf.append((char)ch);
	      } else if (ch <= 0x007f) {		// other ASCII
		       sbuf.append(hex[ch]);
	      } else if (ch <= 0x07FF) {		// non-ASCII <= 0x7FF
		       sbuf.append(hex[0xc0 | (ch >> 6)]);
		       sbuf.append(hex[0x80 | (ch & 0x3F)]);
	      } else {					// 0x7FF < ch <= 0xFFFF
		       sbuf.append(hex[0xe0 | (ch >> 12)]);
		       sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
		       sbuf.append(hex[0x80 | (ch & 0x3F)]);
	      }
	    }
	    return sbuf.toString();
	  }
	final static String[] hex = {
	    "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
	    "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
	    "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
	    "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
	    "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
	    "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
	    "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
	    "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
	    "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
	    "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
	    "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
	    "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
	    "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
	    "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
	    "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
	    "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
	    "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
	    "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
	    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
	    "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
	    "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
	    "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
	    "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
	    "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
	    "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
	    "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
	    "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
	    "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
	    "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
	    "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
	    "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
	    "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
	  };
}
