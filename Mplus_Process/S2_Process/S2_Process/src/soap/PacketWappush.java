package soap;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import vms.to.icom.moqueue.MOQueueSenderICOM;

public class PacketWappush extends QuestionManager {
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		try {
			/******
			 * 2010-11-08: PhuongDT
			 * set subcp = -1 de ben VMS khong push MT
			 * ***/
			msgObject.setSubCP(-1);
			String result = "";
			if (msgObject.getObjtype() == 0) {
				//result = msgObject.sendMessageMO(msgObject.getUsertext(),
				//		keyword.getPartner_mt(), keyword.getKeyword());
				/*****
				 * 2010-11-07: PhuongDT
				 * insert them channel Type				 * 
				 * **/
				result= MOQueueSenderICOM.insertMOQueueICOM(msgObject) +"";
				
			} else {
				result = msgObject.sendMessageMT(msgObject.getUsertext(),
						keyword.getPartner_mo(), keyword.getKeyword());
			}
			
			int retriesnum = msgObject.getRetries_num();
			
			if (result.equals("1")) {				
				/****
				 * 2010-11-07: PhuongDT
				 * Sau khi push sang icom thanh cong, thuc hien dang ky ben vms.
				 * **/
				Util.logger.info("@SoapWappushReg@Push mo queue sang icom thanh cong!@user_id=" + msgObject.getUserid() + "@keyword=" +msgObject.getKeyword() + "@request_id=" + msgObject.getRequestid());
				
				Collection<MsgObject> collection = new ArrayList<MsgObject>();
				collection.add(msgObject);
				return collection;
				
			}else{
				retriesnum = retriesnum + 1;
			}

			Util.logger.info(this.getClass().getName() + "@"
					+ "Got "+ result+" Going For Retry, Sleeping,Details: "
					+ "Msisdn: " + msgObject.getUserid() + " @Shortcode: "
					+ msgObject.getServiceid() + " @Keyword: "
					+ msgObject.getKeyword() + " @RequestID: "
					+ msgObject.getRequestid() + " @CommandCode: "
					+ msgObject.getCommandCode() + " @subCp:"
					+ msgObject.getSubCP() + " Online Retry countdown: "
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
						+ msgObject.getUserid() + "@" + msgObject.getMsg_id(),"");
			} else {
				msgObject.setRetries_num(retriesnum + 1);

				ExecuteReSendQueue.add2queueResend(msgObject);
			}
		} catch (Exception e) {
			int retriesnum = msgObject.getRetries_num();
			Util.logger
					.error(getClass().getName()
							+ "@"
							+ "Some Exception..!! Got -1, Going For Retry, Sleeping,Details: "
							+ "Msisdn: " + msgObject.getUserid()
							+ " Shortcode: " + msgObject.getServiceid()
							+ " Keyword: " + msgObject.getKeyword()
							+ " RequestID: " + msgObject.getRequestid()
							+ "CommandCode: " + keyword.getKeyword()
							+ " Online Retry countdown: " + retriesnum);
			Util.logger.info(getClass().getName() + "@" + "Exception: "
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
						+ msgObject.getUserid() + "@" + msgObject.getMsg_id(),"");
			} else {
				msgObject.setRetries_num(retriesnum + 1);
				ExecuteReSendQueue.add2queueResend(msgObject);
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
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,CHANNEL_TYPE)"
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
				BigDecimal localBigDecimal = new BigDecimal(-1);
				return localBigDecimal;
			}
			statement.close();
			BigDecimal localBigDecimal = msgObject.getRequestid();
			return localBigDecimal;
		} catch (SQLException e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			BigDecimal localBigDecimal = new BigDecimal(-1);
			return localBigDecimal;
		} catch (Exception e) {
			Util.logger.error("add2MOSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			BigDecimal localBigDecimal = new BigDecimal(-1);
			return localBigDecimal;
		} finally {
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
				BigDecimal localBigDecimal = new BigDecimal(-1);
				return localBigDecimal;
			}
			statement.close();
			BigDecimal localBigDecimal = msgObject.getRequestid();
			return localBigDecimal;
		} catch (SQLException e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			BigDecimal localBigDecimal = new BigDecimal(-1);
			return localBigDecimal;
		} catch (Exception e) {
			Util.logger.error("add2MTSendFailed:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			BigDecimal localBigDecimal = new BigDecimal(-1);
			return localBigDecimal;
		} finally {
			dbpool.cleanup(connection);
		}

	}
}

