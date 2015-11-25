package tongdai;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import cs.ExecuteADVCR;

public class Kichhoatbaihat extends ContentAbstract {
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "tongdai";
		String operator = msgObject.getMobileoperator();
		try { 
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);
			String keywords = msgObject.getKeyword();
			String service_id = msgObject.getServiceid();
			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (sTokens.length != 2) {
				String str = "Ban da nhan tin sai.Soan tin "
						+ keywords
						+ " masomonqua gui"
						+ service_id
						+ " de kich hoat bai hat gui tang cho nguoi than yeu.DTHT 1900571566";
				msgObject.setUsertext(str);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				String giff_userid = "";
				String giff_telco = "";
				boolean giff = false;
				int maso = Integer.parseInt(sTokens[1]);

				giff_userid = findGif(dtbase, maso);

				if (giff_userid.equalsIgnoreCase("")) {
					String str = "Ma so kich hoat khong hop le.Vui long kiem tra lai hoac goi dien den tong dai 1900571566 de nghe huong dan tang bai hat.";
					msgObject.setUsertext(str);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					giff_userid = ValidISDNNew(giff_userid);

					giff_telco = getMobileOperatorNew(giff_userid, 2);
					if ("-".equals(giff_telco)
							|| "other"
									.equalsIgnoreCase(checkMobileOperator(giff_telco))) {
						msgObject
								.setUsertext("Hien tai dich vu chua ho tro mang cua thue bao duoc tang");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
					sendGifMsg(
							msgObject.getServiceid(),
							giff_userid,
							giff_telco,
							msgObject.getKeyword(),
							"Ban duoc sdt "
									+ userid
									+ " gui tang mon qua am nhac.Chuc ban vui ve",
							msgObject.getRequestid(), 0);
					updateStatus(maso, dtbase);
					msgObject
							.setUsertext("Ban da kich hoat thanh cong mon qua am nhac.Ma so mon qua cua ban la "
									+ maso
									+ ".Chuc ban vui ve.DTHT 1900571566.");
					msgObject.setContenttype(0);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				}

			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(),
					"Exception:bbbbbbbbbbbbb " + e.getMessage());
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	private static String findGif(String dtbase, int maso) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT sodienthoaiguitang FROM sodienthoai1 WHERE  (id= "
					+ maso + ")";
			Util.logger
					.info("Tim so dien thoai nguoi duoc tang : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
				}
			}
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

	private static boolean updateStatus(int maso, String dtbase) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update status= 1 kich hoat thanh cong

			String sqlUpdate = "UPDATE sodienthoai1 SET status = 1 WHERE id="
					+ maso;
			Util.logger.info(" UPDATE status: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update status, kich hoat thanh cong");
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

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid,
			int contenttype) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(contenttype);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);

			DBUtil.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(), "Send gif Failed");
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

	private String fomatSBD(Integer num) {
		String str = "" + num;
		str = "0000".substring(0, 4 - str.length()) + str;
		return str;

	}

}
