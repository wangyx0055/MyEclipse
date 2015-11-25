package services;

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
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Collection;

public class Help extends QuestionManager {

	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		messages.add(new MsgObject(msgObject));
	
		String tblMoQueueInvalid = "mo_queue_invalid";

		insertToMOInvalid(msgObject, tblMoQueueInvalid);

		return null;
	}
	public static int sendMT2sendqueue(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ExecuteResponseQueue@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ExecuteResponseQueue@sendMT@userid="
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ExecuteResponseQueue@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID,AMOUNT,NOTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setLong(9, msgObject.getAmount());
			statement.setString(10, msgObject.getMsgnotes());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ExecuteResponseQueue@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("ExecuteResponseQueue@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ExecuteResponseQueue@sendMT: Error:@userid="
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
	
	public  int insertToMOInvalid(MsgObject msgObject, String moTableName){
		int iReturn = 1;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO " + moTableName +" ( USER_ID, SERVICE_ID," +
				" MOBILE_OPERATOR, COMMAND_CODE, INFO, RECEIVE_DATE, RESPONDED, REQUEST_ID, CHANNEL_TYPE) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?)";
		Util.logger.info("##############");
		Util.logger.info("S2Process @@@ Class Help @@ STARTING INSERT INTO mo_queue_invalid @ "
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString() + "@");
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2invalid");
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1,msgObject.getUserid());
			stmt.setString(2, msgObject.getServiceid());
			stmt.setString(3, msgObject.getMobileoperator());
			stmt.setString(4, msgObject.getCommandCode());
			stmt.setString(5, msgObject.getUsertext());
			stmt.setString(6, Util.getCurrentDate());
			stmt.setString(7, "0");
			stmt.setString(8, String.valueOf(msgObject.getRequestid()));
			stmt.setInt(9, msgObject.getChannelType());

			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			} else {
				iReturn = -1;
				Util.logger
						.error("Class Help - insertToMOQueueInvalid : execute Error!!");
			}
		} catch (SQLException ex3) {
			iReturn = -1;
			Util.logger
					.error("Class Help - insertToMOQueueInvalid. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			iReturn = -1;
			Util.logger
					.error("Class Help - insertToMOQueueInvalid. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		if(iReturn == 1){
			Util.logger.info("SUCCESSFUL !!!! INSERT INTO MO_QUEUE_INVALID SUCCESSFUL!");
		}else{
			Util.logger.info("FAILURE !!!! INSERT INTO MO_QUEUE_INVALID FAILURE!");
		}
		
		Util.logger.info("#############");
		
		return iReturn;
	}

}
