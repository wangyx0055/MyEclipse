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
 * @author Vietnamnet I-Com TrungVD
 * @version 1.0
 */
public class Nhacchochude6x54 extends ContentAbstract {

	private static String viettelHot = "De tai cai dat nhac cho.Buoc1,soan:DK gui 1221.Buoc2,soan:BH maso gui 1221.CSKH:1900571566.Danh sach ma bai hat nhac cho Hot nhat cua nhom:";
	private static String vinaphoneHot = "De tai cai dat nhac cho.Buoc1,soan:DK gui 9194.Buoc2,soan:TUNE maso gui 9194.CSKH:1900571566.Danh sach ma bai hat nhac cho Hot nhat cua nhom:";
	private static String mobifoneHot = "De tai cai dat nhac cho.Buoc1,soan DK gui 9224.Buoc2,soan:CHON maso gui 9224.CSKH:1900571566.Danh sach ma bai hat nhac cho Hot nhat cua nhom:";
	private static String beelineHot = "De tai cai dat nhac cho.Soan:CHON masobaihat gui 1221.CSKH:1900571566.Danh sach ma bai hat nhac cho Hot nhat cua nhom: ";

	private static String viettelHD = "De cai dat nhac cho.Buoc1,soan:DK gui 1221.Buoc2,soan:BH maso gui 1221";
	private static String vinaphoneHD = "De cai dat nhac cho.Buoc1,soan:DK gui 9194.Buoc2,soan:TUNE maso gui 9194";
	private static String mobifoneHD = "De cai dat nhac cho.Buoc1,soan DK gui 9224.Buoc2,soan:CHON maso gui 9224";
	private static String beelineHD = "De cai dat nhac cho.Soan:CHON masobaihat gui 1221";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		String inv_msg = "Ban da nhan tin sai cu phap.De cai dat GPRS tu dong, soan GPRS gui 8751.De tai cac bai hat HOT nhat soan BH TOP gui 8751.CSKH:1900571566";
		String last_msg = "NC tenbaihat gui 6554.DTHT 1900571566";
		String ismtadd = "1";
		try {

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String numberid = "6x54";
			String dtbase = "textbase";
			String dbcontent = "icom_nhaccho_casy";
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
				last_msg = getString(_option, "last_msg", last_msg);
				ismtadd = getString(_option, "ismtadd", ismtadd);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			String infoid = "";
			Timestamp timeEndDate = msgObject.getTTimes();

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
					|| ("Beeline".equalsIgnoreCase(msgObject
							.getMobileoperator()))) {
				infoid = "beeline";
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Hien tai dich vu chua ho tro mang cua ban.Vui long quay tro lai sau.DTHT 1900571566");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
		String userid = msgObject.getUserid();
			String userText = msgObject.getUsertext();
			userText = replaceAllWhiteWithOne(userText);
			String[] sTokens = userText.split(" ");

			String serviceid = msgObject.getServiceid();

			if (sTokens.length > 1) {
				String singer = "";
				String singer_sms = "";
				for (int k = 1; k < sTokens.length; k++) {
					singer_sms = singer_sms + sTokens[k] + " ";
					singer = singer + sTokens[k];
				}
			String musicid = getMusicID(dbcontent, userid);
				Util.logger.info("Music ID: " + musicid);
				if ("-".equalsIgnoreCase(musicid)) {
					musicid = "";
					saverequest(dbcontent, userid, musicid);
				}
				String[] resultMusic = new String[1];
				resultMusic = findMusic(dtbase, userid, singer, infoid,
						musicid, 1);

				if ("".equalsIgnoreCase(resultMusic[0])) {

					if (!checkSinger(dtbase, singer, infoid)) {

						String mtret = "";

						if ("viettel".equalsIgnoreCase(infoid)) {
							mtret = "Nhom ban thich chua co trong he thong."
									+ viettelHD
									+ ".Tang ban cac ma nhac cho Hot nhat";
						} else if ("vinaphone".equalsIgnoreCase(infoid)) {
							mtret = "Nhom ban thich chua co trong he thong."
									+ vinaphoneHD
									+ ".Tang ban cac ma nhac cho Hot nhat";
						} else if ("mobifone".equalsIgnoreCase(infoid)) {
							mtret = "Nhom ban thich chua co trong he thong."
									+ mobifoneHD
									+ " .Tang ban cac ma nhac cho Hot nhat";
						} else if ("beeline".equalsIgnoreCase(infoid)) {
							mtret = "Nhom ban thich chua co trong he thong."
									+ beelineHD
									+ " .Tang ban cac ma nhac cho Hot nhat";
						}

						msgObject.setUsertext(mtret);

						String[] resultMusic1 = new String[1];
						resultMusic1 = findMusic(dtbase, userid, "HOT", infoid,
								musicid, 1);

						// Ghi lai yeu cau khach hang chua dap ung duoc.
						saveClient(userid, operator, userText, serviceid,
								timesend, requestid);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));

						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						// Send 2 MT cho khach hang

						if (!"".equalsIgnoreCase(resultMusic1[0])) {
							msgObject.setUsertext(resultMusic1[0]);
							msgObject.setMsgtype(0);
							msgObject.setContenttype(0);
							// messages.add(new MsgObject(msgObject));
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
						}

						// Send MT cuoi cung quang cao
						msgObject
								.setUsertext("Van con hang ngan BH khac tren "
										+ numberid
										+ ". De nhan duoc ma so nhac cho theo ten BH ban yeu thich.Soan: "
										+ last_msg);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
						return null;

					} else {
						// MT1
						msgObject
								.setUsertext("He thong dang tiep tuc cap nhat nhung bai hat nhac cho moi nhat cua nhom "
										+ singer_sms
										+ ".Tang ban tron bo cac bai hat nhac cho HOT nhat hien nay:");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						// MT 2 - MT 6
						String[] musicHot = new String[1];
						musicHot = findListofMusic(dtbase, userid, infoid,
								musicid, 1);

						if (!"".equalsIgnoreCase(musicHot[0])) {
							msgObject.setUsertext(musicHot[0]);
							msgObject.setContenttype(0);
							msgObject.setMsgtype(0);
							// messages.add(new MsgObject(msgObject));
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}

						// MT 7
						msgObject
								.setUsertext("Van con hang ngan BH khac tren "
										+ numberid
										+ ". De nhan duoc ma so nhac cho theo ten BH ban yeu thich.Soan: "
										+ last_msg);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						// messages.add(new MsgObject(msgObject));
						DBUtil.sendMT(msgObject);

						return null;

					}
				} else {

					// MT 1
					String info2Client = "";
					if ("viettel".equalsIgnoreCase(infoid)) {
						info2Client = viettelHot;
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						info2Client = vinaphoneHot;
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						info2Client = mobifoneHot;
					} else if ("beeline".equalsIgnoreCase(infoid)) {
						info2Client = beelineHot;
					}

					msgObject.setUsertext(info2Client + singer_sms);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					// messages.add(new MsgObject(msgObject));
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);

