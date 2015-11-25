package dangky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

public class DangkykhamBachMai extends ContentAbstract {
	private static int year = 2011;
	private static int month = 3;
	private static int day = 1;
	private static String tablename = "bachmai201103";
	private static String stringCalendar = "bachmai201103";
	private static String numb = "500";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();

		try {
			int[] result = new int[2];
			String infoid = msgObject.getMobileoperator();
			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| "mobifone".equalsIgnoreCase(msgObject
							.getMobileoperator())) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("VINAPHONE".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "vinaphone";
			} else if (("GTEL".equalsIgnoreCase(msgObject.getMobileoperator()))
					|| ("BEELINE".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "beeline";
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Dich vu chua ho tro mang cua ban. DTHT 1900571566");
				msgObject.setMsgtype(2);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			}
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				numb = getString(_option, "numb", numb);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}
			String mt = "";

			// int stt = 0;

			int stt = findCode(3);

			if (sTokens.length == 2) {
				if (sTokens[1].equalsIgnoreCase("BM")) {

					saveClient(tablename, msgObject.getUserid(),
							stringCalendar, stt);
					mt = "Ban da dang ky lich kham thanh cong. Ban se duoc xep lich kham vao ngay:"
							+ Format(day)
							+ "/"
							+ Format(month)
							+ "/"
							+ year
							+ " . So thu tu cua ban la:" + stt;

				} else {
					mt = "Ban soan tin sai cu phap. Soan: HD KB gui 8151 de duoc huong dan. Ho tro:1900571566";
				}
			} else {
				mt = "Ban soan tin sai cu phap. Soan: HD KB gui 8151 de duoc huong dan. Ho tro:1900571566";
			}

			String[] mtsplit = mt.split("###");
			for (int j = 0; j < mtsplit.length; j++) {
				if (!"".equalsIgnoreCase(mtsplit[j])) {
					msgObject.setUsertext(mtsplit[j]);
					if (j == 0) {
						msgObject.setMsgtype(1);
					} else
						msgObject.setMsgtype(0);

					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}
			}

			return null;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

		}

	}

	private static boolean saveClient(String table, String sodienthoai,
			String ngaykham, int stt) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			Util.logger.info("Truoc lenh get conect ");

			connection = dbpool.getConnection("bachmai");
			// connection = dbpool.getConnection("gateway");
			if (connection == null) {
				Util.logger
						.error("Impossible to connect to DB. ko connect dc db bachmai");
				return false;
			}
			String sqlInsert = "INSERT INTO  " + table
					+ "(stt,sodienthoai,ngaykham)" + "VALUES(?,?,?)";
			Util.logger.info("cau lenh insert : " + sqlInsert);

			statement = connection.prepareStatement(sqlInsert);
			statement.setInt(1, stt);
			statement.setString(2, sodienthoai);
			statement.setString(3, ngaykham);

			statement.executeUpdate();
			Util.logger.info("sqlInsert  : " + sqlInsert);
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

	public static String Format(int month) {
		String monthchar = "";
		if (month < 10)
			monthchar = "0" + month;
		else
			monthchar = "" + month;
		return monthchar;

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

	private static int findCode(int n) {
		int result = 1;
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, n);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DATE);
		tablename = "bachmai" + year + Format(month);
		String day3 = year + "-" + Format(month) + "-" + Format(day);
		stringCalendar = sdf.format(cal.getTime());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = " SELECT STT FROM " + tablename
					+ " WHERE date(ngaykham)='" + day3
					+ "' order by STT desc limit 1";

			Util.logger.info("SEARCH STT  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
					if (result < 1)
						result = 1;
					Util.logger.info("STT: " + result);
					if (result > Integer.parseInt(numb)) {
						Util.logger.info("result  " + n + ":" + result);

						result = findCode(n + 1);
					} else
						result = result + 1;
					return result;

				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
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

}
