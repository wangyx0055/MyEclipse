package xoso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import icom.common.DBUtils;
import icom.common.Util;
import icom.process.Constants;
import icom.process.ContentAbstract;
import icom.process.DBPool;
import icom.process.Keyword;
import icom.process.MsgObject;

public class DangkyXSMB extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		HashMap _option = new HashMap();

		String MLIST = "mlist_xsmb";
		String urlCharging = Constants._prop.getProperty("url_charging");
		String[] price = {"15000", "10000", "5000"};
		String[] mt_fress = {"30", "20", "10"};
		String charge = "";
		String amount = "";

 		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		MLIST = getStringfromHashMap(_option, "mlist", "mlist_xsmb");
		//String mtfree = getStringfromHashMap(_option, "mtfree", "x");
		String mtfree = "";
		int mtfreeInt = 0;
		String duration = getStringfromHashMap(_option, "duration", "0");
		String sservice = getStringfromHashMap(_option, "service", "XSTD");
		String companyid = getStringfromHashMap(_option, "companyid", "1");
		String errMsg = getStringfromHashMap(_option, "error_msg",
				"Tin nhan sai cu phap. Soan HD gửi 9292 de duoc huong dan. Soan DK 15 hoac DK 30 gui 9292  de su dung goi Standard nghe 100phut/thang voi gia 15.000d hoac goi Premium nghe 300phut/thang voi gia 30.000d. Soan XSMB gui 9292 de su dung goi dich vu tra cuu KQ Xo so Mien Bac hang ngay voi gia 15.000d/thang. Soan SCMB de su dung goi dich vu tra cuu thong tin soi cau, du doan KQ Xo so Mien Bac hang ngay voi gia 15.000d/thangLien he 9191 hoac truy cap:http://9292 .vinaphone.com.v");

		String DBCONTENT ="gateway";

		String duplicate = getStringfromHashMap(_option, "duplicate",
				"Ban da dang ky dich vu truoc do");
		String success = "";

		String info = msgObject.getUsertext().toUpperCase().trim();
		info = replaceAllWhiteWithOne(info.trim());

		String[] arrInfo = info.split(" ");

		boolean infodetails = false;
	//	Util.logger.info("info:" + info);

		String xosoMB = "XSTD";
		if (arrInfo.length > 1) {

			msgObject
					.setUsertext(errMsg);
			msgObject.setContenttype(0);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;

		} 
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		for(int i =0; i<price.length; i++) {
			charge = chargeRequest.resultChargeRequest(urlCharging, msgObject.getId(), msgObject.getUserid().substring(2), price[i], timeStamp, price[i],"0", "9292", "VOICEINFO");
			Util.logger.info("charge: " + charge);
			String[] result = {};
			int result1 = 0;
			if(charge != "") {
				result = charge.split("<ns1:Pps_chargeRequestReturn xsi:type=\"xsd:string\">");
				Util.logger.info("result: " + result[1]);
				result1 = result[1].indexOf("|");
				Util.logger.info("result1: " + result[1].substring(result1+1));
				if("0".equals(result[1].substring(result1+1))) {
					mtfree = mt_fress[i];
					amount = price[i];
					mtfreeInt = Integer.parseInt(mtfree);
					break;
				} else {
					mtfreeInt = 0;
					mtfree = "0";
					
					if (isexist(msgObject.getUserid(), MLIST, sservice, xosoMB)) {

						// Cong them tai khoan cho nguoi dung
						updateData(msgObject.getUserid(), msgObject.getServiceid(),
								msgObject.getServiceid(), MLIST, msgObject, mtfreeInt, 0,
								msgObject.getKeyword(), companyid, sservice, xosoMB);
						msgObject.setUsertext(duplicate);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(21);
						DBUtils.sendMT(msgObject);
						Thread.sleep(500);
						return null;

					} else {
						// Thong bao dang ky thanh cong insert vao csdl
						insertData(msgObject.getUserid(), msgObject.getServiceid(),
								msgObject.getServiceid(), MLIST, msgObject, mtfree, 0,
								msgObject.getKeyword(), companyid, sservice, xosoMB, "0");
					msgObject.setUsertext("Ban da dang ky thanh cong goi dich vu tin nhan tra cuu KQ Xo so Mien Bac hang ngay cua VinaPhone. Hang ngay, he thong se tu dong tra tin cho ban ngay sau khi co ket qua. Ban chua the su dung dich vu do tai khoan khong du, hay nap them tien de su dung. Lien he 9191 hoac truy cap:http://9292 .vinaphone.com.vn");
					msgObject.setContenttype(21);
					msgObject.setMsgtype(0);
					DBUtils.sendMT(msgObject);
					Thread.sleep(500);
					return null;
					}
				}
			}
		}
		
		// Kiem tra su ton tai cua khach hang
		if (isexist(msgObject.getUserid(), MLIST, sservice, xosoMB)) {

			// Cong them tai khoan cho nguoi dung
			updateData(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfreeInt, 0,
					msgObject.getKeyword(), companyid, sservice, xosoMB);

			msgObject.setUsertext(duplicate);
			msgObject.setContenttype(0);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;

		} else {

			// Thong bao dang ky thanh cong insert vao csdl
			insertData(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfree, 0,
					msgObject.getKeyword(), companyid, sservice, xosoMB, amount);
			if(amount=="15000") {
				success = "Ban da dang ky thanh cong goi dich vu tin nhan tra cuu KQ Xo so Mien Bac hang ngay cua VinaPhone. Cuoc thue bao:15.000d/thang. Hang ngay, he thong se tu dong tra tin cho ban ngay sau khi co ket qua. Cam on ban da su dung dich vu cua VinaPhone. Lien he 9191 hoac truy cap:http://9292 .vinaphone.com.vn";
			} else if(amount=="10000") {
				success = "Ban da dang ky thanh cong goi dich vu tin nhan tra cuu KQ Xo so Mien Bac hang ngay cua VinaPhone voi cuoc phi 10.000d/20 ngay su dung. Hang ngay, he thong se tu dong tra tin cho ban ngay sau khi co ket qua. Cam on ban da su dung dich vu cua VinaPhone. Lien he 9191 hoac truy cap:http://9292 .vinaphone.com.vn";
			} else if(amount =="5000") {
				success = "Ban da dang ky thanh cong goi dich vu tin nhan tra cuu KQ Xo so Mien Bac hang ngay cua VinaPhone voi cuoc phi 5.000d/10 ngay su dung. Hang ngay, he thong se tu dong tra tin cho ban ngay sau khi co ket qua. Cam on ban da su dung dich vu cua VinaPhone. Lien he 9191 hoac truy cap:http://9292 .vinaphone.com.vn";
			}
			msgObject.setUsertext(success);
			msgObject.setContenttype(21);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));

			return messages;

		}
	}

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String companyid,
			String service, String xosoMB, String amount) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,options, amount) values ('"
				+ service.toUpperCase()
				+ "','"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ companyid + ",'" + xosoMB + "','" + amount + "' )";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
				/*Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);*/
				ireturn = -1;
			}
			
		} catch (Exception ex) {
		/*	Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");*/
			ireturn = -1;
		//	Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private int updateData(String user_id, String service_id,
			String command_code, String mlist, icom.process.MsgObject msgObject,
			int mtfreeint, int msgtype, String Service_ss_id, String companyid,
			String service, String bongda) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Update " + mlist + " set mt_free=mt_free + "
				+ mtfreeint + " WHERE upper(user_id)='" + user_id.toUpperCase()
				+ "' AND options='" + bongda.toUpperCase() + "'";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			/*Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");*/
			ireturn = -1;
		//	Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	// Check xem co ton tai trong danh sach hay khong
	private static boolean isexist(String userid, String mlist, String service,
			String options) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool = new DBPool();
		connection = null;
		statement = null;

		try {

			connection = dbpool.getConnectionGateway();

			String query3 = "select * from " + mlist + " where user_id='"
					+ userid + "' and upper(service)='"
					+ service.trim().toUpperCase() + "' AND upper(options)='"
					+ options.trim().toUpperCase() + "'";

			Util.logger.info(query3);

			Vector result3 = DBUtils.getVectorTable(connection, query3);
			if (result3.size() > 0) {
				Vector item = (Vector) result3.elementAt(0);
				return true;
			}
			return false;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			Util.logger.printStackTrace(e);
			return true;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}

	}

	// Lay cung hoang dao

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}
			return temp;
		} catch (Exception e) {
			return _defaultval;
		}

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


}
