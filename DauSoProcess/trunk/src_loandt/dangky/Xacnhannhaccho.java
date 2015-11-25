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
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.sendXMLRING;

import cs.ExecuteADVCR;

/**
 * Nhaccho class.<br>
 * 
 * <pre>
 * ・
 * </pre>
 * 
 * @author Vietnamnet I-Com HaPTT
 * @version 1.0
 */
public class Xacnhannhaccho extends ContentAbstract {

	/* First String */

	private static String dbcontent = "icom_dangky_nhaccho_tructiep";
	private static String urlring = "http://203.162.71.165:8686/SetRingBack.asmx?";

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "1";

		try {
			Collection messages = new ArrayList();

			String infoid = "HOT";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Ban nhan tin sai cu phap. DTHT 1900571566 ";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			/* kiem tra thue bao khach hang */
			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {
				infoid = "viettel";
			} else {
				infoid = "other";
			}

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// lay so thue bao nguoi gui
			String mt1 = "";
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext()
					.toUpperCase());
			String operator = msgObject.getMobileoperator();
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String serviceid = msgObject.getServiceid();
			BigDecimal request_id = msgObject.getRequestid();
			/*
			 * nc ten bai hat
			 */

			if (sTokens.length == 2) {
				Util.logger.info("stoken.length=2");

				String contentid = getContentID(userid, "nhaccho_viettel");
				Util.logger.info("getContentID");

				String usergif = getUsergift(userid, "nhaccho_viettel");
				if (contentid.equalsIgnoreCase("")) {
					Util.logger.info("contentid=null");

					mt1 = "Ban can dang ky nhac cho de lay ma kiem tra truoc. Soan tin DK gui 8551";
					Util.logger
							.info("Ban can dang ky nhac cho de lay ma kiem tra truoc. Soan tin DK gui 8551");
				} else {
					Util.logger.info("contentid=" + contentid);

					mt1 = "Ban dang ky nhac cho chua thanh cong vui long soan DK gui 1221";
					try {
						String result = sendXMLRING.SendXML(urlring,
								"ConfirmValidCode", userid, sTokens[1], "",
								now());
						Util.logger.info("result" + result);
						if (result.equalsIgnoreCase("0")) {
							mt1="";
						} else {
							mt1 = "Ma kiem tra khong chinh xac. Xin vui long xac nhan lai.DTHT 1900571566";
							Util.logger
									.info("Ma kiem tra khong chinh xac. Xin vui long xac nhan lai");
						}

					} catch (Exception ex) {
						Util.logger.error("Khong thanh cong");

					}

				}
			} else {
				String info2Client = "Tin nhan sai cu phap. Dien thoai ho tro :1900571566";
				msgObject.setUsertext(info2Client);
				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

			}
			if (!mt1.equalsIgnoreCase("")) {
				msgObject.setUsertext(mt1);
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
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}

	private static String Registry(String userid, String contenid) {
		String mt = "Ban cai dat bai hat chua thanh cong. Xin vui long thu lai";

		try {
			String result = sendXMLRING.SendXML(urlring, "SetTone", userid, "",
					contenid, now());
			Util.logger.info("result SetTone" + result);
			if (result.equalsIgnoreCase("0")) {
				mt = "";
				deleteUser(userid);
				Util.logger.info("Cai dat nhac cho thanh cong");

			} else if (result.equalsIgnoreCase("501")) {
				mt = "Bai hat da co trong bo suu tap cua ban";
				Util.logger.info("Bai hat da co trong bo suu tap");
			} else if (result.equalsIgnoreCase("101")) {
				mt = "Ban cai dat nhac cho chua thanh cong do bi khoa hoac khong du tien. Vui long thu lai sau. Tang ban TOP bai hat HOT:";
				Util.logger.info("Cai dat nhac cho thanh cong");

			}

		} catch (Exception ex) {
			Util.logger.error("Khong thanh cong");

		}

		return mt;
	}

	public String GetuserVT(String sISDN) {
		Util.logger.info(this.getClass().getName() + "GetuserVT?*" + sISDN
				+ "*");

		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = tempisdn.substring(3);
			} else if (tempisdn.startsWith("0")) {
				tempisdn = tempisdn.substring(2);
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	/* ghi lai dsach khach hang */
	public String ValidISDN(String sISDN) {
		Util.logger.info(this.getClass().getName() + "ValidISDN?*" + sISDN
				+ "*");
		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}

		String tempisdn = "-";

		if (sISDN.trim().length() < 8) {
			return "-";
		}
		try {
			long itemp = Integer.parseInt(sISDN);
			Util.logger.info(this.getClass().getName() + "itemp?*" + itemp
					+ "*");
			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
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

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static String getContentID(String userid, String dtbase) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT contentid FROM " + dtbase
					+ " WHERE userid= '" + userid.toUpperCase()
					+ "'  order by id desc  limit 1";
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

	private static String getUsergift(String userid, String dtbase) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;

		String sequence_temp = "";
		try {
			cnn = dbpool.getConnectionGateway();

			String query = "SELECT usergift FROM " + dtbase
					+ " WHERE userid= '" + userid.toUpperCase()
					+ "'  order by id desc  limit 1";
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

	private static boolean deleteUser(String user) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM nhaccho_viettel  WHERE userid="
					+ user;
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Loi xoa user " + user
						+ "trong bang nhaccho_viettel");
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
