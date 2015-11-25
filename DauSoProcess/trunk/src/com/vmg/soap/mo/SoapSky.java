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
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MOSender;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.vmg.soap.mo.sendXMLLongcheer;

public class SoapSky {
	public static boolean getMessages(MsgObject msgObject)
			throws InterruptedException {
		int iRetries = 1;
		int iTimeout = 1;

		String result = "";
		while (iRetries > 0) {
			try {
				String url = "http://115.238.91.226:17020/chargeoversea/mo.do?";

				url = Constants._prop.getProperty("url_sky", url);

				String msisdn = msgObject.getUserid();
				String sms = msgObject.getUsertext().replaceAll(" ", "+");
				String linkid = msgObject.getRequestid() + "";
				String shortcode = msgObject.getServiceid();
				String mcn=getMCN(msgObject.getMobileoperator());
				url = url
						+ "linkid="
						+ linkid
						+ "&motype=0&spcode=315&mobile="
						+ msisdn
						+ "&content="
						+ sms
						+ "&shortcode="
						+ shortcode
						+ "&cost="+getcost(msgObject.getServiceid())+"&feetype=ITEM&motime="+now()+"&Oprcode=452"+mcn+"&country=452";
				  Util.logger.info("url: " + url);
					
					
				Http_get(url);
				// Util.logger.info("Result: " + result);

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

				Util.logger.printStackTrace(e);
				iRetries--;

				continue;
			}

		}
		add2SMSSendFailed(msgObject);
		return false;
	}
	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
	public static String getMCN(String operator) {
		String mcn = "00";
		if (operator.equalsIgnoreCase("VMS"))
			return "01";
		if (operator.equalsIgnoreCase("GPC"))
			return "02";
		if (operator.equalsIgnoreCase("SFONE"))
			return "03";
		if (operator.equalsIgnoreCase("VIETTEL"))
			return "04";
		if (operator.equalsIgnoreCase("VNM"))
			return "05";
		if (operator.equalsIgnoreCase("EVN"))
			return "06";
		if (operator.equalsIgnoreCase("BEELINE"))
			return "07";

		return mcn;
	}
	public static String getcost(String service_id) {
		String mcn = "0";
		if (service_id.equalsIgnoreCase("8051"))
			return "500";
		if (service_id.equalsIgnoreCase("8151"))
			return "1000";
		if (service_id.equalsIgnoreCase("8251"))
			return "2000";
		if (service_id.equalsIgnoreCase("8351"))
			return "3000";
		if (service_id.equalsIgnoreCase("8451"))
			return "4000";
		if (service_id.equalsIgnoreCase("8551"))
			return "5000";
		if (service_id.equalsIgnoreCase("8651"))
			return "10000";
		if (service_id.equalsIgnoreCase("8751"))
			return "15000";

		return mcn;
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
