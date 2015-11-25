package advice;

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

import services.textbases.VnnLinksTextbaseTicket;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ãƒ»
 * </pre>
 * 
 * @author Vietnamnet ICom TrungVD
 * @version 1.0
 */
public class Nhacchochude6x89 extends ContentAbstract {

	/* First String */
	private static final String MT1_VIETTEL = "Neu ban chua dang ky dich vu nhac cho cua Viettel, soan: DK gui 1221. Neu da dang ky, soan: BH MaSo gui 1221 de cai bai hat ban thich lam nhac cho";
	private static final String MT1_MOBIFONE = "Neu ban chua dang ky dich vu nhac cho cua Mobifone, soan: DK gui 9224. Neu da dang ky, soan: CHON MaSo gui 9224 de cai bai ban thich lam nhac cho";
	private static final String MT1_VINAPHONE = "Neu ban chua dang ky dich vu nhac cho cua Vinaphone, soan: DK gui 9194. Neu da dang ky, soan: TUNE MaSo gui 9194 de cai bai hat ban thich lam nhac cho";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.";
		String inv_msg = "Ban da nhan tin sai cu phap.";
		String ismtadd = "1";

		try {

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String numberid = "6x54";
			String dtbase = "gateway";
			String dbcontent = "client_nhaccho_6x89";
			String operator = msgObject.getMobileoperator();
			BigDecimal requestid = msgObject.getRequestid();
			Timestamp timesend = msgObject.getTTimes();

			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				numberid = getString(_option, "numberid", numberid);
				dtbase = getString(_option, "dtbase", dtbase);
				inv_msg = getString(_option, "inv_msg", inv_msg);
				ismtadd = getString(_option, "ismtadd", ismtadd);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			String infoid = "";
			Timestamp timeEndDate = msgObject.getTTimes();

			// Kiem tra thue bao cua khach hang
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
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// Lay info khach hang gui den
			String userid = msgObject.getUserid();
			String userText = msgObject.getUsertext();
			userText = replaceAllWhiteWithOne(userText);
			String[] sTokens = userText.split(" ");

			if (sTokens.length > 1) {
				String singer = "";
				String singer_sms = "";
				for (int k = 1; k < sTokens.length; k++) {
					singer_sms = singer_sms + sTokens[k] + " ";
					singer = singer + sTokens[k];
				}

				// Lay danh sach cac bai hat da gui cho khach hang lan truoc
				String lastid = getMusicID(dtbase, dbcontent, userid);
				Util.logger.info("Music ID: " + lastid);

				// Ghi lai thong tin da gui.
				if ("".equalsIgnoreCase(lastid)) {
					saverequest(dtbase, dbcontent, userid, lastid);
				}

				int max = 10;

				// Tim 10 bai hat cua cac ca sy
				String listofMusic = findListofMusic(dtbase, dbcontent, userid,
						singer, infoid, lastid, max);

				if ("".equalsIgnoreCase(listofMusic)) {

					if (!checkSinger(dtbase, singer, infoid)) {
						msgObject
								.setUsertext("Chu de ban thich hien tai chua duoc cap nhat trong he thong.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else {

						// Neu co chu de ma da gui het roi thi gui lai
						updateListMusic(dbcontent, userid, "", dtbase);
						String reSendMusic = findListofMusic(dtbase, dbcontent,
								userid, singer, infoid, "", max);
						Util.logger.info("reSendMusic: " + reSendMusic);

						// MT 1
						String info2Client = "";
						if ("viettel".equalsIgnoreCase(infoid)) {
							info2Client = MT1_VIETTEL;
						} else if ("vinaphone".equalsIgnoreCase(infoid)) {
							info2Client = MT1_VINAPHONE;
						} else if ("mobifone".equalsIgnoreCase(infoid)) {
							info2Client = MT1_MOBIFONE;
						}

						msgObject.setUsertext(info2Client);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						// MT2 den MT3
						String[] send2Client = new String[2];
						send2Client = splitString(reSendMusic);

						for (int i = 0; i < 2; i++) {
							if (!"".equalsIgnoreCase(send2Client[i])) {
								msgObject.setUsertext(send2Client[i]);
								msgObject.setContenttype(0);
								msgObject.setMsgtype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							}
						}
						return null;
					}
				} else {

					// MT 1
					String info2Client = "";
					if ("viettel".equalsIgnoreCase(infoid)) {
						info2Client = MT1_VIETTEL;
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						info2Client = MT1_VINAPHONE;
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						info2Client = MT1_MOBIFONE;
					}

					msgObject.setUsertext(info2Client);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// MT2 den MT3
					String[] send2Client = new String[2];
					send2Client = splitString(listofMusic);

					for (int i = 0; i < 2; i++) {
						if (!"".equalsIgnoreCase(send2Client[i])) {
							msgObject.setUsertext(send2Client[i]);
							msgObject.setContenttype(0);
							msgObject.setMsgtype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
						}
					}
				}

				return null;
			} else {

				msgObject.setUsertext(inv_msg);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
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

			/* Tách chuỗi thứ 2 */
			while (!(resultBoolean)) {
				// Nếu là ; và <160
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

	private static boolean updateListMusic(String dbcontent, String userid,
			String lastid, String dtbase) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			if (lastid.length() > 1000) {
				lastid = "";
			}
			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE " + dbcontent + " SET musichotid = '"
					+ lastid + "' WHERE upper(userid)='" + userid.toUpperCase()
					+ "'";
			Util.logger.info(" UPDATE MUSIC ID SEND: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list music to send " + userid + " to "
						+ dbcontent);
				return true;
			}
			return false;
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

	// Tim cac musicid cua ca sy da gui cho khach hang
	private static String getMusicID(String dtbase, String dtcontent,
			String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "";
		try {
			cnn = dbpool.getConnection(dtbase);

			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}

			String query = "SELECT musichotid FROM " + dtcontent
					+ " WHERE upper(userid) = '" + userid.toUpperCase() + "'";
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

	private static boolean saverequest(String dtbase, String dbcontent,
			String userid, String lastid) {

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
					+ "(userid, musichotid) VALUES ('" + userid + "','"
					+ lastid + "')";
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

	// Tim cac musicid cua ca sy da gui cho khach hang
	private static boolean checkSinger(String dtbase, String find, String infoid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		boolean result = false;
		try {
			cnn = dbpool.getConnection(dtbase);

			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String query = "SELECT musicid "
					+ " FROM icom_music WHERE ( upper(operator)= '"
					+ infoid.toUpperCase() + "') AND ( upper(group1) = '"
					+ find.toUpperCase() + "' OR upper(group2) = '"
					+ find.toUpperCase() + "' OR upper(group3) = '"
					+ find.toUpperCase() + "' OR upper(group4) = '"
					+ find.toUpperCase() + "' OR upper(group5) = '"
					+ find.toUpperCase() + "')";
			statement = cnn.prepareStatement(query);
			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getString(1);
					return true;
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

	/* Tìm list bai hat theo chu de */
	private static String findListofMusic(String dtbase, String dbcontent,
			String userid, String hotmusic, String infoid, String lastid,
			int max) {
		String result = "";
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

			String sqlSelect = "SELECT musicname_sms, singer, musicid FROM icom_music WHERE ( ( upper(group1) = '"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group2)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group3)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group4)='"
					+ hotmusic.toUpperCase()
					+ "') OR (upper(group5)='"
					+ hotmusic.toUpperCase()
					+ "') ) AND ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}
			sqlSelect = sqlSelect + " ORDER BY rand()";

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			int count = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					count++;
					result = result + rs.getString(1) + "(" + rs.getString(2)
							+ "):" + rs.getString(3) + "; ";

					if ((count == max) || (result.length() > 320)) {
						lastid = lastid + "," + rs.getString(3);
						updateListMusic(dbcontent, userid, lastid, dtbase);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(3);
						} else {
							lastid = lastid + "," + rs.getString(3);
						}
					}
				}
				if ((count < 10) || (result.length()) < 320) {
					updateListMusic(dbcontent, userid, lastid, dtbase);
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error SQL:" + e.toString());
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
