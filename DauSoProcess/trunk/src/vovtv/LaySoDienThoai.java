package vovtv;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class LaySoDienThoai extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String ismtadd = "0";
		try {
			Collection messages = new ArrayList();

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			
			// lay so thue bao nguoi gui
			String userid = msgObject.getUserid();
			/* lay noi dung gui */
			String userText = replaceAllWhiteWithOne(msgObject.getUsertext());
			
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			Calendar cal = Calendar.getInstance();
			List<String> lstNickName = new ArrayList<String>();
			String sdt = "";
			String isView = "";
			
			if (sTokens.length == 1) {
				if(sTokens[0].substring(keyword.getKeyword().length(), sTokens[0].length()).matches("[0-9]+$")) {
					lstNickName = Getsdtbynick(sTokens[0].substring(keyword.getKeyword().length(), sTokens[0].length()));
					isView = lstNickName.get(1);
					sdt = lstNickName.get(0);
					if (sdt.equalsIgnoreCase("")) {
						msgObject
								.setUsertext("Khong tim thay ma so thanh vien nay tren VOVTV. DTHT: 1900571566");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					} else {
						if("1".equalsIgnoreCase(isView)) {
							msgObject
							.setUsertext("SDT cua thanh vien "+sTokens[0].substring(keyword.getKeyword().length(), sTokens[0].length())+": " + sdt);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
							return null;
						} else {
							msgObject
							.setUsertext("Thanh vien ma so "+sTokens[0].substring(keyword.getKeyword().length(), sTokens[0].length())+" chua chap nhan cung cap so dien thoai. De chat rieng voi thanh vien nay, soan: CR <masothanhviendo> <noidungchat> gui 8551. DTHT:1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
							return null;
						}
							
					}
				} else {
					msgObject
					.setUsertext("Tin ban gui khong hop le. DTHT 1900571566.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
				}
			} else {
				lstNickName = Getsdtbynick(sTokens[1]);
				isView = lstNickName.get(1);
				sdt = lstNickName.get(0);
				if (sdt.equalsIgnoreCase("")) {
					msgObject
							.setUsertext("Khong tim thay ma so thanh vien nay tren VOVTV. DTHT: 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
				} else {
					if("1".equalsIgnoreCase(isView)) {
						msgObject
						.setUsertext("SDT cua thanh vien "+sTokens[1]+": " + sdt);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					} else {
						msgObject
						.setUsertext("Thanh vien ma so "+sTokens[1]+" chua chap nhan cung cap so dien thoai. De chat rieng voi thanh vien nay, soan: CR <masothanhviendo> <noidunchat> gui 8551. DTHT:1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					}
						
				}
				
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
	
	
	private static List<String> Getsdtbynick(String nick) {
		List<String> result = new ArrayList<String>();
		String sdt = "";
		int IsViewMSISDN = 0;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

	//	String proceExe = "{ call [dbo].[Sp_Nick_Select](3,"+nick+") }";
		String proceExe = "SELECT MSISDN, IsViewMSISDN FROM NickName WHERE NickName = '" + nick +"'";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("VOVTV");
			}
			callStmt = connection.prepareCall(proceExe);

			Util.logger.info("proceExe:"+proceExe);
			if (callStmt.execute()) {
				Util.logger.info("execute");
				rs = callStmt.getResultSet();
				while (rs.next()) {
					sdt = rs.getString(1);
					IsViewMSISDN = rs.getInt(2);
					result.add(sdt);
					result.add(String.valueOf(IsViewMSISDN));
					return result;
				}
			}
			Util.logger.info("excuted");
			return result;
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_Nick_Select]1:" + ex3.getMessage());
			return result;
		} catch (Exception ex2) {
			Util.logger.error("[Sp_Nick_Select]2:" + ex2.getMessage());
			ex2.printStackTrace();
			return result;
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
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
