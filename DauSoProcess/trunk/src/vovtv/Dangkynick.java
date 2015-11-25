package vovtv;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

/**
 * Game.<br>
 * 
 * <pre>
 * ich vu cho phep tai ve danh sach ten game, ma so game
 * </pre>
 * 
 * @author Haptt
 * @version 1.0
 */
public class Dangkynick extends ContentAbstract {

	/* First String */

	public String poolName = "VOVTV";
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "0";
		String isforwardMO = "0";
		String namsinh = "";
		String noio = "";
		
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";
			String mt1 = "De tai Game theo ma so, soan: GAME maso gui 8751.Cai dat GPRS 3.0 tu dong ve may de co the luot web va choi game,tai nhac,soan tin GPRS gui 8751.";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				ismtadd = getString(_option, "ismtadd", ismtadd);
				mt1 = getString(_option, "mt1", mt1);
				isforwardMO = getString(_option, "mo", isforwardMO);

			} catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			/* kiem tra thue bao khach hang */
/*
			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {
				msgObject.setUsertext(inv_telco);
				msgObject.setMsgtype(2);
				messages.add(new MsgObject(msgObject));
				return messages;
			}*/

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
		//	String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";
//truong hop sai cu phap
			if (sTokens.length <3) {
				msgObject
						.setUsertext("Tin khong hop le. Neu la nu, soan XX namsinh noio gui 8751. VD: XX 1992 HaNoi gui 8751. Neu la nam, thay XX bang XY, soan XY namsinh noio gui 8751. DTHT: 1900571566.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				//TH: nhan namsinh lien
				if(!(sTokens[0].matches("[A-Za-z]+$")) && sTokens[1].matches("[A-Za-z]+$")) {
					if(sTokens[0].toUpperCase().contains("XY")) {
						namsinh = sTokens[0].substring(3, sTokens[0].length());
					} else if(sTokens[0].toUpperCase().contains("XX")) {
						namsinh = sTokens[0].substring(2, sTokens[0].length());
					}
					if(namsinh.matches("[a-zA-Z]+$")) {
						msgObject
						.setUsertext("Tin khong hop le. Neu la nu, soan XX namsinh noio gui 8751. VD: XX 1992 HaNoi gui 8751. Neu la nam, thay XX bang XY, soan XY namsinh noio gui 8751. DTHT: 1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					} else {
						for(int i =1; i< sTokens.length; i++) {
							noio += sTokens[i] + " ";
						}
						noio = noio.substring(0, noio.length()-1);
					}
				} else {
					if(sTokens[1].matches("[a-zA-Z]+$")) {
						msgObject
						.setUsertext("Tin khong hop le. Neu la nu, soan XX namsinh noio gui 8751. VD: XX 1992 HaNoi gui 8751. Neu la nam, thay XX bang XY, soan XY namsinh noio gui 8751. DTHT: 1900571566.");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					} else {
						namsinh = sTokens[1];
						for(int i =2; i< sTokens.length; i++) {
							noio = sTokens[i] + " ";
						}
						noio = noio.substring(0, noio.length()-1);
					}
					
				}

				String nick = "";
				 {
					nick = Getnickbysdt(msgObject.getUserid());

					if (!nick.equalsIgnoreCase("")) {
						msgObject
								.setUsertext("SDT nay da dky nick tren VOVTV. DTHT: 1900571566");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
					} else {
							msgObject
									.setUsertext("Thong tin cua ban da chuyen sang che do cho duyet. Vui long don xem. Binh chon bai hat soan: VT tenbaihat gui 8751. DTHT: 1900571566.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							// insert vao bang queue
							savenickqueue(msgObject.getUserid(), namsinh,noio, msgObject.getKeyword());
							String nickId = SelectUser(msgObject.getUserid());
							//String nickName = keyword.getKeyword().toUpperCase().concat(nickId);
							UpdateNick(nickId, nickId, msgObject.getUserid());
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

	private int savenickqueue(String user_id, String birthday,String address, String keyword) throws Exception {

		int result = 1;
		int sex = 0;
		if(keyword.equalsIgnoreCase("XY")) {
			sex = 1;
		} else {
			sex = 2;
		}
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String param = "";
		param =	  "<Parent>" 
				+ "<Child>"
				+ "<MSISDN>"
				+ user_id
				+ "</MSISDN>"
				+ "<NickName>"
				+ ""
				+ "</NickName>"
				+ "<SexID>"
				+ sex
				+ "</SexID>"
				+ "<Address>"
				+ address
				+ "</Address>"
				+ "<Brithday>"
				+ birthday
				+ "</Brithday>"
				+ "<Status> 1 </Status>"
				+ "<CheckStatus> 4 </CheckStatus>"
				+ "<IsValid> 1 </IsValid>"
				+ "</Child>"
				+ "</Parent>";
		Util.logger.info("Param:"+param);
		String proceExe = "{ call [dbo].[Sp_Nick_Insert](?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("VOVTV");
			}
			callStmt = connection.prepareCall(proceExe);
			
			callStmt.setInt(1, 1);
			callStmt.setString(2, param);
			Util.logger.info("proceEx:"+proceExe);

			if (callStmt.executeUpdate() != 1) {
				Util.logger.error("Call store insert Failed");
				result = -1;
				return result;
			}
			
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}

	private String SelectUser(String userid) throws Exception {
		String result = "";
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		//String proceExe = "SELECT SCOPE_IDENTITY() AS [SCOPE_IDENTITY]";
		String proceExe = "SELECT NickID FROM NickName WHERE MSISDN = '" + userid +"'";
		try {

			if (connection == null) {
				connection = dbpool.getConnection("VOVTV");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				Util.logger.info("execute");
				rs = callStmt.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
					return result;
				}
			}
			
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	private int UpdateNick(String nickId, String nick, String sdt) throws Exception {
		int result = 0;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "UPDATE NickName SET NickName= '" +nick + "' WHERE NickID = '" + nickId + "' and MSISDN = '"+sdt+"'";
		
		try {
			Util.logger.info(proceExe);
			if (connection == null) {
				connection = dbpool.getConnection("VOVTV");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				Util.logger.error("UPDATE NickName");
				return result;
			}
			result = 1;
			return result;
			
		} catch (SQLException ex3) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("[Sp_CheckUserID_SMS]:" + ex2.toString());
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

		return result;
	}
	
	private static String Getnickbysdt(String sdt) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](2,"+sdt+") }";

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
					result = rs.getString(2);
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

	private static String Checknick(String nick) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](3,'"+nick+"') }";

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
					result = rs.getString(1);
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

	/* Chia thanh 2 MT */
	private static String[] splitString(String splitS) {
		String[] result = new String[2];
		boolean resultBoolean = false;
		int i = 160;
		int j = 0;
		String tempString = splitS;
		if (splitS.length() >= 160) {
			while (!(resultBoolean)) {
				if (splitS.charAt(i) == ';') {
					result[0] = splitS.substring(0, i);
					j = i + 1;
					resultBoolean = true;
				}
				i--;
			}
			resultBoolean = false;
			i = tempString.length() - 1;

			/* tach thanh 2 */
			while (!(resultBoolean)) {
				// neu <160
				if ((tempString.charAt(i) == ';') && ((i - j) <= 160)) {
					result[1] = tempString.substring(j, i);
					resultBoolean = true;
				}
				i--;
			}
		} else {
			result[0] = splitS;
			result[1] = "";
		}

		return result;

	}

	// Tim sdt co trong danh sach da gui tin hay nhan tin hay khong.
	private static int getUserID(String userid, String dtbase, int subcode) {
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT times FROM " + dtbase + " WHERE userid= '"
				+ userid.toUpperCase() + "' AND subcode =" + subcode;

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			int sequence_temp = -1;
			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					sequence_temp = rs.getInt(1);
				}
			}
			return sequence_temp;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
		return -1;
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

}
