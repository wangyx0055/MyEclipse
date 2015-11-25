package songxanh;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class GameWappush extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	// String SERVER = "http://6x54.com.vn";
	// String URL_INV = "http://6x54.com.vn/?c=wap3";
	String SERVER = "http://www.mobinet.com.vn";
	String URL = "http://mobinet.com.vn/?p=841226307035&c=quatanggame&f=JavaGame&g=12";
	String URL_INV = "http://www.mobinet.com.vn/?c=wap3";
	// directory = Constants._prop.getProperty("dirtextbase", directory);
	String TOP_CODE = "1";
	int NUM_SUBCODE = 0;
	String infoid = "";

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

			String mtcdma = getString(_option, "mtcdma",
					"Xin loi,dich vu nay ko ho tro mang di dong nay.Moi thac mac lien he:19001515");

			String type = getString(_option, "type", "media");
			URL = getString(_option, "url", URL);

			SERVER = Constants._prop.getProperty("WAP.SERVER",
					"http://www.mobinet.com.vn");
			URL_INV = Constants._prop.getProperty("WAP.INV",
					"http://www.mobinet.com.vn/?c=wap3");

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

			String userid = msgObject.getUserid();

			if ("other".equalsIgnoreCase(infoid)) {
				msgObject.setUsertext(mtcdma);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			GID = getOption(_option, 0);
			TOP_CODE = getTopcode(_option, "1");
			NUM_SUBCODE = getNumsubcode(_option, 0);

			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

			// String MASO = "1";
			if (sTokens.length == 1) {

				VALID_CODE = TOP_CODE;
			} else {
				VALID_CODE = validcode(sTokens[1], GID, type);

			}

			if (!INVALID_CODE.equalsIgnoreCase(VALID_CODE)) {
				String phone = msgObject.getUserid();
				String code = VALID_CODE;

				if (saverequest(phone, VALID_CODE, type, GID)) {

					String giff_userid = "";
					String giff_telco = "";
					boolean giff = false;

					if (sTokens.length > NUM_SUBCODE + 1) {
						giff_userid = ValidISDN(sTokens[NUM_SUBCODE + 1]);
						if (!giff_userid.equalsIgnoreCase("-")) {
							// giff_telco = getMobileOperator(giff_userid);
							giff_telco = Utils
									.getMobileOperator(giff_userid, 1);

							if ("-".equals(giff_telco)
									|| "other"
											.equalsIgnoreCase(checkMobileOperator(giff_telco))) {
								giff = false;
							} else {
								giff = true;
							}

						}
					}

					if (giff == true) {

						saverequest(phone, code, type, GID);
						sendGifMsg(msgObject.getServiceid(), giff_userid,
								giff_telco, msgObject.getKeyword(), "Game:" + SERVER + "/?p="
										+ phone + "&c=" + code + "&f=" + type
										+ "&g=" + GID,
								msgObject.getRequestid(), 8);
						Thread.sleep(1000);
						sendGifMsg(msgObject.getServiceid(), giff_userid,
								giff_telco, msgObject.getKeyword(), "Tang kem 10 game:" + URL,
								msgObject.getRequestid(), 8);
						Thread.sleep(1000);
						sendGifMsg(
								msgObject.getServiceid(),
								giff_userid,
								giff_telco,
								msgObject.getKeyword(),
								"Ban vua nhan dc java game tu so dt"
										+ msgObject.getUserid()
										+ ".Hay cai dat tu dong GRPS,s.tin:GPRS gui 6793.Dt ho tro:19001515",
								msgObject.getRequestid(), 0);
					msgObject
								.setUsertext("Ban gui tang so dt "
										+ giff_userid
										+ " java GAME.De cai dat nhac cho cuc HOT cho DE,s.tin:NC5 dau cach <tenbaihat> gui 6593.Dt ho tro:19001515");
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						messages.add(new MsgObject(msgObject));
						return messages;

					} else {
						msgObject.setUsertext("Game:" + SERVER + "/?p=" + phone
								+ "&c=" + code + "&f=" + type + "&g=" + GID);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(8);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						msgObject.setUsertext("Tang ban 10 game:" + URL);
						msgObject.setContenttype(8);
						msgObject.setMsgtype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

						msgObject
								.setUsertext("C.on ban s.dug d.vu.Hay cai dat cau hinh tu dong GPRS&album khuyen mai game,nhac va hinh cho DE,s.tin:GPRS gui 6793.CSKH:19001515");
						msgObject.setMsgtype(0);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return messages;

					}
				}

			} else {
				
				saverequest(msgObject.getUserid(), TOP_CODE, type, GID);
				msgObject
						.setUsertext("Ma so khong hop le.De nhan ngay game HANG KHUNG ve may,s.tin:GA daucach (macode) gui 6793.Hoac de cai dat tu dong GRPS,s.tin:GPRS gui 6793.CSKH:19001515");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
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
		return TOP_CODE;
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
}