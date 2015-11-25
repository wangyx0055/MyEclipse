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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MOSender;
import common.DBUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SoapQMFibo extends ContentAbstract {

	public String sendMessageMO(MsgObject msgObject, String textMessage,
			String partner, String commandcode) throws Exception {

		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");
		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mo.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mo.soap." + partner
				+ ".encode", "");
		String telco = Constants._prop.getProperty("mo.soap." + partner
				+ ".telco", Constants.TELCOLIST);

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".url");

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".module");

		MOSender sender = (MOSender) Class.forName(module).newInstance();
		String result = sender.sendMO(url, username, password, action,
				msgObject, textMessage, commandcode, encode, telco);

		return result;
	}

	private boolean checkspam(String user_id, String service_id) {
		String tablename = "sms_receive_log"
				+ new SimpleDateFormat("yyyyMM").format(new Date());

		String sqlselect = "select count(*),service_id from sms_receive_log200905 where user_id= '84963536888' and  receive_date > TIMESTAMPADD (minute,-60*24,current_timestamp ) group by service_id ";
		Connection connection = null;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			if ("8751".equalsIgnoreCase(service_id)) {
				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-30,current_timestamp ) group by service_id ";

				Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
				if (result2.size() > 0) {
					Vector item = (Vector) result2.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 3) {

						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>3MO/30minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-1440,current_timestamp ) group by service_id ";

				Vector result3 = DBUtils.getVectorTable(connection, sqlselect);
				if (result3.size() > 0) {
					Vector item = (Vector) result3.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 10) {
						Util.logger
								.sysLog(2, this.getClass().getName(),
										"checkspam:userid=" + user_id
												+ " &serviceid=" + service_id
												+ "@rule:>10MO/1440minute");
						return true;
					}

				}

			} else {
				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-5,current_timestamp ) group by service_id ";

				Vector result2 = DBUtils.getVectorTable(connection, sqlselect);
				if (result2.size() > 0) {
					Vector item = (Vector) result2.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 3) {

						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>3MO/5minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-60,current_timestamp ) group by service_id ";

				Vector result3 = DBUtils.getVectorTable(connection, sqlselect);
				if (result3.size() > 0) {
					Vector item = (Vector) result3.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 10) {
						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id + "@rule:>10MO/60minute");
						return true;
					}

				}

				sqlselect = "select count(*),service_id from "
						+ tablename
						+ " where user_id= '"
						+ user_id
						+ "' and service_id='"
						+ service_id
						+ "' and  receive_date > TIMESTAMPADD (minute,-1440,current_timestamp ) group by service_id ";

				Vector result4 = DBUtils.getVectorTable(connection, sqlselect);
				if (result4.size() > 0) {
					Vector item = (Vector) result4.elementAt(0);
					String scount = (String) item.elementAt(0);
					int icount = Integer.parseInt(scount);
					if (icount > 100) {
						Util.logger.sysLog(2, this.getClass().getName(),
								"checkspam:userid=" + user_id + " &serviceid="
										+ service_id
										+ "@rule:>100MO/1440minute");
						return true;
					}

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			Util.logger.error("checkspam:" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);
		}

		return false;
	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		int iRetries = 10;
		int iTimeout = 10;

		String resultok = Constants._prop.getProperty("mo.soap."
				+ keyword.getOptions() + ".ok", "1");
		if (checkspam(msgObject.getUserid(), msgObject.getServiceid()) == true) {

			Collection messages = new ArrayList();
			String spaminfo15k = "Ban khong duoc gui qua 3SMS/30phut hoac 10SMS/24h. DT ho tro 04-35561862";
			String spaminfo = "Ban khong duoc gui toi dau so 8751 qua 3SMS/5phut hoac 10SMS/1gio hoac 100SMS/24h. DT ho tro 04-35561862";

			if ("8751".equalsIgnoreCase(msgObject.getServiceid())) {
				msgObject.setUsertext(spaminfo15k);
			} else {
				msgObject.setUsertext(spaminfo);
			}

			msgObject.setMsgtype(2);

			messages.add(new MsgObject(msgObject));

			return messages;
		}
		String result = "";
		while (iRetries > 0) {
			try {
				result = sendMessageMO(msgObject, msgObject.getUsertext(),
						keyword.getOptions(), msgObject.getKeyword());
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
				}

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
