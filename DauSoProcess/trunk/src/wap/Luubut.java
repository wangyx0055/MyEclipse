package wap;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author Haptt
 * @version 1.0
 */

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

import org.apache.axis.transport.jms.TopicConnector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.Utils;

import cs.ExecuteADVCR;

public class Luubut extends ContentAbstract {

	String INVALID_CODE = "0";
	int GID = 0;
	String SERVER = "http://www.mobinet.com.vn";
	String URL_INV = "http://www.mobinet.com.vn/?c=wap3";
	String TOP_CODE = "1";
	int NUM_SUBCODE = 1;
	String filmname = "Nguoi thu ba hanh phuc";
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

	String getOption(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("gid")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	String getTopcode(HashMap _option1, String defaultvalue) {
		try {
			return ((String) _option1.get("topcode")).toUpperCase();
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	int getNumsubcode(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("numsubcode"));
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	private static String findCodemax(int gid, String ftype) {
		String result = "1";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String dbcontent = "icom_wap.dbo.upload";
		try {
			connection = dbpool.getConnection("content");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT max(code) FROM " + dbcontent
					+ " WHERE upper(filetype)='" + ftype.toUpperCase().trim()
					+ "' and cgroup=" + gid  ;

			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
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

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| (temp == null)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			String mtcdma = getString(
					_option,
					"mtcdma",
					"Xin loi,dich vu nay ko ho tro mang di dong nay.Moi thac mac lien he: 19001515");

			String type = getString(_option, "type", "media");

			SERVER = Constants._prop.getProperty("WAP.SERVER",
					"http://www.mobinet.com.vn");
			URL_INV = Constants._prop.getProperty("WAP.INV",
					"http://www.mobinet.com.vn/?c=wap3");
			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext(mtcdma);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			GID = getOption(_option, 0);
			NUM_SUBCODE = getNumsubcode(_option, 0);
			filmname = getString(_option, "filmname", filmname);
			TOP_CODE = findCodemax(GID, type);

			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (sTokens.length >= 4) {
				msgObject.setUsertext("Ban da nt sai cu phap.De tai clip soan LB maso,de tang clip soan LB maso sodtnhan gui 6793.DT htro: 1900571566 ");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}

			if (sTokens.length == 1) {
				VALID_CODE = TOP_CODE;
			} else {
				VALID_CODE = validcode(sTokens[1], GID, type);

			}

			if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
				String phone = msgObject.getUserid();
				String code = VALID_CODE;
				int code1 = Integer.parseInt(code) + 1;

				if (saverequest(phone, VALID_CODE, type, GID)) {

					String giff_userid = "";
					String giff_telco = "";
					boolean giff = false;

					if (sTokens.length > 2) {
						giff_userid = ValidISDNNew(sTokens[2]);
						if (!giff_userid.equalsIgnoreCase("-")) {
							// giff_telco = getMobileOperator(giff_userid);
							giff_telco = Utils
									.getMobileOperator(giff_userid, 1);

							if ("-".equals(giff_telco)
									|| "EVN".equalsIgnoreCase(giff_telco)
									|| ("SFONE".equalsIgnoreCase(giff_telco))) {
								giff = false;
								msgObject
										.setUsertext("Hien tai dich vu chua ho tro mang cua thue bao duoc tang");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								messages.add(new MsgObject(msgObject));
								return messages;

							} else {
								giff = true;
							}

						}else
						{
							msgObject.setUsertext("Ban da nt sai cu phap.De tai clip soan LB maso,de tang clip soan LB maso sodtnhan gui 6793.DT htro: 1900571566 ");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							messages.add(new MsgObject(msgObject));

							return messages;
						}
						
					}

					if (giff == true) {
						saverequest(phone, code, type, GID);
						sendGifMsg(
								msgObject.getServiceid(),
								giff_userid,
								giff_telco,
								msgObject.getKeyword(),
								"Ban da duoc so dt "
										+ msgObject.getUserid()
										+ " gui tang video luu but.De tai duoc clip dthoai phai cai GPRS,de cai GPRS Soan tin: GPRS gui 6793",
								msgObject.getRequestid(), 0);
						Thread.sleep(1000);
						sendGifMsg(msgObject.getServiceid(), giff_userid,
								giff_telco, msgObject.getKeyword(), "Video:"
										+ SERVER + "/?p=" + phone + "&c="
										+ code + "&f=" + type + "&g=" + GID,
								msgObject.getRequestid(), 8);
						Thread.sleep(1000);
						sendGifMsg(
								msgObject.getServiceid(),
								giff_userid,
								giff_telco,
								msgObject.getKeyword(),
								"De tai duoc clip dthoai cua ban phai cai GPRS,de cai GPRS soan tin GPRS gui 6793",
								msgObject.getRequestid(), 0);

						Thread.sleep(1000);

						msgObject
								.setUsertext("Ban da gui tang thanh cong den so dien thoai "
										+ giff_userid
										+ ",cai dat GPRS tu dong Soan tin: GPRS gui  6793 ");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;

					} else {
						msgObject.setUsertext("Video:" + SERVER + "/?p=" + phone
								+ "&c=" + code + "&f=" + type + "&g=" + GID);
						msgObject.setContenttype(8);
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						msgObject
								.setUsertext("De tai duoc clip dthoai cua ban phai cai GPRS,de cai GPRS soan tin GPRS gui 6793");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						return messages;
					}
				}

			} else {
				msgObject
						.setUsertext("Ma so video sai.De tai clip soan LB maso,de tang clip soan LB maso sodtnhan gui 6793.DT htro: 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(1);
				messages.add(new MsgObject(msgObject));
				return messages; 

			}

		} catch (Exception e) {
			Util.logger.error("Exception: " + e.getMessage());
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

		return messages;
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

	private String validcode(String code, int gid, String ftype) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			String query1 = "select code,filetype from icom_wap.dbo.upload where (upper(code)='"
					+ code.toUpperCase()
					+ "' or upper(code)='"
					+ code.toUpperCase()
					+ "P' ) and upper(filetype)='"
					+ ftype.toUpperCase().trim() + "' and cgroup=" + gid;

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {

				Vector item = (Vector) result.elementAt(0);

				String codetemp = (String) item.elementAt(0);

				return codetemp;
			}

		} catch (Exception e) {
		} finally {
			dbpool.cleanup(connection);

		}
		return INVALID_CODE;
	}

	private static boolean saverequest(String userid, String code, String type,
			int gid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("content");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "Insert into icom_wap.dbo.download( phone,code,filetype,cgroup) values ('"
					+ userid + "','" + code + "','" + type + "'," + gid + ")";
			// Util.logger.error("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to download");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(":Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	/*
	 * Thay nhieu dau _____ -> _
	 */
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
			Util.logger.info(this.getClass().getName() + "Exception?*"
					+ e.toString() + "*");
			return "-";
		}
		return tempisdn;
	}

	private String getMobileOperator(String mobileNumber) {
		if (mobileNumber.startsWith("8491") || mobileNumber.startsWith("+8491")
				|| mobileNumber.startsWith("091")
				|| mobileNumber.startsWith("91")
				|| mobileNumber.startsWith("8494")
				|| mobileNumber.startsWith("+8494")
				|| mobileNumber.startsWith("094")
				|| mobileNumber.startsWith("94")
				|| mobileNumber.startsWith("0123")
				|| mobileNumber.startsWith("84123")
				|| mobileNumber.startsWith("84125")
				|| mobileNumber.startsWith("0125")) {
			return "GPC";
		} else if (mobileNumber.startsWith("8490")
				|| mobileNumber.startsWith("+8490")
				|| mobileNumber.startsWith("090")
				|| mobileNumber.startsWith("90")
				|| mobileNumber.startsWith("8493")
				|| mobileNumber.startsWith("+8493")
				|| mobileNumber.startsWith("093")
				|| mobileNumber.startsWith("93")
				|| mobileNumber.startsWith("0122")
				|| mobileNumber.startsWith("84122")
				|| mobileNumber.startsWith("84126")
				|| mobileNumber.startsWith("0126")) {
			return "VMS";
		} else if (mobileNumber.startsWith("8498")
				|| mobileNumber.startsWith("+8498")
				|| mobileNumber.startsWith("098")
				|| mobileNumber.startsWith("98")
				|| mobileNumber.startsWith("8497")
				|| mobileNumber.startsWith("+8497")
				|| mobileNumber.startsWith("097")
				|| mobileNumber.startsWith("97")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0168")
				|| mobileNumber.startsWith("84168")
				|| mobileNumber.startsWith("0169")
				|| mobileNumber.startsWith("84169")
				|| mobileNumber.startsWith("84166")
				|| mobileNumber.startsWith("0166")) {
			return "VIETTEL";
		} else if (mobileNumber.startsWith("8495")
				|| mobileNumber.startsWith("+8495")
				|| mobileNumber.startsWith("095")
				|| mobileNumber.startsWith("95")) {
			return "SFONE";
		} else if (mobileNumber.startsWith("8492")
				|| mobileNumber.startsWith("+8492")
				|| mobileNumber.startsWith("092")
				|| mobileNumber.startsWith("92")) {
			return "HTC";
		} else if (mobileNumber.startsWith("8496")
				|| mobileNumber.startsWith("+8496")
				|| mobileNumber.startsWith("096")
				|| mobileNumber.startsWith("96")) {
			return "EVN";
		} else {
			return "-";
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
			Util.logger
					.sysLog(2, this.getClass().getName(), "sendGifMsgFailed");
		}
	}
}
