package icom.common;



import icom.Constants;
import icom.DBPool;
import icom.MsgObject;

import java.sql.*;
import java.util.*;

public class DBUtils {

	public static String getStringValue(Connection cnn, String fldName,
			String tblName, String con) throws Exception {
		String tmpVal = "";

		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

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
			e.printStackTrace();
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
		Vector vtValue = null;
		ResultSet rs = null;

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
		Vector vtValue = null;
		ResultSet rs = null;

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
		Vector vtValue = null;
		ResultSet rs = null;

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
		Vector vt = null;
		PreparedStatement pstm = null;
		Vector vtValue = null;
		ResultSet rs = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (vtValue != null) {
				for (int i = 0; i < vtValue.size(); i++) {
					pstm.setString(i + 1, vtValue.elementAt(i).toString());
				}
				vtValue = null;
			}
			rs = pstm.executeQuery();
			vt = DBUtils.convertToVector(rs);
			// System.err.println("AAA:" + vt.size());
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

	public static List getListTable(Connection cnn, String strSQL)
			throws Exception {
		List lst = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List lsValue = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtils.convertToList(rs);
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
		List lst = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List lsValue = null;

		try {
			pstm = cnn.prepareStatement(strSQL);
			if (lsValue != null) {
				for (int i = 0; i < lsValue.size(); i++) {
					pstm.setString(i + 1, lsValue.get(i).toString());
				}
				lsValue = null;
			}
			rs = pstm.executeQuery();
			lst = DBUtils.convertToList(rs);
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
			Util.logger.info("uppdate Statement: "
					+ e.getMessage());
			return -1;
		} catch (Exception e) {
			Util.logger.info("uppdate Statement: "
					+ e.getMessage());
			return -1;
		} finally {
			closeObject(statement);

		}
	}

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

		int tksize = tk.countTokens();
		int tkcount =0;
		while (tk.hasMoreElements()) {
			String token = (String) tk.nextElement();
			tkcount ++;
			if (temp.equals("")) {
				temp = temp + token;
			} else {
				temp = temp + " " + token;
			}

			if (temp.length() > segment) {
				temp = token;
				j++;
				if((tkcount == tksize) && (j <=3)) {
					result[j]=token;
				}
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

	public static int sendMT(MsgObject msgObject) {
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
		int idsendqueue = Constants.getIDdbsendqueue(msgObject.getUserid());

		if (msgObject.getContenttype() == 0
				&& msgObject.getUsertext().length() > 160) {

			String mtcontent = msgObject.getUsertext();

			Collection listmt = splitMsg(mtcontent);
			Iterator itermt = listmt.iterator();
			int cnttype = msgObject.getContenttype();

			for (int j = 1; itermt.hasNext(); j++) {
				String temp = (String) itermt.next();

				msgObject.setUsertext(temp);
				if (j == 1) {
					msgObject.setMsgtype(cnttype);
					sendMT1(msgObject);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block

					}
				} else {
					msgObject.setMsgtype(0);
					sendMT1(msgObject);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block

					}
				}

			}
			return 1;

		} else
			return sendMT1(msgObject);

	}

	public static int sendMT1(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DBUtil@sendMT@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();
		int idsendqueue = Constants.getIDdbsendqueue(msgObject.getUserid());

		Util.logger.info("DBUtil@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getCnnSendQueue(msgObject.getUserid());
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
			String idseq = Constants.getTableSendQueue(idsendqueue);

			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ idseq
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,"
					+ " MESSAGE_ID, CONTENT_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setString(5, msgObject.getUsertext());
			statement.setTimestamp(6, null);
			statement.setTimestamp(7, null);
			statement.setInt(8, 0);
			statement.setInt(9, msgObject.getMsgtype());
			statement.setBigDecimal(10, msgObject.getRequestid());
			statement.setString(11, "1");
			statement.setInt(12, msgObject.getContenttype());
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("DBUtil@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("DBUtil@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("DBUtil@sendMT: Error:@userid="
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

	public static int Alert(String domain, String issue, String level,
			String alertmsg, String contact) {

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

			return -1;
		} finally {
			dbpool.cleanup(connection, stmt);
		}

	}

}
