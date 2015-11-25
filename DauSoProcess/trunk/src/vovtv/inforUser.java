package vovtv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class inforUser extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String ismtadd = "0";
		String isforwardMO = "0";
		try {
			Collection messages = new ArrayList();

			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
			
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				ismtadd = getString(_option, "ismtadd", ismtadd);
				isforwardMO = getString(_option, "mo", isforwardMO);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */

			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			// lay so thue bao nguoi gui
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext());
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DATE);
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);
			String date = FormatNumber(day) + "/" + FormatNumber(month) + "/"
					+ year;
			String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";

			if (sTokens.length == 1) {
				msgObject
						.setUsertext("Tin ban gui khong hop le. De tim ban theo nam sinh, soan: SINH namsinh gui 6554. De tim ban theo noi o va nam sinh, soan SINH namsinh tinh gui 6554.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				List<String> lstInfor = findInforUser(msgObject.getUserid(), sTokens[1]);
				msgObject
				.setUsertext("Thong tin cua nick "+sTokens[1]+ " : gioi tinh "+lstInfor.get(1)+", nam sinh "+lstInfor.get(2) +", tinh "+lstInfor.get(3) +", so thich "+lstInfor.get(4) );
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				
				msgObject
				.setUsertext("De noi chuyen voi thanh vien khac tren 360 do Am nhac, soan: C nicknguoinhan noidung gui 6354. DTHT: 1900571566");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			if ("1".equalsIgnoreCase(ismtadd))
				ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
						.getServiceid(), msgObject.getUserid(), msgObject
						.getKeyword(), msgObject.getRequestid(), msgObject
						.getTTimes(), msgObject.getMobileoperator());
		}
	}
	
	/* tim ma so cac bai hat */
	private static List<String> findInforUser(String user_id, String nick) {

	//	String[] result = new String[2];
		List<String> lstInfor = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("VOVTV");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return lstInfor;
			}

			/*String sqlSelect = "SELECT MSISDN,SexID,Brithday, Address,Favorites  FROM [VOVTV].[dbo].NickName WHERE MSISDN LIKE '"
					+ user_id + "' and NickName LIKE '"+nick+"'";*/
			String sqlSelect = "SELECT MSISDN,SexID,Brithday, Address,Favorites  FROM [dbo].NickName WHERE NickName LIKE '"+nick+"'";

			Util.logger.info("SEARCH INFOR  : " + sqlSelect);
			/*statement = connection.prepareStatement(sqlSelect);
			Util.logger.info("SEARCH THEO BIRTHDAY AND ADDRESS");
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = Integer.valueOf(rs.getString(1));
					//result[1] = rs.getString(2);
				}
			}*/
			
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);

			Util.logger.info("DBUtil.getCode: queryStatement:" + sqlSelect);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				String msisdn = item.elementAt(0).toString();
				String sexid = item.elementAt(1).toString();
				String birthday = item.elementAt(2).toString();
				String address = item.elementAt(3).toString();
				String favorites = item.elementAt(4).toString();
				lstInfor.add(msisdn);
				String gt = "";
				if(sexid.equalsIgnoreCase("1")) {
					gt = "NAM";
				} else {
					gt = "NU";
				}
				lstInfor.add(gt);
				lstInfor.add(birthday);
				lstInfor.add(address);
				lstInfor.add(favorites);
				return lstInfor;
			}
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return lstInfor;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return lstInfor;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return lstInfor;
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
	
	/* ghi lai dsach khach hang */
	public static String FormatNumber(int i) {
		try {
			if (i >= 0 && i < 10)
				return "0" + i;
			else
				return "" + i;
		} catch (Exception e) {
			return "";
		}
	}
}
