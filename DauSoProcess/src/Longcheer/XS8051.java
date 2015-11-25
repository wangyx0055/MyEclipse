package Longcheer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・
 * </pre>
 * 
 * @author Vietnamnet I-Com Haptt
 * @version 1.0
 */
public class XS8051 extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.";
			String inv_syntax = "Ban da nhan tin sai cu phap. Vui long nhan XS<Matinh> gui 8051";
			String keywords = msgObject.getKeyword();

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String type = "MB";

			try {
				Util.logger.info(this.getClass().getName() + " options: "
						+ options);
				_option = getParametersAsString(options);
				inv_syntax = getString(_option, "inv_syntax", inv_syntax);
				type = getString(_option, "type", type);

			} catch (Exception e) {
				Util.logger.error(this.getClass().getName() + "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}
			String user_id = msgObject.getUserid();
			String mobile_operator = msgObject.getMobileoperator();

			String userid = msgObject.getUserid();
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String serviceid = msgObject.getServiceid();
			String mt = "Ma tinh ban nhan chua chinh xac.";
			String[] splitresult = new String[3];
			/*
			 */
			String[] result = new String[2];

			if (type.equalsIgnoreCase("MB")) {
				result = getResult(1);
				Util.logger.info("result:" + result[0]);

				mt = "MB" + result[1] + result[0].substring(2);
				msgObject.setUsertext(mt);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				sendMT(msgObject);
				return null;
			}
			int company_id = 0;
			String company_name = "";
			int[] list_companyid = new int[3];
			userText.replaceAll(" ", "");

			if (type.equalsIgnoreCase("MT")) {
				list_companyid = getCompanyIDMT();

			}
			list_companyid = getCompanyIDMN();
			for (int i = 0; i < 3; i++) {
				company_name = getCompanyName(list_companyid[i]);
				
					result = getResult(list_companyid[i]);
					Util.logger.info("result:" + result[0]);

					mt = company_name + " " + result[1] 
							+ result[0].substring(2);
				
				if (i == 0) {
					msgObject.setMsgtype(1);

				} else
					msgObject.setMsgtype(0);

				msgObject.setUsertext(mt);

				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);


			}
			return null;
		} catch (Exception e) {

			Util.logger.error(this.getClass().getName() + "Exception:"
					+ e.getMessage());
			return null;

		} finally {

		}
	}

	public String[] getResult(int companyId) {
		Connection conn = null;
		String[] strResult = new String[2];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("lotterynew");

			sSql = " Select RESULT_TEXT,to_char(result_date,'dd/mm ') resultdate from LASTEST_RESULT_FULL where RESULT_COMPANY_ID="
					+ companyId + " and rownum=1 order by Result_id desc ";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult[0] = rs.getString("RESULT_TEXT");
					strResult[1] = rs.getString("resultdate");
				}
			}
			Util.logger.info("strResult:" + strResult);
			return strResult;

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return null;
	}

	public String[] getResultCau(int companyId) {
		Connection conn = null;
		String[] strResult = new String[2];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("servicelottery");

			sSql = "Select RESULT_TEXT,to_char(result_date,'dd/mm:') resultdate from LASTEST_CAU_FULL where RESULT_COMPANY_ID="
					+ companyId + " and rownum=1 order by Result_id desc ;";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult[0] = rs.getString("RESULT_TEXT");
					strResult[1] = rs.getString("resultdate");
				}
			}
			Util.logger.info("strResult:" + strResult);
			return strResult;

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return null;
	}

	// String sqlSelect =
	// "select RESULT_COMPANY_ID from LOTTERY_RESULTS_FULL  where  (to_char(result_date,'dd/mm/yyyy')=to_char(sysdate,'dd/mm/yyyy') or to_char(result_date,'dd/mm/yyyy')=to_char(sysdate-1,'dd/mm/yyyy')) and rownum<=3 and RESULT_COMPANY_ID in (281,271,311,301,331,251,261,341,351,211,321,241,221,291)";

	public int[] getCompanyIDMT() {
		Connection conn = null;
		int[] strResult = new int[3];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("lotterynew");

			String sqlSelect = "select RESULT_COMPANY_ID from LOTTERY_RESULTS_FULL  where  (to_char(result_date,'dd/mm/yyyy')=to_char(sysdate,'dd/mm/yyyy') or to_char(result_date,'dd/mm/yyyy')=to_char(sysdate-1,'dd/mm/yyyy')) and rownum<=3 and RESULT_COMPANY_ID in (281,271,311,301,331,251,261,341,351,211,321,241,221,291)";
			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Util.logger.info("get company_id: " + sSql);
			stmt = conn.prepareStatement(sSql);
			int i = 0;
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					strResult[i] = rs.getInt(1);
					i++;
				}
			}
			return strResult;

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return null;
	}

	public int[] getCompanyIDMN() {
		Connection conn = null;
		int[] strResult = new int[3];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("lotterynew");

			sSql = "select RESULT_COMPANY_ID from LOTTERY_RESULTS_FULL  where  (to_char(result_date,'dd/mm/yyyy')=to_char(sysdate,'dd/mm/yyyy') or to_char(result_date,'dd/mm/yyyy')=to_char(sysdate-1,'dd/mm/yyyy')) and rownum<=3 and RESULT_COMPANY_ID in (21,31,141,151,101,81,91,51,61,71,111,121,131,201,191,161,171,231,181,11,41)";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Util.logger.info("get company_id: " + sSql);
			stmt = conn.prepareStatement(sSql);
			int i = 0;
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					strResult[i] = rs.getInt(1);
					i++;
				}
			}
			return strResult;

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return null;
	}

	// Replace ____ with _
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

	/* Chia thành 2 MT */
	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;
		if (splitS.length() >= 160) {
			while (!(resultBoolean)) {
				if (splitS.charAt(i) == ';') {
					result[0] = splitS.substring(0, i);
					j = i + 1;
					resultBoolean = true;
				}
				i--;
			}
			resultBoolean = false;
			i = tempString.length() - 1;

			while (!(resultBoolean)) {
				if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
					result[1] = tempString.substring(j, i);
					resultBoolean = true;
				}
				i--;
			}
		} else {
			result[0] = splitS;
			result[1] = "";
		}

		return result;

	}

	private static String getCompany(int company_id) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "";

		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT company_code FROM companyxs WHERE company_id= "
					+ company_id;
			Util.logger.info(query);
			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return sequence_temp;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return sequence_temp;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static String getCompanyName(int company_id) {
		String Name = "";

		String[] Names = { "BDI", "DLK", "QNG", "QNA", "TTH", "KH", "DNA",
				"QB", "QT", "KT", "DNO", "NT", "GL", "PY", "VL", "TV", "DT",
				"CM", "BTH", "VT", "BL", "CT", "ST", "BT", "TN", "AG", "HCM",
				"LA", "BP", "TG", "KG", "HG", "DL", "BD", "DN", };
		int[] ID = { 281, 271, 311, 301, 331, 251, 261, 341, 351, 211, 321,
				241, 221, 291, 21, 31, 141, 151, 101, 81, 91, 51, 61, 71, 111,
				121, 131, 201, 191, 161, 171, 231, 181, 11, 41 };

		for (int i = 0; i < ID.length; i++) {
			if (company_id == ID[i]) {
				Name = Names[i];
			}
		}
		return Name;
	}

	// Insert vao database khach hang da nhan tin
	private static boolean saverequest(String dtbase, String userid,
			String dbcontent) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO " + dbcontent
					+ "(userid) VALUES ('" + userid + "' )";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into dbcontent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static boolean updateListMusic(String dtbase, String userid,
			String lastid, String dbcontent) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE " + dbcontent + " SET musichotid = '"
					+ lastid + "' WHERE upper(userid )= '"
					+ userid.toUpperCase() + "'";

			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list music to send " + userid
						+ " to dbcontent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;

		HashMap _params = new HashMap();
		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;
	}

	private static int sendMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("sendMT@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis("sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO ems_send_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("sendMT: Error:@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("sendMT: Error:@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	public static String getMobileOperatorNew(String dtbase, String userid,
			int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection(dtbase);

			String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
					+ userid + "',1, length(prefix)) ";
			if (type == 1 || type == 0) {
				query += " and type=" + type;
			}

			Vector result = DBUtil.getVectorTable(connection, query);
			for (int i = 0; i < result.size(); i++) {
				Vector item = (Vector) result.elementAt(i);
				tmpOperator = (String) item.elementAt(0);
			}

			return tmpOperator;
		} catch (Exception ex) {
			Util.logger.info("getMobileOperator: Get MobileOpereator Failed"
					+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

}
