package soap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import org.tempuri.WsMoReceiveSoapProxy;

import icom.Constants;
import icom.DBPool;
import icom.ExecuteInsertSendLog;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

public class SoapMoThienNgan extends QuestionManager {
	String className = "SoapMoThienNgan ";

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		try {
			String result = "";
			String sMsisdn =  msgObject.getUserid();
			msgObject.setSubCP(-1);
			String[] sTokens = null;
			String commandcode = "";
			if (msgObject.getObjtype() == 0) {
				// Xu ly rieng cho VASC
				commandcode = keyword.getKeyword();
				sTokens = commandcode.split(" ");
				if (sTokens.length > 1) {
					commandcode = sTokens[1].trim();
				}
				result = msgObject.sendMessageMO(msgObject.getUsertext(),
						"thienngan", commandcode);
			} else {
				Util.logger.info("Operator: " + msgObject.getMobileoperator());
				result = msgObject.sendMessageMT(msgObject.getUsertext(),
						"thienngan", keyword.getKeyword());
			}

			Util.logger.info(className + " @return code:" + result + "\t@user:"
					+ msgObject.getUserid());

			if (result.equals("1")) {
				Util.logger
						.info(className
								+ " @Call class DBUtil.RegisterServices for register \t@user:"
								+ sMsisdn);

				DBUtil.RegisterServices(msgObject, keyword,
						Constants.TYPE_OF_SERVICE_TEXTBASE, services);
			} else if (result.equals("2")|| result.equals("3") || result.equals("4")
					|| result.equals("5") || result.equals("6") || result.equals("255")) {
				msgObject.setTTimes(DateProc.createTimestamp());
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
			}
		} catch (Exception e) {
			Util.logger.info(this.getClass().getName() + "@" + "Exception: "
					+ e.toString());
			Util.logger.printStackTrace(e);
			msgObject.setTTimes(DateProc.createTimestamp());
			if (msgObject.getObjtype() == 0) {
				add2MOSendFailed(msgObject);
			} else {
				add2MTSendFailed(msgObject);
				msgObject.setProcess_result(Constants.RET_SEND_FAILED);
				msgObject.setMsgNotes(1, "Max retires:" + e.getMessage());
				msgObject
						.setTDoneTime(new Timestamp(System.currentTimeMillis()));
				ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
			}
		}

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
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE, CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,?)";

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
			statement.setInt(8, msgObject.getChannelType());
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
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,NOTES,CHANNEL_TYPE)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";

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
			statement.setInt(10, msgObject.getChannelType());

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
		} finally {
			dbpool.cleanup(connection);

		}
	}
}
