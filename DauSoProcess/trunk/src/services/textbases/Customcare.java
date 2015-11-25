package services.textbases;

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
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Customcare extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		/*
		 * cu phap Kh diachi noi dung
		 */

		String[] sTokens = replaceAllWhiteWithOne(msgObject.getUsertext())
				.split(" ");

		if (sTokens.length < 3) {
			// Tin sai cu phap
			msgObject
					.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			DBUtil.sendMT(msgObject);
			return null;
		}
		// check dia chi lay so dien thoai

		String send_userid = "";
		// phai co 1 ham de get so dien thoai tu dia chi vung mien
		// send_userid= Getsdt(sTokens[1]);
		String send_telco = "";
		boolean send = false;

		send_userid = ValidISDN(send_userid);
		if (!send_userid.equalsIgnoreCase("-")) {
			send_telco = getMobileOperatorNew(send_userid, 2);

			if ("-".equals(send_telco)) {
				send = false;
			} else {
				send = true;
			}

		}
		if (send) {
			String subTokens = "";
			for (int k = 2; k < sTokens.length; k++) {
				subTokens = subTokens + sTokens[k] + " ";
			}

			sendMsg(
					msgObject.getServiceid(),
					send_userid,
					send_telco,
					msgObject.getKeyword(),
					subTokens, msgObject
							.getRequestid(), 0);
		
			msgObject
					.setUsertext("Ban gui yeu cau thanh cong den nhan vien vung mien.");
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
			
			
		} else {
			// Dia chi chua hop le, khong lay duoc so dien thoai
			msgObject.setUsertext("Dia chi chua hop le");
			msgObject.setMsgtype(1);
			DBUtil.sendMT(msgObject);
		}
		return null;
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

	/**
	 * Ham check so dien thoai dung
	 * 
	 * @param sISDN
	 * @return
	 */
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
	
	/**
	 * Ham gui tin nhan cho nhan vien vung mien
	 * @param serviceid
	 * @param userid
	 * @param operator
	 * @param service
	 * @param mtcontent
	 * @param requestid
	 * @param contenttype
	 * 
	 */
	private void sendMsg(String serviceid, String userid, String operator,
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

}
