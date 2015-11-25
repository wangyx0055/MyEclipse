package vovtv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.tempuri.Tv_updateSoapProxy;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class danhsachbaihat extends ContentAbstract {

	/* First String */

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";
		String isforwardMO = "0";
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String url_vote = Constants._prop.getProperty("url_bcbh");
			String username = Constants._prop.getProperty("user_bcbh");
			String password = Constants._prop.getProperty("pass_bcbh");
			Tv_updateSoapProxy wsvote = new Tv_updateSoapProxy();
			wsvote.setEndpoint(url_vote);
			String result = "";
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				inv_telco = getString(_option, "inv_telco", inv_telco);
				ismtadd = getString(_option, "ismtadd", ismtadd);

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
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			
			if(sTokens.length > 1) {
				String listgame = "Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.";
				msgObject.setUsertext(listgame);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);
			/*	
				msgObject.setUsertext("Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);*/

				return null;
			}
			String ds_baihat = "";
			ds_baihat = wsvote.searchSongHot(username, password, "", 7);
			if(ds_baihat != "-1" || ds_baihat != "-2" || ds_baihat != "-3") {
				String listgame = "Danh sach bai hat phat song: "+ ds_baihat;
				msgObject.setUsertext(listgame);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);
				
				msgObject.setUsertext("Binh chon bai hat soan VT tenbaihat Gui 8751.DTHT:1900571566.");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);
				
				return null;
			} else {
				Util.logger.info("@class: goi ws loi");
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static String getGame(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT magame FROM " + dtbase + " WHERE userid= '"
					+ userid.toUpperCase() + "' AND subcode=" + subcode;
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

	private static boolean saverequest(String userid, String dtbase, int subcode) {

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
					+ "(userid, subcode) VALUES ('" + userid + "'," + subcode
					+ ")";
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

	// cap nhat danh sach khach hang
	private static boolean updateListGame(String userid, String lastid,
			String dtbase, int subcode) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE " + dtbase + " SET magame = '" + lastid
					+ "' WHERE upper(userid)='" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list game to send " + userid
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

	/* Tim list bai hat */
	private static String[] findListofGame(String date) {
		String[] result = new String[2];
		String result1 = "";
		String result2 = "";

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

			String sqlSelect = "select top 6 replace(NameVote,' ','') NameVote,Code from PlaysList where DatePlay='"
					+ date + "' order by newid() ; ";

			Util.logger.info("SEARCH List game : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				int i = 0;
				while (rs.next()) {
					if (i < 3)

						result1 = result1
								+ rs.getString(2).trim()
								+ "_"
								+ rs.getString(1).substring(0,
										rs.getString(1).length() - 4) + ";";

					else

						result2 = result2
								+ rs.getString(2).trim()
								+ "_"
								+ rs.getString(1).substring(0,
										rs.getString(1).length() - 4) + ";";

					i++;
				}

				result[0] = result1;
				result[1] = result2;

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
