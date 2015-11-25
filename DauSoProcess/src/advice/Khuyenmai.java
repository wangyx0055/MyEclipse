package advice;

import java.math.BigDecimal;
import java.sql.Connection;
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

public class Khuyenmai extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		try {

			HashMap _option = new HashMap();
			String GIFF_MSG = "";
			String GIFF_USER = "-";
			String GIFF_USER_TELCO = "-";
			String SUCCESS_MSG = "";
			String options = keyword.getOptions();
			String info = "De nhan tron bo K/M. May phai cai dat GPRS.Soan tin GPRS gui 8751 de cai dat GPRS tu dong";

			String giff_viettel = "Qua tang:http://www.mobinet.com.vn/Gift.aspx?o=3";
			String giff_mobifone = "Qua tang:http://www.mobinet.com.vn/Gift.aspx?o=1";
			String giff_vinaphone = "Qua tang:http://www.mobinet.com.vn/Gift.aspx?o=2";

			try {

				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);

				GIFF_MSG = (String) _option.get("giff_msg");
				Util.logger.info("GIFF_MSG: " + GIFF_MSG);
				SUCCESS_MSG = ((String) _option.get("success_msg"));
				Util.logger.info("SUCCESS_MSG: " + SUCCESS_MSG);
				info = getString(_option, "info", info);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			String operator = msgObject.getMobileoperator();
			String usertext = msgObject.getUsertext();
			String serviceid = msgObject.getServiceid();
			String sKeyword = msgObject.getKeyword();
			String USERID = msgObject.getUserid();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");

			if (sTokens.length > 1) {
				GIFF_USER = ValidISDNNew(sTokens[1]);
				if (!"-".equalsIgnoreCase(GIFF_USER)) {
					GIFF_USER_TELCO = getMobileOperatorNew(GIFF_USER, 2);
				}
			}

			Util.logger.info("GIFF_USER_TELCO: " + GIFF_USER_TELCO);

			if (!"-".equalsIgnoreCase(GIFF_USER_TELCO)) {
				String infoid = checkOperator(GIFF_USER_TELCO);

				if ("other".equalsIgnoreCase(infoid)) {
					msgObject
							.setUsertext("Hien tai dich vu chua ho tro mang cua thue bao "
									+ GIFF_USER
									+ " ma ban gui tang. Vui long quay tro lai sau");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					// Gui thong bao thanh cong
					GIFF_MSG = GIFF_MSG.replace("#1", GIFF_USER);
					msgObject.setUsertext(GIFF_MSG);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));

					// Gui thong bao da duoc gui thanh cong
					SUCCESS_MSG = SUCCESS_MSG.replace("#1", USERID);
					sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO
							.toUpperCase(), sKeyword, SUCCESS_MSG, msgObject
							.getRequestid(), 0);

					if ("viettel".equalsIgnoreCase(infoid)) {
						sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO
								.toUpperCase(), sKeyword, giff_viettel,
								msgObject.getRequestid(), 8);
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO
								.toUpperCase(), sKeyword, giff_mobifone,
								msgObject.getRequestid(), 8);
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						sendGifMsg(serviceid, GIFF_USER, GIFF_USER_TELCO
								.toUpperCase(), sKeyword, giff_vinaphone,
								msgObject.getRequestid(), 8);

					}
				}

			} else {
				String infoid = checkOperator(operator);
				Util.logger.info("infoid: " + infoid);
				if ("other".equalsIgnoreCase(infoid)) {
					msgObject
							.setUsertext("Cam on ban da quan tam toi cac DV cua 8x51. Hien tai DV chua ho tro mang cua ban. Hay quay tro lai sau.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {

					msgObject.setUsertext(info);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					if ("viettel".equalsIgnoreCase(infoid)) {
						msgObject.setUsertext(giff_viettel);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(8);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else if ("mobifone".equalsIgnoreCase(infoid)) {
						msgObject.setUsertext(giff_mobifone);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(8);
						messages.add(new MsgObject(msgObject));
						return messages;
					} else if ("vinaphone".equalsIgnoreCase(infoid)) {
						msgObject.setUsertext(giff_vinaphone);
						msgObject.setMsgtype(0);
						msgObject.setContenttype(8);
						messages.add(new MsgObject(msgObject));
						return messages;

					}
				}
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
		return messages;
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

	private static String checkOperator(String sInput) {
		String infoid = "other";
		if ("VIETTEL".equalsIgnoreCase(sInput)
				|| "VIETEL".equalsIgnoreCase(sInput)) {
			infoid = "viettel";
		} else if (("VMS".equalsIgnoreCase(sInput))
				|| "mobifone".equalsIgnoreCase(sInput)) {
			infoid = "mobifone";
		} else if (("GPC".equalsIgnoreCase(sInput))
				|| ("VINAPHONE".equalsIgnoreCase(sInput))) {
			infoid = "vinaphone";
		} else {
			infoid = "other";
		}
		return infoid;
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

}
