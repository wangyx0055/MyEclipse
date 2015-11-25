package MostStep;


import javax.xml.parsers.*;

import oracle.net.aso.MD5;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.icom.math.BigInteger;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;

public class Subscriber_bySendXML {
	
public void SendMO(String url,String userName, String passWord, String userID,
		String serviceID, String info, String moID,
		String command_code, String price)throws Exception {
		
		// Lay ngay thang hien tai
		
		Calendar now = Calendar.getInstance();
		
		String time =  now.get(Calendar.YEAR) +  FormatNumber(now.get(Calendar.MONTH))+  FormatNumber(now.get(Calendar.DAY_OF_MONTH)) +
		FormatNumber(now.get(Calendar.HOUR_OF_DAY)) + FormatNumber(now.get(Calendar.MINUTE))+ FormatNumber(now.get(Calendar.SECOND)) + "";
		
		Timestamp GetNow = Timestamp.valueOf(time);
		// ma hoa chuoi pass truyen theo kieu md5 
		// 
		String sPass = userName + passWord+  moID + userID + info + serviceID +  command_code + "Airtel";
		sPass = getMD5(sPass);
		String call =  " http://api1.cobill.net/MO.php?msisdn="+userID+"&" +
		"shortcode="+serviceID+"&content="+info+"&moid="+moID+"&telco=Airtel&price="+price+"+" +
		"&timestamp="+GetNow+"&area=Himachal%20Pradesh&mcc=404&currency=INR&plmn=40403&ver=1&"+
		"keyword="+command_code+"&username="+userName+"&password="+sPass+"";
		
		 Util.logger.info("call:" + call);		 
		 call = call.replace(" ","%20");
		 Http_get(call);	
		 
			//result = getValue(xml, "RESULT");
			//Util.logger.info("result:" + result);
			
	}


public void SendMT(String url,String userName, String passWord, String userID,
		String serviceID, String info, String moID,String mtID,
		String command_code, String price)throws Exception {
		
		// Lay ngay thang hien tai
		
		Calendar now = Calendar.getInstance();
		
		String time =  now.get(Calendar.YEAR) +  FormatNumber(now.get(Calendar.MONTH))+  FormatNumber(now.get(Calendar.DAY_OF_MONTH)) +
		FormatNumber(now.get(Calendar.HOUR_OF_DAY)) + FormatNumber(now.get(Calendar.MINUTE))+ FormatNumber(now.get(Calendar.SECOND)) + "";
		
		Timestamp GetNow = Timestamp.valueOf(time);
		String sPass = userName + passWord+  moID + mtID + userID + info + serviceID +  command_code + "Airtel";
		sPass = getMD5(sPass);
		String call =  " http://api1.cobill.net/MT.php?msisdn="+userID+"&" +
		"shortcode="+serviceID+"&content="+info+"&moid="+moID+"&mtid="+mtID+"&telco=Airtel&price="+price+"+" +
		"&timestamp="+GetNow+"&area=Himachal%20Pradesh&mcc=404&currency=INR&plmn=40403&ver=1&"+
		"keyword="+command_code+"&username="+userName+"&password="+sPass+"";
		
		 Util.logger.info("call:" + call);		 
		 call = call.replace(" ","%20");
		 Http_get(call);	
		 
			//result = getValue(xml, "RESULT");
			//Util.logger.info("result:" + result);
			
	}

public void Http_get(String surl) throws IOException {
	URL url = new URL(surl);
	Util.logger.info("urldk:   " + surl);
	
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setRequestMethod("GET");

	BufferedReader reader = new BufferedReader(new InputStreamReader(
			connection.getInputStream()));
	Util.logger.info("getResponseCode : " + connection.getResponseCode());
	String line = null;
	String data = "";
	while ((line = reader.readLine()) != null) {
		data += line;
	}
	Util.logger.info("data :   " + data);
	//return data;
}
public static String getMD5(String input) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
    catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
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

	Credentials credentials = new UsernamePasswordCredentials("icom", "sendmo2vasc");
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

public static String FormatNumber(int i)
{
try
{
if (i >= 0 && i < 10)return "0" + i;
else return "" + i;
}
catch (Exception e)
{
return "";
}
}
public static String getCharacterDataFromElement(Element e) {
Node child = e.getFirstChild();
if (child instanceof CharacterData) {
   CharacterData cd = (CharacterData) child;
   return cd.getData();
}
return "?";
}

public void Http_post(String surl) throws IOException {
	URL url = new URL(surl);
	Util.logger.info("UrlGetStatus :   " + surl);
	
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setRequestMethod("POST");

	BufferedReader reader = new BufferedReader(new InputStreamReader(
			connection.getInputStream()));
	Util.logger.info("getResponseCode : " + connection.getResponseCode());
	String line = null;
	String data = "";
	while ((line = reader.readLine()) != null) {
		data += line;
	}
	Util.logger.info("data :   " + data);
	//return data;
}

}
