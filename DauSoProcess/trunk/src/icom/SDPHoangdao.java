package icom;

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

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.soap.mo.SoapLongcheerMO;
import com.vmg.soap.mo.SoapZonefreeMO;
import com.vmg.soap.mo.SoapZonefreeMT;

import cs.ExecuteADVCR;

public class SDPHoangdao extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();
		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);

			String mt = "";
			String info = msgObject.getUsertext().toUpperCase().trim();

			info = info.replace('-', ' ');
			info = info.replace(';', ' ');
			info = info.replace('+', ' ');
			info = info.replace('.', ' ');
			info = info.replace(',', ' ');
			info = info.replace('_', ' ');
			info = info.replace('/', ' ');

			info = replaceAllWhiteWithOne(info.trim());

			String[] arrInfo = info.split(" ");

			boolean infodetails = false;
			Util.logger.info("info:" + info);

			String HOROSCOPE = "";
			if (arrInfo.length > 2) {

				String sngay = arrInfo[1];
				String sthang = arrInfo[2];
				
				long requesttime = validdate(sngay, sthang);
				String shoroscope = getHoroscope(requesttime);
				if (!"x".equalsIgnoreCase(shoroscope)) {
					infodetails = true;
					HOROSCOPE = shoroscope;
				}
			} else {
				msgObject
						.setUsertext("Tin nhan sai cu phap. Soan tin "
								+ msgObject.getKeyword()
								+ " <ngaysinh> <thangsinh> gui "
								+ msgObject.getServiceid()
								+ " de duoc bat mi nhung bi mat ve cong viec, cuoc song, tinh cam.");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			if (infodetails == false) {

				// Loi tin nhan
				msgObject.setUsertext("Tin nhan sai cu phap. Soan tin "
						+ msgObject.getKeyword()
						+ " ngaysinh thangsinh gui "
						+ msgObject.getServiceid()
						+ " de duoc bat mi nhung bi mat ve cong viec, cuoc song, tinh cam.");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			mt = getContent(arrInfo[0]+HOROSCOPE);
			if (!mt.equalsIgnoreCase("")) {
				String[] mtsplit = mt.split("###");
				String total = "";
				String index = "";
				String ismore = "";
				int intindex = 0;
				for (int j = 0; j < mtsplit.length; j++) {
					if (!"".equalsIgnoreCase(mtsplit[j])) {

						msgObject.setUsertext(mtsplit[j]);
						if (j == 0) {
							msgObject.setMsgtype(1);

						} else
							msgObject.setMsgtype(0);

						msgObject.setContenttype(0);
						total = mtsplit.length + "";
						intindex = j + 1;
						index = intindex + "";
						if (j == mtsplit.length - 1) {
							ismore = 0 + "";
						} else
							ismore = 1 + "";

						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}
				}
			} else {
				msgObject
						.setUsertext("Ban nhan tin sai cu phap. DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

			}
			return null;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}
	String getHoroscope(long requesttime) {

		try {

			if (requesttime < 1)
				return "x";
			long bachduong1 = validdate("21", "3");
			long bachduong2 = validdate("19", "4");

			if (requesttime >= bachduong1 && requesttime <= bachduong2) {
				return "BD";
			}

			long kimnguu1 = validdate("20", "4");
			long kimnguu2 = validdate("20", "5");

			if (requesttime >= kimnguu1 && requesttime <= kimnguu2) {
				return "KN";
			}

			long songtu1 = validdate("21", "5");
			long songtu2 = validdate("21", "6");

			if (requesttime >= songtu1 && requesttime <= songtu2) {
				return "ST";
			}

			long cugiai1 = validdate("22", "6");
			long cugiai2 = validdate("22", "7");

			if (requesttime >= cugiai1 && requesttime <= cugiai2) {
				return "CG";
			}

			long sutu1 = validdate("23", "7");
			long sutu2 = validdate("22", "8");

			if (requesttime >= sutu1 && requesttime <= sutu2) {
				return "SUT";
			}

			long xunu1 = validdate("23", "8");
			long xunu2 = validdate("22", "9");

			if (requesttime >= xunu1 && requesttime <= xunu2) {
				return "XN";
			}

			long thienbinh1 = validdate("23", "9");
			long thienbinh2 = validdate("22", "10");

			if (requesttime >= thienbinh1 && requesttime <= thienbinh2) {
				return "TB";
			}

			long hocap1 = validdate("23", "10");
			long hocap2 = validdate("21", "11");

			if (requesttime >= hocap1 && requesttime <= hocap2) {
				return "HC";
			}

			long nhanma1 = validdate("22", "11");
			long nhanma2 = validdate("21", "12");
			if (requesttime >= nhanma1 && requesttime <= nhanma2) {
				return "NM";
			}

			long maket1 = validdate("22", "12");
			long maket2 = validdate("19", "1");
			if (requesttime >= maket1 && requesttime <= maket2) {
				return "MK";
			}

			long baobinh1 = validdate("20", "1");
			long baobinh2 = validdate("18", "2");

			if (requesttime >= baobinh1 && requesttime <= baobinh2) {
				return "BB";
			}

			long songngu1 = validdate("19", "2");
			long songngu2 = validdate("20", "3");

			if (requesttime >= songngu1 && requesttime <= songngu2) {
				return "SN";
			}
		} catch (Exception e) {
		}
		return "x";

	}

	long validdate(String sday, String smonth) {
		try {

			java.util.Calendar calendarcur = java.util.Calendar.getInstance();

			int iday = Integer.parseInt(sday);
			int imonth = Integer.parseInt(smonth) - 1;
			if (iday > 31 && iday < 1) {
				return 0;
			}
			if (imonth > 11 && imonth < 0) {
				return 0;
			}

			java.util.Calendar calendar = java.util.Calendar.getInstance();
			int iyear = calendarcur.get(calendarcur.YEAR);
			if (imonth == 0 && iday <= 19) {
				iyear = iyear + 1;
			}
			calendar.set(iyear, imonth, iday);
			return calendar.getTime().getTime();

		} catch (Exception e) {
		}
		return 0;
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
			Util.logger.sysLog(2, this.getClass().getName(), "Send gif Failed");
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

	private static int getValidPhone(String[] sTokens) {
		int place = 0;
		for (int i = 0; i < sTokens.length; i++) {
			if (!"-".equalsIgnoreCase(ValidISDNNew(sTokens[i]))) {
				place = i;
				return place;
			}
		}
		return place;
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

	public static String getContent(String Code) {
		// tach lastcode

		String content;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "select Content from [IComStore].[dbo].[SDPText] where Code = '"
				+ Code + "'  order by CreateDate desc";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				content = item.elementAt(0).toString();
				return content;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return "";
		} finally {
			dbpool.cleanup(connection);
		}
		return "";
	}

}
