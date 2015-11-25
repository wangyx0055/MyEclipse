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
import icom.common.DBDelete;
import icom.common.DBInsert;
import icom.common.DBSelect;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.MTPushObject;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class SoapMOCancel extends QuestionManager {

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

				// Xu ly rieng cho VASC
				String commandcode = keyword.getKeyword();
				String[] sTokens = commandcode.split(" ");
				if (sTokens.length > 1) {
					commandcode = sTokens[1];
				}
								
				result = msgObject.sendMessageMO(msgObject.getUsertext(),
						keyword.getPartner_mt(), commandcode);
			} else {

				Util.logger.info("Operator: " + msgObject.getMobileoperator());

				result = msgObject.sendMessageMT(msgObject.getUsertext(),
						keyword.getPartner_mo(), keyword.getKeyword());

			}
			
			result = result.trim();
			
			Util.logger.info("SoapMOCancel ; user_id = " + msgObject.getUserid() 
					+ "; user text = " + msgObject.getUsertext() +" ### Send Vasc result = " + result);
			// -2 : loi he thong
			// -1 : Sai cu phap
			// 0 : da dang ky
			// 1 : Dang ky / Huy Thanh cong
			// 2 : chua dang ky nhung huy
			if(result.equals("1") || result.equals("2") || result.equals("0")){
				if(msgObject.getUsertext().toUpperCase().trim().equals("HUY TIN")){
					String poolName = "s2mtpush";
					String tableName = "mtpush_s2vms_diemtin";
					String tableNameCancel = "mtpush_s2vms_diemtin_cancel";
					
					DBSelect dbSelect = new DBSelect();
					MTPushObject mtPushObject = dbSelect.getMTPushByUserId(msgObject.getUserid(), tableName);
					if(mtPushObject != null){
						DBInsert dbInsert = new DBInsert();
						Util.logger.info("SoapMOCancel ## " +
								"insert into mtpush_s2vms_diemtin_cancel, userid = " 
								 + msgObject.getUserid());
						dbInsert.insertMTPush(mtPushObject, tableNameCancel,poolName);
						
						Util.logger.info("SoapMOCancel ## " +
								"delete from mtpush_s2vms_diemtin, userid = " 
								 + msgObject.getUserid());
						DBDelete dbDelete = new DBDelete();
						dbDelete.deleteByID(mtPushObject.getID(), tableName,poolName);
					}
				}
			}
			
			if (result.equalsIgnoreCase("1")) {
				 /****
				 * 2010-11-07: PhuongDT
				 * Sau khi push sang vasc thanh cong, thuc hien huy ben vms
				 * **/
			
				Util.logger.info("@SoapMOCancel@Push mo queue sang icom thanh cong!@user_id=" + msgObject.getUserid() + "@keyword=" +msgObject.getKeyword() + "@request_id=" + msgObject.getRequestid());
								
				return DBUtil.UnRegisterServices(msgObject, keyword);
				
			} else if (result.equalsIgnoreCase("-2")) {
				// -2 thi retries
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
			}else if(result.equalsIgnoreCase("2")){
				// Chua dang ky da huy => Huy ben VMS cho chac an.
				DBUtil.UnRegisterServices(msgObject, keyword);
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
