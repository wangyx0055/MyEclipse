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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.vmg.soap.mo.sendXMLLongcheer;

public class SoapLongcheerMO {

	public static boolean getMessages(MsgObject msgObject)
			throws InterruptedException {
		int iRetries = 1;
		int iTimeout = 1;
		String username = "ICOM";
		String url = "http://ocs.oh3g.org/ocs/interface/icomMO.php";
		String result = "";
		while (iRetries > 0) {
			try {

				String from = msgObject.getUserid();
				String text = msgObject.getUsertext();
				String msgid = msgObject.getRequestid().toString();
				String shortcode = msgObject.getServiceid();
				String telcoid = msgObject.getMobileoperator();
				String messagetype = msgObject.getMsgtype() + "";
				String toEnc = username + msgid + telcoid + from + shortcode;
				MessageDigest mdEnc = MessageDigest.getInstance("MD5");
				Date date = new Date();
				Format formatter = new SimpleDateFormat("yyyyMMddhhmmss");
				String time = formatter.format(date);

				mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
				String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
				text = text.replace(" ", "%20");
				String price=GetPrice(shortcode);
				
				url = url + "?from=" + from+ "&text=" + text   + "&time="
						+ time + "&msgid="+msgid
						+ "&shortcode="
						+ shortcode + "&telcoid=" + telcoid
						+ "&countryid=452&price="+price+"&username=" + username
						+ "&password=" + md5 + "";

				Http_Post(url);
				iRetries--;
				return true;

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
		return false;

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
}
