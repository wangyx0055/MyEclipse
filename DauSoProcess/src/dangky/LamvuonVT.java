package dangky;

import java.math.BigDecimal;
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

import cs.ExecuteADVCR;

public class LamvuonVT extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();
		String stime = "2010-05-15 23:59:00";
		String MLIST = "mlist_farmvt";
		String MLISTCANCEL = "mlist_farmvt_cancel";

		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);

			int[] result = new int[2];
			String infoid = msgObject.getMobileoperator();
			Timestamp time = msgObject.getTTimes();
			Timestamp endtime = Timestamp.valueOf(stime);

			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			Calendar cal = Calendar.getInstance();
			int nowday = cal.get(Calendar.DATE);

			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.DATE, 1);
			int day1 = cal1.get(Calendar.DATE);
			int month1 = cal1.get(Calendar.MONTH) + 1;
			// Sau 30 ngay
			Calendar cal30 = Calendar.getInstance();
			cal30.add(Calendar.DATE, 30);
			int day30 = cal30.get(Calendar.DATE);
			int month30 = cal30.get(Calendar.MONTH) + 1;

			// Sau 14 ngay
			Calendar cal29 = Calendar.getInstance();
			cal29.add(Calendar.DATE, 29);
			int day29 = cal29.get(Calendar.DATE);
			int month29 = cal29.get(Calendar.MONTH) + 1;
			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			}  else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext("Ban nhan tin sai cu phap.Dien thoai ho tro 1900571566");
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			if (sTokens.length == 1) {

				// ==1 :FA : Dang ky Dich vu
				Util.logger.info(" Dang ky dich vu lam vuon ");

				if (isexist(msgObject.getUserid(), MLIST)) {
					// Dang choi
					// Khong lam gi, chi tra tin dang dang ky dich vu
					Util.logger.info(" Da dang ky va dang choi: "
							+ msgObject.getUserid());

					msgObject
							.setUsertext("Ban da dang ky va dang choi game Lam Vuon That Tuyet.Soan tin FA HD gui 8151 de duoc ho tro ve cu phap.Chi tiet tai http://funzone.vn.DTHT 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					
				} else { // Dang khong choi
					// Luu DB

					if (!isexist(msgObject.getUserid(), MLISTCANCEL)) {// Chua
						// dky
						// tra tin dang ky thanh cong
						Util.logger.info(" Chua dky bao gio "
								+ msgObject.getUserid());

						insertData(msgObject.getUserid(), msgObject
								.getServiceid(), msgObject.getKeyword(), MLIST,
								msgObject, day30, day29);

						msgObject
								.setUsertext("Ban da dang ky thanh cong game Lam Vuon That Tuyet.Mien phi thue bao den ngay "
										+ FormatNumber(day29)
										+ "/"
										+ FormatNumber(month29)
										+ ".Tu ngay "
										+ FormatNumber(day30)
										+ "/"
										+ FormatNumber(month30)
										+ " phi thue bao 10,000 d/thang.Link choi game:http://funzone.vn");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						// mt2
						msgObject
								.setUsertext("Cam on ban da su dung dich vu hop tac ICom-Viettel.Dien thoai ho tro 1900571566.");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					} else {// Da tung dang ky choi
						// Tra tin chuc mung ban quay lai
						Util.logger.info(" Tro lai voi Game: "
								+ msgObject.getUserid());

						insertData(msgObject.getUserid(), msgObject
								.getServiceid(), msgObject.getKeyword(), MLIST,
								msgObject, nowday, 0);


						msgObject
								.setUsertext("Chuc mung ban da dang ky thanh cong choi game Lam Vuon That Tuyet.Phi thue bao 10.000d/thang.De huy dich vu soan FA OFF gui 8151.Chi tiet tai http://funzone.vn");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						msgObject
								.setUsertext("Cam on ban da su dung dich vu hop tac ICom-Viettel.DTHT:1900571566");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}

				}
				return null;

			} else if (sTokens.length == 2) {
				if (sTokens[1].equalsIgnoreCase("OFF")) {// FA OFF Huy dich vu
					if (!isexist(msgObject.getUserid(), MLIST)) {
						// khong choi ma huy

						Util.logger.info(" Khong choi ma huy: "
								+ msgObject.getUserid());

						msgObject
								.setUsertext("Ban chua dang ky dich vu.Soan tin FA gui 8151 de dang ky tham gia game Lam Vuon That Tuyet.Dien thoai ho tro 1900571566.Chi tiet tai http://funzone.vn");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					} else {
						// Dang choi ma huy
						// xoa trong mlist

						Util.logger.info(" Huy dich vu: "
								+ msgObject.getUserid());

						deleteUser(msgObject.getUserid(), MLIST);
						// luu trong mlist cancel
						insertData(msgObject.getUserid(), msgObject
								.getServiceid(), msgObject.getKeyword(),
								MLISTCANCEL, msgObject, nowday, 0);
						// tra tin huy thanh cong
						msgObject
								.setUsertext("Ban da huy thanh cong dich vu game Lam Vuon That Tuyet.De dang ky lai soan FA gui 8151.Chi tiet tai http://funzone.vn");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						// mt2
						msgObject
								.setUsertext("Cam on ban da su dung dich vu hop tac ICom-Viettel.Dien thoai ho tro 1900571566");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}

				} else if (sTokens[1].equalsIgnoreCase("LINK")) {// FA LINK Lay
					// lai Link

					Util.logger.info(" Lay lai Link: " + msgObject.getUserid());
					if (isexist(msgObject.getUserid(), MLIST)) {
						msgObject
								.setUsertext("Chao mung ban tro lai voi Game Lam Vuon That Tuyet.Bam vao link http://funzone.vn/lamvuon de quay tro lai trang trai cua ban.Dien thoai ho tro 1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					} else {
						msgObject
								.setUsertext("Ban chua dang ky dich vu.Soan tin FA de dang ky tham gia game Lam Vuon That Tuyet.Dien thoai ho tro 1900571566.Chi tiet tai http://funzone.vn");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}
				} else if (sTokens[1].equalsIgnoreCase("HD")) {// FA HD Huong
					// dan

					Util.logger.info("Lay huong dan: " + msgObject.getUserid());

					msgObject
							.setUsertext("De dang ky game Lam vuon that tuyet.Soan FA gui 8151.De HUY dich vu,soan FA OFF gui 8151.De nhan lai url game, soan FA LINK gui 8151.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					// mt2
					msgObject
							.setUsertext("Dien thoai ho tro 1900571566.Chi tiet tai http://funzone.vn");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				} else {// Tin sai cu phap

					msgObject
							.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

				}

			} else {
				msgObject
						.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			}

			return null;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
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
			String sqlUpdate = "DELETE FROM " + table + "  WHERE user_id="
					+ user;
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Loi xoa user " + user
						+ "trong bang cdrfarm_queue");
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
						.format(new Date()) + "','LAMVUON',"
				+ msgObject.getRequestid() + ",'"
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

	private static boolean saveClient(String dbcontent, String userid,
			int code, String filmname) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO " + dbcontent
					+ "(user_id, code,filmname)" + "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setInt(2, code);
			statement.setString(3, filmname);
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int[] getUserID(String userid, String dtbase, String filmname) {
		int[] result = new int[2];
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT id, code FROM " + dtbase + " WHERE user_id= '"
				+ userid.toUpperCase() + "' and filmname='" + filmname + "'";
		result[0] = 0;
		result[1] = 0;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);
			Util.logger.info("get userid  : " + query);
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[0] = rs.getInt(1);
					result[1] = rs.getInt(2);
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

	private static int findCode(String dbcontent, String filmname) {
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

			String sqlSelect = "SELECT code FROM " + dbcontent
					+ " WHERE filmname='" + filmname
					+ "' order by code desc limit 1";

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

}
