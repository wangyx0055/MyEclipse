package songxanh;

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

import cs.ExecuteADVCR;

public class KetquadanthuMC extends ContentAbstract {
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();
		String starttime="2010-03-15 23:59:00";
		String endtime="2011-05-15 23:59:00";
		
		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);
			String keywords = msgObject.getKeyword();
			String service_id = msgObject.getServiceid();
			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			starttime = getString(_option, "endtime", starttime);
			endtime = getString(_option, "endtime", endtime);
			
			Timestamp time = msgObject.getTTimes();
			Timestamp stime = Timestamp.valueOf(starttime);
			Timestamp etime = Timestamp.valueOf(endtime);

			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			if (time.getTime() > etime.getTime()) {
				msgObject
						.setUsertext("Xin loi hien tai chua co ket qua cua chuong trinh nay.De tham gia dien thu vai moi trong du an IFim,LH:19001515 hoac truy cap:http://ifim.vn");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			if (time.getTime() < stime.getTime()) {
				msgObject
						.setUsertext("Xin loi hien tai chua co ket qua cua chuong trinh nay.De tham gia dien thu vai moi trong du an IFim,LH:19001515 hoac truy cap:http://ifim.vn");
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			if (sTokens.length != 2) {
				String str = "Xin loi sai cu phap.De xem ket qua chuong trinh dan thu MC,soan tin "
						+ keywords
						+ " sobaodanh gui"
						+ service_id
						+ " Dien thoai ho tro:19001515 hoac truy cap: http://ifim.vn";
				msgObject.setUsertext(str);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				boolean giff = false;
				int sbd = Integer.parseInt(sTokens[1]);

				int kt = findGif(dtbase, sbd);

				if (kt != -1) {
					String str = "Chuc mung SBD:"
							+ fomatSBD(sbd)
							+ " da trung tuyen chuong trinh tham gia dan thu MC.Moi t.tin chi tiet bag LH:19001515 hoac truy cap: http://ifim.vn";
					msgObject.setUsertext(str);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					msgObject
							.setUsertext("De lay ket qua xo so nhanh nhat &chinh xac nhat trog 7 ngay,s.tin:KQ<matinh>3 gui 6793.Hoac de cai dat YAHOO CHAT,s.tin:CHAT gui 6793.Dt ho tro:19001515");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return messages;
				} else {
					String str = "Rat tiec SBD:"
							+ fomatSBD(sbd)
							+ " da khong trung tuyen chuong trinh tham gia dan thu MC.Moi t.tin chi tiet bag LH:19001515 hoac truy cap: http://ifim.vn";
					msgObject.setUsertext(str);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					msgObject
							.setUsertext("De lay ket qua xo so nhanh nhat &chinh xac nhat trog 7 ngay,s.tin:KQ<matinh>3 gui 6793.Hoac de cai dat YAHOO CHAT,s.tin:CHAT gui 6793.Dt ho tro:19001515");
					msgObject.setMsgtype(0);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return messages;
				}

			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(),
					"Exception:bbbbbbbbbbbbb " + e.getMessage());
			return null;

		} finally {
			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	private static int findGif(String dtbase, int sbd) {
		int result = -1;
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
            String strsbd=fomatSBD(sbd);
			String sqlSelect = "SELECT sbd FROM ketqua_mc WHERE sbd=" + strsbd;
			Util.logger.info("Tim so bao danh trung thuong: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
				}
			}
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
	public static String fomatSBD(Integer num) {
		String str = "" + num;
		str = "0000".substring(0, 4 - str.length()) + str;
		return str;

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

	
}
