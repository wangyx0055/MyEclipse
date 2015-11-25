package MostStep;

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
import com.vmg.soap.mo.SoapZonefreeMT;

import cs.ExecuteADVCR;

public class Chamsoc extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String gameid = "CHAMSOC";
		String operator = msgObject.getMobileoperator();
		try {
			Util.logger.sysLog(2, this.getClass().getName(), "options: "
					+ options);

			_option = getParametersAsString(options);
			dtbase = getString(_option, "dtbase", dtbase);
			gameid = getString(_option, "gameid", gameid);

			int[] result = new int[2];
			String infoid = msgObject.getMobileoperator();
			Timestamp time = msgObject.getTTimes();

			if ("-".equals(infoid) || "other".equalsIgnoreCase(infoid)) {
				msgObject
						.setUsertext("Xin loi,dich vu nay ko ho tro mang cua ban.Moi thac mac lien he: 19001515");
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}
			// log send MO		
			String temp1 = SoapMOMT.SendMO(msgObject);
			Util.logger.info("temp1____" + temp1);
			
			
			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			String mt = "";
			
				mt = getContent(sTokens[1]);
				if (mt.equalsIgnoreCase("")) {

					mt = "Tin sai cu phap.Lien he tong dai 1900571566 de biet them chi tiet.";
				}
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

						String mtcount = msgObject.getRequestid()+"";
						int si = j+1;
						mtcount =mtcount +"/"+ si;
						// log send MT		
						String temp3 = SoapMOMT.SendMT2(msgObject,mtcount);
						Util.logger.info("temp3____" + temp3);
						
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);

					}
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

			// log send MT		
			//String temp3 = SoapMOMT.SendMT(msg);
			//Util.logger.info("temp3____" + temp3);
			
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

	private static String getContent(String infoType) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String sequence_temp = "";
		try {
			cnn = dbpool.getConnection("contenthappyinfo");

			if (cnn == null) {
				Util.logger.error("Impossible to connect to DB");
				return sequence_temp;
			}

			String query = "SELECT content FROM icom_infoservice  where info_type='"
					+ infoType + "' order by id desc ,info_date  desc limit 1";
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
	public static String infoType(String command_code) {
		String  type = "";
		if (command_code.equalsIgnoreCase("DA")) {
			type = "CHAM SOC DA";
		}
		else if (command_code.equalsIgnoreCase("TOC")) {
			type = "CHAM SOC TOC";
		}
		

		return type;
	}

}
