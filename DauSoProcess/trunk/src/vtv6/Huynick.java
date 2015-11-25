package vtv6;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.MoSenderMediatech;

import cs.ExecuteADVCR;

/**
 * Game.<br>
 * 
 * <pre>
 * ich vu cho phep tai ve danh sach ten game, ma so game
 * </pre>
 * 
 * @author Haptt
 * @version 1.0
 */
public class Huynick extends ContentAbstract {

	/* First String */

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "0";
		String isforwardMO = "1";
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
		//	String mt1 = "De tai Game theo ma so, soan: GAME maso gui 8751.Cai dat GPRS 3.0 tu dong ve may de co the luot web va choi game,tai nhac,soan tin GPRS gui 8751.";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				ismtadd = getString(_option, "ismtadd", ismtadd);
		//		mt1 = getString(_option, "mt1", mt1);
				isforwardMO = getString(_option, "mo", isforwardMO);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */

			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// lay so thue bao nguoi gui
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
		
			String nick = "";

			nick = Getnickbysdt(msgObject.getUserid());

			if (nick.equalsIgnoreCase("")) {
				msgObject
						.setUsertext("Ban chua dang ky Chat tren Hop Nhac So. Soan, NAM/NU namsinh noio gui 8751. VD: Ban la Nu, 1994, o Ha Noi, soan: NU 1994 Hanoi gui 8751. DTHT: 1900571566.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			} else {
				/*if (isforwardMO.equalsIgnoreCase("1")) {
					try {
						String result = MoSenderMediatech.SendXML(msgObject
								.getRequestid()
								+ "", msgObject.getUserid(), msgObject
								.getServiceid(), msgObject.getKeyword(),
								"HUYN", msgObject.getMobileoperator());
						if (result.equalsIgnoreCase("0")) {
							Util.logger.info("Send ok");
						} else {

							Util.logger.info(this.getClass().getName() + "@"
									+ "Got " + result
									+ ", Going For Retry, Sleeping,Details: "
									+ "Msisdn: " + msgObject.getUserid()
									+ " Shortcode: " + msgObject.getServiceid()
									+ " Keyword: " + msgObject.getKeyword()
									+ " RequestID: " + msgObject.getRequestid()
									+ "CommandCode: " + msgObject.getKeyword());
						}
					} catch (Exception e) {

						Util.logger.sysLog(2, this.getClass().getName(),
								"Exception: " + e.getMessage());

					} finally {

					}
				}*/
				deleteUser(msgObject.getUserid());
				msgObject
						.setUsertext("Nick "
								+ nick
								+ " cua ban da duoc huy. De dang ky nick khac, soan: NAM/NU namsinh noio gui 8751.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			}

			return null;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			if ("1".equalsIgnoreCase(ismtadd))
				ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
						.getServiceid(), msgObject.getUserid(), msgObject
						.getKeyword(), msgObject.getRequestid(), msgObject
						.getTTimes(), msgObject.getMobileoperator());
		}
	}

	private static boolean deleteUser(String user) {
		boolean result = true;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "DELETE FROM NickName WHERE MSISDN = '" + user +"'";
		Util.logger.error("[Delete User]:" + proceExe);
		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				return result;
			}
			
			return false;
		} catch (SQLException ex3) {
			Util.logger.error("[Delete User]:" + ex3.toString());
			return false;
		} catch (Exception ex2) {
			Util.logger.error("[Delete User]:" + ex2.toString());
			return false;
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

	}


	private static String Getnickbysdt(String sdt) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](2,'"+sdt+"') }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			Vector result1 = DBUtil.getVectorTable(connection, proceExe);

			Util.logger.info("DBUtil.getCode: queryStatement:" + proceExe);

			if (result1.size() > 0) {
				Vector item = (Vector) result1.elementAt(0);
				String code = item.elementAt(3).toString();
				return code;
			}
			Util.logger.info("excuted");
			return result;
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_Nick_Select]1:" + ex3.getMessage());
			return result;
		} catch (Exception ex2) {
			Util.logger.error("[Sp_Nick_Select]2:" + ex2.getMessage());
			ex2.printStackTrace();
			return result;
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}
	}


	private static String findListofMusic(String date) {
		String result = "";

		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String lastresult = "";
		try {
			connection = dbpool.getConnection("mediatech");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "select top 3 replace(NameVote,' ','') NameVote,Code from PlaysList where DatePlay='"
					+ date + "' order by newid() ; ";

			Util.logger.info("SEARCH List game : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				int i = 0;
				while (rs.next()) {

					result = result
							+ rs.getString(2).trim()
							+ "_"
							+ rs.getString(1).substring(0,
									rs.getString(1).length() - 4) + ";";

				}

				return result;

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


	/* ghi lai dsach khach hang */
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

	/* Chia thanh 2 MT */
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

			/* tach thanh 2 */
			while (!(resultBoolean)) {
				// neu <160
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM " + dtbase + " WHERE userid= '"
				+ userid.toUpperCase() + "' AND subcode =" + subcode;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
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
		return -1;
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

}
