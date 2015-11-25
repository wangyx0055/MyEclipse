package icom;

import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ExecuteQueueMO1 extends Thread {

	int threadID = 0;
	MsgQueue queue = null;

	BigDecimal AM = new BigDecimal(-1);

	public ExecuteQueueMO1(MsgQueue queue, int threadID) {
		this.queue = queue;
		
		this.threadID = threadID;
	}

	public static String replaceAllPointWithSpace(String sInput) {
		String strTmp = sInput;
		for (int i = 0; i < sInput.length(); i++) {
			char ch = sInput.charAt(i);
			if (ch == '.') {
				strTmp = strTmp.replace(ch, ' ');
			}
		}
		return strTmp;
	}

	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}

			} else {
				strResult = strResult + ch;
			}

		}
		return strResult;
	}

	@Override
	public void run() {

		MsgObject msgObject = null;
		String serviceId = "";
		String info = "";

		int process_result = 0;
		Timestamp tTime;

		try {
			sleep(1000);
		} catch (InterruptedException ex1) {
		}

		while (Sender.processData) {
			process_result = 0;
			try {
				msgObject = (MsgObject) queue.remove();
				serviceId = msgObject.getServiceid();
				info = msgObject.getUsertext();
				if (msgObject.getObjtype() == 0) {
					info = info.replace('-', ' ');
					info = info.replace('.', ' ');
					info = info.replace(',', ' ');
					info = info.replace('_', ' ');
					info = replaceAllWhiteWithOne(info.trim());
				}

				String sgetkeyword = info;

				if (msgObject.getObjtype() != 0) {
					sgetkeyword = msgObject.getKeyword();

				}

				Keyword keyword = Sender.loadconfig.getKeyword(sgetkeyword
						.toUpperCase(), serviceId);

				if (msgObject.getObjtype() == 0) {
					msgObject.setKeyword(keyword.getKeyword());
				}
				msgObject.setCp_mo(keyword.getCpmo());
				msgObject.setCp_mt(keyword.getCpmt());
				
				process_result = processQueueMsg(msgObject, keyword, LoadConfig.hServices);

				tTime = new Timestamp(System.currentTimeMillis());

				sleep(100);

			} catch (Exception ex) {
				Util.logger.error("Execute queue MO1. Ex:" + ex.toString());
				Util.logger.printStackTrace(ex);
				queue.add(msgObject);

			}

		}

	}

	private int processQueueMsg(MsgObject msgObject, Keyword keyword, Hashtable services) {

		try {
			QuestionManager delegate = null;
			String classname = "services.Invalid";
			// String classname = keyword.getClassname();
			if (msgObject.getObjtype() == 0) {
				classname = keyword.getClass_mo();
			} else {
				classname = keyword.getClass_mt();
				// classname = "icom.SoapQM1";
			}

			Util.logger.info("{processQueueMsg}{"
					+ ((msgObject.getObjtype() == 0) ? "MO" : "MT") + "}"
					+ msgObject.getUserid() + "@" + msgObject.getUsertext()
					+ "@" + msgObject.getRequestid().toString() + "@"
					+ classname);

			if (Constants.INV_KEYWORD.equalsIgnoreCase(keyword.getKeyword()))
			{
				return 1;
			}
			/*
			 * if (Constants.INV_KEYWORD.equalsIgnoreCase(keyword.getKeyword()))
			 * { Util.logger.info("processQueueMsg:@INVKEYWORD==>Save into inv@"
			 * + msgObject.getUserid() + "@" + msgObject.getUsertext() + "@" +
			 * msgObject.getRequestid().toString() + "@" + classname); if
			 * (msgObject.getObjtype() == 0) { add2InvalidMO(msgObject); } else
			 * { add2InvalidMT(msgObject); }
			 * 
			 * return 1;
			 * 
			 * }
			 */

			Class delegateClass = Class.forName(classname);
			Object delegateObject = delegateClass.newInstance();
			delegate = (QuestionManager) delegateObject;

			delegate.start(Constants._prop, msgObject, keyword, services);
			return 1;

		} catch (Exception e) {
			Util.logger.crisis("processQueueMsg:" + msgObject.getUserid() + "@"
					+ msgObject.getUsertext() + "@"
					+ msgObject.getRequestid().toString() + "@" + e.toString());

			String notes = e.toString();
			notes = (notes.length() > 100) ? notes.substring(1, 100) : notes;

			msgObject.setMsgNotes(notes);

			return 0;
		}

	}

	private static BigDecimal add2InvalidMO(MsgObject msgObject) {
		Util.logger.info("add2InvalidMO:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_invalid";
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
				Util.logger.error("add2InvalidMO:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2InvalidMO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2InvalidMO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2InvalidMT(MsgObject msgObject) {
		Util.logger.info("add2InvalidMT:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mt_invalid";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID)"
				+ " values(?,?,?,?,?,?,?,?)";

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

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2InvalidMT:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2InvalidMT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2InvalidMT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

}
