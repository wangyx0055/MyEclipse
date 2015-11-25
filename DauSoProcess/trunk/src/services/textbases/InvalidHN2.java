package services.textbases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.sql.ResultSet;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;

/**
 * InvalidHN class.<br>
 * 
 * <pre>
 * ・</pre>
 * 
 * @author Vietnamnet ICom TrungVD
 * @version 1.0
 */
public class InvalidHN2 extends ContentAbstract {

	private static String MTCONTENT = "Cai dat GPRS 3.0 theo cong nghe moi va hien dai nhat,soan:SETO gui 8751.Cai dat va so huu tron bo nhac cho sieu hot tuyet hay,soan:MCY gui 8751.CSKH:19001745";
	private static String viettelGuide = "Dang ky sd nhac cho.Buoc 1,soan:DK gui 1221.Buoc 2,chon bai hat,soan:BH maso gui 1221.CSKH:19001745.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String vinaphoneGuide = "Dang ky sd nhac cho.Buoc 1,soan:DK gui 9194.Buoc 2,chon bai hat,soan:TUNE maso gui 9194.CSKH:19001745.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mobifoneGuide = "Dang ky sd nhac cho.Buoc 1,soan:DK gui 9224.Buoc 2,chon bai hat,soan:CHON maso gui 9224.CSKH:19001745.Danh sach ma cac BH nhac cho Hot trong cac tin nhan sau";
	private static String mt3 = "Cai dat GPRS 3.0 theo cong nghe moi va hien dai nhat,soan:SETO gui 8751.Tham gia game sieu toc va so huu NokiaE72 sanh dieu,soan:AV gui 8751.CSKH:19001745";

	private static String dbcontent = "icom_nhaccho_6554";
	private static String dtbase = "nhaccho";

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {
			Collection messages = new ArrayList();
			// MsgObject mt = msgObject;
			String infoid = "";
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.";

			String sUserid = msgObject.getUserid();
			String sKeyword = "ST";
			String sServiceid = msgObject.getServiceid();
			String operator = msgObject.getMobileoperator();

			/* Kiểm tra xem thuê bao của khách hàng */
			if ("VIETTEL".equalsIgnoreCase(operator)
					|| "VIETEL".equalsIgnoreCase(operator)) {
				infoid = "viettel";
			} else if (("VMS".equalsIgnoreCase(operator))
					|| "mobifone".equalsIgnoreCase(operator)) {
				infoid = "mobifone";
			} else if (("GPC".equalsIgnoreCase(operator))
					|| ("VINAPHONE".equalsIgnoreCase(operator))) {
				infoid = "vinaphone";
			} else {
				infoid = "other";
			}

			String sMTReturn = "";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);

			if ("6054".equalsIgnoreCase(sServiceid)
					|| "6154".equalsIgnoreCase(sServiceid)) {
				sMTReturn = MTCONTENT;
				msgObject.setUsertext(sMTReturn);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				// Dua ra thong bao neu khach hang khong thuoc 3 mang Viettel,
				// Mobifone, Vinaphone
				if ("other".equalsIgnoreCase(infoid)) {
					msgObject.setUsertext(inv_telco);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;
				}

				// Neu thoa man 3 mang Viettel, Mobifone, Vinaphone tim bai hat
				// HOT
				// Send MT huong dan
				String mt1 = "";
				if ("viettel".equalsIgnoreCase(infoid)) {
					mt1 = viettelGuide;
				} else if ("vinaphone".equalsIgnoreCase(infoid)) {
					mt1 = vinaphoneGuide;
				} else if ("mobifone".equalsIgnoreCase(infoid)) {
					mt1 = mobifoneGuide;
				}

				msgObject.setUsertext(mt1);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				// Send MT2
				int timesend = getUserID(sUserid, dbcontent, 0);
				String lastid = "";
				if (timesend >= 1) {
					// Gui lan thu 2
					lastid = getMusicID(sUserid, dbcontent, 0);
				} else {
					// Ghi lai thong tin ghi nhan khach hang da gui den 6x54
					saverequest(sUserid, dbcontent, 0);
				}
				Util.logger.info("Last ID:" + lastid);

				if (lastid.equalsIgnoreCase("null") || lastid == null) {
					lastid = "";
				}

				// Tim list bai hat HOT trong 1 MT
				String mt2 = "";
				mt2 = findMusic(dtbase, sUserid, "HOT", infoid, lastid);

				if (!"".equalsIgnoreCase(mt2)) {
					// Send cho khach hang
					// Send MT 2 va 3 cho khach hang
					String[] music2send = splitString(mt2);

					if (!"".equalsIgnoreCase(music2send[0])) {
						msgObject.setUsertext(music2send[0]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}
				} else {
					mt2 = findListofMusic(dtbase, sUserid, infoid, lastid, 0);
					String[] music2send = splitString(mt2);

					if (!"".equalsIgnoreCase(music2send[0])) {
						msgObject.setUsertext(music2send[0]);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}
				}

				// Send MT 3 cho khach hang
				msgObject.setUsertext(mt3);
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(500);

			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
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

			DBUtils.sendMT(msg);

		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "###");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			_params.put(key, value);
		}
		return _params;
	}

	private String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}

	/* Tìm list bai hat */
	private static String findListofMusic(String dtbase, String userid,
			String infoid, String lastid, int subcode) {
		String musiclist = lastid;
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

			String sqlSelect = "SELECT musicname_sms, musicid FROM icom_music WHERE ( upper(operator) = '"
					+ infoid.toUpperCase() + "')";

			if (!"".equalsIgnoreCase(lastid)) {
				sqlSelect = sqlSelect + " AND musicid not in(" + lastid + ") ";
			}

			Util.logger.info("SEARCH SONG MUSIC : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = result + rs.getString(1) + "-" + rs.getString(2)
							+ ";";
					if (result.length() > 160) {

						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, dbcontent, subcode);
						return result;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
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

	// Cập nhật danh sách các id của các bài hát đã gửi cho khách hàng.
	private static boolean updateListMusic(String userid, String lastid,
			String dtbase1, int subcode) {

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
			String sqlUpdate = "UPDATE " + dtbase1 + " SET musichotid = '"
					+ lastid + "' WHERE upper(userid)='" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
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

	/* Tim nhac HOT */
	private static String findMusic(String dtbase, String userid, String find,
			String infoid, String lastid) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String temp = "";

		try {
			connection = dbpool.getConnection(dtbase);
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return "";
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

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {

					temp = temp + rs.getString(1) + "-" + rs.getString(2) + ";";
					if (temp.length() > 160) {

						// cap nhat cho khach hang list danh sach da gui
						updateListMusic(userid, lastid, dbcontent, 0);
						return temp;
					} else {
						if ("".equalsIgnoreCase(lastid)) {
							lastid = lastid + rs.getString(2);
						} else {
							lastid = lastid + "," + rs.getString(2);
						}
					}
				}
			}
			updateListMusic(userid, lastid, dbcontent, 0);
			return temp;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return temp;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return temp;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
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
	private static String getMusicID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT musichotid FROM " + dtbase
					+ " WHERE userid= '" + userid.toUpperCase()
					+ "' AND subcode=" + subcode;
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

}