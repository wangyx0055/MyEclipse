package daugia;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import servicesPkg.MlistInfo;
import servicesPkg.ServiceMng;

public class DaugiaCommon {
	/**
	 * 
	 * @param tableName
	 * @param userID
	 * @return
	 */
	public MlistInfo getAllMlistInfoByUser(String tableName, String userID) {
		MlistInfo mlistInfo = null;
		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " where USER_ID = '" + userID + "'";
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
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIANGAY - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIANGAY - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return mlistInfo;
	}

	/**
	 * tuannq add
	 * 
	 * @param userid
	 * @return
	 */
	public String getCommandCodeByUserId(String userid) {
		String commmandCode = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT command_code FROM "
				+ DGConstants.TABLE_MLIST_DG + " WHERE  user_id = '" + userid
				+ "'";
		Util.logger.info("getCommandCodeByUserId @sql = " + sqlQuery);

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					commmandCode = rs.getString("command_code");

				}
			} else {
				Util.logger
						.error("DAUGIA - getCommandCodeByUserId : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - getCommandCodeByUserId. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getCommandCodeByUserId. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return commmandCode;
	}

	public int InsertDGCharge(MsgObject msg, String tableName, String strAmount) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO "
				+ tableName
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, "
				+ "DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, RETRIES_NUM, "
				+ "NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, RESULT_CHARGE, DGAMOUNT, TIME_SEND_MO ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, msg.getUserid());
			stmt.setString(2, msg.getServiceid());
			stmt.setString(3, msg.getMobileoperator());
			stmt.setString(4, msg.getCommandCode());
			stmt.setInt(5, msg.getContenttype());
			stmt.setString(6, msg.getUsertext());
			stmt.setTimestamp(7, msg.getTSubmitTime());
			stmt.setTimestamp(8, msg.getTDoneTime());
			stmt.setInt(9, msg.getProcess_result());
			stmt.setInt(10, msg.getMsgtype());
			stmt.setBigDecimal(11, msg.getRequestid());
			stmt.setLong(12, msg.getMsg_id());
			stmt.setInt(13, msg.getRetries_num());
			stmt.setString(14, "");
			stmt.setString(15, msg.getServiceName());
			stmt.setInt(16, msg.getChannelType());
			stmt.setInt(17, msg.getContentId());
			stmt.setLong(18, msg.getAmount());
			stmt.setInt(19, 0);
			stmt.setString(20, strAmount);
			stmt.setString(21, msg.getTimeSendMO());

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@DaugiaCommon InsertDGCharge: Error@userid="
								+ msg.getUserid() + "@serviceid="
								+ msg.getServiceid() + "@usertext="
								+ msg.getUsertext() + "@messagetype="
								+ msg.getMsgtype() + "@requestid="
								+ msg.getRequestid().toString());
				return -1;
			}

			Util.logger
					.info("@DaugiaCommon InsertDGCharge SUCCESSFUL !!! \n @ Query = "
							+ sqlQuery);

			return 1;
		} catch (SQLException ex3) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

	public SanPhamDG getSPDauGia() {

		SanPhamDG spObj = null;

		String currTime = Util.getCurrentDate();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, MA_SP, TEN_SP, START_DATE,"
				+ " END_DATE, IMAGE_LINK, MOTA_SP, IS_SENT_MT_WEEKLY,"
				+ " GIA_SP, TIME_DAILY, TIME_WEEKLY, TIME_DAILY_MANUAL "
				+ " FROM daugia_sanpham WHERE START_DATE <= '" + currTime
				+ "' AND IS_SENT_MT_WEEKLY = 0";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					spObj = new SanPhamDG();
					spObj.setId(rs.getInt("ID"));
					spObj.setMaSP(rs.getString("MA_SP"));
					spObj.setTenSP(rs.getString("TEN_SP"));
					spObj.setStartDate(rs.getString("START_DATE"));
					spObj.setEndDate(rs.getString("END_DATE"));
					spObj.setImageLink(rs.getString("IMAGE_LINK"));
					spObj.setMotaSP(rs.getString("MOTA_SP"));
					spObj.setIsSendMTWeekly(rs.getInt("IS_SENT_MT_WEEKLY"));
					spObj.setGiaSP(rs.getString("GIA_SP"));
					spObj.setTimeDaily(rs.getString("TIME_DAILY"));
					spObj.setTimeAlert(rs.getString("TIME_WEEKLY"));
					spObj.setTimeManual(rs.getString("TIME_DAILY_MANUAL"));
				}
			} else {
				Util.logger.error("DAUGIA - getSPDauGia : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - getSPDauGia. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getSPDauGia. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}

	/***
	 * LAY THONG TIN SAN PHAM DAU GIA VUA MOI TRA MT CHO KHACH HANG TRUNG
	 * THUONG.
	 * 
	 * @return
	 */

	public SanPhamDG getSPDGNotResponseWeekly() {

		SanPhamDG spObj = null;

		String currTime = Util.getCurrentDate();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, MA_SP, TEN_SP, START_DATE, END_DATE, IMAGE_LINK, MOTA_SP, IS_SENT_MT_WEEKLY"
				+ " FROM daugia_sanpham WHERE END_DATE < '"
				+ currTime
				+ "' AND IS_SENT_MT_WEEKLY = 1";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					spObj = new SanPhamDG();
					spObj.setId(rs.getInt("ID"));
					spObj.setMaSP(rs.getString("MA_SP"));
					spObj.setTenSP(rs.getString("TEN_SP"));
					spObj.setStartDate(rs.getString("START_DATE"));
					spObj.setEndDate(rs.getString("END_DATE"));
					spObj.setImageLink(rs.getString("IMAGE_LINK"));
					spObj.setMotaSP(rs.getString("MOTA_SP"));
					spObj.setIsSendMTWeekly(rs.getInt("IS_SENT_MT_WEEKLY"));
				}
			} else {
				Util.logger
						.error("DAUGIA - getSPDGNotResponseWeekly : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getSPDGNotResponseWeekly. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getSPDGNotResponseWeekly. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}

	public int sendMT(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DaugiaCommon@sendMT\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("DaugiaCommon@sendMT\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DaugiaCommon@sendMT: Error connection == null"
								+ msgObject.getUserid() + "\tTO"
								+ msgObject.getServiceid() + "\t"
								+ msgObject.getUsertext() + "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ DGConstants.MTQUEUE_DKHUY
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

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
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DaugiaCommon@sendMT: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("DaugiaCommon@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DaugiaCommon@sendMT: Error:@userid="
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

	public int pushMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DaugiaCommon@pushMT\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("DaugiaCommon@pushMT\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DaugiaCommon@pushMT: Error connection == null"
								+ msgObject.getUserid() + "\tTO"
								+ msgObject.getServiceid() + "\t"
								+ msgObject.getUsertext() + "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ DGConstants.MTQUEUE_PUSH
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			Util.logger.info("DaugiaCommon@pushMT @sql :" + sqlString);
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
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DaugiaCommon@pushMT: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("DaugiaCommon@pushMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@\n"
					+ e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DaugiaCommon@pushMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@\n"
					+ e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public int pushMTWin(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DaugiaCommon@pushMTWin\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("DaugiaCommon@pushMTWin\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DaugiaCommon@pushMTWin: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ DGConstants.MTQUEUE_WIN
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			Util.logger.info("DaugiaCommon@pushMTWin @sql : " + sqlString);
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
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DaugiaCommon@pushMTWin: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("DaugiaCommon@pushMTWin: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@\n"
					+ e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DaugiaCommon@pushMTWin: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@\n"
					+ e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public ArrayList<DGChargeInfo> getDGChargeResult() {

		ArrayList<DGChargeInfo> arrResult = new ArrayList<DGChargeInfo>();

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR,"
				+ " COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, "
				+ "PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, "
				+ "RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, "
				+ "AMOUNT, RESULT_CHARGE, DGAMOUNT, TIME_SEND_MO FROM "
				+ DGConstants.TABLE_DG_CHARGE_RESULT
				+ " ORDER BY ID ASC LIMIT 1000";

		DGChargeInfo resultInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// Util.logger.info("LoadChargeResult - SQL:" + sqlSelect);

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					resultInfo = null;
					resultInfo = new DGChargeInfo();

					resultInfo.setId(rs.getInt("ID"));
					resultInfo.setUserId(rs.getString("USER_ID"));
					resultInfo.setServiceId(rs.getString("SERVICE_ID"));
					resultInfo.setMobileOperator(rs
							.getString("MOBILE_OPERATOR"));
					resultInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					resultInfo.setContentType(rs.getInt("CONTENT_TYPE"));
					resultInfo.setInfo(rs.getString("INFO"));
					try {
						resultInfo
								.setSubmitDate(rs.getTimestamp("SUBMIT_DATE"));
					} catch (Exception ex) {

					}

					try {
						resultInfo.setDoneDate(rs.getString("DONE_DATE"));
					} catch (Exception ex) {

					}

					resultInfo.setProcessResult(rs.getInt("PROCESS_RESULT"));
					resultInfo.setMsgType(rs.getInt("MESSAGE_TYPE"));
					resultInfo.setRequestID(rs.getBigDecimal("REQUEST_ID"));
					resultInfo.setMsgID(rs.getString("MESSAGE_ID"));
					resultInfo.setTotalSegments(rs.getInt("TOTAL_SEGMENTS"));
					resultInfo.setRetriesNumber(rs.getInt("RETRIES_NUM"));
					resultInfo.setInsertDate(rs.getTimestamp("INSERT_DATE"));
					resultInfo.setNotes(rs.getString("NOTES"));
					resultInfo.setServiceName(rs.getString("SERVICE_NAME"));
					resultInfo.setChannelType(rs.getInt("CHANNEL_TYPE"));
					resultInfo.setContendID(rs.getString("CONTENT_ID"));
					resultInfo.setAmount(rs.getInt("AMOUNT"));
					resultInfo.setReslultCharge(rs.getInt("RESULT_CHARGE"));
					resultInfo.setDgAmount(rs.getString("DGAMOUNT"));
					resultInfo.setTimeSendMO(rs.getString("TIME_SEND_MO"));

					arrResult.add(resultInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("DGCommon - getDGChargeResult. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DGCommon - getDGChargeResult. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrResult;
	}

	public int deleteTableByID(int id, String tableName) {
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("DAUGIA-@@-DBDelete@deleteTableByID \tID:" + id);
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DAUGIA-@@-DBDelete@deleteTableByID: Error connection == null");
				iReturn = -1;
			}

			sqlString = "DELETE FROM " + tableName + " WHERE ID = " + id;

			statement = connection.prepareStatement(sqlString);

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DAUGIA deleteTableByID Error@ID=" + id
						+ ";; table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger.crisis("DAUGIA deleteTableByID: Error@ID=" + id
					+ ";; table name = " + tableName);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.crisis("DAUGIA deleteTableByID:: Error@ID=" + id
					+ ";; table name = " + tableName);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}

	public int deleteTableByUserID(String userId, String tableName) {
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("DAUGIA-@@-DBDelete@deleteTableByID \tuser_ID:"
				+ userId + "; Table = " + tableName);
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DAUGIA-@@-DBDelete@deleteTableByID: Error connection == null");
				iReturn = -1;
			}

			sqlString = "DELETE FROM " + tableName + " WHERE user_id = '"
					+ userId + "'";

			statement = connection.prepareStatement(sqlString);

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DAUGIA deleteTableByID Error@userId="
						+ userId + ";; table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger.error("DAUGIA deleteTableByID: Error@userId =" + userId
					+ ";; table name = " + tableName + ";; ERORR ="
					+ e.getMessage());
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.error("DAUGIA deleteTableByID: Error@userId =" + userId
					+ ";; table name = " + tableName + ";; ERORR ="
					+ e.getMessage());
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}

	public int deleteTable(String tableName) {
		int iReturn = 1;
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("DAUGIA-@@-DBDelete@deleteTable \t table: "
				+ tableName);
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("DAUGIA-@@-DBDelete@deleteTable: Error connection == null");
				iReturn = -1;
			}

			sqlString = "DELETE FROM " + tableName;

			statement = connection.prepareStatement(sqlString);

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DAUGIA deleteTableByID Error@"
						+ " table name = " + tableName);
				iReturn = -1;
			}

		} catch (SQLException e) {
			Util.logger.crisis("DAUGIA deleteTableByID Error@"
					+ " table name = " + tableName);
			iReturn = -1;
		} catch (Exception e) {
			Util.logger.crisis("DAUGIA deleteTableByID Error@"
					+ " table name = " + tableName);
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}

	public String getCurrYearMonth() {
		DateFormat formatter = new SimpleDateFormat("yyyyMM");
		java.util.Date today = new java.util.Date();
		String currHour = formatter.format(today);
		return currHour;
	}

	public ArrayList<MlistInfo> getMlistInfo(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " WHERE ACTIVE = 0 LIMIT 100";

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

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}

	public ArrayList<MlistInfo> getAllMlistInfoDaily(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " where ACTIVE = 0";

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

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}

	public ArrayList<MlistInfo> getAllMlistInfo(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName;

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

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - GET ARRAY MLIST WHERE ACTIVE = 0 - getMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}

	public ArrayList<MlistInfo> getMlistToPushMTAdvertise(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName + " WHERE IS_PUSH_AD = 0 LIMIT 100";

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

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("DAUGIA - getMlistToPushMTAdvertise. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DAUGIA - getMlistToPushMTAdvertise. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}

	public int updateSendWeeklyMT(int isend, int id) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + DGConstants.TABLE_DG_SANPHAM
				+ " SET IS_SENT_MT_WEEKLY = " + isend + " WHERE ID = " + id;

		Util.logger.info("@updateSendWeeklyMT@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger
						.error("updateSendWeeklyMT@"
								+ ": uppdate Statement: UPDATE  "
								+ DGConstants.TABLE_DG_SANPHAM + " Failed:"
								+ sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateSendWeeklyMT@: UPDATE  "
					+ DGConstants.TABLE_DG_SANPHAM + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public DGAmount getUserIdWin(String maSP) {

		DGAmount dgObj = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT ID, USER_ID, SERVICE_ID, TIME_SEND_MO, DG_AMOUNT, MA_SP"
				+ " FROM daugia_trungthuong WHERE MA_SP = '"
				+ maSP
				+ "' AND WIN_RANK = " + DGConstants.WIN_RANK_FIRST;
		// System.out.println("getMlistTableName: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					dgObj = new DGAmount();
					dgObj.setId(rs.getInt("ID"));
					dgObj.setUserId(rs.getString("USER_ID"));
					dgObj.setServiceId(rs.getString("SERVICE_ID"));
					dgObj.setTimeSendMO(rs.getString("TIME_SEND_MO"));
					dgObj.setDgAmount(rs.getString("DG_AMOUNT"));
					dgObj.setMaSP(rs.getString("MA_SP"));
				}
			} else {
				Util.logger.error("DAUGIA - getUserIdWin : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - getUserIdWin. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getUserIdWin. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return dgObj;
	}

	public int resetMlistActive(String tableMlist) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET ACTIVE = 0";

		Util.logger.info("@updateMlistActive1@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("ResetDGMList@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("ResetDGMList@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public int updateIsPushAD(String tableMlist, int pushAd, int id) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET IS_PUSH_AD = "
				+ pushAd + " WHERE ID = " + id;

		Util.logger.info("@updateIsPushAD@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateIsPushAD@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateIsPushAD@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public int resetIsPushAD(String tableMlist, int pushAd) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET IS_PUSH_AD = "
				+ pushAd;

		Util.logger.info("@updateIsPushAD@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateIsPushAD@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateIsPushAD@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public int resetIsPushAD(String tableMlist, int pushAd, String userId,
			String commandCode) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET IS_PUSH_AD = "
				+ pushAd + " WHERE USER_ID = '" + userId
				+ "' AND COMMAND_CODE = '" + commandCode + "'";

		Util.logger.info("@updateIsPushAD@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateIsPushAD@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateIsPushAD@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * insert into mlist_service
	 * 
	 * @param msg
	 */
	public int insertMlistService(MsgObject msg, Keyword keyword,
			String tblMlist) {

		String tableName = tblMlist;
		ServiceMng serviceMng = new ServiceMng();
		serviceMng.deleteInMlist(tableName, msg.getUserid(), msg
				.getCommandCode(), msg.getServiceid());

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		int msg1mt = 1; // Message Type
		String mtfree = "0";

		String sqlInsert = "INSERT INTO "
				+ tableName
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,ACTIVE,CHANNEL_TYPE,REG_COUNT, IS_PUSH_AD) values ('"
				+ msg.getUserid()
				+ "','"
				+ msg.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ msg.getOption()
				+ "',"
				+ 0
				+ ",'"
				+ msg.getLast_code()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + msg.getCommandCode()
				+ "','" + msg.getLongRequestid() + "','" + msg1mt + "','"
				+ msg.getMobileoperator() + "','" + 0 + "'," + mtfree + ","
				+ keyword.getDuration() + "," + keyword.getAmount() + ","
				+ msg.getContentId() + ",'" + msg.getCommandCode() + "','"
				+ msg.getCompany_id() + "'," + 0 + "," + msg.getChannelType()
				+ "," + 1 + ", 1 )";

		Util.logger.info("@insertMlistService@SQL Insert: " + sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("insertMlistService@"
						+ ": uppdate Statement: Insert  " + tableName
						+ " Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("insertMlistService@:Insert  " + tableName
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public String formatDate(String strDate) {
		String strResult = "";

		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

		Date date = null;
		try {
			date = (Date) format1.parse(strDate);
			strResult = format2.format(date);
		} catch (ParseException e) {
			Util.logger.info("DAUGIA formatDate: ERROR, String Date "
					+ "khong dung dinh dang strDate Input = " + strDate);
		}

		return strResult;
	}

	public int insertWinnerTmp(String userId, String amount, String maSp,
			int typeWin) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO daugia_winner_tmp"
				+ "(user_id,amount,MA_SP,type)" + " VALUES(?,?,?,?)";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, userId);
			stmt.setString(2, amount);
			stmt.setString(3, maSp);
			stmt.setInt(4, typeWin);

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@DaugiaCommon insertWinnerTmp: Error@userid="
								+ userId + "; amount = " + amount);
			}

			Util.logger
					.info("@DaugiaCommon InsertDGCharge SUCCESSFUL !!! \n @ Query = "
							+ sqlQuery);

			return 1;
		} catch (SQLException ex3) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

	public Boolean isExistAmount(String userId, String amount, String maSp) {

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM daugia_amount"
				+ " WHERE user_id ='" + userId + "'" + " AND DG_AMOUNT = '"
				+ amount + "'"; // + " AND MA_SP = '" + maSp +"'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("DAUGIA - isExistAmount : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - isExistAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - isExistAmount. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
	}

	public Boolean isExistDG(String userId) {

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM daugia_amount"
				+ " WHERE user_id ='" + userId + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("DAUGIA - isExistDG : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - isExistDG. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - isExistDG. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
	}

	public Boolean isInMaxSession(String userId) {

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM "
				+ " daugia_max_session WHERE " + " user_id = '" + userId + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("DAUGIA - isInMaxSession : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - isInMaxSession. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - isInMaxSession. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;

	}

	/**
	 * 
	 * @param dateTime
	 *            format dang yyyy-MM-dd HH:mm:ss
	 * @return 14h30
	 */
	public String getHour(String dateTime) {
		String hour = dateTime.substring(11, 13).trim();
		String minute = dateTime.substring(14, 16).trim();
		if (!minute.equals("00")) {
			hour = hour + "h" + minute;
		} else {
			hour = hour + "h";
		}
		return hour;
	}

	public SanPhamDG getNextSP() {

		SanPhamDG spObj = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT * FROM daugia_sanpham "
				+ " WHERE IS_SENT_MT_WEEKLY = 0 AND START_DATE > '"
				+ getCurrentDate(0) + "'" + " ORDER BY START_DATE ASC LIMIT 1";

		Util.logger.info("DAUGIA getNextSP sql = " + sqlQuery);
		// System.out.println("getSPExpiredDateLastest sqlQuerry = " +
		// sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					spObj = new SanPhamDG();
					spObj.setId(rs.getInt("ID"));
					spObj.setMaSP(rs.getString("MA_SP"));
					spObj.setTenSP(rs.getString("TEN_SP"));
					spObj.setStartDate(rs.getString("START_DATE"));
					spObj.setEndDate(rs.getString("END_DATE"));
					spObj.setImageLink(rs.getString("IMAGE_LINK"));
					spObj.setMotaSP(rs.getString("MOTA_SP"));
					spObj.setIsSendMTWeekly(rs.getInt("IS_SENT_MT_WEEKLY"));
					break;
				}
			} else {
				Util.logger.error("DAUGIA - getNextSP : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - getNextSP. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getNextSP. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return spObj;
	}

	public ArrayList<DGAmount> getUserWinThird(String userIdFirst,
			String userIdSecond) {

		int numberCount = 0;
		int maxNumberCount = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT user_id,COUNT(*) AS numberCount,MA_SP,time_send_mo "
				+ " FROM daugia_amount WHERE user_id <> '"
				+ userIdFirst
				+ "' AND user_id <> '"
				+ userIdSecond
				+ "' "
				+ "  GROUP BY user_id " + "  ORDER BY numberCount DESC limit 2";

		Util.logger.info("DaugiaCommon getUserWinThird "
				+ "; Find max number DAUGIA, sql = " + sqlQuery);
		// System.out.println("getSPExpiredDateLastest sqlQuerry = " +
		// sqlQuery);
		// Find MAX Number DAUGIA
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				int i = 0;
				while (rs.next()) {

					int count = rs.getInt("numberCount");

					if (i == 1) {
						numberCount = count;
					}

					if (i == 0) {
						maxNumberCount = count;
					}
					i = i + 1;

				}
			} else {
				Util.logger
						.error("DaugiaCommon getUserWinThird: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DaugiaCommon getUserWinThird. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DaugiaCommon getUserWinSecond. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return findUserThird(sqlQuery, maxNumberCount, numberCount);

	}

	public ArrayList<DGAmount> findUserThird(String sqlQuery,
			int maxNumberCount, int numberCount) {

		ArrayList<DGAmount> arrThird = new ArrayList<DGAmount>();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// Find user win
		String sqlFind = "SELECT * FROM (" + sqlQuery
				+ ")t1 where t1.numberCount IN " + "(" + maxNumberCount + ","
				+ numberCount + ") ORDER BY time_send_mo ASC limit 2";

		Util.logger.info("DaugiaCommon getUserWinThird "
				+ "; Find USER GIAI BA, sql = " + sqlFind);

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlFind,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();

				while (rs.next()) {
					DGAmount dgWinThird = new DGAmount();
					dgWinThird = new DGAmount();
					dgWinThird.setUserId(rs.getString("user_id"));
					dgWinThird.setNumberDG(rs.getInt("numberCount"));
					dgWinThird.setMaSP(rs.getString("MA_SP"));

					arrThird.add(dgWinThird);

				}
			} else {
				Util.logger
						.error("DaugiaCommon getUserWinThird: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DaugiaCommon getUserWinThird. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DaugiaCommon getUserWinSecond. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrThird;

	}

	public DGAmount getUserWinSecond(String userIdFirst, String maSP) {

		return findWinSecond(getArrayUserWinSeconds(userIdFirst, maSP));

	}

	public ArrayList<DGAmount> getArrayUserWinSeconds(String userIdFirst,
			String maSP) {

		ArrayList<DGAmount> arrWinSeconds = new ArrayList<DGAmount>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		int numberCount = -1;

		String sqlQuery = " SELECT user_id,COUNT(*) AS numberCount,MA_SP  "
				+ " FROM daugia_winner_tmp " + " WHERE user_id <> '"
				+ userIdFirst + "' AND MA_SP = '" + maSP + "'"
				+ " GROUP BY user_id ORDER BY numberCount DESC ";

		Util.logger.info("DaugiaCommon getArrayUserWinSeconds sql = "
				+ sqlQuery);
		// System.out.println("getSPExpiredDateLastest sqlQuerry = " +
		// sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					DGAmount dgWinSecond = new DGAmount();
					dgWinSecond.setUserId(rs.getString("user_id"));
					dgWinSecond.setNumberWinnerTmp(rs.getInt("numberCount"));
					dgWinSecond.setMaSP(rs.getString("MA_SP"));

					if (numberCount < 0) {
						numberCount = dgWinSecond.getNumberWinnerTmp();
					}
					if (dgWinSecond.getNumberWinnerTmp() == numberCount) {
						arrWinSeconds.add(dgWinSecond);
					} else {

						break;
					}

				}
			} else {
				Util.logger
						.error("DaugiaCommon getArrayUserWinSeconds: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DaugiaCommon getArrayUserWinSeconds. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DaugiaCommon getArrayUserWinSeconds. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrWinSeconds;

	}

	public DGAmount findWinSecond(ArrayList<DGAmount> arrSecond) {

		if (arrSecond == null) {
			DGAmount dgwin = new DGAmount();
			return dgwin;
		}

		if (arrSecond.size() == 0) {
			DGAmount dgwin = new DGAmount();
			return dgwin;
		}

		DGAmount dgWin = arrSecond.get(0);
		if (arrSecond.size() == 1) {
			return dgWin;
		}

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = " SELECT user_id,COUNT(*) AS number,time_send_mo "
				+ " FROM daugia_amount " + " WHERE user_id IN (";

		for (int i = 0; i < arrSecond.size(); i++) {
			DGAmount dgTmp = arrSecond.get(i);
			if (i > 0) {
				sqlQuery = sqlQuery + ",";
			}
			sqlQuery = sqlQuery + "'" + dgTmp.getUserId() + "'";
		}

		sqlQuery = sqlQuery + ") GROUP BY user_id ORDER BY number DESC";

		Util.logger.info("DaugiaCommon getArrayUserWinSeconds sql = "
				+ sqlQuery);
		// System.out.println("getSPExpiredDateLastest sqlQuerry = " +
		// sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				int i = 0;
				while (rs.next()) {
					if (i == 0) {
						dgWin.setUserId(rs.getString("user_id"));
						dgWin.setNumberDG(rs.getInt("number"));
						dgWin.setTimeSendMO(rs.getString("time_send_mo"));
					} else {

						int numberDG = rs.getInt("number");
						if (numberDG < dgWin.getNumberDG())
							break;

						String timeSendMO = rs.getString("time_send_mo");
						if (timeSendMO.compareTo(dgWin.getTimeSendMO()) < 0) {
							dgWin.setUserId(rs.getString("user_id"));
							dgWin.setNumberDG(numberDG);
							dgWin.setTimeSendMO(timeSendMO);
						}

					}

					i = i + 1;

				}
			} else {
				Util.logger
						.error("DaugiaCommon getArrayUserWinSeconds: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("DaugiaCommon getArrayUserWinSeconds. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("DaugiaCommon getArrayUserWinSeconds. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return dgWin;
	}

	/**
	 * check valid MO from daugia_blacklist
	 * 
	 * @param userId
	 * @return true if mo valid false if number mo of user is too much in
	 *         duration time
	 */
	public Boolean isValidMOBlackList(String userId) {

		String currTime = getCurrentDate(0);
		String timeBefore = getCurrentDate((-1)
				* DGConstants.DAUGIA_DURATION_TIME_CHECK_BLACK_LIST);

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT count(*) as numberCount FROM daugia_blacklist "
				+ "WHERE time_send_mo > '"
				+ timeBefore
				+ "'"
				+ "AND time_send_mo < '" + currTime + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("numberCount");
				}
			} else {
				Util.logger
						.error("DAUGIA - isValidMOBlackList : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - isValidMOBlackList. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - isValidMOBlackList. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count < DGConstants.DAUGIA_NUMBERMO_INVALID) {
			check = true;
		}

		return check;

	}

	public String getCurrentDate(int minutes) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minutes);
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now
				.getTime());
	}

	public void insertDaugiaBlackList(String userId, String timeSendMO) {

		String sqlInsert = "INSERT INTO daugia_blacklist"
				+ "(user_id,time_send_mo)" + "VALUES('" + userId + "'," + "'"
				+ timeSendMO + "');";

		DBUtil.executeSQL("gateway", sqlInsert);

	}

	public String getContentPush(int typeContent) {

		String content = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT NOIDUNG FROM daugia_noidung_push WHERE TYPE = "
				+ typeContent;

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					content = rs.getString("NOIDUNG");

				}
			} else {
				Util.logger.error("PushMT - getContentManual: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("PushMT - getContentManual: SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("PushMT - getContentManual: SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return content;
	}

	public Boolean isFreeList(String userId) {

		Boolean check = false;
		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM daugia_freelist WHERE "
				+ " user_id = '" + userId + "'";
		// System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger.error("DAUGIA - isFreeList : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - isFreeList. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - isFreeList. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		// System.out.println("count = " + count);
		if (count > 0) {
			check = true;
		}

		return check;
	}

	public int getNumberRecord(String tableName) {

		int numberRecord = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT count(*) as number FROM " + tableName;

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					numberRecord = rs.getInt("number");

				}
			} else {
				Util.logger.error("getNumberRecord: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("getNumberRecord: SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("getNumberRecord: SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return numberRecord;
	}

	// get keyword

	public String getKeyword(String info) {

		String strKey = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT keyword FROM keywords WHERE service_name ="
				+ " 'DAUGIA' AND keyword LIKE 'DG%' ORDER BY keyword DESC";

		try {

			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					strKey = rs.getString("keyword");
					if (info.toUpperCase().startsWith(strKey))
						break;
				}
			} else {
				Util.logger.error("getKeyword: execute Error!!");
			}

		} catch (SQLException ex3) {
			Util.logger.error("getKeyword: SQLException:" + ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("getKeyword: SQLException:" + ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return strKey;

	}

	public static int InsertWinnerByDays(String userId, String dgAmount,
			String mtPush, String maSP) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO `winner_by_days`(`user_id`,`dg_amount`,`mt_push`,`ma_sp`)"
				+ "VALUES('"
				+ userId
				+ "','"
				+ dgAmount
				+ "','"
				+ mtPush
				+ "','" + maSP + "');";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@DaugiaCommon InsertDGCharge: Error@userid="
								+ userId + "@dgAmount=" + dgAmount + "@mtPush="
								+ mtPush + "@maSP=" + maSP + "\t@sql:"
								+ sqlQuery);
				return -1;
			}

			Util.logger
					.info("@DaugiaCommon InsertWinnerByDays SUCCESSFUL !!! \n @ Query = "
							+ sqlQuery);

			return 1;
			
		} catch (SQLException ex3) {
			Util.logger.error("@DaugiaCommon InsertWinnerByDays. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

}
