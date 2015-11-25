package services.textbases;

import java.math.BigDecimal;
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

import cs.ExecuteADVCR;

public class Cuochenlytuong extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		HashMap _option = new HashMap();
		String dtbase = "icom_dangky_cuochenlytuong";
		String advert = "Tai nhac cho HOT. Soan MCA tenbaihat gui 8551. De nhan tu van lam dep Soan LI gui 8751.";

		try {

			// Option
			String options = keyword.getOptions();
			String GAME_ID = "";
			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";
			String DBCONTENT = "textbase";
			String GIFF_MSG = "";
			String SUCCESS_MSG = "";
			String maxS = "";
			String service = "8351/8751";

			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);

				_option = getParametersAsString(options);
				maxS = ((String) _option.get("max")).toUpperCase();
				GAME_ID = ((String) _option.get("game_id")).toUpperCase();
				GIFF_MSG = (String) _option.get("giff_msg");
				DBCONTENT = getString(_option, "dbcontent", DBCONTENT);
				SUCCESS_MSG = ((String) _option.get("success_msg"));
				service = getString(_option, "service", service);
			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			// Info client
			int max = Integer.parseInt(maxS);
			String USERID = msgObject.getUserid();
			String serviceid = msgObject.getServiceid();
			String usertext = msgObject.getUsertext();
			String sKeyword = msgObject.getKeyword();
			String USER_TELCO = msgObject.getMobileoperator();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");

			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Ban da nhan tin sai cu phap. De nhan nhung y tuong lang man, soan tin "
								+ sKeyword + " Nam/Nu gui " + service);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else if (sTokens.length > 2) {
				GIFF_USER = ValidISDNNew(sTokens[2]);
				if (!"-".equalsIgnoreCase(GIFF_USER)) {
					GIFF_USER_TELCO = getMobileOperatorNew(DBCONTENT,
							GIFF_USER, 2);
				}
			}

			// Subcode1
			String subcode1 = sTokens[1];
			subcode1 = validSubcode(subcode1);
			Util.logger.info("Subcode1: " + subcode1);
			if ("-".equalsIgnoreCase(subcode1)) {
				msgObject
						.setUsertext("Ban da nhan tin sai cu phap. De nhan nhung y tuong lang man, soan tin "
								+ sKeyword + " Nam/Nu gui " + service);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			if (!"-".equalsIgnoreCase(GIFF_USER_TELCO)) {

				/* Thông báo số thuê bao xxx đã gửi tặng thành công */
				GIFF_MSG = GIFF_MSG.replace("#1", GIFF_USER);
				msgObject.setUsertext(GIFF_MSG);
				msgObject.setMsgtype(1);
				msgObject.setMobileoperator(msgObject.getMobileoperator());
				messages.add(new MsgObject(msgObject));

				// Thong bao da dc tang thanh cong.
				SUCCESS_MSG = SUCCESS_MSG.replace("#1", USERID);
				sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO.toUpperCase(),
						sKeyword, SUCCESS_MSG, msgObject.getRequestid());

				/* Lay noi dung va gui tang */
				String lastid = getLastID(GIFF_USER, dtbase);
				String[] result = new String[max];

				if ("".equalsIgnoreCase(lastid)) {
					saverequest(GIFF_USER, dtbase, lastid);
				}

				result = findListofSend(GIFF_USER, GAME_ID, subcode1, lastid,
						max, dtbase);

				for (int i = 0; i < result.length; i++) {
					if (i == result.length - 1) {
						result[i] = result[i]
								+ ". Nhan tin de co them bi quyet.";
					}
					if (!"".equalsIgnoreCase(result[i])) {
						sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO,
								sKeyword, result[i], msgObject.getRequestid());
					}
				}

				if (max == 5) {
					sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO, sKeyword,
							advert, msgObject.getRequestid());
				}

			} else {
				/* Lay noi dung va gui tang */
				String lastid = getLastID(USERID, dtbase);
				String[] result = new String[max];

				if ("".equalsIgnoreCase(lastid)) {
					saverequest(USERID, dtbase, lastid);
				}
				result = findListofSend(USERID, GAME_ID, subcode1, lastid, max,
						dtbase);

				for (int i = 0; i < result.length; i++) {
					if (!"".equalsIgnoreCase(result[i])) {

						if (i == result.length - 1) {
							result[i] = result[i]
									+ ". Nhan tin de co them bi quyet.";
						}
						msgObject.setUsertext(result[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);
						} else {
							msgObject.setMsgtype(0);
						}
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
				}

				if (max == 5) {
					msgObject.setUsertext(advert);
					msgObject.setContenttype(0);
					msgObject.setMsgtype(0);
					messages.add(new MsgObject(msgObject));
				}

			}
			return messages;
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

	// Cập nhật danh sách mã số đã gửi đến khách hàng.
	private static boolean updateListSend(String userid, String lastid,
			String dtbase) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			if (lastid.length() > 1000) {
				lastid = "";
			}
			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE " + dtbase + " SET lastid = '" + lastid
					+ "' WHERE upper(userid)='" + userid.toUpperCase() + "'";
			Util.logger.info(" UPDATE List send: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list send to Client " + userid
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

	/* Tìm list bai hat */
	private static String[] findListofSend(String userid, String gameid,
			String subcode1, String lastid, int max, String dtbase) {
		String[] result = new String[max];
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

			for (int i = 0; i < max; i++) {
				result[i] = "";
			}

			String sqlSelect = "SELECT content, id FROM icom_textbase_data WHERE ( upper(gameid)='"
					+ gameid.toUpperCase()
					+ "') AND ( upper(subcode1) ='"
					+ subcode1.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND id not in(" + lastid + ") ";
			}

			Util.logger.info("SEARCH CONTENT Cuoc hen ly tuong : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			int i = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result[i] = rs.getString(1);
					if (i == max - 1) {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
						updateListSend(userid, lastid, dtbase);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
						i++;
					}
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

	public static String getMobileOperatorNew(String dbcontent, String userid,
			int type) {

		String tmpOperator = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnection(dbcontent);

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

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Integer.parseInt(sISDN);

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

	private static String getLastID(String userid, String dtbase) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "";

		try {

			String query = "SELECT lastid FROM " + dtbase + " WHERE userid= '"
					+ userid.toUpperCase() + "'";

			cnn = dbpool.getConnectionGateway();
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
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return sequence_temp;
	}

	private void sendGifMsg(String serviceid, String userid, String operator,
			String service, String mtcontent, BigDecimal requestid) {
		try {

			MsgObject msg = new MsgObject();
			msg.setContenttype(0);
			msg.setUserid(userid);
			msg.setMobileoperator(operator);
			msg.setMsgtype(0);
			msg.setRequestid(requestid);
			msg.setServiceid(serviceid);
			msg.setKeyword(service);
			msg.setUsertext(mtcontent);

			DBUtil.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	private static boolean saverequest(String userid, String dtbase,
			String lastid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO " + dtbase
					+ "(userid, lastid) VALUES ('" + userid + "','" + lastid
					+ "')";
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

	private static String validSubcode(String subcode) {
		subcode = subcode.toUpperCase();
		if (subcode.startsWith("NAM")
				|| (subcode.startsWith("BOY") || (subcode.startsWith("MAN")))) {
			return "NAM";
		} else if (subcode.startsWith("NU")
				|| (subcode.startsWith("GIRL") || (subcode.startsWith("WOMAN")))) {
			return "NU";
		} else {
			return "-";
		}

	}
}
