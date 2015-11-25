package com.vasc.smpp.gateway;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.math.*;

import com.vasc.common.util.Queue;
import com.vasc.smpp.cdr.CDR;
import com.vasc.smpp.cdr.MobileProfile;
import com.vasc.common.Utilities;
import com.vasc.smpp.cdr.Logger;
import com.vasc.ftp.*;
import com.vasc.ftp.io.*;
import com.vasc.common.FileTool;

import java.util.Vector;
import java.io.File;
import java.io.IOException;

import com.vasc.common.*;
import com.vasc.smpp.gateway.Preference;

public class DBTools
{
	private Utilities util = null;
	private Logger Logger;

	public DBTools()
	{
		util = new Utilities();
	}

	public BigDecimal add2SMSSendQueue(String userId, String serviceId, String mobileOperator, String commandCode, String info, int numberOfSend,
			int processResult, int messageType, BigDecimal requestId, int totalSegments, int segmentSeqnum, int moreMsgsToSend)
	{
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO SMS_SEND_QUEUE (ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,"
					+ "INFO, FIRST_SEND_TIME, LAST_SEND_TIME, NUMBER_OF_SEND, PROCESS_RESULT, MESSAGE_TYPE, "
					+ "REQUEST_ID, TOTAL_SEGMENTS, SEGMENT_SEQNUM, MORE_MSGS_TO_SEND, MESSAGE_ID) "
					+ "VALUES (S_SMS_SEND_QUEUE.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, userId);
			preStmt.setString(2, serviceId);
			preStmt.setString(3, mobileOperator);
			preStmt.setString(4, commandCode);
			preStmt.setString(5, info);
			preStmt.setTimestamp(6, time);
			preStmt.setTimestamp(7, time);
			preStmt.setInt(8, numberOfSend);
			preStmt.setInt(9, processResult);
			preStmt.setInt(10, messageType);
			preStmt.setBigDecimal(11, requestId);
			preStmt.setInt(12, totalSegments);
			preStmt.setInt(13, segmentSeqnum);
			preStmt.setInt(14, moreMsgsToSend);

			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				System.out.println("DBTools.addToSMSSendQueue(): Error adding row");
			}
			else
			{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("Select S_SMS_SEND_QUEUE.currval from dual");
				if (rs.next())
				{
					id = rs.getBigDecimal(1);
					conn.commit();
				}
				else
				{
					conn.rollback();
					System.out.println("DBTools.addToSMSSendQueue(): Error in S_SMS_SEND_QUEUE.currval !!!");
				}
			}
			conn.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.addToSMSSendQueue:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			return id;
		}
	}

	public BigDecimal add2SMSSendQueueEx(String userId, String serviceId, String mobileOperator, String commandCode, String info, int numberOfSend,
			int processResult, int messageType, BigDecimal requestId, int totalSegments, int segmentSeqnum, int moreMsgsToSend)
	{
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("Select S_SMS_SEND_QUEUE.nextval from dual");
			if (stmt.executeUpdate() < 1)
			{
				conn.rollback();
				System.out.println("DBTools.addToSMSSendQueue(): Error adding row");
				return null;
			}
			rs = stmt.getResultSet();
			if (rs.next())
			{
				id = rs.getBigDecimal(1);
			}
			else
			{
				conn.rollback();
				System.out.println("DBTools.addToSMSSendQueue(): Error in S_SMS_SEND_QUEUE.currval !!!");
				return null;
			}

			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO SMS_SEND_QUEUE (ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,"
					+ "INFO, FIRST_SEND_TIME, LAST_SEND_TIME, NUMBER_OF_SEND, PROCESS_RESULT, MESSAGE_TYPE, "
					+ "REQUEST_ID, TOTAL_SEGMENTS, SEGMENT_SEQNUM, MORE_MSGS_TO_SEND, MESSAGE_ID) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, id);
			preStmt.setString(2, userId);
			preStmt.setString(3, serviceId);
			preStmt.setString(4, mobileOperator);
			preStmt.setString(5, commandCode);
			preStmt.setString(6, info);
			preStmt.setTimestamp(7, time);
			preStmt.setTimestamp(8, time);
			preStmt.setInt(9, numberOfSend);
			preStmt.setInt(10, processResult);
			preStmt.setInt(11, messageType);
			preStmt.setBigDecimal(12, requestId);
			preStmt.setInt(13, totalSegments);
			preStmt.setInt(14, segmentSeqnum);
			preStmt.setInt(15, moreMsgsToSend);

			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				System.out.println("DBTools.addToSMSSendQueue(): Error adding row");
			}
			else
			{
				conn.commit();
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.addToSMSSendQueue:" + e.getMessage());
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException ex)
			{
			}
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			return id;
		}
	}

	// ==========================================================================
	public BigDecimal add2EMSSendQueue(String userId, String serviceId, String mobileOperator, String commandCode, int contentType, String text, byte[] bytes,
			int messageType, BigDecimal requestId)
	{
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO EMS_SEND_QUEUE (ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, CONTENT_TYPE, INFO, RAW_INFO, MESSAGE_TYPE, REQUEST_ID) " + "VALUES (S_EMS_SEND_QUEUE.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, userId);
			preStmt.setString(2, serviceId);
			preStmt.setString(3, mobileOperator);
			preStmt.setString(4, commandCode);
			preStmt.setInt(5, contentType);
			preStmt.setString(6, text);
			preStmt.setBytes(7, bytes);
			preStmt.setInt(8, messageType);
			preStmt.setBigDecimal(9, requestId);
			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				System.out.println("DBTools.add2EMSSendQueue(): Error adding row");
			}
			else
			{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("Select S_EMS_SEND_QUEUE.currval from dual");
				if (rs.next())
				{
					id = rs.getBigDecimal(1);
					conn.commit();
				}
				else
				{
					conn.rollback();
					System.out.println("DBTools.add2EMSSendQueue(): Error in S_EMS_SEND_QUEUE.currval !!!");
				}
			}
			conn.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.toString());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2EMSSendQueue:" + e.toString());
		}
		finally
		{
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			GatewayCDR.dbPool.add(conn);
			return id;
		}
	}

	public boolean removeFromSMSSendQueue(BigDecimal id)
	{
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		boolean result = false;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "DELETE FROM SMS_SEND_QUEUE WHERE ID = ?";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, id);
			if (preStmt.executeUpdate() < 1)
			{
				System.out.println("Error deleting row");
			}
			else
			{
				result = true;
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.removeFromSMSSendQueue:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(preStmt, null);
			return result;
		}
	}

	static String whereClause = null;

	public Collection getAllSMSesInSendQueue() throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		SMSData sms = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();

			if (whereClause == null)
			{
				whereClause = "";
				for (Iterator it = Preference.sourceAddressList.iterator(); it.hasNext();)
				{
					String sAddress = (String) it.next();
					whereClause += "SERVICE_ID LIKE '%" + sAddress + "%'";
					if (it.hasNext())
					{
						whereClause += " OR ";
					}
				}
			}
			strSQL = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO, "
					+ "FIRST_SEND_TIME, LAST_SEND_TIME, NUMBER_OF_SEND, PROCESS_RESULT, " + "MESSAGE_TYPE, REQUEST_ID, TOTAL_SEGMENTS, SEGMENT_SEQNUM, "
					+ "MORE_MSGS_TO_SEND, MESSAGE_ID FROM SMS_SEND_QUEUE WHERE " + whereClause;
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				sms = new SMSData();
				sms.setId(rs.getBigDecimal(1));
				sms.setUserId(rs.getString(2));
				sms.setServiceId(rs.getString(3));
				sms.setMobileOperator(rs.getString(4));
				sms.setCommandCode(rs.getString(5));
				sms.setInfo(rs.getString(6));
				sms.setFirstSendTime(rs.getTimestamp(7));
				sms.setLastSendTime(rs.getTimestamp(8));
				sms.setNumberOfSend(rs.getInt(9));
				sms.setProcessResult(rs.getInt(10));
				sms.setMessageType(rs.getInt(11));
				sms.setRequestId(rs.getBigDecimal(12));
				sms.setTotalSegments(rs.getInt(13));
				sms.setSegmentSeqnum(rs.getInt(14));
				sms.setMoreMsgsToSend(rs.getInt(15));
				sms.setMessageId(rs.getString(16));
				keys.addElement(sms);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllSMSesInSendQueue:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	public Collection getAllSMSesInSendQueueOld() throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();

			if (whereClause == null)
			{
				whereClause = "";
				for (Iterator it = Preference.sourceAddressList.iterator(); it.hasNext();)
				{
					String sAddress = (String) it.next();
					whereClause += "SERVICE_ID LIKE '%" + sAddress + "%'";
					if (it.hasNext())
					{
						whereClause += " OR ";
					}
				}
			}

			strSQL = "SELECT ID FROM SMS_SEND_QUEUE WHERE " + whereClause;
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				BigDecimal id = rs.getBigDecimal(1);
				keys.addElement(id);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllSMSesInSendQueue:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	// ***************************************************************************
	// CDR LOG
	// ***************************************************************************
	public BigDecimal add2CdrQueue(String userId, String serviceId, String mobileOperator, String commandCode, String info, String submitDate, String doneDate,
			int totalSegments) throws DBException
	{
		System.out.println("     --> Add to CDR queue ");
		DBException exception = null;
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO CDR_QUEUE (ID, USER_ID, SERVICE_ID, " + "MOBILE_OPERATOR, COMMAND_CODE, INFO, " + "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS) "
					+ "VALUES (S_CDR_QUEUE.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, userId);
			preStmt.setString(2, serviceId);
			preStmt.setString(3, mobileOperator);
			preStmt.setString(4, commandCode);
			preStmt.setString(5, info);
			preStmt.setString(6, submitDate);
			preStmt.setString(7, doneDate);
			preStmt.setInt(8, totalSegments);
			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error adding row");
			}
			else
			{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("Select S_CDR_QUEUE.currval from dual");
				if (rs.next())
				{
					id = rs.getBigDecimal(1);
					conn.commit();
				}
				else
				{
					conn.rollback();
					System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				}
			}
			conn.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			System.out.println("Error executing " + strSQL + " >>> " + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2CdrQueue:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);

			if (exception != null)
			{
				throw exception;
			}
			return id;
		}
	}

	// Note: submitDate and doneDate must be updated in SMSData
	private String[] chargeFreeList = { "912099902", "904060007", "912492727", // baopv
			"903200003", "903256275", "918077770", "989152696", "958867571"
	// "904061617", "912816396", "989068604",
	// "903495868" //Tanhle
	};

	public BigDecimal add2CdrQueue(SMSData sms) throws DBException
	{
		for (int i = 0; i < chargeFreeList.length; i++)
		{
			if (sms.getUserId().endsWith(chargeFreeList[i]))
			{
				return new BigDecimal(0);
			}
		}
		System.out.println("     --> Add to CDR queue ");
		DBException exception = null;
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO CDR_QUEUE (ID, USER_ID, SERVICE_ID, " + "MOBILE_OPERATOR, COMMAND_CODE, INFO, " + "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS) "
					+ "VALUES (S_CDR_QUEUE.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, sms.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL));
			preStmt.setString(2, sms.getServiceIdEx(Constants.SERVICEID_FORMAT_INTERNATIONAL));
			preStmt.setString(3, sms.getMobileOperator());
			preStmt.setString(4, sms.getCommandCode());
			String info = sms.getInfoEx(true);
			if (info != null && info.length() > 20)
			{
				info = info.substring(0, 19) + "...";
			}
			preStmt.setString(5, info);
			preStmt.setString(6, sms.getSubmitDate());
			preStmt.setString(7, sms.getDoneDate());
			preStmt.setInt(8, sms.getTotalSegments());
			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error adding row");
			}
			else
			{
				stmt = conn.createStatement();
				rs = stmt.executeQuery("Select S_CDR_QUEUE.currval from dual");
				if (rs.next())
				{
					id = rs.getBigDecimal(1);
					conn.commit();
				}
				else
				{
					conn.rollback();
					System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				}
			}
			conn.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			System.out.println("Error executing " + strSQL + " >>> " + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2CdrQueue: " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			if (exception != null)
			{
				throw exception;
			}
			return id;
		}
	}

	public BigDecimal add2CdrQueueEx(SMSData sms) throws DBException
	{
		/*
		 * for(int i=0; i<chargeFreeList.length; i++) { if
		 * (sms.getUserId().endsWith(chargeFreeList[i])) { return new
		 * BigDecimal(0); } }
		 */
		System.out.println("     --> Add to CDR queue ");
		DBException exception = null;
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("Select S_CDR_QUEUE.nextval from dual");
			if (stmt.executeUpdate() < 1)
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				return null;
			}
			rs = stmt.getResultSet();
			if (rs.next())
			{
				id = rs.getBigDecimal(1);
			}
			else
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				return null;
			}
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO CDR_QUEUE (ID, USER_ID, SERVICE_ID, " + "MOBILE_OPERATOR, COMMAND_CODE, INFO, " + "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, id);
			preStmt.setString(2, sms.getUserIdEx(Constants.USERID_FORMAT_INTERNATIONAL));
			preStmt.setString(3, sms.getServiceIdEx(Constants.SERVICEID_FORMAT_INTERNATIONAL));
			preStmt.setString(4, sms.getMobileOperator());
			preStmt.setString(5, sms.getCommandCode());
			String info = sms.getInfoEx(true);
			if (info != null && info.length() > 20)
			{
				info = info.substring(0, 19) + "...";
			}
			preStmt.setString(6, info);
			preStmt.setString(7, sms.getSubmitDate());
			preStmt.setString(8, sms.getDoneDate());
			preStmt.setInt(9, sms.getTotalSegments());
			if (preStmt.executeUpdate() != 1)
			{

				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error adding row");
			}
			else
			{

				conn.commit();
			}

		}
		catch (SQLException e)
		{
			System.out.println("Error executing " + strSQL + " >>> " + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2CdrQueue: " + e.getMessage());
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException ex)
			{
			}
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			if (exception != null)
			{
				throw exception;
			}
			return id;
		}
	}

	public BigDecimal add2CdrQueueEx(String userID, String serviceID, String mobileOperator, String commandCode, String info, String submitDate,
			String doneDate, int totalSegments) throws DBException
	{
		/*
		 * for(int i=0; i<chargeFreeList.length; i++) { if
		 * (userID.endsWith(chargeFreeList[i])) { return new BigDecimal(0); } }
		 */
		System.out.println("     --> Add to CDR queue ");
		DBException exception = null;
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("Select S_CDR_QUEUE.nextval from dual");
			if (stmt.executeUpdate() < 1)
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				return null;
			}
			rs = stmt.getResultSet();
			if (rs.next())
			{
				id = rs.getBigDecimal(1);
			}
			else
			{
				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error in S_CDR_QUEUE.currval !!!");
				return null;
			}
			Timestamp time = new Timestamp(System.currentTimeMillis());
			strSQL = "INSERT INTO CDR_QUEUE (ID, USER_ID, SERVICE_ID, " + "MOBILE_OPERATOR, COMMAND_CODE, INFO, " + "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, id);
			preStmt.setString(2, SMSData.formatUserId(userID, Constants.USERID_FORMAT_INTERNATIONAL));
			preStmt.setString(3, SMSData.formatServiceId(serviceID, Constants.SERVICEID_FORMAT_INTERNATIONAL));
			preStmt.setString(4, mobileOperator);
			preStmt.setString(5, commandCode);
			if (info != null && info.length() > 20)
			{
				info = info.substring(0, 19) + "...";
			}
			preStmt.setString(6, info);
			preStmt.setString(7, submitDate);
			preStmt.setString(8, doneDate);
			preStmt.setInt(9, totalSegments);
			if (preStmt.executeUpdate() != 1)
			{

				conn.rollback();
				System.out.println("DBTools.add2CdrQueue(): Error adding row");
			}
			else
			{

				conn.commit();
			}

		}
		catch (SQLException e)
		{
			System.out.println("Error executing " + strSQL + " >>> " + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2CdrQueue: " + e.getMessage());
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException ex)
			{
			}
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			if (exception != null)
			{
				throw exception;
			}
			return id;
		}
	}

	public BigDecimal add2CdrLog(CDR cdr)
	{
		DBException exception = null;
		BigDecimal id = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String strSQL = null;
		DBPool dbpool = new DBPool();
		try
		{
			// conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn = dbpool.getConnectionGateway();
			conn.setAutoCommit(false);
			/*
			 * stmt =
			 * conn.prepareStatement("Select S_CDR_LOG.nextval from dual"); if
			 * (stmt.executeUpdate() < 1) { conn.rollback(); System.out.println(
			 * "DBTools.add2CdrLog(): Error in S_CDR_LOG.currval !!!"); return
			 * null; } rs = stmt.getResultSet(); if (rs.next()) { id =
			 * rs.getBigDecimal(1); } else { conn.rollback();
			 * System.out.println(
			 * "DBTools.addToSMSSendQueue(): Error in S_SMS_SEND_QUEUE.currval !!!"
			 * ); return null; } Timestamp time = new
			 * Timestamp(System.currentTimeMillis());
			 */
			String tableCDR_LOG = "cdr_log" + DateProc.Timestamp2YYYYMMDD(DateProc.createTimestamp(), "").substring(0, 6);

			id = cdr.getId();
			strSQL = "INSERT INTO " + tableCDR_LOG + "  ( USER_ID, SERVICE_ID, " + "MOBILE_OPERATOR, COMMAND_CODE, INFO, "
					+ "SUBMIT_DATE, DONE_DATE, TOTAL_SEGMENTS,process_result,MESSAGE_TYPE,REQUEST_ID,CPID) " + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";
			strSQL = strSQL.toLowerCase();
			preStmt = conn.prepareStatement(strSQL);
			// preStmt.setBigDecimal(1, id);
			preStmt.setString(1, cdr.getUserId());
			preStmt.setString(2, cdr.getServiceId());
			preStmt.setString(3, cdr.getMobileOperator());
			preStmt.setString(4, cdr.getCommandCode());
			preStmt.setString(5, cdr.getInfo());
			// preStmt.setString(7, cdr.getSubmitDate());
			preStmt.setTimestamp(6, cdr.getSubmit_date_timestamp());
			preStmt.setTimestamp(7, cdr.getDone_date_timestamp());
			preStmt.setInt(8, cdr.getTotalSegments());
			preStmt.setString(9, cdr.getProcessResult());
			preStmt.setString(10, cdr.getMessageType());
			preStmt.setString(11, cdr.getRequestId());
			preStmt.setInt(12, cdr.getCPid());
			if (preStmt.executeUpdate() != 1)
			{
				conn.rollback();
				// System.out.println("DBTools.add2CdrLog(): Error adding row");
				Logger.info("INSERT CDR_LOG:", "DBTools.add2CdrLog(): Error adding row");
				return null;
			}
			else
			{
				conn.commit();
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error executing " + strSQL + " >>> " +
			// e.getMessage());
			Logger.info("INSERT CDR_LOG", "Error executing " + strSQL + " >>> " + e.getMessage());
		}
		catch (Exception e)
		{
			// System.out.println("DBTools.add2CdrLog: " + e.getMessage());
			Logger.info("DBTools.add2CdrLog:", e.getMessage());
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException ex)
			{
			}
			// GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, stmt);
			util.cleanup(conn, stmt);
			return id;
		}
	}

	public boolean removeFromCdrQueue(BigDecimal queueId)
	{
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		boolean result = false;
		DBPool dbpool = new DBPool();

		try
		{
			// conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn = dbpool.getConnectionGateway();
			strSQL = "DELETE FROM CDR_QUEUE WHERE ID = ?";
			strSQL = strSQL.toLowerCase();
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, queueId);
			if (preStmt.executeUpdate() < 1)
			{
				// System.out.println("Error deleting row");
				Logger.info("DBTools", "Error deleting row");
			}
			else
			{
				result = true;
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error executing SQL " + strSQL + ">>>" +
			// e.getMessage());
			Logger.info("DELETE CDR_QUEUE", "Error executing " + strSQL + " >>> " + e.getMessage());
		}
		catch (Exception e)
		{
			// System.out.println("DBTools.removeFromCdrQueue(id=" + queueId +
			// "): " + e.getMessage());
			Logger.info("DBTools.removeFromCdrQueue:", e.getMessage());
		}
		finally
		{
			// GatewayCDR.dbPool.add(conn);
			util.cleanup(preStmt, null);
			util.cleanup(conn, null);
			return result;
		}
	}

	public CDR getCDRinQueue(BigDecimal queueId)
	{
		CDR cdr = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "SELECT USER_ID, SERVICE_ID, MOBILE_OPERATOR, " + "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "TOTAL_SEGMENTS FROM CDR_QUEUE WHERE ID = ?";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setBigDecimal(1, queueId);
			rs = preStmt.executeQuery();
			if (rs.next())
			{
				cdr = new CDR();
				cdr.setId(queueId);
				cdr.setUserId(rs.getString(1));
				cdr.setServiceId(rs.getString(2));
				cdr.setMobileOperator(rs.getString(3));
				cdr.setCommandCode(rs.getString(4));
				cdr.setInfo(rs.getString(5));
				cdr.setSubmitDate(rs.getString(6));
				cdr.setDoneDate(rs.getString(7));
				cdr.setTotalSegments(rs.getInt(8));
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getCDRinQueue(id=" + queueId + "): " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);
			return cdr;
		}
	}

	public Collection getAllCDRsInQueueEx() throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		CDR cdr = null;
		Vector keys = new Vector();
		DBPool dbpool = new DBPool();
		try
		{
			// conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			conn = dbpool.getConnectionGateway();

			strSQL = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR,  "
					+ " COMMAND_CODE, INFO, DATE_FORMAT(SUBMIT_DATE,'%Y%m%d%H%i%s') SUBMIT_DATE, DATE_FORMAT(DONE_DATE,'%Y%m%d%H%i%s') DONE_DATE, "
					+ " TOTAL_SEGMENTS , process_result,Message_Type ,SUBMIT_DATE SUBMIT_DATE_TIMESTAMP , DONE_DATE DONE_DATE_TIMESTAMP,REQUEST_ID,CPID FROM cdr_queue  where MOBILE_OPERATOR='VIETTEL'  and  USER_ID not in (select PhoneNumber  from _cc_phone ) and (Message_Type like '2%'  or Message_Type ='1'  or Message_Type ='3') ";
			// strSQL = strSQL.toLowerCase();
			Logger.info("queuery database", strSQL);
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			// System.out.println("==>"+strSQL);
			while (rs.next())
			{
				// System.out.println("==>"+rs.getBigDecimal(1));
				cdr = new CDR();
				cdr.setId(rs.getBigDecimal(1));
				cdr.setUserId(rs.getString(2));
				cdr.setServiceId(rs.getString(3));
				cdr.setMobileOperator(rs.getString(4));
				cdr.setCommandCode(rs.getString(5));
				cdr.setInfo(rs.getString(6));
				cdr.setSubmitDate(rs.getString(7));
				cdr.setDoneDate(rs.getString(8));
				cdr.setTotalSegments(rs.getInt(9));
				cdr.setProcessResult(rs.getString(10));
				cdr.setMessageType(rs.getString(11));
				cdr.setSubmit_date_timestamp(rs.getTimestamp(12));
				cdr.setDone_date_timestamp(rs.getTimestamp(13));
				cdr.setRequestId(rs.getString(14));
				cdr.setCPId(rs.getInt(15));
				keys.addElement(cdr);
			}
		}
		catch (SQLException e)
		{
			// System.out.println("Error executing SQL " + strSQL + ">>>" +
			// e.getMessage());
			Logger.info("DBTools.getAllCDRsInQueue:" + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			// System.out.println("DBTools.getAllCDRsInQueue: " +
			// e.getMessage());
			Logger.info("DBTools.getAllCDRsInQueue:" + e.getMessage());
		}
		finally
		{
			// GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);
			util.cleanup(conn, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	public Collection getAllCDRsInQueueALL() throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		CDR cdr = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, " + "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "TOTAL_SEGMENTS FROM CDR_QUEUE WHERE (LENGTH(SERVICE_ID)>6 OR SERVICE_ID IN ('84996','84997','84998','996','997','998') "
					+ " OR (SERVICE_ID LIKE'%99' AND LENGTH(SERVICE_ID)<7))";
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			// System.out.println("==>"+strSQL);
			while (rs.next())
			{
				// System.out.println("==>"+rs.getBigDecimal(1));
				cdr = new CDR();
				cdr.setId(rs.getBigDecimal(1));
				cdr.setUserId(rs.getString(2));
				cdr.setServiceId(rs.getString(3));
				cdr.setMobileOperator(rs.getString(4));
				cdr.setCommandCode(rs.getString(5));
				cdr.setInfo(rs.getString(6));
				cdr.setSubmitDate(rs.getString(7));
				cdr.setDoneDate(rs.getString(8));
				cdr.setTotalSegments(rs.getInt(9));
				keys.addElement(cdr);
				// Can xoa sau khi debug
				// System.out.println("==>"+rs.getBigDecimal(1));
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllCDRsInQueue: " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	public Collection getAllCDRsInQueueEx8x99() throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		CDR cdr = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, " + "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "TOTAL_SEGMENTS FROM CDR_QUEUE WHERE SERVICE_ID LIKE'%99' AND LENGTH(SERVICE_ID)<7";
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				cdr = new CDR();
				cdr.setId(rs.getBigDecimal(1));
				cdr.setUserId(rs.getString(2));
				cdr.setServiceId(rs.getString(3));
				cdr.setMobileOperator(rs.getString(4));
				cdr.setCommandCode(rs.getString(5));
				cdr.setInfo(rs.getString(6));
				cdr.setSubmitDate(rs.getString(7));
				cdr.setDoneDate(rs.getString(8));
				cdr.setTotalSegments(rs.getInt(9));

				keys.addElement(cdr);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllCDRsInQueue: " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	public Collection getAllCDRsInQueue(String mobileOperator) throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "SELECT ID FROM CDR_QUEUE WHERE UPPER(MOBILE_OPERATOR) = ?";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, mobileOperator);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				BigDecimal id = rs.getBigDecimal(1);
				keys.addElement(id);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllCDRsInQueue(" + mobileOperator + "): " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}

	public Map getAllCDRsInLog(String mobileOperator, String serviceId, String yymmdd)
	{
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		Map counters = new HashMap();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "select count(*), COMMAND_CODE from CDR_LOG WHERE" + " UPPER(MOBILE_OPERATOR) = '" + mobileOperator.toUpperCase()
					+ "' and SERVICE_ID like '%" + serviceId + "' and SUBMIT_DATE like '" + yymmdd + "%' GROUP BY COMMAND_CODE";

			System.out.println("SQL: " + strSQL);

			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				String count = rs.getString(1);
				String code = rs.getString(2);
				counters.put(code, count);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllCDRsInLog(" + mobileOperator + "): " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);
			return counters;
		}
	}

	// returns CDR in CDR_LOG
	public CDR moveCDRFromQueueToLog(BigDecimal queueId)
	{
		CDR cdr = getCDRinQueue(queueId);
		if (cdr != null)
		{
			// update mobile profile
			// .........
			BigDecimal logId = add2CdrLog(cdr);
			if (logId != null)
			{
				int nloop = 0;
				while (!removeFromCdrQueue(queueId))
				{
					nloop++;
					if (nloop >= 3)
					{
						break;
					}
				}
				cdr.setId(logId);
				return cdr;
			}
		}
		return null;
	}

	public CDR moveCDRFromQueueToLogEx(CDR cdr)
	{
		if (cdr != null)
		{
			// update mobile profile
			// .........
			BigDecimal logId = add2CdrLog(cdr);
			if (logId != null)
			{
				int nloop = 0;
				while (!removeFromCdrQueue(cdr.getId()))
				{
					nloop++;
					if (nloop >= 3)
					{
						break;
					}
				}
				cdr.setId(logId);
				return cdr;
			}
		}
		return null;
	}

	// ***************************************************************************
	// MOBILE PROFILE
	// ***************************************************************************
	/**
	 * @param soTB84
	 *            : 849xxx
	 * @param lastChargedTime
	 * @param shortCode
	 *            : 996, 997, 998
	 */
	public boolean add2MobileProfile(String soTB84, Timestamp lastChargedTime, String shortCode) throws DBException
	{
		DBException exception = null;

		boolean result = false;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "INSERT INTO MOBILE_PROFILE  (SOTB84, " + "LAST_CHARGED_TIME, LAST_CHARGED_SHORTCODE) " + "VALUES (?, ?, ?)";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, soTB84);
			preStmt.setTimestamp(2, lastChargedTime);
			preStmt.setString(3, shortCode);
			if (preStmt.executeUpdate() != 1)
			{
				System.out.println("DBTools.add2MobileProfile(): Error adding row");
			}
			else
			{
				result = true;
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing " + strSQL + " >>> " + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.add2MobileProfile:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(preStmt, null);

			if (exception != null)
			{
				throw exception;
			}
			return result;
		}
	}

	public boolean updateMobileProfile(MobileProfile profile)
	{
		boolean result = false;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		String strSQL = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "UPDATE MOBILE_PROFILE SET LAST_CHARGED_TIME = ?, " + "LAST_CHARGED_SHORTCODE = ? WHERE SOTB84 = ?";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setTimestamp(1, profile.getLastChargedTime());
			preStmt.setString(2, profile.getLastChargedShortCode());
			preStmt.setString(3, profile.getSoTB84());
			if (preStmt.executeUpdate() < 1)
			{
				System.out.println("DBTools.updateMobileProfile(" + profile.getSoTB84() + ") Row does not exist");
			}
			else
			{
				result = true;
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.updateMobileProfile:" + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(preStmt, null);
			return result;
		}
	}

	public MobileProfile getMobileProfile(String soTB84)
	{
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		MobileProfile profile = null;
		try
		{
			conn = (java.sql.Connection) GatewayCDR.dbPool.remove();
			strSQL = "select LAST_CHARGED_TIME, LAST_CHARGED_SHORTCODE " + "from MOBILE_PROFILE where SOTB84 = ?";
			preStmt = conn.prepareStatement(strSQL);
			preStmt.setString(1, soTB84);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				profile = new MobileProfile();
				profile.setLastChargedTime(rs.getTimestamp(1));
				profile.setLastChargedShortCode(rs.getString(2));
				profile.setSoTB84(soTB84);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getMobileProfile(" + soTB84 + ") " + e.getMessage());
		}
		finally
		{
			GatewayCDR.dbPool.add(conn);
			util.cleanup(rs);
			util.cleanup(preStmt, null);
			return profile;
		}
	}

	public static void main(String args[])
	{
		DBTools dbTools = new DBTools();
		GatewayCDR gateway = new GatewayCDR();
		try
		{
			Preference.loadProperties("gateway.cfg");
		}
		catch (IOException e)
		{
			System.out.println("GatewayCDR.main(): khong tim thay file cau hinh ");
		}

		gateway.addMoreConnection2Pool(1);

		try
		{

			BigDecimal id = dbTools.add2CdrQueue("0904060008", "84996", "VMS", "DA", "DA 123", "0402011040", "0402011040", 1);

		}
		catch (DBException ex)
		{
			ex.printStackTrace();
		}

		Timestamp time = new Timestamp(System.currentTimeMillis());

		gateway.closeAllConnectionInPool();
	}

	public static int log_alert(String domain, String alertmsg, int alert_status, int checked, String level, String contactInfo) throws DBException
	{
		Connection connection = null;
		PreparedStatement stmt = null;
		Utilities util = new Utilities();
		DBPool dbpool = new DBPool();
		String sSQL = "insert into msg_alerter(domain,alertmsg,alert_status,checked,level,contact) " + "values(?,?,?,?,?,?)";
		try
		{
			// connection =
			// util.getDBConnectionAlert(Preference.db_DriverClassName_alert,
			// Preference.db_server_alert,
			// Preference.db_name_alert,
			// Preference.db_user_alert,
			// Preference.db_password_alert,
			// Preference.db_port_alert);
			connection = dbpool.getConnectionAlert();
			stmt = connection.prepareStatement(sSQL);
			stmt.setString(1, domain);
			stmt.setString(2, alertmsg);
			stmt.setInt(3, alert_status);
			stmt.setInt(4, checked);
			stmt.setString(5, level);
			stmt.setString(6, contactInfo);
			if (stmt.executeUpdate() == -1)
			{
				return -1;
			}
			return 1;
		}
		catch (Exception ex)
		{
			System.out.println("( Log error:" + ex.toString());
			System.out.println(">>" + sSQL);
			if (ex.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				throw new DBException(ex.getMessage());
			}
			return -1;
		}
		finally
		{
			util.cleanup(connection, stmt);
		}
	}

	/**
	 * For pushCDR to file
	 */
	public Collection getAllCDRsInLogEx8x99(String fromNumber, String toNumber) throws DBException
	{
		DBException exception = null;
		java.sql.Connection conn = null;
		PreparedStatement preStmt = null;
		ResultSet rs = null;
		String strSQL = null;
		CDR cdr = null;
		Vector keys = new Vector();
		try
		{
			conn = (java.sql.Connection) GatewayCDR.util.getDBConnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:oci8:@mcdb27", "vmsgw",
					"smsadmin12345");
			strSQL = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, " + "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, "
					+ "TOTAL_SEGMENTS FROM CDR_LOG WHERE SERVICE_ID LIKE'%99' AND LENGTH(SERVICE_ID)<7 AND TO_NUMBER(SUBMIT_DATE) BETWEEN " + fromNumber
					+ " AND " + toNumber;
			preStmt = conn.prepareStatement(strSQL);
			rs = preStmt.executeQuery();
			while (rs.next())
			{
				cdr = new CDR();
				cdr.setId(rs.getBigDecimal(1));
				cdr.setUserId(rs.getString(2));
				cdr.setServiceId(rs.getString(3));
				cdr.setMobileOperator(rs.getString(4));
				cdr.setCommandCode(rs.getString(5));
				cdr.setInfo(rs.getString(6));
				cdr.setSubmitDate(rs.getString(7));
				cdr.setDoneDate(rs.getString(8));
				cdr.setTotalSegments(rs.getInt(9));
				keys.addElement(cdr);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Error executing SQL " + strSQL + ">>>" + e.getMessage());
			if (e.getMessage().startsWith("ORA-03114"))
			{ // ORA-03114: not connected to ORACLE
				exception = new DBException(e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.out.println("DBTools.getAllCDRsInQueue: " + e.getMessage());
		}
		finally
		{
			util.cleanup(rs);
			util.cleanup(conn, preStmt);

			if (exception != null)
			{
				throw exception;
			}
			return keys;
		}
	}
}
