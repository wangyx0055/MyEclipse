package icom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapEBC;
import com.vmg.soap.mo.SoapLiveSMS;
import com.vmg.soap.mo.SoapSky;
import com.vmg.soap.mo.SoapZonefreeMO;
import com.vmg.soap.mo.SoapZonefreeMT;
import com.vmg.sms.common.DBUtil;

public class SendMOSky extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		String mt = getString(
				_option,
				"mt",
				"Cam on ban da su dung dich vu. DTHT:1900571566. Truy cap http://www.funzone.vn");

		Collection messages = new ArrayList();

		try {
			if (msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setMsgtype(2);
				msgObject
						.setUsertext("Dich vu chua ho tro mang cua ban. DTHT 1900571566");
			}
			if (SoapSky.getMessages(msgObject))
				Util.logger.info("OK");
			else
				Util.logger.info("Chua post duoc");
			msgObject.setUsertext(mt);
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
			Thread.sleep(1000);
			return null;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

		}

	}

	public static int InsertCdr_queue(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("DBUtil@InsertCdr_queue@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("InsertCdr_queue@InsertCdr_queue@userid="
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("InsertCdr_queue@InsertCdr_queue: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}
			sqlString = "INSERT INTO cdr_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, "
					+ "COMMAND_CODE, INFO, SUBMIT_DATE, DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID,cpid) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?,?)";

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
			statement.setInt(11, msgObject.getCpid());
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("InsertCdr_queue: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("InsertCdr_queue: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("InsertCdr_queue: Error:@userid="
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

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			int day_charge, int day_14) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(user_id, service_id, date,command_code,request_id,mobile_operator,day_charge,day_14) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + msgObject.getKeyword()
				+ "'," + msgObject.getRequestid() + ",'"
				+ msgObject.getMobileoperator() + "'," + day_charge + ","
				+ day_14 + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
			if (temp == null) {
				return _defaultval;
			}
			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"));
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	public static String getMobileOperatorNew(String userid, int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("gateway");

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
			Util.logger.sysLog(LogValues.INFORMATIVE, "Utils",
					"getMobileOperator: Get MobileOpereator Failed"
							+ ex.toString());
			return tmpOperator;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private static int getValidPhone(String[] sTokens) {
		int place = 0;
		for (int i = 0; i < sTokens.length; i++) {
			if (!"-".equalsIgnoreCase(ValidISDNNew(sTokens[i]))) {
				place = i;
				return place;
			}
		}
		return place;
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Long.parseLong(sISDN);

			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
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

	private static boolean isexist(String userid, String mlist) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "' ";

			Util.logger.info(query3);
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
}