					// MT2 den MT 6
					int max = 0;
					for (int i = 0; i < resultMusic.length; i++) {
						if (!"".equalsIgnoreCase(resultMusic[i])) {
							max++;
							msgObject.setUsertext(resultMusic[i]);
							msgObject.setContenttype(0);
							msgObject.setMsgtype(0);
							// messages.add(new MsgObject(msgObject));
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						}
					}

					// Neu chua du 5MT
					if (max < 1) {
						// Lay danh sach cac bai hat da gui
						String lastid = getMusicID(dbcontent, userid);
						String[] musicHot = new String[1 - max];
						musicHot = findListofMusic(dtbase, userid, infoid,
								lastid, 1);
						for (int i = 0; i < musicHot.length; i++) {
							if (!"".equalsIgnoreCase(musicHot[i])) {
								max++;
								msgObject.setUsertext(musicHot[i]);
								msgObject.setContenttype(0);
								msgObject.setMsgtype(0);
								// messages.add(new MsgObject(msgObject));
								DBUtil.sendMT(msgObject);
								Thread.sleep(500);
							}
						}
					}

					// MT7
					msgObject
							.setUsertext("Van con hang ngan BH khac tren "
									+ numberid
									+ ". De nhan duoc ma so nhac cho theo ten BH ban yeu thich.Soan: "
									+ last_msg);
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					// messages.add(new MsgObject(msgObject));
					DBUtil.sendMT(msgObject);

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
	private static boolean saveClient(String userid, String operator,
			String keyword, String serviceid, Timestamp timesend,
			BigDecimal requestid) {
		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO icom_list_client( userid, operator, keyword, serviceid, timesend, requestid) VALUES ('"
					+ userid
					+ "','"
					+ operator.toUpperCase()
					+ "','"
					+ keyword
					+ "','"
					+ serviceid.toUpperCase()
					+ "','"
					+ timesend
					+ "','" + requestid + "')";
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.execute()) {
				Util.logger.error("Insert into icom_list_client");
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

	private static String[] findMusic(String dtbase, String userid,
			String find, String infoid, String lastid, int max) {

		String[] result = new String[max];
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String temp_id = lastid;

		for (int j = 0; j < max; j++) {
			result[j] = "";
		}

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid"
					+ " FROM icom_music WHERE ( upper(operator)= '"
					+ infoid.toUpperCase() + "') AND ( upper(group1) = '"
					+ find.toUpperCase() + "' OR upper(group2) = '"
					+ find.toUpperCase() + "' OR upper(group3) = '"
					+ find.toUpperCase() + "' OR upper(group4) = '"
					+ find.toUpperCase() + "' OR upper(group5) = '"
					+ find.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			String sqlOrder = " ORDER BY rand() LIMIT 1000";
			sqlSelect = sqlSelect + sqlOrder;

			Util.logger.info(" SEARCH MUSIC OF SINGER: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			int i = 0;
			String temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					temp = temp + rs.getString(1) + "-" + rs.getString(2) + ";";
					if (temp.length() > 160) {
						i = i + 1;
						temp = "";
					} else {
						result[i] = temp;
					}

					// Update Last String
					if ("".equalsIgnoreCase(lastid)) {
						lastid = lastid + rs.getString(2);
					} else {
						lastid = lastid + "," + rs.getString(2);
					}

					// If i =4
					if (i == max) {
						updateListMusic(userid, lastid, "icom_nhaccho_casy");
						return result;
					}
				}
			}
			updateListMusic(userid, lastid, "icom_nhaccho_casy");
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

	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;

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
		while (!(resultBoolean)) {
			if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
				result[1] = tempString.substring(j, i);
				resultBoolean = true;
			}
			i--;
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

	/* Cap nhat danh sach cac bai hat da gui den khach hang */
	private static boolean updateListMusic(String userid, String lastid,
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
			String sqlUpdate = "UPDATE " + dtbase + " SET musicid = '" + lastid
					+ "' WHERE upper(userid)='" + userid.toUpperCase() + "'";
			Util.logger.info(" UPDATE DATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Update list music to send " + userid
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

	// Tim cac musicid cua ca sy da gui cho khach hang
	private static String getMusicID(String dtcontent, String userid) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "-";
		try {
			cnn = dbpool.getConnectionGateway();

			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}

			String query = "SELECT musicid FROM " + dtcontent
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

	private static boolean saverequest(String dbcontent, String userid,
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

			String sqlInsert = "INSERT INTO " + dbcontent
					+ "(userid, musicid) VALUES ('" + userid + "','" + lastid
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

	private static String[] findListofMusic(String dtbase, String userid,
			String infoid, String lastid, int max) {
		String musiclist = lastid;
		String[] result = new String[max];
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		result[0] = "Ma so cac bai hat HOT khac: ";
		if (max > 1) {
			for (int i = 1; i < max; i++) {
				result[i] = "";
			}
		}

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			String sqlOrder = " ORDER BY rand() LIMIT 1000";

			sqlSelect = sqlSelect + sqlOrder;

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			String temp = result[0];
			int i = 0;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					temp = temp + rs.getString(1) + "-" + rs.getString(2) + ";";
					if (temp.length() > 160) {
						i = i + 1;
						temp = "";
					} else {
						result[i] = temp;
					}

					// Update Last String
					if ("".equalsIgnoreCase(lastid)) {
						lastid = lastid + rs.getString(2);
					} else {
						lastid = lastid + "," + rs.getString(2);
					}

					// If i =4
					if (i == max) {
						updateListMusic(userid, lastid, "icom_nhaccho_casy");
						return result;
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

}
