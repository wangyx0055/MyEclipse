package icom.gateway;

import icom.common.Queue;
import icom.common.Utilities;
import icom.gateway.Constants;
import icom.gateway.EMSData;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.sql.*;
import java.math.*;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class DBTools {
	// private Logger logger = new Logger("DBTools");
	private Utilities util = null;

	public DBTools() {
		util = new Utilities();
	}

	// ==========================================================================

	public static int add2SMSReceiveQueueR(String userId, String serviceId,
			String mobileOperator, String commandCode, String info,
			String RequestID, String DestinationPort) throws DBException {
		DBException exception = null;
		int id = -1;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		String cmdcode = "INV";

		if (!Preference.CheckServiceId(serviceId)) {
			return 1;
		}
		try {

			conn = getConnection(Preference.LIST_DB_RECEIVE_QUEUE[Preference
					.getIDdbReceiveQueue(userId)]);

			Timestamp time = new Timestamp(System.currentTimeMillis());
			int NOT_RESPONDED = 0;

			strSQL = "INSERT INTO sms_receive_queue (USER_ID, SERVICE_ID, "
					+ "MOBILE_OPERATOR, COMMAND_CODE, INFO, RECEIVE_DATE, RESPONDED,REQUEST_ID,DPORT) "
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?,?,?)";

			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, userId);
			preStmt.setString(2, serviceId);
			preStmt.setString(3, mobileOperator);
			preStmt.setString(4, cmdcode);
			preStmt.setString(5, info);
			preStmt.setTimestamp(6, time);
			preStmt.setInt(7, NOT_RESPONDED);
			preStmt.setString(8, RequestID);
			preStmt.setString(9, DestinationPort);
			if (preStmt.executeUpdate() != 1) {
				conn.rollback();
				Logger.error("DBTools.add2SMSReceiveQueue",
						"DBTools.add2SMSReceiveQueue(): Error adding row");

				DBTools
						.ALERT("DBTools", "add2SMSReceiveQueue",
								Constants.ALERT_WARN, Preference.Channel
										+ "@Error adding row",
								Preference.ALERT_CONTACT);
				EMSData ems = new EMSData();
				ems.setUserId(userId);
				ems.setServiceId(serviceId);
				ems.setMobileOperator(mobileOperator);
				ems.setCommandCode(cmdcode);
				ems.setText(info);
				ems.setsRequestID(RequestID);
				Gateway.saveSMSObject(RequestID + ".modb", ems);
			} else {

				Logger.info("DBTools.add2SMSReceiveQueue",
						"{Add-MO2DB}{Request_ID=" + RequestID + "}{User_ID="
								+ userId + "}{Service_ID=" + serviceId
								+ "}{Info=" + info + "}");
				// /BigDecimal a = new BigDecimal(1);
				id = 1;
			}
		} catch (SQLException e) {
			Logger.error("DBTools.add2SMSReceiveQueue", "Error executing "
					+ strSQL + " >>> " + e.getMessage());
			DBTools.ALERT("DBTools", "add2SMSReceiveQueue",
					Constants.ALERT_WARN, Preference.Channel
							+ "@Error adding row:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			// Gateway.util.logMO(userId + "," + serviceId + "," +
			// mobileOperator + "," +
			// commandCode + "," + info);
			EMSData ems = new EMSData();
			ems.setUserId(userId);
			ems.setServiceId(serviceId);
			ems.setMobileOperator(mobileOperator);
			ems.setCommandCode(cmdcode);
			ems.setText(info);
			ems.setsRequestID(RequestID);
			Gateway.saveSMSObject(RequestID + ".modb", ems);
			if (e.getMessage().startsWith("ORA-03114")) { // ORA-03114: not
				// connected to
				// ORACLE
				exception = new DBException(e.getMessage());
			}
		} catch (Exception e) {
			Logger.error("DBTools.add2SMSReceiveQueue", "Exception: "
					+ e.getMessage());

			DBTools.ALERT("DBTools", "add2SMSReceiveQueue",
					Constants.ALERT_WARN, Preference.Channel
							+ "@Error adding row:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			EMSData ems = new EMSData();
			ems.setUserId(userId);
			ems.setServiceId(serviceId);
			ems.setMobileOperator(mobileOperator);
			ems.setCommandCode(cmdcode);
			ems.setText(info);
			ems.setsRequestID(RequestID);
			Gateway.saveSMSObject(RequestID + ".modb", ems);
			// System.out.println("DBTools.add2SMSReceiveQueue:" +
			// e.getMessage());
			// Gateway.util.logMO(userId + "," + serviceId + "," +
			// mobileOperator + "," +
			// commandCode + "," + info);
		} finally {

			// Gateway.dbPool.enqueue(conn);
			// util.cleanup(rs);
			try {
				if (rs != null) {

					rs.close();
				}
				if (preStmt != null) {
					preStmt.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException ex1) {
			}
		}

		// util.cleanup(conn, preStmt);
		if (exception != null) {
			throw exception;
		}
		
		return id;
		
	}

	static String whereClause = null;

	public static int ALERT(String domain, String issue, String level,
			String alertmsg, String contact) {

		Logger.info("ALERT:" + "{domain=" + domain + "}{issue=" + issue
				+ "}{level=" + level + "}{domain=" + level + "}{alertmsg="
				+ alertmsg + "}{contact=" + contact + "}");
		if (Gateway.running != true) {
			return 1;
			// Gateway.running
		}

		if (Preference.ALERT == 1) {
			Connection connection = null;
			PreparedStatement stmt = null;
			Utilities util = new Utilities();
			String newissue = issue;
			String newalertmsg = alertmsg;
			String newdomain = Preference.Channel + "@" + domain;
			if (issue.length() > 50) {
				newissue = issue.substring(0, 49);
			}
			if (alertmsg.length() > 200) {
				newalertmsg = alertmsg.substring(0, 199);
			}
			String sSQL = "insert into msg_alerter( domain, issue, level,alertmsg,contact) "
					+ "values(?,?,?,?,?)";
			try {
				if (connection == null)
					connection = getConnection(Preference.LIST_DB_ALERT[0]
							.trim());
				stmt = connection.prepareStatement(sSQL);
				stmt.setString(1, newdomain);
				stmt.setString(2, newissue);
				stmt.setString(3, level);
				stmt.setString(4, newalertmsg);
				stmt.setString(5, contact);
				if (stmt.executeUpdate() == -1) {
					return -1;
				}
				return 1;
			} catch (Exception ex) {
				Logger.error("DBTools", "{Alert Error:}" + ex.getMessage());

				return -1;
			} finally {
				util.cleanup(connection, stmt);
			}
		} else {
			return 0;
		}
	}

	// Them truc tiep vao Queue
	public boolean getAllEMSSendQueue(String sRound, String iMod, String iNum,
			Queue Ems, int idDB) throws DBException {
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		Keyword keyword = null;
		// Vector keys = new Vector();
		String sR = "";
		try {

			sR = Preference.getTableSendQueue(idDB);

			conn = getConnection(Preference.LIST_DB_SEND_QUEUE[idDB]);
			Timestamp ts = new Timestamp(System.currentTimeMillis());

			// strSQL = "select id, user_id, service_id, mobile_operator,
			// command_code, content_type, "
			// + "info, submit_date, done_date, process_result, message_type, "
			// + "request_id, message_id, total_segments,cpid "
			// + "from ems_send_queue"
			// + sR
			// + " where info is not null "
			// + "and (mod(id," + iMod + ") = " + iNum + ")";
			strSQL = "select id, user_id, service_id, mobile_operator, command_code, content_type, "
					+ "info,  process_result, message_type, "
					+ "request_id, message_id, total_segments,cpid "
					+ "from ems_send_queue"
					+ sR
					+ " where  info is not null "
					+ "and (mod(id,"
					+ iMod
					+ ") = "
					+ iNum
					+ ") and upper(mobile_operator)=upper('"
					+ Preference.mobileOperator + "') and service_id in ("
										+ Preference.strLIST_SERVICEID + ")";

			preStmt = conn.prepareStatement(strSQL,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (preStmt.execute()) {
				rs = preStmt.getResultSet();
				while (rs.next()) {
					EMSData ems = new EMSData();
					ems.setId(rs.getBigDecimal(1));
					ems.setUserId(rs.getString(2));
					ems.setServiceId(rs.getString(3));
					ems.setMobileOperator(rs.getString(4));
					ems.setCommandCode(rs.getString(5));
					ems.setContentType(rs.getInt(6));
					ems.setText(rs.getString(7));
					// ts
					// ems.setSubmitDate(rs.getTimestamp(8));
					ems.setSubmitDate(ts);
					// ems.setDoneDate(rs.getTimestamp(9));
					ems.setDoneDate(ts);
					ems.setProcessResult(rs.getInt(8));
					ems.setMessageType(rs.getInt(9));
					ems.setRequestId(rs.getBigDecimal(10));
					ems.setMessageId(rs.getString(11));
					ems.setTotalSegments(rs.getInt(12));

					String commandcode = rs.getString(5);
					String serviceid = rs.getString(3);
					if (Preference.CheckServiceId(serviceid) & Gateway.bound) {

						if ("1".equalsIgnoreCase(Preference.MAPCP)) {
							keyword = Gateway.loadconfig.getKeyword(
									commandcode, serviceid);
							ems.setCpid(keyword.getCpid());
						} else {
							ems.setCpid(rs.getInt(13));
						}

						try {
							Logger.info(this.getClass().getName(),
									"{getMTfromDB}{Request_ID="
											+ ems.getRequestId() + "}{User_ID="
											+ ems.getUserId() + "}");
							rs.deleteRow();
							Ems.enqueue(ems);
							// Ems.enqueuecongt(ems);
						} catch (Exception ex) {
							Logger.error("DBTools",
									"{getAllEMSSendQueue: Error executing SQL }{getMTfromDB}{Request_ID="
											+ ems.getRequestId() + "}{User_ID="
											+ ems.getUserId() + "}" + strSQL
											+ ": " + ex.toString());
							DBTools.ALERT("DBTools", "getAllEMSSendQueue",
									Constants.ALERT_WARN, Preference.Channel
											+ "@getAllEMSSendQueue:"
											+ ex.getMessage(),
									Preference.ALERT_CONTACT);
							Logger.printStackTrace(ex);
						}
					}
				}
			}
			return true;
		} catch (SQLException e) {
			Logger.error("DBTools", "{getAllEMSSendQueue: SQLException}"
					+ e.getMessage());
			Logger.error("DBTools",
					"{getAllEMSSendQueue: Error executing SQL }" + strSQL
							+ ": " + e.toString());
			DBTools.ALERT("DBTools", "getAllEMSSendQueue",
					Constants.ALERT_WARN, Preference.Channel
							+ "@getAllEMSSendQueue:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);

			return true;
		} catch (Exception e) {
			Logger.error("DBTools", "{getAllEMSSendQueue: Exception}"
					+ e.getMessage());

			DBTools.ALERT("DBTools", "getAllEMSSendQueue",
					Constants.ALERT_WARN, Preference.Channel
							+ "@getAllEMSSendQueue:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return true;
		} finally {
			// releaseConnection(conn, preStmt, rs);
			util.cleanup(rs);
			util.cleanup(conn, preStmt);
			if (exception != null) {
				throw exception;
			}

		}
	}

	public int add2EMSSendLog(EMSData ems) {
		if (ems == null) {
			return 0;
		}

		int result = 0;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try {
			conn = getConnection(Preference.LIST_DB_SEND_LOG[0].trim());

			String tablename = "ems_send_log"
					+ new SimpleDateFormat("yyyyMM").format(new Date());
			strSQL = "insert into "
					+ tablename
					+ " ( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,NOTES,cpid) "
					+ "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

			preStmt = conn.prepareStatement(strSQL);

			preStmt.setString(1, ems.getUserId());
			preStmt.setString(2, ems.getServiceId());
			preStmt.setString(3, ems.getMobileOperator());
			preStmt.setString(4, ems.getCommandCode());
			preStmt.setInt(5, ems.getContentType());
			preStmt.setString(6, ems.getText());
			preStmt.setTimestamp(7, ems.getSubmitDate());
			preStmt.setTimestamp(8, ems.getDoneDate());
			preStmt.setInt(9, ems.getProcessResult());
			preStmt.setInt(10, ems.getMessageType());
			preStmt.setBigDecimal(11, ems.getRequestId());
			preStmt.setString(12, ems.getMessageId());
			preStmt.setInt(13, ems.getTotalSegments());
			preStmt.setInt(14, ems.getSendNum());
			preStmt.setString(15, ems.getNotes());
			preStmt.setInt(16, ems.getCpid());
			result = preStmt.executeUpdate();
			return result;

		} catch (SQLException e) {
			Logger.error(this.getClass().getName(),
					"SQLException: Error executing SQL " + strSQL + ">>>"
							+ e.getMessage());
			Gateway.saveSMSObject(ems.getId() + ".slog", ems);
			DBTools.ALERT("DBTools", "add2EMSSendLog", Constants.ALERT_WARN,
					Preference.Channel + "@add2EMSSendLog:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return result;
		} catch (Exception e) {
			Logger.error(this.getClass().getName(), "Exception: "
					+ e.toString());
			Gateway.saveSMSObject(ems.getId() + ".slog", ems);
			DBTools.ALERT("DBTools", "add2EMSSendLog", Constants.ALERT_WARN,
					Preference.Channel + "@add2EMSSendLog:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return result;
		} finally {
			util.cleanup(rs);
			util.cleanup(conn, preStmt);

		}
	}

	public int add2BlacklistLog(EMSData ems) {
		if (ems == null) {
			return 0;
		}

		int result = 0;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try {
			conn = getConnection(Preference.LIST_DB_SEND_LOG[0].trim());

			String tablename = "ems_send_blacklist";
			strSQL = "insert into "
					+ tablename
					+ " ( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, RETRIES_NUM,NOTES,cpid) "
					+ "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

			preStmt = conn.prepareStatement(strSQL);

			preStmt.setString(1, ems.getUserId());
			preStmt.setString(2, ems.getServiceId());
			preStmt.setString(3, ems.getMobileOperator());
			preStmt.setString(4, ems.getCommandCode());
			preStmt.setInt(5, ems.getContentType());
			preStmt.setString(6, ems.getText());
			preStmt.setTimestamp(7, ems.getSubmitDate());
			preStmt.setTimestamp(8, ems.getDoneDate());
			preStmt.setInt(9, ems.getProcessResult());
			preStmt.setInt(10, ems.getMessageType());
			preStmt.setBigDecimal(11, ems.getRequestId());
			preStmt.setString(12, ems.getMessageId());
			preStmt.setInt(13, ems.getTotalSegments());
			preStmt.setInt(14, ems.getSendNum());
			preStmt.setString(15, ems.getNotes());
			preStmt.setInt(16, ems.getCpid());
			result = preStmt.executeUpdate();
			return result;

		} catch (SQLException e) {
			Logger.error(this.getClass().getName(),
					"SQLException: Error executing SQL " + strSQL + ">>>"
							+ e.getMessage());
			Gateway.saveSMSObject(ems.getId() + ".blist", ems);
			DBTools.ALERT("DBTools", "add2BlacklistLog", Constants.ALERT_WARN,
					Preference.Channel + "@add2BlacklistLog:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return result;
		} catch (Exception e) {
			Logger.error(this.getClass().getName(), "Exception: "
					+ e.toString());
			Gateway.saveSMSObject(ems.getId() + ".blist", ems);
			DBTools.ALERT("DBTools", "add2BlacklistLog", Constants.ALERT_WARN,
					Preference.Channel + "@add2BlacklistLog:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return result;
		} finally {
			util.cleanup(rs);
			util.cleanup(conn, preStmt);

		}
	}

	// ***************************************************************************
	// CDR LOG
	// ***************************************************************************
	// Note: submitDate and doneDate must be updated in EMSData

	public static boolean add2CdrQueueb(EMSData ems, String MessageType)
			throws DBException {
		
		if ( ems.getMobileOperator().equalsIgnoreCase("SFONE")) {
			return add2CdrLogb(ems, MessageType);
		}
		DBException exception = null;
		boolean rt = false;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		try {

			if (conn == null) {
				conn = getConnection(Preference.LIST_DB_CDR_QUEUE[0].trim());
			}

			conn.setAutoCommit(false);
			strSQL = "insert into cdr_queue ( USER_ID, SERVICE_ID, "
					+ "MOBILE_OPERATOR, COMMAND_CODE, INFO, "
					+ "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS,Message_Type,process_result,request_id,cpid) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, ems
					.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL));
			preStmt.setString(2, ems
					.getServiceIdEx(Constants.SERVICEID_FORMAT_INTERNATIONAL));
			preStmt.setString(3, ems.getMobileOperator());
			preStmt.setString(4, ems.getCommandCode());

			String info = ems.getTextEx(true);
			if (info == null) {
				info = ems.getCommandCode();
			} else if (info.length() > 20) {
				info = info.substring(0, 20);
				info = info + "...";
			}
			preStmt.setString(5, info);
			preStmt.setTimestamp(6, ems.getSubmitDate()); // YYMMDDhhmmss
			preStmt.setTimestamp(7, ems.getDoneDate()); // YYMMDDhhmmss
			preStmt.setInt(8, ems.getTotalSegments());
			preStmt.setInt(9, Integer.parseInt(MessageType));
			preStmt.setInt(10, 1);
			preStmt.setBigDecimal(11, ems.getRequestId());
			preStmt.setInt(12, ems.getCpid());

			if (preStmt.executeUpdate() > 0) {
				conn.commit();
				rt = true;
			} else {
				conn.rollback();
				rt = false;
				Logger.error("DBTools", "add2CdrQueueb");
				DBTools.ALERT("DBTools", "add2BlacklistLog",
						Constants.ALERT_WARN, Preference.Channel
								+ "@Cannot add CDRQueue",
						Preference.ALERT_CONTACT);

			}
			return rt;
		} catch (SQLException e) {
			Logger.error("DBTools.add2CdrQueue", "Error executing " + strSQL
					+ " >>> " + e.toString());
			Gateway.saveSMSObject(ems.getId() + ".cdr", ems);
			DBTools.ALERT("DBTools", "add2CdrQueueb", Constants.ALERT_WARN,
					Preference.Channel + "@add2CdrQueueb:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);

			if (e.getMessage().startsWith("ORA-03114")) { // ORA-03114: not
				// connected to
				// ORACLE
				exception = new DBException(e.getMessage());
			}
			return rt;
		} catch (Exception e) {
			Logger.error("DBTools.add2CdrQueue", "Error:" + e.toString());
			Gateway.saveSMSObject(ems.getId() + ".cdr", ems);
			DBTools.ALERT("DBTools", "add2CdrQueueb", Constants.ALERT_WARN,
					Preference.Channel + "@add2CdrQueueb:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return rt;
		} finally {
			try {
				conn.setAutoCommit(true);
				preStmt.close();
				conn.close();
			} catch (SQLException ex) {
				Logger.error("DBTools", "Error:" + ex.toString());
				Logger.printStackTrace(ex);
			}

			if (exception != null) {
				throw exception;
			}

		}
	}

	public static boolean add2CdrLogb(EMSData ems, String MessageType)
			throws DBException {
		DBException exception = null;
		boolean rt = false;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		try {

			if (conn == null) {
				conn = getConnection(Preference.LIST_DB_CDR_QUEUE[0].trim());
			}

			conn.setAutoCommit(false);
			strSQL = "insert into cdr_log" +  new SimpleDateFormat("yyyyMM").format(new Date())+" ( USER_ID, SERVICE_ID, "
					+ "MOBILE_OPERATOR, COMMAND_CODE, INFO, "
					+ "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS,Message_Type,process_result,request_id,cpid) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, ems
					.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL));
			preStmt.setString(2, ems
					.getServiceIdEx(Constants.SERVICEID_FORMAT_INTERNATIONAL));
			preStmt.setString(3, ems.getMobileOperator());
			preStmt.setString(4, ems.getCommandCode());

			String info = ems.getTextEx(true);
			if (info == null) {
				info = ems.getCommandCode();
			} else if (info.length() > 20) {
				info = info.substring(0, 20);
				info = info + "...";
			}
			preStmt.setString(5, info);
			preStmt.setTimestamp(6, ems.getSubmitDate()); // YYMMDDhhmmss
			preStmt.setTimestamp(7, ems.getDoneDate()); // YYMMDDhhmmss
			preStmt.setInt(8, ems.getTotalSegments());
			preStmt.setInt(9, Integer.parseInt(MessageType));
			preStmt.setInt(10, 1);
			preStmt.setBigDecimal(11, ems.getRequestId());
			preStmt.setInt(12, ems.getCpid());

			if (preStmt.executeUpdate() > 0) {
				conn.commit();
				rt = true;
			} else {
				conn.rollback();
				rt = false;
				Logger.error("DBTools", "add2CdrQueueb");
				DBTools.ALERT("DBTools", "add2BlacklistLog",
						Constants.ALERT_WARN, Preference.Channel
								+ "@Cannot add CDRQueue",
						Preference.ALERT_CONTACT);

			}
			return rt;
		} catch (SQLException e) {
			Logger.error("DBTools.add2CdrQueue", "Error executing " + strSQL
					+ " >>> " + e.toString());
			Gateway.saveSMSObject(ems.getId() + ".cdr", ems);
			DBTools.ALERT("DBTools", "add2CdrQueueb", Constants.ALERT_WARN,
					Preference.Channel + "@add2CdrQueueb:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);

			if (e.getMessage().startsWith("ORA-03114")) { // ORA-03114: not
				// connected to
				// ORACLE
				exception = new DBException(e.getMessage());
			}
			return rt;
		} catch (Exception e) {
			Logger.error("DBTools.add2CdrQueue", "Error:" + e.toString());
			Gateway.saveSMSObject(ems.getId() + ".cdr", ems);
			DBTools.ALERT("DBTools", "add2CdrQueueb", Constants.ALERT_WARN,
					Preference.Channel + "@add2CdrQueueb:" + e.getMessage(),
					Preference.ALERT_CONTACT);
			Logger.printStackTrace(e);
			return rt;
		} finally {
			try {
				conn.setAutoCommit(true);
				preStmt.close();
				conn.close();
			} catch (SQLException ex) {
				Logger.error("DBTools", "Error:" + ex.toString());
				Logger.printStackTrace(ex);
			}

			if (exception != null) {
				throw exception;
			}

		}
	}

	// ==========================================================================
	private void releaseConnection(Connection conn, PreparedStatement preStmt) {
		try {
			if (preStmt != null) {
				preStmt.close();
			}
		} catch (SQLException e) {
			Logger.printStackTrace(e);
		}

	}

	private void releaseConnection(Connection conn, PreparedStatement preStmt,
			ResultSet rs) {
		releaseConnection(conn, preStmt);
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			Logger.printStackTrace(e);
		}
	}

	public static Connection getConnection(String PoolName) {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx
					.lookup("java:comp/env/" + PoolName);
			conn = ds.getConnection();

		} catch (SQLException e) {
			Logger.error("DBTools", "getConnection Failed!" + e);
			Logger.printStackTrace(e);
		}

		catch (Exception e) {
			Logger.error("DBTools", "getConnection Failed!" + e);
			Logger.printStackTrace(e);
		}
		return conn;
	}

	public void cleanup(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			Logger.error("cleanup Connection" + e.toString());
		}

		catch (Exception e) {
			Logger.error("cleanup Connection,PreparedStatement" + e.toString());
		}
	}

}
