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
 * @author not attributable
 * @version 1.0
 */

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.vmg.soap.mo.sendXMLStorenew;

import cs.ExecuteADVCR;

public class MusicStore extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://www.mobinet.com.vn";
	String URL_INV = "http://www.mobinet.com.vn/?c=wap3";

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
			// TODO: handle exception
			return defaultvalue;
		}

	}

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			// TODO: handle exception
			// Integer.parseInt((String) _option.get("num_cnt"));
			return defaultvalue;
		}

	}

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject
						.setUsertext("Dich vu khong ho tro mang cua ban. DTHT 1900571566");

				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}
			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			GID = getOption(_option, 0);
			String amount = getString(_option, "amount", "5000");
			String nametype = getString(_option, "nametype", "Ringtone");

			// System.err.println("GID:" + GID);
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			int code = 0;
			if (sTokens.length == 1) {
				msgObject
						.setUsertext("Ban nhan tin sai cu phap. Tang ban TOP nhac:"
								+ URL_INV);

				msgObject.setMsgtype(1);
				msgObject.setContenttype(8);
				messages.add(new MsgObject(msgObject));

				return messages;
			} else {
				String subTokens = "";
				for (int k = 1; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k];
				}

				String giff_userid = "";
				String giff_telco = "";
				boolean giff = false;

				if (sTokens.length > 2) {
					giff_userid = ValidISDN(sTokens[sTokens.length - 1]);
					if (!giff_userid.equalsIgnoreCase("-")) {
						giff_telco = getMobileOperator(giff_userid);

						if ("-".equals(giff_telco)
								|| "EVN".equalsIgnoreCase(giff_telco)
								|| ("SFONE".equalsIgnoreCase(giff_telco))) {
							giff = false;
						} else {
							giff = true;
						}

					}
				}

				if (giff == true) {
					
					subTokens="";
					for (int k = 1; k < sTokens.length - 1; k++) {
						subTokens = subTokens + sTokens[k];
					}
					code = getRingstore(subTokens);
					String link = "";
					if (code != 0) {
						link = sendXMLStorenew.SendXML(4,
								msgObject.getUserid(), code + "", 3, amount,
								now());

						if (link.startsWith("0")) {
							link = nametype + ":" + link.substring(2);
							sendGifMsg(msgObject.getServiceid(), giff_userid,
									giff_telco, msgObject.getKeyword(), link,
									msgObject.getRequestid(), 8);

							msgObject
									.setUsertext("Ban da gui tang bai hat thanh cong toi thue bao "
											+ giff_userid + ".");
							msgObject.setContenttype(0);
							msgObject.setMsgtype(1);
							DBUtil.sendMT(msgObject);
							
							return null;
						} else
							link = "Bai hat chua co tren he thong. Tang ban TOP nhac:"
									+ URL_INV;
						msgObject.setUsertext(link);
						msgObject.setContenttype(0);
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						
						return null;
					} else
						link = "Bai hat chua co tren he thong. Tang ban TOP nhac:"
								+ URL_INV;
					msgObject.setUsertext(link);
					msgObject.setContenttype(0);
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					
					return null;

				} else {
					code = getRingstore(subTokens);

					String link = "";
					if (code != 0) {
						link = sendXMLStorenew.SendXML(4,
								msgObject.getUserid(), code + "", 3, amount,
								now());

					}
					if (link.startsWith("0")) {
						link = nametype + ":" + link.substring(2);
					} else
						link = "Bai hat chua co tren he thong. Tang ban TOP nhac:"
								+ URL_INV;
					msgObject.setUsertext(link);
					msgObject.setContenttype(8);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
				}

				return messages;

			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

		return null;
	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}

	}

	public static int getRingstore(String ringtonename) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [RingtoneID]  FROM [IComStore].[dbo].[Ringtone] where  SearchRingtoneName like '%, "
				+ ringtonename + "%' ";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
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
		if (sISDN.trim().length() < 9) {
			return "-";
		}
		try {
			long itemp = Long.parseLong(sISDN);
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

	public static String getMobileOperator(String userid) {

		String code = "-";
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT operator FROM icom_isdnseries WHERE prefix= substr('"
				+ userid + "',1, length(prefix)) ";

		try {
			connection = dbpool.getConnection("gateway");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = item.elementAt(0).toString();
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
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
}
