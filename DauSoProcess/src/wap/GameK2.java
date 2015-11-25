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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.axis.transport.jms.TopicConnector;

import com.vmg.soap.mo.MOSender;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;

import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.Utils;

import cs.ExecuteADVCR;

public class GameK2 extends ContentAbstract {

	String INVALID_CODE = "xxxxxxxxxx";
	int GID = 0;
	String SERVER = "http://mobinet.vn/kdownload.ashx";
	String TOP_CODE = "1";
	int NUM_SUBCODE = 0;

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
					"Dich vu khong ho tro mang CDMA");

			String type = getString(_option, "type", "1");
			String type_name = getString(_option, "type_name", "Game");

			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext(mtcdma);

				msgObject.setMsgtype(2);
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
			
			String strTextEncoded =sTokens[1]+"&"+msgObject.getUserid()+"&" +msgObject.getRequestid()+"&"+type;
			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			strTextEncoded = encoder.encode(strTextEncoded.getBytes());
		
				insertMO2lottery(msgObject);
			
					msgObject.setUsertext(type_name+":" + SERVER + "/?key=" + strTextEncoded);
					msgObject.setContenttype(8);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));
					return messages;

				

		

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

	private static String getValue(String xml, String tagName) {
		String openTag = "<" + tagName + ">";
		String closeTag = "</" + tagName + ">";

		int f = xml.indexOf(openTag) + openTag.length();
		int l = xml.indexOf(closeTag);

		return (f > l) ? "" : xml.substring(f, l);
	}

	public String sendMessageMO(MsgObject msgObject, String textMessage,
			String partner, String commandcode) throws Exception {

		String url = Constants._prop.getProperty("mo.soap." + partner + ".url");

		String username = Constants._prop.getProperty("mo.soap." + partner
				+ ".username");
		String password = Constants._prop.getProperty("mo.soap." + partner
				+ ".password");
		String action = Constants._prop.getProperty("mo.soap." + partner
				+ ".action");
		String module = Constants._prop.getProperty("mo.soap." + partner
				+ ".module");
		String encode = Constants._prop.getProperty("mo.soap." + partner
				+ ".encode", "");
		String telco = Constants._prop.getProperty("mo.soap." + partner
				+ ".telco", Constants.TELCOLIST);

		if (url == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".url");

		if (module == null)
			throw new Exception("In the profile is missing mo.soap." + partner
					+ ".module");

		MOSender sender = (MOSender) Class.forName(module).newInstance();
		String result = sender.sendMO(url, username, password, action,
				msgObject, textMessage, commandcode, encode, telco);

		return result;
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
	public String insertMO2lottery(MsgObject msgObject) throws Exception {

		Util.logger.info("insertMO2icom_wap:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "WAPICOM.DBO.MO_K2";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICEID, MOBILE_OPERATOR, COMMAND_CODE,  INFO, REQUEST_ID)"
				+ " values(?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnection("wapicom");
			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setBigDecimal(6, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertMO2icom_wap:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return "-1";
			}
			statement.close();
			return "1";
		} catch (SQLException e) {
			Util.logger.error("insertMO2icom_wap:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from MO_ZF:" + e.toString());
			return "-1";
		} catch (Exception e) {
			Util.logger.error("insertMO2icom_wap:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from MO_ZF:" + e.toString());
			return "-1";
		}

		finally {
			dbpool.cleanup(connection);

		}

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
