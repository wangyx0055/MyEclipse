package com.vmg.sms.process;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import spam.SpamQM;

import com.vmg.sms.common.Util;

public class ExecuteInsertReceiveLog extends Thread {

	static MsgQueue queueLog = null;

	BigDecimal AM = new BigDecimal(-1);

	public ExecuteInsertReceiveLog(MsgQueue queueLog) {
		this.queueLog = queueLog;
	}

	public static void add2queueReceiveLog(MsgObject msgObject) {
		queueLog.add(msgObject);
	}

	public void run() {

		MsgObject msgObject = null;

		BigDecimal returnId = AM;

		try {
			sleep(1000);
		} catch (InterruptedException ex1) {
		}

		try {
			while (ConsoleSRV.processData) {
				returnId = AM;
				try {

					msgObject = (MsgObject) queueLog.remove();

					returnId = processQueueMsg(msgObject);
					if (returnId.equals(AM)) {
						queueLog.add(msgObject);
					}

				} catch (Exception ex) {
					Util.logger.error(ex.toString());
					queueLog.add(msgObject);

				}
				sleep(50);

			}

		}

		catch (Exception ex) {
			Util.logger.crisis("Error: ExecuteAddReceivelog.run :"
					+ ex.toString());

		}
	}

	private BigDecimal processQueueMsg(MsgObject msgObject) {
		
		String isspam = "0";
		String ischeckspam = Constants._prop.getProperty("spam.active", "1");

		String mtspam = Constants._prop.getProperty("spam.mt."
				+ msgObject.getMobileoperator().toLowerCase(),
				"Ban da vi pham quy dinh chong Spam");
		String msgtype = Constants._prop.getProperty("spam.msgtype", "2");
		String autoreply = Constants._prop.getProperty("spam.autoreply", "1");

		if ("1".equalsIgnoreCase(ischeckspam)) {
			
		
			Util.logger.info("[SPAM]processQueueMsg:{userid="
					+ msgObject.getUserid() + "}{usertext="
					+ msgObject.getUsertext() + "}{requestid="
					+ msgObject.getRequestid().toString() + "}@");
			boolean bret = isSpam(msgObject, mtspam, msgtype, autoreply);
			if (bret == true) {
				isspam = "1";
			}
		}
		
		BigDecimal returnid = add2SMSReceiveLog(msgObject,isspam);

		

		return returnid;
	}

	
	
	private static BigDecimal add2SMSReceiveDay(MsgObject msgObject) {
		
		Util.logger.info("add2SMSReceiveDay:{userid=" + msgObject.getUserid()
				+ "}{usertext=" + msgObject.getUsertext() + "}{requestid="
				+ msgObject.getRequestid().toString() + "}");
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_day_" + msgObject.getMobileoperator().toLowerCase();
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
				Util.logger.error("add2SMSReceiveDay: {userid="
						+ msgObject.getUserid() + "}{usertext="
						+ msgObject.getUsertext() + "}{requestid="
						+ msgObject.getRequestid().toString() + "}"
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2SMSReceiveDay:{userid=" + msgObject.getUserid()
					+ "}{usertext=" + msgObject.getUsertext() + "}{requestid="
					+ msgObject.getRequestid().toString() + "}"
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2SMSReceiveDay:{userid=" + msgObject.getUserid()
					+ "}{usertext=" + msgObject.getUsertext() + "}{requestid="
					+ msgObject.getRequestid().toString() + "}:Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
	private boolean isSpam(MsgObject msgObj, String smtspam, String smsgtype,
			String sautoreply) {
		try {
			SpamQM spamqm = null;
			String classcheckspam = "spam."
					+ msgObj.getMobileoperator().toLowerCase();
			Class delegateclass = Class.forName(classcheckspam);
			Object delegateObj = delegateclass.newInstance();
			spamqm = (SpamQM) delegateObj;

			return spamqm.isSpam(msgObj, sautoreply, smtspam, smsgtype);

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.error("CheckSpam:{userid=" + msgObj.getUserid()
					+ "}{usertext=" + msgObj.getUsertext() + "}{requestid="
					+ msgObj.getRequestid().toString() + "}@" + e.toString());
		}
		return false;
	}
	private static BigDecimal add2SMSReceiveLog(MsgObject msgObject,String isspam) {

		Util.logger.info("add2SMSReceiveLog:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_log"
				+ new SimpleDateFormat("yyyyMM").format(new Date());
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE,RESPONDED,CPID,SPAM)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";

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
			statement.setString(10, isspam);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2ReceiveLog:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2ReceiveLog:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			Util.logger.printStackTrace(e);
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
