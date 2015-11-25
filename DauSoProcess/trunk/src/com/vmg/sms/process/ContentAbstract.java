package com.vmg.sms.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.Util;

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
public abstract class ContentAbstract {

	public void start(MsgObject msgObject, Keyword keyword) throws Exception {
		try {

			Collection messages = getMessages(msgObject, keyword);
			if (messages != null) {
				Iterator iter = messages.iterator();
				for (int i = 1; iter.hasNext(); i++) {
					MsgObject msgMT = (MsgObject) iter.next();
					sendMT(msgMT);
				
					/*
					 * if (msgObject.getUsertext().length() <= 160 ||
					 * msgObject.getContenttype() != 0) { sendMT(msgMT); } else {
					 * Util.logger .info("ContentAbstract@start: usertext's
					 * length > 160 --> split"); Collection listmt =
					 * splitMsg(msgObject.getUsertext()); Iterator itermt =
					 * listmt.iterator(); for (int j = 1; itermt.hasNext(); j++) {
					 * MsgObject itemmt = msgMT; String temp = (String)
					 * itermt.next(); if (j == 1) { itemmt.setUsertext(temp);
					 * 
					 * sendMT(new MsgObject(itemmt)); } else { if
					 * (msgMT.getMsgtype() != 0 || msgMT.getMsgtype() != 3) {
					 * itemmt.setMsgtype(0); } itemmt.setUsertext(temp);
					 * sendMT(new MsgObject(itemmt));
					 *  }
					 *  }
					 *  }
					 */

				}
			} else {
				Util.logger.info("ContentAbstract@start:"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + ": LOST MESSAGE");

			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.logger
					.info("ContentAbstract@start:" + msgObject.getUserid()
							+ "@TO" + msgObject.getServiceid() + "@"
							+ msgObject.getUsertext() + ": LOST MESSAGE"
							+ e.toString());

		}

	}

	protected abstract Collection getMessages(MsgObject msgObject,
			Keyword keyword) throws Exception;

	private static synchronized Collection splitMsg(String arg) {
		String[] result = new String[3];
		Vector v = new Vector();
		int segment = 0;

		if (arg.length() <= 160) {
			result[0] = arg;
			v.add(result[0]);
			return v;

		} else {
			segment = 160;
		}

		StringTokenizer tk = new StringTokenizer(arg, " ");
		String temp = "";
		int j = 0;

		while (tk.hasMoreElements()) {
			String token = (String) tk.nextElement();
			if (temp.equals("")) {
				temp = temp + token;
			} else {
				temp = temp + " " + token;
			}

			if (temp.length() > segment) {
				temp = token;
				j++;
			} else {
				result[j] = temp;
			}

			if (j == 3) {
				break;
			}
		}

		for (int i = 0; i < result.length; i++) {
			if (result[i] != null) {
				v.add(result[i]);
			}
		}

		return v;
	}

	private static int sendMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@sendMT@userid="
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}
			sqlString = "INSERT INTO ems_send_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,"
					+ " MESSAGE_ID, CONTENT_TYPE,cpid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setTimestamp(6, null);
			statement.setTimestamp(7, null);
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getMsgtype());
			statement.setBigDecimal(10, msgObject.getRequestid());
			statement.setString(11, "1");
			statement.setInt(12, msgObject.getContenttype());
			statement.setInt(13, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

}
