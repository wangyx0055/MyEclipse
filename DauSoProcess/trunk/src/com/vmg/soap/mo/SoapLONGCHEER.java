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

import java.text.SimpleDateFormat;
import java.util.Date;
import com.vmg.soap.mo.sendXMLLongcheer;
public class SoapLONGCHEER extends ContentAbstract {
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		int iRetries = 1;
		int iTimeout = 1;

		String resultok = Constants._prop.getProperty("mo.soap."
				+ keyword.getOptions() + ".ok", "1");
		String result = "";
		while (iRetries > 0) {
			try {
				String url = Constants._prop.getProperty("mo.soap." + keyword.getOptions() + ".url");
				String username = Constants._prop.getProperty("mo.soap." +keyword.getOptions()
						+ ".username");
				String password = Constants._prop.getProperty("mo.soap." +keyword.getOptions()
						+ ".password");
			
				if (url == null)
					throw new Exception("In the profile is missing mo.soap." +keyword.getOptions()
							+ ".url");

				String from=msgObject.getUserid();
				String text=msgObject.getUsertext();
				String msgid=msgObject.getRequestid().toString();
				String time=msgObject.getTTimes().toString();
				String shortcode=msgObject.getServiceid();
				String telcoid=msgObject.getMobileoperator();
				result = sendXMLLongcheer.updateStatus( url,  from, text, time, msgid, shortcode,telcoid, username);

					
				Util.logger.info("Result: " + result);
				/**
				if (result.equals(resultok)) {
					Util.logger.info(this.getClass().getName() + "@"
							+ "send ok ,Details: " + "Msisdn: "
							+ msgObject.getUserid() + " Shortcode: "
							+ msgObject.getServiceid() + " Keyword: "
							+ msgObject.getKeyword() + " RequestID: "
							+ msgObject.getRequestid() + "CommandCode: "
							+ msgObject.getKeyword()
							+ " Online Retry countdown: " + iRetries);
					return null;
				} else {

					Util.logger.info(this.getClass().getName() + "@" + "Got "
							+ result + ", Going For Retry, Sleeping,Details: "
							+ "Msisdn: " + msgObject.getUserid()
							+ " Shortcode: " + msgObject.getServiceid()
							+ " Keyword: " + msgObject.getKeyword()
							+ " RequestID: " + msgObject.getRequestid()
							+ "CommandCode: " + msgObject.getKeyword()
							+ " Online Retry countdown: " + iRetries);
					iRetries--;
					Thread.sleep(iTimeout * 1000);
					continue;
				}**/
				return null;
				

			} catch (Exception e) {
				Util.logger.error(this.getClass().getName() + "@"
						+ "Some Exception..!! Got " + result
						+ ", Going For Retry, Sleeping,Details: " + "Msisdn: "
						+ msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ msgObject.getKeyword() + " Online Retry countdown: "
						+ iRetries);

				Util.logger.info(this.getClass().getName() + "@"
						+ "Exception: " + e.toString());

				Util.logger.printStackTrace(e);
				iRetries--;
				Thread.sleep(iTimeout * 1000);
				continue;
			}

		}
		add2SMSSendFailed(msgObject);
		return null;
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
