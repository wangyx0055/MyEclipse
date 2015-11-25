package advice;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import cs.ExecuteADVCR;

public class Loichuc extends ContentAbstract {

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

	int getOption(HashMap _option1, int defaultvalue) {
		try {
			return Integer.parseInt((String) _option1.get("gid"));
		} catch (Exception e) {
			return defaultvalue;
		}

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

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String VALID_CODE = "";

		try {

			HashMap _option = new HashMap();

			String options = keyword.getOptions();

			_option = getParametersAsString(options);

			if (msgObject.getMobileoperator().equalsIgnoreCase("EVN")
					|| msgObject.getMobileoperator().equalsIgnoreCase("SFONE")) {
				msgObject.setUsertext("Dich vu chua ho tro mang cua ban");

				msgObject.setMsgtype(2);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));

				return messages;
			}

			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			_option = getParametersAsString(options);
			String GAME_ID = "loichuc";

			String content = getContent(GAME_ID);
			Util.logger.info("content:"+content);
			if ("".equalsIgnoreCase(content))
			{
				content="Chuc mung nam moi";
			}
			if (sTokens.length == 1) {
				content=content+"###De tiep tuc nhan duoc loi chuc hay. Soan:LCA gui 8751. Tang cho ban be, soan tin : LCA sdtnguoinhan gui 8751.DTHT:1900571566";
				String[] sContent = content.split("###");

				for (int i = 0; i < sContent.length; i++) {
					if (!"".equalsIgnoreCase(sContent[i])) {
						msgObject.setUsertext(sContent[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);
						} else {
							msgObject.setMsgtype(0);
						}
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
				}

				return messages;
			} else {

				String phone = msgObject.getUserid();

				String giff_userid = "";
				String giff_telco = "";
				boolean giff = false;

				giff_userid = ValidISDN(sTokens[1]);
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

				if (giff == true) {
					content="Ban nhan duoc loi chuc tu so dien thoai:"+phone+"###"+content;
					String[] sContent = content.split("###");

					for (int i = 0; i < sContent.length; i++) {
						if (!"".equalsIgnoreCase(sContent[i])) {

							sendGifMsg(msgObject.getServiceid(), giff_userid,
									giff_telco, msgObject.getKeyword(),
									sContent[i], msgObject.getRequestid(), 0);
						}
					}

					msgObject
							.setUsertext("Ban da gui tang loi chuc thanh cong toi thue bao "
									+ giff_userid + ".");
					msgObject.setContenttype(0);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));

				} else {
					msgObject
							.setUsertext("Tin nhan sai cu phap.DTHT:1900571566. Cai dat nhac cho truc tiep theo ten bai hat,duy nhat chi co tren 8x51 soan tin: MCA tenbaihat gui 8751.");

					msgObject.setMsgtype(1);

					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));

				}

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

		return null;
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
			Util.logger.info("Insert:" + sqlInsert);
			statement = connection.prepareStatement(sqlInsert);
			if (statement.executeUpdate() != 1) {
				Util.logger.error("Insert in to download");
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

	public String getContent(String gameid) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		Statement s = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "select content from icom_textbase_data where upper(gameid) = '"
					+ gameid.toUpperCase() + "' order by rand()";
			s = connection.createStatement();
			rs = s.executeQuery(query1);
			while (rs.next()) {
				content = rs.getString(1);
			}

		} catch (SQLException e) {

			System.out.println(e);
			return content;
		} finally {
			dbpool.cleanup(s);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}

		return content;
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
			long itemp = Long.parseLong(sISDN.trim());
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
			Util.logger.sysLog(2, this.getClass().getName(),
					"Insert  vmg_vnnlinks_winner Failed");
		}
	}
}
