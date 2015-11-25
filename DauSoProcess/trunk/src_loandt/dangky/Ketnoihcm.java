package dangky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

public class Ketnoihcm extends ContentAbstract {

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

			if ("SFONE".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Dich vu chua ho tro mang cua ban. DTHT 1900571566");
				msgObject.setMsgtype(2);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			}

			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Tin nhan dang ky khong thanh cong,soan: HA gioitinh sothich gui 8751 De dang ky lai. DTHT 1900571566");
				msgObject.setMsgtype(1);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else if (sTokens.length > 2) {

				if (!sTokens[1].equalsIgnoreCase("nam")
						&& !sTokens[1].equalsIgnoreCase("nu")) {
					msgObject
							.setUsertext("Tin nhan dang ky khong thanh cong,soan: HA gioitinh sothich gui 8751 De dang ky lai. DTHT 1900571566");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
				}
				int stt = findCode() + 1;
				String subTokens = "";
				for (int k = 2; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k] + " ";
				}

				if (saveClient(msgObject.getUserid(), stt, sTokens[1],
						subTokens)) {

					msgObject
							.setUsertext("Chuc mung ban da tro thanh thanh vien cua chuong trinh KNYT, ma so thanh vien cua ban la: "
									+ stt + ". DT ho tro 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					msgObject
							.setUsertext("Soan tin HA maso noidungchat gui 8351 de ket ban giao luu va chatting voi cac thanh vien khac cua KNYT. DT ho tro 1900571566");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				} else {
					msgObject
							.setUsertext("Tin nhan dang ky khong thanh cong, soan: BA gioitinh sothich gui 8751 De dang ky lai.Luu y tin nhan khong co dau. DTHT 1900571566");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				}

			} else {
				// dang ky binh chon. Neu dang trong thoi gian dien ra thi luu
				// binh chon
				// if isprocess=1
				// 
				int phien = getPhien();
				if (phien == -1) {
					msgObject
							.setUsertext("Tin nhan cua ban da het han vui long nhan tin lai vao chuong trinh phat song ke tiep. DT ho tro 1900571566");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
				} else {
					int capdoi = -1;
					try {
						capdoi = Integer.parseInt(sTokens[1]);
					} catch (Exception ex) {

					}
					if (capdoi == -1) {
						msgObject
								.setUsertext("Tin nhan binh chon khong thanh cong,soan:BA masocapdoi gui 8751 de binh chon.DTHT 1900571566");
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					}
					savevote(userid, sTokens[1], phien);
					msgObject
							.setUsertext("Ban da vote thanh cong cho cap doi so "
									+ sTokens[1]
									+ " hay doi den cuoi chuong trinh de biet ket qua ch�nh x�c. DT ho tro 1900571566");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
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

	private static int getPhien() {
		int result = -1;
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT phien FROM ketnoiphienhcm  where isprocess=1 ";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
					return result;
				}
			}
			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static int findCode() {
		int result = 0;
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT stt FROM ketnoihcm order by stt desc limit 1";

			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
					Util.logger.info("Code: " + result);
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

	private static boolean saveClient(String userid, int stt, String gioitinh,
			String sothich) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO ketnoihcm (userid,stt,gioitinh,sothich)"
					+ "VALUES(?,?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setInt(2, stt);
			statement.setString(3, gioitinh);
			statement.setString(4, sothich);

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

	private static boolean savevote(String userid, String capdoi, int phien) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO ketnoivotehcm (userid,capdoi,phien)"
					+ "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, capdoi);
			statement.setInt(3, phien);

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

}
