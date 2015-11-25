package icom.common;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.LoadConfig;
import icom.MsgObject;
import icom.Sender;
import icom.Services;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import daugia.DaugiaCommon;

import mtPush.MTPushCommon;
import mtPush.PushMTConstants;

import services.SubXoso;

public class DBUtil {

	// private static PreparedStatement pstm = null;
	// private static ResultSet rs = null;
	// private static Vector vtValue = null;
	// private static List lsValue = null;

	// public static Vector getVtValue() {
	// return vtValue;
	// }

	// public static void setVtValue(Vector vtValue) {
	// DBUtil.vtValue = vtValue;
	// }

	// public static List getLsValue() {
	// return lsValue;
	// }

	// /public static void setLsValue(List lsValue) {
	// DBUtil.lsValue = lsValue;
	// }

	public static String getStringValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {

		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;

		String tmpVal = "";

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getString(1) : "";
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public static int getIntValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		int tmpVal = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getInt(1) : 0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public static double getDoubleValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		double tmpVal = 0.0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {

			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();

			tmpVal = (rs.next()) ? rs.getDouble(1) : 0.0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public float getFloatValue(Connection cnn, String fldName, String tblName,
			String con) throws Exception {

		float tmpVal = 0;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;

		String strSQL = "SELECT " + fldName + " FROM " + tblName;
		if (!con.equals("")) {
			strSQL = strSQL + " WHERE " + con;
		}

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			tmpVal = (rs.next()) ? rs.getFloat(1) : 0;
		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return tmpVal;
	}

	public static String getSequenceValue(Connection cnn, String sequenceName)
			throws Exception {
		// SQL command to sequence value
		String strSQL = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
		String strReturn = "";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;

		try {
			// Get query data
			pstm = cnn.prepareStatement(strSQL);
			rs = pstm.executeQuery();

			// Validation
			if (!rs.next()) {
				throw new Exception("Sequence " + sequenceName
						+ " does not exist");
			}

			strReturn = rs.getString(1);

		} catch (Exception e) {
			throw e;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return strReturn;
	}

	public static Vector getVectorTable(Connection cnn, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		Vector vt = null;
		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtil.convertToVector(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return vt;
	}

	public static Vector getVectorTable(String poolname, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		Vector vt = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();
		try {
			cnn = dbpool.getConnection(poolname);
			if (cnn == null)
				return new Vector();
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtil.convertToVector(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
			closeObject(cnn);
		}
		return vt;
	}

	public static List getListTable(Connection cnn, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		List lst = null;
		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtil.convertToList(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return lst;
	}

	public static List getListTablePartition(Connection cnn, String strSQL)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Vector vtValue = null;
		List lsValue = null;
		List lst = null;
		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtil.convertToList(rs);
		} catch (SQLException ex) {
			// Throw exception if has error
			lst = null;

		} catch (Exception ex) {
			// Throw exception if has error
			ex.printStackTrace();
			throw ex;
		} finally {
			// Close connection and objects databse
			closeObject(rs);
			closeObject(pstm);
		}
		return lst;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Convert data from ResultSet into Vector
	 * 
	 * @param rsData
	 *            Opened ResultSet
	 * @throws Exception
	 *             when working with ResultSet
	 */
	// ///////////////////////////////////////////////////////////////
	public static Vector convertToVector(ResultSet rsData) throws Exception {
		Vector vctReturn = new Vector();
		int iColumnCount = rsData.getMetaData().getColumnCount();
		while (rsData.next()) {
			Vector vctRow = new Vector();
			for (int i = 1; i <= iColumnCount; i++) {
				String strValue = rsData.getString(i);
				if (strValue == null) {
					strValue = "";
				}
				vctRow.addElement(strValue);
			}
			vctReturn.addElement(vctRow);
		}
		vctReturn.trimToSize();
		return vctReturn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Convert data from ResultSet into List
	 * 
	 * @param rsData
	 *            Opened ResultSet
	 * @throws Exception
	 *             when working with ResultSet
	 */
	// ///////////////////////////////////////////////////////////////
	public static List convertToList(ResultSet rsData) throws Exception {
		List lstReturn = new ArrayList();
		int iColumnCount = rsData.getMetaData().getColumnCount();
		while (rsData.next()) {
			List lstRow = new ArrayList();
			for (int i = 1; i <= iColumnCount; i++) {
				String strValue = rsData.getString(i);
				if (strValue == null) {
					strValue = "";
				}
				lstRow.add(strValue);
			}
			lstReturn.add(lstRow);
		}
		return lstReturn;
	}

	public static String addCondition(String strField, String strValue) {
		String strReturn = "";
		if (!strValue.equals("") && strValue.indexOf("%") == -1) {
			strReturn = " AND " + strField + " = '" + strValue + "' ";
		} else if (!strValue.equals("")) {
			strReturn = " AND " + strField + " like '" + strValue + "' ";
		}
		return strReturn;
	}

	public static String addDateCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
					+ strValue + "','DD/MM/YYYY')";
		}
		return strReturn;
	}

	public static String addDateTextCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			// to_date(TO_CHAR (param2, 'dd-MM-yyyy hh24:mi:ss'),'dd-mm-yyyy
			// hh24:mi:ss')
			// strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
			// + strValue + "','DD/MM/YYYY')";
			strReturn = " AND to_date(TO_CHAR (" + strField
					+ ", 'dd-MM-yyyy hh24:mi:ss'),'dd-mm-yyyy hh24:mi:ss') "
					+ strCompare + " TO_DATE('" + strValue + "','DD/MM/YYYY')";
		}
		return strReturn;
	}

	public static String addTimeCondition(String strField, String strValue,
			String strCompare) {
		String strReturn = "";
		if (!strValue.equals("")) {
			strReturn = " AND " + strField + " " + strCompare + " TO_DATE('"
					+ strValue + "','DD/MM/YYYY hh24:mi:ss')";
		}
		return strReturn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(PreparedStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(CallableStatement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Statement obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(ResultSet obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact (tuong tac) with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Connection obj) {
		try {
			if (obj != null) {
				if (!obj.isClosed()) {
					if (!obj.getAutoCommit()) {
						obj.rollback();
					}
					obj.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getDayOfWeek(Timestamp ts) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeek() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	public static int getHourOfDay() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.HOUR_OF_DAY);
	}

	public static int getHourOfDay(Timestamp ts) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new java.util.Date(ts.getTime()));
		return calendar.get(calendar.HOUR_OF_DAY);
	}

	public static int executeSQL(Connection obj, String sql) {

		PreparedStatement statement = null;

		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.printStackTrace(e);
			return -1;
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			closeObject(statement);

		}
	}

	public static int executeSQL(String poolname, String sql) {

		PreparedStatement statement = null;
		Connection cnn = null;
		DBPool dbpool = new DBPool();

		try {

			cnn = dbpool.getConnection(poolname);
			statement = cnn.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.printStackTrace(e);

			return -1;
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			closeObject(statement);
			closeObject(cnn);

		}
	}

	public static int Alert(String domain, String issue, String level,
			String alertmsg, String contact) {

		if ("".equalsIgnoreCase(contact))
			contact = "KhienHD:0943941966;PhuongDT:0914923008";

		Connection connection = null;
		PreparedStatement stmt = null;
		DBPool dbpool = new DBPool();
		String newissue = issue;
		if (issue.length() > 20) {
			newissue = issue.substring(0, 19);
		}
		String newalert = alertmsg;
		if (newalert.length() > 130) {
			newalert = newalert.substring(0, 130);
		}
		String sSQL = "insert into msg_alerter( domain, issue, level,alertmsg,contact) "
				+ "values(?,?,?,?,?)";
		try {
			if (connection == null)
				connection = dbpool.getConnection("alert");
			stmt = connection.prepareStatement(sSQL);
			stmt.setString(1, domain);
			stmt.setString(2, newissue);
			stmt.setString(3, level);
			stmt.setString(4, newalert);
			stmt.setString(5, contact);
			if (stmt.executeUpdate() == -1) {
				return -1;
			}
			return 1;
		} catch (Exception ex) {
			Util.logger.info("DBTools" + "{Alert Error:}" + ex.getMessage());
			Util.logger.printStackTrace(ex);

			return -1;
		} finally {
			dbpool.cleanup(connection, stmt);
		}

	}

	public static boolean Update(String strUpdate) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("DBUtil@Update@connection is null.");
				return false;
			}
			statement = connection.prepareStatement(strUpdate);
			if (statement.execute()) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error("DBUtil@Update@: Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error("DBUtil@Update@: Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public static Vector getListUserFromListSend(String serviceName)
			throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, options,channel_type from list_send "
				+ " where upper(command_code) like '"
				+ serviceName.toUpperCase() + "%'";
		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromListSendByLastCode(String serviceName,
			String lastCode) throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, options,channel_type from list_send "
				+ " where last_code = '"
				+ lastCode
				+ "' AND upper(command_code) like '"
				+ serviceName.toUpperCase() + "%' ";
		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromMlist(String serviceName, String mList)
			throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, mt_count,mt_free,duration,options, channel_type,command_code,date,reg_count from "
				+ mList
				+ " where upper(command_code) like '"
				+ serviceName.toUpperCase() + "%' and active=0";
		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromListSendForXoso(String serviceName,
			int companyId) throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id,channel_type  from list_send "
				+ " where company_id="
				+ companyId
				+ " and upper(command_code) like '"
				+ serviceName.toUpperCase()
				+ "%'";
		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromMlistForXoso(String serviceName,
			String mList, int companyId) throws Exception {
		String sqlSelect = "select id,user_id,service_id,last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, mt_count,mt_free,duration,options, channel_type,command_code,date,reg_count from "
				+ mList + " where company_id=" + companyId + " and active=0";
		/******
		 * PhuongDT: 2010-12-14 Lay thong tin user theo command_code *
		 *****/
		sqlSelect += " and upper(command_code) like '"
				+ serviceName.toUpperCase() + "%'";
		/*** End ***/

		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromListSendForXosoDaily(
			String serviceName, int companyId, int areaid) throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id,channel_type from list_send "
				+ " where (company_id="
				+ companyId
				+ " or company_id="
				+ areaid + ")";
		/******
		 * PhuongDT: 2010-12-14 Lay thong tin user theo command_code *
		 *****/
		String commandCodeXS = "";
		if (areaid == 901)
			commandCodeXS = "XSMN";
		else if (areaid == 902)
			commandCodeXS = "XSMT";

		if (!commandCodeXS.equals(""))
			sqlSelect += " and upper(command_code) like '" + serviceName
					+ "' or upper(command_code) like '" + commandCodeXS + "'";
		else
			sqlSelect += " and upper(command_code) like '" + serviceName + "'";
		/*** End ***/

		Util.logger.info("@getListUserFromListSendForXosoDaily@SQL="
				+ sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	public static Vector getListUserFromMlistForXosoDaily(String serviceName,
			String mList, int companyId, int areaid) throws Exception {
		String sqlSelect = "select id,user_id,service_id,last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, mt_count,mt_free,duration, options, channel_type,command_code,date,reg_count , active from "
				+ mList
				+ " where (company_id="
				+ companyId
				+ " or company_id="
				+ areaid + ") and ( active=0 or active = 2 )";
		/******
		 * PhuongDT: 2010-12-14 Lay thong tin user theo command_code *
		 *****/
		sqlSelect += " and upper(command_code) like 'XS%'";
		/*** End ***/
		Util.logger.info("@getListUserFromMlistForXosoDaily@SQL=" + sqlSelect);

		Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}

	/*********
	 * funtion insert thong tin vao db - thong tin nay la can cu de tinh doi
	 * soat sau nay - table: icom_game201011, save theo thang *
	 **/

	public static void SaveInfoGame() {

	}

	// Chuyen thong tin vao bang mt_queue
	public static int sendMTQueue(String type, MsgObject msgObject,
			String sclassname, String service_name, String sSplit, int iNumber,
			int sendToVMS) {
		/**
		 * neu sendToVMS = 1: dich vu ben VMS, send vao mt_queue VMS neu
		 * sendToVMS = 0: dich vu ben ICOM, send vao mt_queue ben ICOM
		 * 
		 * */
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = "";
		String temp = "-1";

		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(sclassname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;
		}

		DBPool dbpool = new DBPool();

		Util.logger.info(sclassname + "@sendMT:\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tmobile_operator:"
				+ msgObject.getMobileoperator() + "\tkeyword:"
				+ msgObject.getKeyword() + "\tcontent_type:"
				+ msgObject.getContenttype() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tamount:"
				+ msgObject.getAmount() + "\tservice_name:" + service_name
				+ "\t" + msgObject.getCommandCode());
		try {

			connection = dbpool.getConnectionGateway();
			Util.logger.info("Connection: " + connection);

			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID, AMOUNT, CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

			Util.logger.info("Type: " + type);

			if ("1".equalsIgnoreCase(type)) {
				// Truoc khi gui phai split
				String[] content = msgObject.getUsertext().split("###");

				for (int i = 0; i < content.length; i++) {
					if (!"".equalsIgnoreCase(content[i])) {
						statement = connection.prepareStatement(sqlString);
						statement.setString(1, msgObject.getUserid());
						statement.setString(2, msgObject.getServiceid());
						statement.setString(3, msgObject.getMobileoperator());
						statement.setString(4, msgObject.getCommandCode());
						statement.setInt(5, msgObject.getContenttype());
						statement.setString(6, content[i]);
						// statement.setInt(7, msgObject.getMsgtype());
						if (i == 0) {
							statement.setInt(7, 1);
						} else {
							statement.setInt(7, 0);
						}
						statement.setBigDecimal(8, msgObject.getRequestid());
						statement.setLong(9, msgObject.getAmount());
						statement.setInt(10, msgObject.getChannelType());

						if (statement.executeUpdate() != 1) {
							Util.logger.crisis(sclassname
									+ "@sendMT: Error@userid="
									+ msgObject.getUserid() + "@serviceid="
									+ msgObject.getServiceid() + "@usertext="
									+ msgObject.getUsertext() + "@messagetype="
									+ msgObject.getMsgtype() + "@requestid="
									+ msgObject.getRequestid().toString());

							return -1;
						}
					}
				}
				return 1;
			} else {
				// Xem xep vao bang nao
				int iMod = Integer.parseInt(sSplit);

				int iDu = 0;
				iDu = iNumber % iMod;

				String sAdd = "";
				if (iDu == 0) {
					sAdd = "";
				} else {
					sAdd = iDu + "";
				}
				if (sendToVMS == 1) {
					sqlString = "INSERT INTO "
							+ Constants.tblMTQueue
							+ sAdd
							+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID, AMOUNT, CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

				} else {
					sqlString = "INSERT INTO "
							+ Constants.tblMTQueue
							+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID, AMOUNT, CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
				}

				Util.logger.info("SQL Insert: " + sqlString);

				statement = connection.prepareStatement(sqlString);
				statement.setString(1, msgObject.getUserid());
				statement.setString(2, msgObject.getServiceid());
				statement.setString(3, msgObject.getMobileoperator());
				statement.setString(4, msgObject.getCommandCode());
				statement.setInt(5, msgObject.getContenttype());
				statement.setString(6, msgObject.getUsertext());
				statement.setInt(7, msgObject.getMsgtype());
				statement.setBigDecimal(8, msgObject.getRequestid());
				// Them vao ngay 002-06-2010
				statement.setLong(9, msgObject.getAmount());
				statement.setInt(10, msgObject.getChannelType());

				// Util.logger.info(sclassname + "@sendMT\tuser_id:"
				// + msgObject.getUserid() + "\tservice_id:"
				// + msgObject.getServiceid() + "\tmobile_operator:"
				// + msgObject.getMobileoperator() + "\tkeyword:"
				// + msgObject.getKeyword() + "\tcontent_type:"
				// + msgObject.getContenttype() + "\tuser_text:"
				// + msgObject.getUsertext() + "\tmessage_type:"
				// + msgObject.getMsgtype() + "\trequest_id:"
				// + msgObject.getRequestid().toString() + "\tamount:"
				// + msgObject.getAmount() + "\tservice_name:"
				// + service_name + "\t" + msgObject.getCommandCode());

				if (statement.executeUpdate() != 1) {
					Util.logger.crisis(sclassname + "@sendMT: Error:\tuser_id:"
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tuser_text:"
							+ msgObject.getUsertext() + "\tmessage_type:"
							+ msgObject.getMsgtype() + "\trequest_id:"
							+ msgObject.getRequestid().toString());

					return -1;
				}

				return 1;
			}
		} catch (SQLException e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());

			Util.logger.printStackTrace(e);
			return -1;
		} catch (Exception e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	// Chuyen thong tin vao bang vms_charge_online
	public static int sendVMSChargeOnline(String type, MsgObject msgObject,
			String sclassname, String service_name) {

		// Util.logger.info("content hoang dao:" + msgObject.getUsertext());
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(sclassname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;
		}

		DBPool dbpool = new DBPool();

		Util.logger.info(sclassname + "@sendMT:\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tmobile_operator:"
				+ msgObject.getMobileoperator() + "\tkeyword:"
				+ msgObject.getKeyword() + "\tcontent_type:"
				+ msgObject.getContenttype() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tamount:"
				+ msgObject.getAmount() + "\tservice_name:" + service_name
				+ "\t" + msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "\tTO"
						+ msgObject.getServiceid() + "\t"
						+ msgObject.getUsertext() + "\trequest_id:"
						+ msgObject.getRequestid().toString());
				return -1;
			}
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID

			sqlString = "INSERT INTO "
					+ Constants.tblVMSChargeOnline
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID, CONTENT_ID, AMOUNT, CHANNEL_TYPE, SERVICE_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Util.logger.info(sclassname + "@sendMT@Sql=" + sqlString);
			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID

			if ("1".equalsIgnoreCase(type)) {

				// Truoc khi gui phai split
				String[] content = msgObject.getUsertext().split("###");

				for (int i = 0; i < content.length; i++) {
					if (!"".equalsIgnoreCase(content[i])) {
						statement = connection.prepareStatement(sqlString);
						statement.setString(1, msgObject.getUserid());
						statement.setString(2, msgObject.getServiceid());
						statement.setString(3, msgObject.getMobileoperator());
						statement.setString(4, msgObject.getKeyword());
						statement.setInt(5, msgObject.getContenttype());
						statement.setString(6, content[i]);
						// statement.setInt(7, msgObject.getMsgtype());
						if (i == 0) {
							statement.setInt(7, 1);
						} else {
							statement.setInt(7, 0);
						}
						statement.setBigDecimal(8, msgObject.getRequestid());

						// THem vao ngay 002-06-2010
						statement.setInt(9, msgObject.getContentId());
						statement.setLong(10, msgObject.getAmount());

						// Quy dinh voi VASC thi khac
						statement.setInt(11, msgObject.getChannelType());
						// Ten dich vu
						statement.setString(12, service_name);

						if (statement.executeUpdate() != 1) {
							Util.logger.crisis(sclassname
									+ "@sendMT: Error@userid="
									+ msgObject.getUserid() + "@serviceid="
									+ msgObject.getServiceid() + "@usertext="
									+ msgObject.getUsertext() + "@messagetype="
									+ msgObject.getMsgtype() + "@requestid="
									+ msgObject.getRequestid().toString());

							return -1;
						}

					}
				}
				return 1;
			} else {
				statement = connection.prepareStatement(sqlString);
				statement.setString(1, msgObject.getUserid());
				statement.setString(2, msgObject.getServiceid());
				statement.setString(3, msgObject.getMobileoperator());
				statement.setString(4, msgObject.getKeyword());
				statement.setInt(5, msgObject.getContenttype());
				statement.setString(6, msgObject.getUsertext());
				statement.setInt(7, msgObject.getMsgtype());
				statement.setBigDecimal(8, msgObject.getRequestid());
				// THem vao ngay 002-06-2010
				statement.setInt(9, msgObject.getContentId());
				statement.setLong(10, msgObject.getAmount());
				statement.setInt(11, msgObject.getChannelType());
				// Ten dich vu
				statement.setString(12, service_name);
				if (statement.executeUpdate() != 1) {
					Util.logger.crisis(sclassname + "@sendMT: Error\tuser_id:"
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tuser_text:"
							+ msgObject.getUsertext() + "\tmessage_type:"
							+ msgObject.getMsgtype() + "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcommand_code:" + msgObject.getCommandCode());

					return -1;
				}
				return 1;
			}
		} catch (SQLException e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} catch (Exception e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public static int insertData2cancel(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String mt_count) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into "
				+ mlist
				+ "_cancel(user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,mt_count) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getLongRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ mt_count + ")";
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("DBUtil@" + ": uppdate Statement: Insert  "
						+ mlist + " Failed:" + sqlInsert);
				DBUtil.Alert("DBUtil", "RUNING", "major", "Co loi khi insert "
						+ mlist + "_cancel", "");
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("DBUtil@" + ":Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static String getCode(String dbcode, String type, String group) {
		String code = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT code FROM icom_wappush WHERE upper(type) = '"
				+ type.toUpperCase() + "' AND upper(group1)='"
				+ group.toUpperCase() + "' AND " + " active = 1";

		query = query + " order by rand() limit 1";

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil@getContent: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = (String) item.elementAt(0);
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil@getContent: Failed" + ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}

	public static String getCode(String dbcode, String type, String group,
			int active, String lastcode) {
		// tach lastcode

		lastcode = Util.SplitLastCode(lastcode);
		String code = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT code FROM icom_wappush WHERE upper(type) = '"
				+ type.toUpperCase() + "' AND upper(group1)='"
				+ group.toUpperCase() + "' AND " + " active = " + active
				+ " AND code not in (" + lastcode + ")";

		query = query + " order by rand() limit 1";

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = (String) item.elementAt(0);
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}

	public static String getNameCode(String code, String group) {
		String namecode = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT namecode FROM icom_wappush WHERE upper(code) = '"
				+ code.toUpperCase()
				+ "' and upper(group1)='"
				+ group.toUpperCase() + "'";

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil:getNamecode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				namecode = (String) item.elementAt(0);
				return namecode;
			}

		} catch (Exception ex) {
			Util.logger.error("DBUtil:getNamecode: Failed" + ex.getMessage());
			Util.logger.printStackTrace(ex);
			return namecode;
		} finally {
			dbpool.cleanup(connection);
		}
		return namecode;
	}

	public static boolean updateCode(String dbcode, String type, String group,
			String code) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;

		String query = "UPDATE icom_wappush SET active = 0 WHERE upper(type) = '"
				+ type.toUpperCase()
				+ "' AND upper(group1)='"
				+ group.toUpperCase()
				+ "' AND upper(code)='"
				+ code.toUpperCase() + "'";

		try {
			connection = dbpool.getConnectionGateway();
			statement = connection.prepareStatement(query);
			if (statement.execute()) {
				Util.logger.info("Update DONE");
				return true;
			}
		} catch (Exception ex) {
			Util.logger.info("DBUtil@getContent: Failed" + ex.getMessage());
			Util.logger.printStackTrace(ex);
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return false;
	}

	// Tao duong link cho khach hang
	public static boolean saveRequest(String dbcontent1, String userid,
			String code, String type, int gid) {
		// TODO: PhuongDT
		// if(1==1) return true;
		Connection connection = null;
		PreparedStatement statement = null;
		boolean rs = true;

		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection(dbcontent1);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "Insert into icom_wap.dbo.download(phone,code,filetype,cgroup) values ('"
					+ userid + "','" + code + "','" + type + "'," + gid + ")";

			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);

			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert into download");
				rs = false;
			}

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());

		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());

		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return rs;
	}

	public static boolean isexist_in_mlist(String userid, String mlist,
			String command_code, int companyid, String option) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "' and upper(command_code)='"
					+ command_code.trim().toUpperCase() + "' and options ='"
					+ option + "'";
			/****
			 * Doi voi cac dich vu xo so thi lay theo companyid
			 * **/
			if (companyid > 0) {
				query3 = "select * from " + mlist + " where user_id='" + userid
						+ "' and company_id=" + companyid;

			}
			Util.logger.info("isexist_in_mlist @sql:" + query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public static boolean isexist_in_cancel(String userid, String mlist,
			String command_code, int companyid, String option) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist
					+ "_cancel where user_id='" + userid
					+ "' and upper(command_code)='"
					+ command_code.trim().toUpperCase() + "' and options ='"
					+ option + "'";
			/****
			 * Doi voi cac dich vu xo so thi lay theo companyid
			 * **/
			if (companyid > 0) {
				query3 = "select * from " + mlist + "_cancel where user_id='"
						+ userid + "' and company_id=" + companyid;

			}
			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
				// tempMilisec = (String) item.elementAt(0);
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private int savefirstmo(MsgObject msgobj, Keyword keyword) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ Constants.tblMO1Queue
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO,  REQUEST_ID) values ('"
				+ msgobj.getUserid() + "','" + msgobj.getServiceid() + "','"
				+ msgobj.getMobileoperator() + "','" + msgobj.getKeyword()
				+ "','" + keyword.getService_ss_id() + "','"
				+ msgobj.getRequestid().toString() + "')";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + sqlInsert
						+ " Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  "
					+ sqlInsert + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;

	}

	/***
	 * xoa trong mlist(mlist or mlist_cancel)
	 * **/
	public static int DelMlist(String mlist, String user_id,
			String command_code, String option) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqldel = "DELETE FROM " + mlist + " WHERE user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "' and OPTIONS = '" + option + "'";

		Util.logger.info("DBUtil@DelMlist@SQL delete: " + sqldel);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqldel) < 0) {
				Util.logger.error("Insert2Mlist@"
						+ ": delete Statement: Delete  " + mlist + " Failed:"
						+ sqldel);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("Insert2Mlist@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static int DelTable(String mlist, String user_id,
			String command_code, String option) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqldel = "DELETE FROM " + mlist + " WHERE user_id='" + user_id
				+ "' and command_code='" + command_code + "' and options ='"
				+ option + "'";

		Util.logger.info("DBUtil@DelMlist@SQL delete: " + sqldel);

		try {

			connection = dbpool.getConnection(PushMTConstants.pushMTPool);
			if (DBUtil.executeSQL(connection, sqldel) < 0) {
				Util.logger.error(" delete Statement: Delete  " + mlist
						+ " Failed:" + sqldel);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("Delete  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * Trong truong hop dang ky moi, insert vao mlist
	 * **/
	public static int Insert2Mlist(String mlist, MsgObject ems, String mtfree,
			int msgtype, long lduration, long amount) {
		int ireturn = 1;

		if (ems.getPkgService())
			return 1;

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "INSERT INTO "
				+ mlist
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,ACTIVE,CHANNEL_TYPE,REG_COUNT, IS_ICOM) values ('"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getOption()
				+ "',"
				+ 0
				+ ",'"
				+ ems.getLast_code()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + ems.getCommandCode()
				+ "','" + ems.getLongRequestid() + "','" + msgtype + "','"
				+ ems.getMobileoperator() + "','" + 0 + "'," + mtfree + ","
				+ lduration + "," + amount + "," + ems.getContentId() + ",'"
				+ ems.getCommandCode() + "','" + ems.getCompany_id() + "'," + 0
				+ "," + ems.getChannelType() + "," + 1 + "," + ems.getIsIcom()
				+ ")";

		Util.logger.info("@Insert2Mlist@SQL Insert: " + sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("Insert2Mlist@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("Insert2Mlist@:Insert  " + mlist + " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * Trong truong hop dang ky lai, move toan bo tu mlist_cancel sang mlist
	 * **/
	public static int InsertMlistCancel2Mlist(String mlist, String user_id,
			long amount, int channelType, String command_code,
			String sOptionsHoangDao, int isIcom, String option) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,IS_ICOM)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
				+ channelType + ",REG_COUNT +1, " + isIcom + " from " + mlist
				+ "_cancel WHERE USER_ID='" + user_id
				+ "' and upper(COMMAND_CODE)='" + command_code.toUpperCase()
				+ "' and options ='" + option + "'";

		if (!"".equalsIgnoreCase(sOptionsHoangDao)) {
			sqlInsert = "insert into "
					+ mlist
					+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
					+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
					+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,IS_ICOM)"
					+ " select USER_ID,SERVICE_ID,DATE,'"
					+ sOptionsHoangDao
					+ "',FAILURES,LAST_CODE,current_timestamp()"
					+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
					+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
					+ channelType + ",REG_COUNT +1, ,IS_ICOM from " + mlist
					+ "_cancel WHERE USER_ID='" + user_id
					+ "' and upper(COMMAND_CODE)='"
					+ command_code.toUpperCase() + "' and options ='" + option
					+ "'";
		}
		Util.logger.info("DbUtil@InsertMlistCancel2Mlist@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlistCancel2Mlist@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlistCancel2Mlist@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * Trong truong hop huy, move toan bo tu mlist sang mlist_cancel
	 * **/
	private static int InsertMlist2MlistCancel(String mlist, String user_id,
			long amount, int channelType, String command_code, int isIcom,
			String option) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "_cancel(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,IS_ICOM)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
				+ channelType + ",REG_COUNT," + isIcom + " from " + mlist
				+ " WHERE USER_ID='" + user_id + "' and upper(COMMAND_CODE)='"
				+ command_code.toUpperCase() + "' and options ='" + option
				+ "'";

		Util.logger.info("DbUtil@InsertMlist2MlistCancel@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlist2MlistCancel@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlist2MlistCancel@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/***
	 * Trong truong hop huy, move toan bo tu mlist sang mlist_cancel
	 * **/
	private static int InsertMlist2MlistCancel(String mlist, String user_id,
			long amount, int channelType, String command_code, String option) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "_cancel(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,IS_ICOM)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ "," + amount + ",CONTENT_ID,SERVICE,COMPANY_ID,0,"
				+ channelType + ",REG_COUNT from " + mlist + " WHERE USER_ID='"
				+ user_id + "' and upper(COMMAND_CODE)='"
				+ command_code.toUpperCase() + "' and options ='" + option
				+ "'";

		Util.logger.info("DbUtil@InsertMlist2MlistCancel@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlist2MlistCancel@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlist2MlistCancel@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static int InsertSubcriber(String mlist, MsgObject ems,
			String mtfree, int msgtype) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into mlist_subcriber(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,channel_type,options,reg_count,IS_ICOM) values ('"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getLongRequestid()
				+ "','"
				+ msgtype
				+ "','"
				+ ems.getMobileoperator()
				+ "',"
				+ mtfree
				+ ","
				+ ems.getCompany_id()
				+ ","
				+ ems.getChannelType()
				+ ",'"
				+ ems.getOption() + "',1," + ems.getIsIcom() + ")";
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

	/*****
	 * Move tu mlist sang mlist_subcriber_cancel Move xong thi Xoa user id ra
	 * khoi Subcriber *
	 ****/
	public static void MoveMlist2SubcriberCancel(String mlist, String user_id,
			String command_code, int channel_type, int company_id, String option) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String whereClause = "where user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "' and options ='" + option + "'";
		if (company_id > 0) {
			whereClause = "where user_id='" + user_id + "' and company_id="
					+ company_id + " and options ='" + option + "'";
		}
		String sqlUpdateMlistUser = "insert into mlist_subcriber_cancel(service,user_id,service_id,date,options"
				+ ",failures,last_code,autotimestamps,command_code,request_id,message_type,mobile_operator,mt_count"
				+ ",mt_free,company_id,duration,active,channel_type,reg_count,IS_ICOM)"
				+ " select service,user_id,service_id,date,options,failures,last_code,autotimestamps,command_code"
				+ ",request_id,message_type,mobile_operator,mt_count,mt_free,company_id,duration,active,"
				+ channel_type
				+ ",reg_count,IS_ICOM "
				+ " from "
				+ mlist
				+ " "
				+ whereClause;

		Util.logger
				.info("DBUtil@MoveSubcriber2SubcriberCancel:Sql insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DBUtil@MoveSubcriber2SubcriberCancel@:move mlist_subcriber to mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	/*****
	 * Move mlist cancel sang mlist_subcriber
	 * 
	 * *
	 ****/
	public static void MoveMlistCancel2Subcriber(String mlist, String user_id,
			String command_code, int channel_type, int company_id, String option) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String whereClause = "where user_id='" + user_id
				+ "' and upper(command_code)='" + command_code.toUpperCase()
				+ "' and options ='" + option + "'";
		if (company_id > 0) {
			whereClause = "where user_id='" + user_id + "' and company_id="
					+ company_id + " and options ='" + option + "'";
		}
		String sqlUpdateMlistUser = "insert into mlist_subcriber(service,user_id,service_id,date,options"
				+ ",failures,last_code,autotimestamps,command_code,request_id,message_type,mobile_operator,mt_count"
				+ ",mt_free,company_id,duration,active,channel_type,reg_count, IS_ICOM)"
				+ " select service,user_id,service_id,date,options,failures,last_code,autotimestamps,command_code"
				+ ",request_id,message_type,mobile_operator,mt_count,mt_free,company_id,duration,active,"
				+ channel_type
				+ ",reg_count + 1,IS_ICOM "
				+ " from "
				+ mlist
				+ "_cancel " + whereClause;

		Util.logger
				.info("DBUtil@MoveSubcriberCancel2Subcriber:Sql insert into:"
						+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DBUtil@MoveSubcriberCancel2Subcriber@:move mlist_subcriber to mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static void DeleteSubcriber(String user_id, String command_code,
			int channel_type, String option) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// delete khoi danh sach user
		String sqlUpdateMlistUser = "delete from mlist_subcriber where user_id='"
				+ user_id
				+ "' and upper(command_code)='"
				+ command_code.toUpperCase()
				+ "' and options ='"
				+ option
				+ "'";

		Util.logger.info("DBUtil@DeleteSubcriber:Sql delete:"
				+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DeleteSubcriber@:Delete  mlist_subcriber, user_id="
							+ user_id + ",command_code=" + command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public static void DeleteSubcriberCancel(String user_id,
			String command_code, int channel_type, String option) {
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// delete khoi danh sach user
		String sqlUpdateMlistUser = "delete from mlist_subcriber_cancel where user_id='"
				+ user_id
				+ "' and upper(command_code)='"
				+ command_code.toUpperCase()
				+ "' and options ='"
				+ option
				+ "'";

		Util.logger.info("DBUtil@DeleteSubcriberCancel:Sql delete:"
				+ sqlUpdateMlistUser);

		try {

			connection = dbpool.getConnectionGateway();

			DBUtil.executeSQL("gateway", sqlUpdateMlistUser);

		} catch (Exception ex) {
			Util.logger
					.error("DeleteSubcriberCancel@:Delete  mlist_subcriber_cancel, user_id="
							+ user_id
							+ ",command_code="
							+ command_code
							+ " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private static String[] RegisterMTReturn(MsgObject msgObject,
			Keyword keyword, int msgtype, int typeOfService, Hashtable services)
			throws Exception {

		HashMap _option = new HashMap();
		String MLIST = "";

		String options = keyword.getOptions();
		_option = Util.getParametersAsString(options);
		MLIST = Util.getStringfromHashMap(_option, "mlist", MLIST);

		String companyid = Util.getStringfromHashMap(_option, "companyid", "0");
		int iCompanyId = Integer.parseInt(companyid);
		String mtfree = Util.getStringfromHashMap(_option, "mtfree", "0");
		mtfree = "0";
		long duration = keyword.getDuration();
		String command_code = keyword.getService_ss_id();
		Boolean ktpro = false;
		// check in Push MT Promotion
		// DANND
		for (int i = 0; i < PushMTConstants.PUSH_MT_SERVICE.length; i++) {
			if (command_code.startsWith(PushMTConstants.PUSH_MT_SERVICE[i])) {
				if (PushMTConstants.isInPromo()) {
					MTPushCommon commonObj = new MTPushCommon();
					String tableName = PushMTConstants.TABLE_MT_PUSH + "_"
							+ PushMTConstants.PUSH_MT_SERVICE[i].toLowerCase();

					// Kiem tra xem co trong bang mtpush
					if (commonObj.isExistInMTPush(tableName, msgObject
							.getUserid()) > 0) {
						MLIST = MLIST + PushMTConstants.PROMOTION_TAG;
						ktpro = true;
					}

				}
				break;
			}
		}

		Util.logger.info("MLIST: " + MLIST);

		// Kiem tra xem da co hay chua?
		String[] mtReturn = new String[1];
		String sOptionsHoangDao = "";
		/*****
		 * Doi voi cac dich vu cau, xo so can phai xet company id *
		 **/
		if (iCompanyId > 0)
			msgObject.setCompany_id(companyid);
		switch (typeOfService) {
		case Constants.TYPE_OF_SERVICE_HOROSCOPE:
			String info = msgObject.getUsertext().toUpperCase().trim();

			info = info.replace('-', ' ');
			info = info.replace(';', ' ');
			info = info.replace('+', ' ');
			info = info.replace('.', ' ');
			info = info.replace(',', ' ');
			info = info.replace('_', ' ');
			info = info.replace('/', ' ');
			info = info.replace('\\', ' ');

			info = Util.replaceAllWhiteWithOne(info.trim());

			String[] arrInfo = info.split(" ");

			boolean infodetails = false;
			Util.logger.info("info:" + info);

			String HOROSCOPE = "";
			if (arrInfo.length > 3) {

				String sngay = arrInfo[2];
				String sthang = arrInfo[3];

				long requesttime = Util.ValidDayMonth(sngay, sthang);

				String shoroscope = Util.getHoroscope(requesttime);
				if (!"x".equalsIgnoreCase(shoroscope)) {
					infodetails = true;
					HOROSCOPE = shoroscope;
				}
			}
			if (infodetails == false) {
				mtReturn[0] = keyword.getErrMsg();
				return mtReturn;
			}
			msgObject.setOption(HOROSCOPE);
			sOptionsHoangDao = HOROSCOPE;
			break;
		case Constants.TYPE_OF_SERVICE_CAU_XS:
			String key = Util.getStringfromHashMap(_option, "key", "XS");
			if ("901".equalsIgnoreCase(companyid)) {
				for (int i = 0; i < SubXoso.southCompanies.length; i++) {
					msgObject.setCommandCode(key + SubXoso.southNames[i]);
					msgObject.setCompany_id(SubXoso.southCompanies[i]);
					Insert2Mlist(MLIST, msgObject, mtfree, msgtype, duration,
							keyword.getAmount());
				}
			} else if ("902".equalsIgnoreCase(companyid)) {
				// Xo so mien trung
				for (int i = 0; i < SubXoso.middleCompanies.length; i++) {
					msgObject.setCommandCode(key + SubXoso.middleNames[i]);
					msgObject.setCompany_id(SubXoso.middleCompanies[i]);
					Insert2Mlist(MLIST, msgObject, mtfree, msgtype, duration,
							keyword.getAmount());
				}
			}
			break;
		case Constants.TYPE_OF_SERVICE_THOI_TIET:
			String infott = msgObject.getUsertext().toUpperCase().trim();

			infott = infott.replace('-', ' ');
			infott = infott.replace(';', ' ');
			infott = infott.replace('+', ' ');
			infott = infott.replace('.', ' ');
			infott = infott.replace(',', ' ');
			infott = infott.replace('_', ' ');
			infott = infott.replace('/', ' ');
			infott = infott.replace('\\', ' ');
			// infott = infott.replace("THOITIET", "THOITIET ");
			infott = infott.replace(keyword.getService_ss_id(), keyword
					.getService_ss_id()
					+ " ");

			infott = Util.replaceAllWhiteWithOne(infott.trim());

			String[] sInfo = infott.split(" ");

			// bug
			msgObject.setOption("");
			/**
			 * tuy thuoc vao khach hang sms thoi tiet len se lay ra
			 * command_code, VD: huy thoitiet -> command_code = thoitiet huy
			 * thoitiet hahoi -> command_code = thoitiethanoi
			 */
			if (sInfo.length == 1)
				msgObject.setCommandCode(keyword.getService_ss_id() + " "
						+ sInfo[0].trim());
			if (sInfo.length == 2)
				msgObject.setCommandCode(keyword.getService_ss_id() + " "
						+ sInfo[1].trim());
			if (sInfo.length == 3) {
				msgObject.setOption(sInfo[2].trim());
				msgObject.setCommandCode(sInfo[1].trim() + sInfo[2].trim());
			}

			command_code = msgObject.getCommandCode();
			break;
		default:
			break;
		}
		msgObject.setOption("");
		if (msgObject.getUsertext().split(" ").length > 2) {
			msgObject.setOption(msgObject.getUsertext().split(" ")[2]);
		}
		msgObject.setCommandCode(keyword.getService_ss_id().trim());
		command_code = keyword.getService_ss_id().trim();
		Util.logger.info("Command_code cua object:"
				+ msgObject.getCommandCode() + "\t@command_code config:"
				+ keyword.getService_ss_id());
		// tuannq add for daugia
		if (command_code.equals("DA")) {
			mtReturn[0] = registerDA(msgObject, MLIST, iCompanyId, keyword);
			return mtReturn;
		}
		if (command_code.equals("DAUGIA")) {
			mtReturn[0] = registerDG(msgObject, MLIST, iCompanyId, keyword);
			return mtReturn;
		}

		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, command_code,
				iCompanyId, msgObject.getOption())) {
			mtReturn[0] = keyword.getExistMsg();
			if (msgObject.getUsertext().equals("DG")) {

			}
		} else {
			/****
			 * 2010-11-06: PhuongDT Trong truong hop chua co dich vu: - da ton
			 * tai trong mlist_cancel: move toan bo tu mlist_cancel, increase
			 * reg_count ++; update autotime=current_date - neu khong ton tai
			 * trong mlist_cancel: truong hop dang ky moi
			 * **/

			/**
			 * 2011-05-05: HaPTT: neu khuyen mai thi lay mt dky khac:
			 * 
			 ***/
			mtReturn[0] = keyword.getSubMsg();
			if (ktpro) {
				mtReturn[0] = Constants._prop.getProperty(
						"MTSUB_" + command_code, keyword.getSubMsg()).trim();
			}
			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (isexist_in_cancel(msgObject.getUserid(), MLIST, command_code,
					iCompanyId, msgObject.getOption())) {
				InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(), keyword
						.getAmount(), msgObject.getChannelType(), command_code,
						sOptionsHoangDao, msgObject.getIsIcom(), msgObject
								.getOption());
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				MoveMlistCancel2Subcriber("mlist_subcriber", msgObject
						.getUserid(), command_code, msgObject.getChannelType(),
						iCompanyId, msgObject.getOption());
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				DelMlist(MLIST + "_cancel", msgObject.getUserid(),
						command_code, msgObject.getOption());
				/******
				 * delete Subcriber_cancel *
				 **/
				DeleteSubcriberCancel(msgObject.getUserid(), command_code,
						msgObject.getChannelType(), msgObject.getOption());

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					updateLastCode(MLIST, msgObject.getUserid(), msgObject
							.getCommandCode(), msgObject.getLast_code());

				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0 2010-11-12: change Mode_adv:
				 * khong dung key nay nua, chuyen qua viec check trong bang
				 * services active_free=0: khong khuyen mai, = 1: khuyen mai
				 * 
				 * **/
				icom.Services service = icom.Services.getService(command_code,
						services);

				if (Util.IsServiceFree(service)) {
					Util.logger.info("Dich vu " + command_code
							+ " duoc free nen lay promoMsg \tuser_id:"
							+ msgObject.getUserid());
					mtReturn[0] = keyword.getPromoMsg();
					mtfree = service.getNumberFree() + "";
					msgtype = 0;
					// mtfree = "0";
				} else {
					Util.logger.info("Dich vu " + command_code + " duoc free?"
							+ Util.IsServiceFree(service));
					mtfree = "0";
				}

				msgObject.setCommandCode(command_code);
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				Insert2Mlist(MLIST, msgObject, mtfree, msgtype, duration,
						keyword.getAmount());
				/******
				 * Them user_id vao Subcriber
				 * 
				 * *
				 **/
				Util.logger
						.info("DBUtil check info to insert subsciber:@Mlist:"
								+ MLIST + "\t@CommandCode:"
								+ msgObject.getCommandCode() + "\t@user:"
								+ msgObject.getUserid());

				InsertSubcriber(MLIST, msgObject, mtfree, msgtype);
			}
		}
		return mtReturn;
	}

	/**
	 * Tuannq add for daugia truong hop keyword ung voi DA
	 * 
	 * @param msgObject
	 * @param MLIST
	 * @param iCompanyId
	 * @param keyword
	 * @return
	 */
	public static String registerDA(MsgObject msgObject, String MLIST,
			int iCompanyId, Keyword keyword) {
		String mtReturn = "";
		String options = "";
		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, "DA", iCompanyId,
				options)
				|| isexist_in_mlist(msgObject.getUserid(), MLIST, "DAUGIA",
						iCompanyId, options)) {
			mtReturn = keyword.getExistMsg();
		} else {
			/****
			 * Trong truong hop chua co dich vu: - da ton tai trong
			 * mlist_cancel: move toan bo tu mlist_cancel, increase reg_count
			 * ++; update autotime=current_date - neu khong ton tai trong
			 * mlist_cancel: truong hop dang ky moi
			 * **/

			mtReturn = keyword.getSubMsg();

			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (isexist_in_cancel(msgObject.getUserid(), MLIST, "DA",
					iCompanyId, options)) {
				InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(), keyword
						.getAmount(), msgObject.getChannelType(), "DA", "",
						msgObject.getIsIcom(), options);
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				MoveMlistCancel2Subcriber("mlist_subcriber", msgObject
						.getUserid(), "DAUGIA", msgObject.getChannelType(),
						iCompanyId, options);
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				DelMlist(MLIST + "_cancel", msgObject.getUserid(), "DA", "");

				/******
				 * delete Subcriber_cancel *
				 **/
				DeleteSubcriberCancel(msgObject.getUserid(), "DAUGIA",
						msgObject.getChannelType(), options);

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					updateLastCode(MLIST, msgObject.getUserid(), msgObject
							.getCommandCode(), msgObject.getLast_code());
				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0: change Mode_adv: khong dung
				 * key nay nua, chuyen qua viec check trong bang services
				 * active_free=0: khong khuyen mai, = 1: khuyen mai Doi voi
				 * truong hop nay khong co khuyen mai
				 * **/
				String mtfree = "0";
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				Insert2Mlist(MLIST, msgObject, mtfree, 1, 0, keyword
						.getAmount());
				/******
				 * Them user_id vao Subcriber Doi command_code thanh daugia de
				 * insert vao bang subcrible *
				 **/
				msgObject.setCommandCode("DAUGIA");
				InsertSubcriber(MLIST, msgObject, mtfree, 1);
			}
		}
		return mtReturn;
	}

	/**
	 * tuannq add for daugia ung voi command_code DG
	 * 
	 * @param msgObject
	 * @param MLIST
	 * @param iCompanyId
	 * @param keyword
	 * @return
	 */
	public static String registerDG(MsgObject msgObject, String MLIST,
			int iCompanyId, Keyword keyword) {
		String mtReturn = "";
		String options = "";
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, "DA", iCompanyId,
				options)) {
			String strUpdate = "UPDATE "
					+ MLIST
					+ " SET command_code = 'DAUGIA', service ='DAUGIA' WHERE user_id = '"
					+ msgObject.getUserid() + "'";
			Util.logger.info("registerDG @strUpdate :" + strUpdate);
			if (Update(strUpdate))
				Util.logger.info("registerDG @strUpdate  success!");
		}
		/*****
		 * Neu da ton tai trong mlist thi lay exist message tu keyword
		 * **/
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, "DAUGIA",
				iCompanyId, options)) {
			mtReturn = keyword.getExistMsg();
		} else {
			/****
			 * Trong truong hop chua co dich vu: - da ton tai trong
			 * mlist_cancel: move toan bo tu mlist_cancel, increase reg_count
			 * ++; update autotime=current_date - neu khong ton tai trong
			 * mlist_cancel: truong hop dang ky moi
			 * **/

			mtReturn = keyword.getSubMsg();

			/****
			 * Neu da ton tai trong mlist_cancel: Dang ky lai Move toan bo tu
			 * mlist_cancel sang, increase reg_count ++, update autotimes
			 * **/
			if (isexist_in_cancel(msgObject.getUserid(), MLIST, "DAUGIA",
					iCompanyId, options)) {
				InsertMlistCancel2Mlist(MLIST, msgObject.getUserid(), keyword
						.getAmount(), msgObject.getChannelType(), "DAUGIA", "",
						msgObject.getIsIcom(), options);
				/******
				 * move mlist_cancel sang subcriber *
				 **/
				MoveMlistCancel2Subcriber("mlist_subcriber", msgObject
						.getUserid(), "DAUGIA", msgObject.getChannelType(),
						iCompanyId, options);
				/***
				 * Sau khi move sang mlist thi xoa ben mlist_cancel
				 * **/
				DelMlist(MLIST + "_cancel", msgObject.getUserid(), "DAUGIA", "");
				/******
				 * delete Subcriber_cancel *
				 **/
				DeleteSubcriberCancel(msgObject.getUserid(), "DAUGIA",
						msgObject.getChannelType(), options);

				if (msgObject.getLast_code() != null
						&& !msgObject.getLast_code().equals("0")
						&& !msgObject.getLast_code().trim().equals("")) {

					updateLastCode(MLIST, msgObject.getUserid(), msgObject
							.getCommandCode(), msgObject.getLast_code());
				}

			} else {
				/****
				 * Neu khong ton tai trong mlist_cancel: Dang ky moi Trong giai
				 * doan khuyen mai, MODE_ADV = 0: change Mode_adv: khong dung
				 * key nay nua, chuyen qua viec check trong bang services
				 * active_free=0: khong khuyen mai, = 1: khuyen mai Doi voi
				 * truong hop nay khong co khuyen mai
				 * **/
				String mtfree = "0";
				/*****
				 * String mlist, MsgObject ems, String mtfree, int msgtype, long
				 * lduration, long amount *
				 **/
				Insert2Mlist(MLIST, msgObject, mtfree, 1, 0, keyword
						.getAmount());
				/******
				 * Them user_id vao Subcriber Doi command_code thanh daugia de
				 * insert vao bang subcrible *
				 **/
				msgObject.setCommandCode("DAUGIA");
				InsertSubcriber(MLIST, msgObject, mtfree, 1);
			}
		}

		return mtReturn;
	}

	/***
	 * Dang ky dich vu: - neu dang ky moi: insert vao list - neu da dang ky:
	 * thong bao la da dang ky - neu dang trong mlist_cancel: move toan bo tu
	 * mlist_cancel sang mlist, update autotime, reg_count++
	 * **/
	public static Collection RegisterServices(MsgObject msgObject,
			Keyword keyword, int typeOfService, Hashtable servicces)
			throws Exception {
		Collection messages = new ArrayList();

		int msg1mt = Integer.parseInt(Constants.MT_PUSH);
		int msg2mt = Integer.parseInt(Constants.MT_CHARGING);

		Util.logger.info("Service_Type: " + keyword.getService_type());
		Util.logger.info("Package: " + Constants.PACKAGE_SERVICE);

		String[] sMTReturn = RegisterMTReturn(msgObject, keyword, msg2mt,
				typeOfService, servicces);

		if (sMTReturn.length >= 1) {
			for (int j = 0; j < sMTReturn.length; j++) {
				if (j == 0) {
					msgObject.setMsgtype(0);
					if (msg1mt == Integer.parseInt(Constants.MT_NOCHARGE)) {
						msgObject.setAmount(0);
					}
				} else {
					msgObject.setMsgtype(Integer
							.parseInt(Constants.MT_NOCHARGE));
					msgObject.setAmount(0);
				}
				msgObject.setContenttype(0);
				msgObject.setUsertext(sMTReturn[j]);
				messages.add(new MsgObject(msgObject));
			}
		}

		return messages;
	}

	private static String[] UnRegisterMTReturn(MsgObject msgObject,
			Keyword keyword, int msgtype) throws Exception {

		String SERVICE_THOITIET = "THOITIET";

		HashMap _option = new HashMap();

		String MLIST = "mlist";

		String options = keyword.getOptions();
		_option = Util.getParametersAsString(options);

		String command_code = keyword.getService_ss_id();
		msgObject.setOption("");
		if (msgObject.getUsertext().split(" ").length > 2) {
			msgObject.setOption(msgObject.getUsertext().split(" ")[2]);
		}
		// DanNd Add for THOITIET Service
		if (command_code.equals(SERVICE_THOITIET)) {
			String infott = msgObject.getUsertext().toUpperCase().trim();

			infott = infott.replace('-', ' ');
			infott = infott.replace(';', ' ');
			infott = infott.replace('+', ' ');
			infott = infott.replace('.', ' ');
			infott = infott.replace(',', ' ');
			infott = infott.replace('_', ' ');
			infott = infott.replace('/', ' ');
			infott = infott.replace('\\', ' ');

			infott = infott.replace("THOITIET", "THOITIET ");

			infott = Util.replaceAllWhiteWithOne(infott.trim());

			String[] sInfo = infott.split(" ");
			msgObject.setCommandCode(keyword.getService_ss_id() + " "
					+ sInfo[2]);
			command_code = msgObject.getCommandCode();
		}
		
		command_code = keyword.getService_ss_id().trim();
		msgObject.setCommandCode(keyword.getService_ss_id());
		Util.logger.info("Command code cua object:" + command_code
				+ "\t@command_code config:" + keyword.getService_ss_id());
		String companyid = Util.getStringfromHashMap(_option, "companyid", "0");
		int iCompanyId = Integer.parseInt(companyid);

		MLIST = ((String) _option.get("mlist"));
		/*
		 * HaPTT update ngay 5/5/2011 Check neu co khuyen mai, thi ktra trong
		 * bang mlist_promotion
		 */
		String[] mtReturn = new String[1];
		for (int i = 0; i < PushMTConstants.PUSH_MT_SERVICE.length; i++) {
			if (command_code.startsWith(PushMTConstants.PUSH_MT_SERVICE[i])) {
				if (PushMTConstants.isInPromo()) {
					MTPushCommon commonObj = new MTPushCommon();
					String tableName = PushMTConstants.TABLE_MT_PUSH + "_"
							+ PushMTConstants.PUSH_MT_SERVICE[i].toLowerCase();

					// Kiem tra xem co trong bang mtpush
					if (commonObj.isExistInMTPush(tableName, msgObject
							.getUserid()) > 0) {
						MLIST = MLIST + PushMTConstants.PROMOTION_TAG;
						mtReturn[0] = keyword.getUnsubMsg();

						if (isexist_in_mlist(msgObject.getUserid(), MLIST,
								command_code, iCompanyId, msgObject.getOption())) {

							/****
							 * Neu ton tai trong mlist, lay thong bao huy dich
							 * vu thanh cong, chuyen thong tin sang mlist_cancel
							 * **/
							InsertMlist2MlistCancel(MLIST, msgObject
									.getUserid(), keyword.getAmount(),
									msgObject.getChannelType(), command_code,
									msgObject.getIsIcom(), msgObject
											.getOption());

							DelMlist(MLIST, msgObject.getUserid(),
									command_code, msgObject.getOption());

						}
						InsertMtpush2MtpushCancel(tableName, msgObject
								.getUserid(), command_code);
						DelTable(tableName, msgObject.getUserid(),
								command_code, msgObject.getOption());
						return mtReturn;
					}
				}
				break;
			}
		}

		if (command_code.equals("DA") || command_code.equals("DAUGIA")) {
			mtReturn[0] = logoutDauGia(msgObject, MLIST, command_code, keyword,
					iCompanyId);
			return mtReturn;
		}
		msgObject.setOption("");
		if (msgObject.getUsertext().split(" ").length > 2) {
			msgObject.setOption(msgObject.getUsertext().split(" ")[2]);
			
		}
		command_code = keyword.getService_ss_id().trim();
		msgObject.setCommandCode(keyword.getService_ss_id());
		Util.logger.info("Command code cua object:" + command_code
				+ "\t@command_code config:" + keyword.getService_ss_id()
				+ "\t@option:" + msgObject.getOption() + "\t@userText:"
				+ msgObject.getUsertext());
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, command_code,
				iCompanyId, msgObject.getOption())) {
			mtReturn[0] = keyword.getUnsubMsg();
			/****
			 * Neu ton tai trong mlist, lay thong bao huy dich vu thanh cong,
			 * chuyen thong tin sang mlist_cancel
			 * **/
			InsertMlist2MlistCancel(MLIST, msgObject.getUserid(), keyword
					.getAmount(), msgObject.getChannelType(), command_code,
					msgObject.getIsIcom(), msgObject.getOption());
			/***
			 * Move sang mlist_subcriber_cancel Sau khi move xong -> xoa user_id
			 * ra khoi mlist_subcriber
			 * **/
			/*****
			 * Move tu mlist sang mlist_subcriber_cancel *
			 ****/
			MoveMlist2SubcriberCancel(MLIST, msgObject.getUserid(),
					command_code, msgObject.getChannelType(), iCompanyId,
					msgObject.getOption());
			/***
			 * Sau khi move sang mlist_subcriber_cancel thi xoa ben mlist
			 * **/
			DelMlist(MLIST, msgObject.getUserid(), command_code, msgObject
					.getOption());
			/*****************************************************************/
			/****
			 * Sau khi move xong -> xoa user_id ra khoi mlist_subcriber
			 * **/
			DeleteSubcriber(msgObject.getUserid(), command_code, msgObject
					.getChannelType(), msgObject.getOption());

		} else {
			// mtReturn[0] =
			// "Ban chua dang ky dich vu. Hay soan DK MaDichVu gui "
			// + msgObject.getServiceid() + " de dang ky dich vu khac";

			if (command_code.equals("DAUGIA")) {
				DaugiaCommon dgComm = new DaugiaCommon();
				if (!isexist_in_mlist(msgObject.getUserid(), MLIST + "_cancel",
						command_code, iCompanyId, msgObject.getOption())) {
					dgComm.insertMlistService(msgObject, keyword, MLIST
							+ "_cancel");
				}

				if (!isexist_in_mlist(msgObject.getUserid(),
						"mlist_subcriber_cancel", command_code, iCompanyId,
						options)) {
					InsertSubcriberCancel(msgObject, "0", 0);
				}
			}

			mtReturn[0] = keyword.getWarMsg();

		}

		return mtReturn;
	}

	/**
	 * DanNd Add
	 * 
	 * @param msgObject
	 * @param keyword
	 * @param msgtype
	 * @param typeOfService
	 * @return
	 * @throws Exception
	 */
	private static String[] UnRegisterMTReturnComplex(MsgObject msgObject,
			Keyword keyword, int msgtype, int typeOfService) throws Exception {

		HashMap _option = new HashMap();

		String MLIST = "mlist";

		String options = keyword.getOptions();
		_option = Util.getParametersAsString(options);

		String command_code = keyword.getService_ss_id();

		// DanNd Add for THOITIET Service
		if (typeOfService == Constants.TYPE_OF_SERVICE_THOI_TIET) {
			String infott = msgObject.getUsertext().toUpperCase().trim();

			infott = infott.replace('-', ' ');
			infott = infott.replace(';', ' ');
			infott = infott.replace('+', ' ');
			infott = infott.replace('.', ' ');
			infott = infott.replace(',', ' ');
			infott = infott.replace('_', ' ');
			infott = infott.replace('/', ' ');
			infott = infott.replace('\\', ' ');

			infott = infott.replace(command_code, command_code + " ");

			infott = Util.replaceAllWhiteWithOne(infott.trim());

			String[] sInfo = infott.split(" ");
			msgObject.setCommandCode(keyword.getService_ss_id() + " "
					+ sInfo[2]);
			command_code = msgObject.getCommandCode();
		}

		String companyid = Util.getStringfromHashMap(_option, "companyid", "0");
		int iCompanyId = Integer.parseInt(companyid);

		MLIST = ((String) _option.get("mlist"));

		// Kiem tra xem da co hay chua?
		String[] mtReturn = new String[1];

		if (isexist_in_mlist(msgObject.getUserid(), MLIST, command_code,
				iCompanyId, options)) {
			mtReturn[0] = keyword.getUnsubMsg();
			/****
			 * Neu ton tai trong mlist, lay thong bao huy dich vu thanh cong,
			 * chuyen thong tin sang mlist_cancel
			 * **/
			InsertMlist2MlistCancel(MLIST, msgObject.getUserid(), keyword
					.getAmount(), msgObject.getChannelType(), command_code,
					msgObject.getIsIcom(), options);
			/***
			 * Move sang mlist_subcriber_cancel Sau khi move xong -> xoa user_id
			 * ra khoi mlist_subcriber
			 * **/
			/*****
			 * Move tu mlist sang mlist_subcriber_cancel *
			 ****/
			MoveMlist2SubcriberCancel(MLIST, msgObject.getUserid(),
					command_code, msgObject.getChannelType(), iCompanyId,
					options);
			/***
			 * Sau khi move sang mlist_subcriber_cancel thi xoa ben mlist
			 * **/
			DelMlist(MLIST, msgObject.getUserid(), command_code, options);
			/*****************************************************************/
			/****
			 * Sau khi move xong -> xoa user_id ra khoi mlist_subcriber
			 * **/
			DeleteSubcriber(msgObject.getUserid(), command_code, msgObject
					.getChannelType(), options);

		} else {
			mtReturn[0] = "Ban chua dang ky dich vu. Hay soan DK MaDichVu gui "
					+ msgObject.getServiceid() + " de dang ky dich vu khac";
		}
		return mtReturn;
	}

	public static Collection UnRegisterServices(MsgObject msgObject,
			Keyword keyword) throws Exception {
		Collection messages = new ArrayList();

		int msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
		int msg2mt = Integer.parseInt(Constants.MT_PUSH);

		String[] sMTReturn = UnRegisterMTReturn(msgObject, keyword, msg2mt);

		Util.logger.info("Length: " + sMTReturn.length);

		if (sMTReturn.length >= 1) {
			for (int j = 0; j < sMTReturn.length; j++) {
				if (j == 0) {
					msgObject.setMsgtype(msg1mt);
				}
				msgObject.setContenttype(0);
				msgObject.setUsertext(sMTReturn[j]);
				messages.add(new MsgObject(msgObject));
			}
		} else {
			return null;
		}
		return messages;
	}

	/**
	 * DanNd Add
	 * 
	 * @param msgObject
	 * @param keyword
	 * @param typeOfService
	 * @return
	 * @throws Exception
	 */
	public static Collection UnRegisterServicesComplex(MsgObject msgObject,
			Keyword keyword, int typeOfService) throws Exception {
		Collection messages = new ArrayList();

		int msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
		int msg2mt = Integer.parseInt(Constants.MT_PUSH);

		String[] sMTReturn = UnRegisterMTReturnComplex(msgObject, keyword,
				msg2mt, typeOfService);

		Util.logger.info("Length: " + sMTReturn.length);

		if (sMTReturn.length >= 1) {
			for (int j = 0; j < sMTReturn.length; j++) {
				if (j == 0) {
					msgObject.setMsgtype(msg1mt);
				}
				msgObject.setContenttype(0);
				msgObject.setUsertext(sMTReturn[j]);
				messages.add(new MsgObject(msgObject));
			}
		} else {
			return null;
		}
		return messages;
	}

	private static int InsertMtpush2MtpushCancel(String mlist, String user_id,
			String command_code) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String sqlInsert = "insert into "
				+ mlist
				+ "_cancel(USER_ID,STATUS,COMMAND_CODE,NUMBER_MT_ALARM,LINK_RING,LAST_CODE)"
				+ " select USER_ID,STATUS,COMMAND_CODE,NUMBER_MT_ALARM,LINK_RING,LAST_CODE from "
				+ mlist + " WHERE USER_ID='" + user_id
				+ "' and upper(COMMAND_CODE)='" + command_code.toUpperCase()
				+ "'";

		Util.logger.info("DbUtil@InsertMlist2MlistCancel@SQL Insert: "
				+ sqlInsert);

		try {

			connection = dbpool.getConnection(PushMTConstants.pushMTPool);
			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertMlist2MlistCancel@"
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertMlist2MlistCancel@:Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static int updateLastCode(String mlistTable, String userId,
			String commandCode, String lastcode) {
		int iReturn = -1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "update " + mlistTable + " set last_code = '"
				+ lastcode + "' " + " where user_id = '" + userId
				+ "' and command_code = '" + commandCode + "'";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("DBUtil updateLastCode@"
						+ ": uppdate Statement: UPDATE  " + mlistTable
						+ " Failed## SQL = " + sqlUpdate);
			} else
				iReturn = 1;

		} catch (Exception ex) {
			Util.logger.error("DBUtil updateLastCodeMlist@@: UPDATE  "
					+ mlistTable + " Failed");
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}

		return iReturn;
	}

	public static int InsertSubcriberCancel(MsgObject ems, String mtfree,
			int msgtype) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into mlist_subcriber_cancel(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,channel_type,options,reg_count) values ('"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getUserid()
				+ "','"
				+ ems.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ ems.getCommandCode()
				+ "','"
				+ ems.getLongRequestid()
				+ "','"
				+ msgtype
				+ "','"
				+ ems.getMobileoperator()
				+ "',"
				+ mtfree
				+ ","
				+ ems.getCompany_id()
				+ ","
				+ ems.getChannelType()
				+ ",'"
				+ ems.getOption() + "',1)";
		Util.logger.info("DbUtil@InsertSubcriberCancel@SQL Insert: "
				+ sqlInsert);
		try {
			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("InsertSubcriberCancel@"
						+ ": insert Statement: Insert Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("InsertSubcriberCancel@:Insert Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public static String logoutDauGia(MsgObject msgObject, String MLIST,
			String command_code, Keyword keyword, int iCompanyId) {
		String strReturn = "";
		String options = "";
		if (isexist_in_mlist(msgObject.getUserid(), MLIST, "DA", iCompanyId,
				options)
				|| isexist_in_mlist(msgObject.getUserid(), MLIST, "DAUGIA",
						iCompanyId, options)) {
			strReturn = keyword.getUnsubMsg();
			/****
			 * Neu ton tai trong mlist, lay thong bao huy dich vu thanh cong,
			 * chuyen thong tin sang mlist_cancel
			 * **/
			InsertMlist2MlistCancel(MLIST, msgObject.getUserid(), keyword
					.getAmount(), msgObject.getChannelType(), "DA", msgObject
					.getIsIcom(), options);
			InsertMlist2MlistCancel(MLIST, msgObject.getUserid(), keyword
					.getAmount(), msgObject.getChannelType(), "DAUGIA",
					msgObject.getIsIcom(), options);
			/***
			 * Move sang mlist_subcriber_cancel Sau khi move xong -> xoa user_id
			 * ra khoi mlist_subcriber
			 * **/
			/*****
			 * Move tu mlist sang mlist_subcriber_cancel *
			 ****/
			MoveMlist2SubcriberCancel("mlist_subcriber", msgObject.getUserid(),
					"DAUGIA", msgObject.getChannelType(), iCompanyId, options);
			/***
			 * Sau khi move sang mlist_subcriber_cancel thi xoa ben mlist
			 * **/
			DelMlist(MLIST, msgObject.getUserid(), "DA", "");
			DelMlist(MLIST, msgObject.getUserid(), "DAUGIA", "");
			/*****************************************************************/
			/****
			 * Sau khi move xong -> xoa user_id ra khoi mlist_subcriber
			 * **/
			DeleteSubcriber(msgObject.getUserid(), "DA", msgObject
					.getChannelType(), options);
			DeleteSubcriber(msgObject.getUserid(), "DAUGIA", msgObject
					.getChannelType(), options);

		} else {
			DaugiaCommon dgComm = new DaugiaCommon();
			if (!isexist_in_mlist(msgObject.getUserid(), MLIST + "_cancel",
					"DAUGIA", iCompanyId, options)) {
				dgComm
						.insertMlistService(msgObject, keyword, MLIST
								+ "_cancel");
				msgObject.setCommandCode("DAUGIA");
				InsertSubcriberCancel(msgObject, "0", 0);
			}
			if (!isexist_in_mlist(msgObject.getUserid(), MLIST + "_cancel",
					"DA", iCompanyId, options)) {
				dgComm
						.insertMlistService(msgObject, keyword, MLIST
								+ "_cancel");
				msgObject.setCommandCode("DAUGIA");
				InsertSubcriberCancel(msgObject, "0", 0);
			}
			strReturn = keyword.getWarMsg();

		}
		return strReturn;
	}

	public static boolean isFreeBaseService(MsgObject obj) {
		boolean checkFree = false;
		Services service = null;
		try {
			service = Services.getService(obj.getServiceName(),
					LoadConfig.hServices);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
		}
		// kiem tra service do dang active hay khong
		if (service.getActiveFree() == 1) {
			// dua vao command_code -> search bang keyword
			obj.getKeyword();
		}

		return checkFree;
	}

}
