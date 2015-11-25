package livesms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.vmg.soap.mo.SoapLiveSMS;

public class GHSMS extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		_option = getParametersAsString(options);

		Collection messages = new ArrayList();
		String sms = getStringfromHashMap(_option, "sms", "DK+LS");

		try {
			String[] info = msgObject.getUsertext().split(" ");
			Calendar cal = Calendar.getInstance();
			int nowday = cal.get(Calendar.DATE);
			int nowmonth = cal.get(Calendar.MONTH) + 1;
			int nowyear = cal.get(Calendar.YEAR);

			Calendar cal30 = Calendar.getInstance();
			cal30.add(Calendar.MONTH, 1);
			int day30 = cal.get(Calendar.DATE);
			int month30 = cal.get(Calendar.MONTH) + 1;
			int year30 = cal.get(Calendar.YEAR);

			Calendar last = Calendar.getInstance();
			last.add(Calendar.MONTH, -1);
			int lday = last.get(Calendar.DATE);
			int lmonth = last.get(Calendar.MONTH) + 1;
			int lyear = last.get(Calendar.YEAR);
			String timelast = lyear + "-" + FormatNumber(lmonth) + "-"
					+ FormatNumber(lday);
			String time = isexistCDRlog(msgObject.getUserid(),
					"cdr_livesms_log", timelast);

			if (!isexist(msgObject.getUserid(), "mlist_livesms")) {
				msgObject
						.setUsertext("Ban chua dang ky su dung Live SMS. De dang ky, soan DKLS gui 8551 de su dung dich vu.DTHT:1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			} else {
				/*
				 * Gia han neu chua co thi insert, neu co trong thang roi thi
				 * cong them cho thang sau
				 */
				if (!time.equalsIgnoreCase("")) {
					DateFormat formatter;
					Date date;
					formatter = new SimpleDateFormat("yyyy-MM-dd");
					date = (Date) formatter.parse(time);
					Calendar calnew = Calendar.getInstance();
					calnew.setTime(date);
					calnew.add(Calendar.MONTH, 1);
					int newday = calnew.get(Calendar.DATE);
					int newmonth = calnew.get(Calendar.MONTH) + 1;
					int newyear = calnew.get(Calendar.YEAR);

					calnew.add(Calendar.MONTH, 1);
					int newday30 = calnew.get(Calendar.DATE);
					int newmonth30 = calnew.get(Calendar.MONTH) + 1;
					int newyear30 = calnew.get(Calendar.YEAR);
					String daycharge = getDaycharge(msgObject.getUserid(),
							"cdr_livesms_log", timelast);
					String newtime = newyear + "-" + FormatNumber(newmonth)
							+ "-" + FormatNumber(newday);

					insertCDRlog2(msgObject.getUserid(), msgObject
							.getServiceid(), msgObject.getServiceid(),
							msgObject, daycharge, 1, newtime);

				} else {

					insertCDRlog(msgObject.getUserid(), msgObject
							.getServiceid(), msgObject.getServiceid(),
							msgObject, nowday + "", 1);

				}
				deleteUser(msgObject.getUserid(), "cdr_livesms_queue");
				String sinfo=msgObject.getUsertext();
				
				msgObject.setUsertext(sinfo.replace(" ", "+"));
				if (SoapLiveSMS.getMessages(msgObject))
					Util.logger.info(msgObject.getUserid()+"@"+msgObject.getUsertext()+"OK");
					else

					Util.logger.info("Chua post duoc");

				
			}
			return null;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

		}

	}

	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}

	private int insertCDRlog(String user_id, String service_id,
			String command_code, MsgObject msgObject, String day_charge,
			int status) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into cdr_livesms_log (user_id,autotimestamps, service_id, date,command_code,request_id,mobile_operator,day_charge,status) values ('"
				+ user_id
				+ "','"
				+ new SimpleDateFormat("dd/MM/yyyy")
						.format(new Date())
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("dd/MM/yyyy")
						.format(new Date())
				+ "','"
				+ msgObject.getKeyword()
				+ "',"
				+ msgObject.getRequestid()
				+ ",'"
				+ msgObject.getMobileoperator() + "',"

				+ day_charge + "," + status + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  cdrfarm_queue Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert cdrfarm_queue Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private int insertCDRlog2(String user_id, String service_id,
			String command_code, MsgObject msgObject, String day_charge,
			int status, String time) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into cdr_livesms_log (user_id,autotimestamps, service_id, date,command_code,request_id,mobile_operator,day_charge,status) values ('"
				+ user_id
				+ "','"
				+ time
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("dd/MM/yyyy")
						.format(new Date())
				+ "','"
				+ msgObject.getKeyword()
				+ "',"
				+ msgObject.getRequestid()
				+ ",'"
				+ msgObject.getMobileoperator() + "',"

				+ day_charge + "," + status + ")";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  cdrfarm_queue Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert cdrfarm_queue Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	static String GetMonth(String time) {
		try {
			String month = "";
			month = time.substring(5, 7);
			return month;
		} catch (Exception e) {
			return "";
		}
	}

	public static int GetDay(String time) {
		try {
			String month = "";
			month = time.substring(8, 10);

			return Integer.parseInt(month);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int GetYear(String time) {
		try {
			String month = "";
			month = time.substring(0, 4);

			return Integer.parseInt(month);
		} catch (Exception e) {
			return 0;
		}
	}

	private static String isexistCDRlog(String userid, String table, String time) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		String tempMilisec = "";
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select date(autotimestamps) from " + table
					+ " where user_id='" + userid
					+ "'  and  date(autotimestamps) > '" + time
					+ "' order by autotimestamps desc limit 1 ";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);

				tempMilisec = (String) item.elementAt(0);
			}
			return tempMilisec;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static String getDaycharge(String userid, String table, String time) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		String tempMilisec = "";
		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select day_charge from " + table
					+ " where user_id='" + userid
					+ "'  and  date(autotimestamps) > '" + time
					+ "' order by autotimestamps desc limit 1 ";

			Util.logger.info(query3);

			Vector result3 = DBUtil.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);

				tempMilisec = (String) item.elementAt(0);
			}
			return tempMilisec;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return tempMilisec;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	private static boolean deleteUser(String user, String table) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM " + table + "  WHERE user_id='"
					+ user + "'";
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger
						.info("Loi xoa user " + user + "trong bang " + table);
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

	private static String checkMobileOperator(String mobile_operator) {

		String infoid = "other";
		if ("VIETTEL".equalsIgnoreCase(mobile_operator)
				|| "VIETEL".equalsIgnoreCase(mobile_operator)) {
			infoid = "viettel";
		} else if (("VMS".equalsIgnoreCase(mobile_operator))
				|| "mobifone".equalsIgnoreCase(mobile_operator)) {
			infoid = "mobifone";
		} else if (("GPC".equalsIgnoreCase(mobile_operator))
				|| ("VINAPHONE".equalsIgnoreCase(mobile_operator))) {
			infoid = "vinaphone";
		} else {
			infoid = "other";
		}
		return infoid;

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

}
