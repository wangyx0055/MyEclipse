package soap;

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

import icom.Constants;
import icom.DBPool;
import icom.ExecuteInsertSendLog;
import icom.ExecuteReSendQueue;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class SoapQMNew extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		try {
			String result = "";
			if (msgObject.getObjtype() == 0) {
				result = msgObject.sendMessageMO(msgObject.getUsertext(),
						keyword.getPartner_mt(), keyword.getKeyword());
			} else {
				
				String sms = msgObject.getUsertext().toUpperCase().replace(" ",
						"_");
				Util.logger.info("Usertext send to Beeline: " + sms);
				Util.logger
						.info("Start With XIN: " + sms.startsWith("XIN_LOI"));
				Util.logger.info("Contains : " + sms.contains("XIN_LOI"));
				Util.logger.info("Usertext send to Beeline: " + sms);

				if (sms.startsWith("XIN_LOI")

				|| sms.contains("XIN_LOI")) {
					msgObject
							.setUsertext("Ban nhap sai ma tran dau. Vui long nhap dung ma tran dau nhu trong lich thi dau.DTHT 199");

					result = msgObject
							.sendMessageMT(
									"Ban nhap sai ma tran dau. Vui long nhap dung ma tran dau nhu trong lich thi dau.DTHT 199",
									keyword.getPartner_mo(), keyword
											.getKeyword());
				} else {
					result = msgObject.sendMessageMT(msgObject.getUsertext(),
							keyword.getPartner_mo(), keyword.getKeyword());
				}

			}
			if (result.equals("1")) {
				Util.logger.info(this.getClass().getName() + "@"
						+ "Got 1, ok,Details: " + "Msisdn: "
						+ msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ keyword.getKeyword() + " Online Retry : "
						+ msgObject.getRetries_num());

				if (msgObject.getObjtype() != 0) {
					msgObject.setProcess_result(Constants.RET_OK);
					msgObject.setTDoneTime(new Timestamp(System
							.currentTimeMillis()));
					ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
				}

				return null;
			} else {
				int retriesnum = msgObject.getRetries_num();

				Util.logger.info(this.getClass().getName() + "@"
						+ "Got -1, Going For Retry, Sleeping,Details: "
						+ "Msisdn: " + msgObject.getUserid() + " Shortcode: "
						+ msgObject.getServiceid() + " Keyword: "
						+ msgObject.getKeyword() + " RequestID: "
						+ msgObject.getRequestid() + "CommandCode: "
						+ keyword.getKeyword() + " Online Retry countdown: "
						+ retriesnum);
				msgObject.setTTimes(DateProc.createTimestamp());

				if (retriesnum >= Constants.MAX_RETRIES) {
					if (msgObject.getObjtype() == 0) {
						add2MOSendFailed(msgObject);
					} else {
						add2MTSendFailed(msgObject);
						msgObject.setProcess_result(Constants.RET_SEND_FAILED);
						msgObject.setMsgNotes(1, "Max retires");
						msgObject.setTDoneTime(new Timestamp(System
								.currentTimeMillis()));
						ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
					}
					DBUtil.Alert("SoapQM", "Sendfailed", "warn", "Sendfailed:"
							+ msgObject.getUserid() + "@"
							+ msgObject.getMsg_id(), "conglt-0963536888");

				} else {
					msgObject.setRetries_num(retriesnum + 1);

					ExecuteReSendQueue.add2queueResend(msgObject);
				}
			}

		} catch (Exception e) {
			int retriesnum = msgObject.getRetries_num();
			Util.logger
					.error(this.getClass().getName()
							+ "@"
							+ "Some Exception..!! Got -1, Going For Retry, Sleeping,Details: "
							+ "Msisdn: " + msgObject.getUserid()
							+ " Shortcode: " + msgObject.getServiceid()
							+ " Keyword: " + msgObject.getKeyword()
							+ " RequestID: " + msgObject.getRequestid()
							+ "CommandCode: " + keyword.getKeyword()
							+ " Online Retry countdown: " + retriesnum);
			Util.logger.info(this.getClass().getName() + "@" + "Exception: "
					+ e.toString());
			e.printStackTrace();
			msgObject.setTTimes(DateProc.createTimestamp());
			if (retriesnum >= Constants.MAX_RETRIES) {
				if (msgObject.getObjtype() == 0) {
					add2MOSendFailed(msgObject);
				} else {
					add2MTSendFailed(msgObject);
					msgObject.setProcess_result(Constants.RET_SEND_FAILED);
					msgObject.setMsgNotes(1, "Max retires:" + e.getMessage());
					msgObject.setTDoneTime(new Timestamp(System
							.currentTimeMillis()));
					ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
				}
				DBUtil.Alert("SoapQM", "Sendfailed", "warn", "Sendfailed:"
						+ msgObject.getUserid() + "@" + msgObject.getMsg_id(),
						"conglt-0963536888");
			} else {
				msgObject.setRetries_num(retriesnum + 1);
				ExecuteReSendQueue.add2queueResend(msgObject);
			}
			// iRetries--;
			// Thread.sleep(iTimeout * 1000);
			// continue;
		}

		// }
		// add2SMSSendFailed(msgObject);
		return null;
	}

	private static BigDecimal add2MOSendFailed(MsgObject msgObject) {
		Util.logger.info("mo_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE)"
				+ " values(?,?,?,?,?,?,?)";

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

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MOSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2MTSendFailed(MsgObject msgObject) {
		Util.logger.info("mt_queue_error:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mt_queue_error";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,NOTES)"
				+ " values(?,?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setString(9, msgObject.getMsgnotes());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2MTSendFailed:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
