package DAO;

import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DTO.MlistInfoDTO;

public class MListDAO {

	public static int insertMListDGTichDiem(MlistInfoDTO mlist,
			String tableName, String dbContent) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int result = 1;

		DBPool dbPool = new DBPool();
		String sqlInsert = "INSERT INTO "
				+ tableName
				+ "(USER_ID,SERVICE_ID,DATECREATE,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,CHANNEL_TYPE,"
				+ " REG_COUNT,IS_PUSH_AD, TOTALSCOREBYDAY, TOTALSCOREBYWEEK,TOTALSCORE)"
				+ "VALUES('"
				+ mlist.getUserId()
				+ "',"
				+ "'"
				+ mlist.getServiceId()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + mlist.getCommandCode()
				+ "','" + mlist.getRequestId() + "'" + ","
				+ mlist.getMessageType() + ",'" + mlist.getMobiOperator()
				+ "'," + mlist.getChanelType() + "," + mlist.getRegCount()
				+ "," + mlist.getIsPushAd() + "," + 0 
				+ "," + 0 + ","
				+ mlist.getTotalScore() + ")";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(dbContent);
			}
			stmt = connection.prepareStatement(sqlInsert);
			stmt.executeUpdate();
			Util.logger.info(sqlInsert);

		} catch (SQLException ex3) {
			Util.logger.error("@" + tableName + " insert dao. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			result = -1;
		} catch (Exception ex2) {
			Util.logger.error("@" + tableName + " insert dao SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			result = -1;
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return result;
	}

	public static int deleteMlist(String tableName, String userId,
			String poolName, String dbContent) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int result = 1;
		DBPool dbPool = new DBPool();
		String sqlInsert = "Delete from " + tableName + " Where USER_ID='"
				+ userId + "'";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(dbContent);
			}
			stmt = connection.prepareStatement(sqlInsert);
			stmt.executeUpdate();
			Util.logger.info(sqlInsert);

		} catch (SQLException ex3) {
			Util.logger.error("PoolName: " + poolName
					+ ",@Mlist delete dao. SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
			result = -1;
		} catch (Exception ex2) {
			Util.logger.error("PoolName: " + poolName
					+ ",@Mlist delete dao SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
			result = -1;
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return result;
	}

	public static MlistInfoDTO getUserId(String tableName, String userId,
			String dbContent) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		MlistInfoDTO result = null;
		DBPool dbPool = new DBPool();
		String sqlQuery = "SELECT * FROM " + tableName + " Where USER_ID='"
				+ userId + "'";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(dbContent);
			}
			stmt = connection.prepareStatement(sqlQuery);

			stmt.execute();
			rs = stmt.getResultSet();
			while (rs.next()) {
				result = new MlistInfoDTO();
				result.setUserId(rs.getString("USER_ID"));
				result.setServiceId(rs.getString("SERVICE_ID"));
				result.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
				result.setCommandCode(rs.getString("COMMAND_CODE"));
				result.setChanelType(rs.getInt("CHANNEL_TYPE"));
				result.setMessageType(rs.getInt("MESSAGE_TYPE"));
				result.setRequestId(rs.getString("REQUEST_ID"));
				result.setRegCount(rs.getInt("REG_COUNT"));
			}

		} catch (SQLException ex3) {
			Util.logger.error("get mlist info: SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("get mlist info: SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return result;
	}

	public static MlistInfoDTO getUserDauGia(String tableName, String userId,
			String dbContent) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		MlistInfoDTO result = null;
		DBPool dbPool = new DBPool();
		String sqlQuery = "SELECT * FROM " + tableName + " Where USER_ID='"
				+ userId + "'";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(dbContent);
			}
			stmt = connection.prepareStatement(sqlQuery);

			stmt.execute();
			rs = stmt.getResultSet();
			while (rs.next()) {
				result = new MlistInfoDTO();
				result.setUserId(rs.getString("USER_ID"));
				result.setServiceId(rs.getString("SERVICE_ID"));
				result.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
				result.setCommandCode(rs.getString("COMMAND_CODE"));
				result.setChanelType(rs.getInt("CHANNEL_TYPE"));
				result.setMessageType(rs.getInt("MESSAGE_TYPE"));
				result.setRequestId(rs.getString("REQUEST_ID"));
				result.setRegCount(rs.getInt("REG_COUNT"));
				result.setIsPushAd(rs.getInt("IS_PUSH_AD"));
				result.setTotalScore(rs.getInt("TOTALSCORE"));
				result.setTotalScoreByDay(rs.getInt("TOTALSCOREBYDAY"));
				result.setTotalScoreByWeek(rs.getInt("TOTALSCOREBYWEEK"));

			}

		} catch (SQLException ex3) {
			Util.logger.error("get mlist info: SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("get mlist info: SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return result;
	}

	public static boolean checkMlist(String tableName, String userId,
			String poolName) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean check = false;
		DBPool dbPool = new DBPool();
		String sqlQuery = "SELECT * From " + tableName + " Where USER_ID='"
				+ userId + "' Limit 1";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(poolName);
			}
			stmt = connection.prepareStatement(sqlQuery);

			stmt.execute();
			rs = stmt.getResultSet();
			while (rs.next()) {
				check = true;
			}

		} catch (SQLException ex3) {
			Util.logger.error("get mlist info: SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("get mlist info: SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return check;
	}

	public static int deleteMlist(String tableName, String userId,
			String poolName) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int result = 1;
		DBPool dbPool = new DBPool();
		String sqlInsert = "Delete from " + tableName + " Where USER_ID='"
				+ userId + "'";

		try {
			if (connection == null) {
				connection = dbPool.getConnection(poolName);
			}
			stmt = connection.prepareStatement(sqlInsert);
			stmt.executeUpdate();
			Util.logger.info(sqlInsert);

		} catch (SQLException ex3) {
			Util.logger.error("PoolName: " + poolName
					+ ",@Mlist delete dao. SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
			result = -1;
		} catch (Exception ex2) {
			Util.logger.error("PoolName: " + poolName
					+ ",@Mlist delete dao SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
			result = -1;
		} finally {
			dbPool.cleanup(rs, stmt);
			dbPool.cleanup(connection);
		}
		return result;
	}

	public static int InsertSubcriber(String mlist, MsgObject ems) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into "
				+ mlist
				+ " (service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,channel_type,options,reg_count,IS_ICOM) values ('"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + ems.getCommandCode()
				+ "','" + ems.getLongRequestid() + "','1','"
				+ ems.getMobileoperator() + "',0,0," + ems.getChannelType()
				+ ",'" + ems.getOption() + "',1," + ems.getIsIcom() + ")";
		Util.logger.info("DbUtil@InsertSubcriber@SQL Insert: " + sqlInsert);
		try {
			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertSubcriber@"
						+ ": insert Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertSubcriber@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

}
