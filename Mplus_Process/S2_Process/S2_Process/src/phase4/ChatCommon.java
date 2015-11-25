package phase4;

import icom.DBPool;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import servicesPkg.MlistInfo;

/**
 * @date 2012-02-17
 * @author DanND
 *
 */
public class ChatCommon {

	public MlistInfo getMlistInfoObject(String tableName, String userId,
			String serviceId, String commandCode) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT, NUMBER_CHARGE"
				+ " FROM " + tableName + " WHERE USER_ID = '" + userId + "'"
				+ " AND SERVICE_ID = '" + serviceId + "' AND COMMAND_CODE = '"
				+ commandCode + "'";

		MlistInfo mlistInfo = new MlistInfo();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		Util.logger.info("getMlistInfoObject - SQL:" + sqlSelect);
		// System.out.println("getMlistInfoObject QUERRY: " + sqlSelect);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
					mlistInfo.setNumberCharge(rs.getInt("NUMBER_CHARGE"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("ChatCommon - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ChatCommon - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return mlistInfo;

	}

	public ChatSendDailyObj getChatDailyObj(String userSend, String userReceive) {

		String sqlSelect = "SELECT id,user_send,user_receive,time_send, is_response, number_sms"
				+ " FROM chat_send_daily WHERE user_send = '"
				+ userSend
				+ "'"
				+ " AND user_receive = '" + userReceive + "'";

		ChatSendDailyObj chatSendObj = new ChatSendDailyObj();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		Util.logger.info("getChatDailyObj - SQL:" + sqlSelect);
		// System.out.println("getMlistInfoObject QUERRY: " + sqlSelect);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					chatSendObj.setId(rs.getInt("id"));
					chatSendObj.setUserSend(rs.getString("user_send"));
					chatSendObj.setUserReceive(rs.getString("user_receive"));
					chatSendObj.setTimeSend(rs.getString("time_send"));
					chatSendObj.setIsResponse(rs.getInt("is_response"));
					chatSendObj.setNumberSms(rs.getInt("number_sms"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("ChatCommon - getChatDailyObj. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ChatCommon - getChatDailyObj. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return chatSendObj;
	}
	
	public int getNumberUserSent(String userSend) {

		String sqlSelect = "SELECT COUNT(id) AS number FROM chat_send_daily" +
				" WHERE user_send = '" + userSend + "'";
		
		int numberUser = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		Util.logger.info("getNumberUserSent - SQL:" + sqlSelect);
		// System.out.println("getMlistInfoObject QUERRY: " + sqlSelect);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					numberUser = rs.getInt("number");
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("ChatCommon - getNumberUserSent. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ChatCommon - getNumberUserSent. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return numberUser;
	}
	
	public ChatBlockedObj getBlockObj(String userRequest, String userBlock) {

		String sqlSelect = "SELECT id,user_request,user_blocked,time_request FROM chat_blocked"
				+ " WHERE user_request = '"
				+ userRequest
				+ "' AND "
				+ " user_blocked = '" + userBlock + "'";

		ChatBlockedObj blockObj = new ChatBlockedObj();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		Util.logger.info("getBlockObj - SQL:" + sqlSelect);
		// System.out.println("getMlistInfoObject QUERRY: " + sqlSelect);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					blockObj.setId(rs.getInt("id"));
					blockObj.setUserRequest(rs.getString("user_request"));
					blockObj.setUserBlocked(rs.getString("user_blocked"));
					blockObj.setTimeRequest(rs.getString("time_request"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("ChatCommon - getBlockObj. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ChatCommon - getBlockObj. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return blockObj;
	}
	
	public int updateMlistActive(String tableMlist, int id, int active) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET ACTIVE = " + active
				+ " WHERE ID = " + id;

		Util.logger.info("@updateMlistActive@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateMlistActive@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistActive@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	
	public int increaseMlistNumberCharge(String tableMlist, int id, int numberCharge) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET number_charge = number_charge + 1"
				+ " WHERE ID = " + id;

		Util.logger.info("@updateMlistNumberCharge@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateMlistActive@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistNumberCharge@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	
	
	public int resetMlist(String tableMlist,int active, int numberCharge) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET ACTIVE = " + active
							+ ", number_charge = " + numberCharge;

		Util.logger.info("@updateMlistActive@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateMlistActive@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistActive@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}
	
	public int insertChatSendDaily(String userSend, String userReceive, int isResponse, int numberSms){
		
		String stringLog = ":: userSend = " + userSend
							+ ";; userReceive = " + userReceive;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("@sendMT: Error connection == null" );
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO `chat_send_daily`(user_send,user_receive,is_response,number_sms)"
						+ "VALUES(?,?,?,?)";
			Util.logger.info(":: sql " + sqlString + stringLog);

			stmt = connection.prepareStatement(sqlString);
			
			stmt.setString(1, userSend);
			stmt.setString(2, userReceive);
			stmt.setInt(3, isResponse);
			stmt.setInt(4, numberSms);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("CHATCommon insertChatSendDaily Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertChatSendDaily SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	public int insertChatBlocked(String userRequest, String userBlocked){
		
		String stringLog = ":: userRequest = " + userRequest
							+ ";; userBlocked = " + userBlocked;
		
		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("@insertChatBlocked: Error connection == null" );
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO chat_blocked(user_request,user_blocked)" +
					"VALUES(?,?)";
			Util.logger.info(":: sql " + sqlString + stringLog);

			stmt = connection.prepareStatement(sqlString);
			
			stmt.setString(1, userRequest);
			stmt.setString(2, userBlocked);

			if (stmt.executeUpdate() != 1) {
				Util.logger.error("CHATCommon insertChatBlocked Execute Error; " + stringLog);
			} else {
				Util.logger.info("insertChatBlocked SUCCESSFUL; " + stringLog);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
		
	}
	
	
	public int increaseNumberSms(int sendId) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("@increaseNumberSms: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE chat_send_daily SET number_sms = number_sms + 1" +
					" WHERE id = " + sendId;
			
			Util.logger.info("updateNumberSms:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("increaseNumberSms Execute Error; " + sqlString);
			} else {
				Util.logger.info("increaseNumberSms SUCCESSFUL; id = " + sendId);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int increaseMlistNumberCharge(int mlistId, String tblMlist) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("@increaseNumberSms: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE " + tblMlist + " SET number_charge = number_charge + 1" +
					" WHERE id = " + mlistId;
			
			Util.logger.info("increaseMlistNumberCharge:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("increaseMlistNumberCharge Execute Error; " + sqlString);
			} else {
				Util.logger.info("increaseMlistNumberCharge SUCCESSFUL; id = " + mlistId);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int updateIsResponse(int sendId, int numberResponse) {

		int iReturn = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("@updateChargingMember: Error connection == null");
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "UPDATE chat_send_daily SET is_response = " + numberResponse +
					" WHERE id = " + sendId;
			
			Util.logger.info("updateNumberSms:: SQL = " + sqlString);
			
			stmt = connection.prepareStatement(sqlString);


			if (stmt.executeUpdate() != 1) {
				Util.logger.error("Ph4Common Execute Error; " + sqlString);
			} else {
				Util.logger.info("updateNumberSms SUCCESSFUL; id = " + sendId);
				iReturn = 1;
			}

		} catch (SQLException ex) {
			Util.logger.printStackTrace(ex);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public ArrayList<MlistInfo> getMlistChatInfoToCharge(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT,NUMBER_CHARGE"
				+ " FROM " + tableName + " WHERE ACTIVE = 0 ";

		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		MlistInfo mlistInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					mlistInfo = null;
					mlistInfo = new MlistInfo();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
					mlistInfo.setNumberCharge(rs.getInt("NUMBER_CHARGE"));

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}


}
