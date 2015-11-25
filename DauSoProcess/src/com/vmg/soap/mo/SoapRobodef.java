package com.vmg.soap.mo;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.net.*;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MOSender;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.vmg.soap.mo.sendXMLLongcheer;

public class SoapRobodef {

	public static String getBalance(MsgObject msgObject)
			throws InterruptedException {
		String url = "http://210.86.231.33/robodef-icom.asp?";
		url = Constants._prop.getProperty("urlRobodef", url);
		String result = "";
		try {

				String amsisdn = msgObject.getUserid();
				// http://210.86.231.33/robodef-icom.asp?cusid=icom&cuspwd=xxxxx
				// &cmdcode=ROREG
				// &shortcode=8751&mobileno=84xxxxxxxx&txid=1234&msg=ROREG
				// GAMEZONE-gzrbd-00314-1-0-fedugabibetepa
				url = url + "cusid=icom&cuspwd=550541moci&cmdcode="
						+ msgObject.getKeyword() + "&shortcode="
						+ msgObject.getServiceid() + "&mobileno=" + amsisdn
						+ "&txid=" + msgObject.getRequestid() + "&msg=" + msgObject.getUsertext();
				String urltam=url.replace(" ","%20");
				result = Http_get(urltam);
				
				return result;

			} catch (Exception e) {
				Util.logger.error("Some Exception..!! Got " + result
						+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
						+ msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ msgObject.getKeyword() );

				Util.logger.info("Exception: " + e.toString());

				Util.logger.printStackTrace(e);
			}

		return result;
	}

	public static void Http_Post(String url) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);

		try {
			client.executeMethod(method);

			if (method.getStatusCode() == HttpStatus.SC_OK) {
				String response = method.getResponseBodyAsString();
				System.out.println("Response = " + response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();

		}

	}

	public static String Http_get(String surl) throws IOException {
		URL url = new URL(surl);

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
		return data;
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
			statement.setInt(8,0);
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
}