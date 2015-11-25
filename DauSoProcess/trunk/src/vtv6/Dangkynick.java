package vtv6;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class Dangkynick extends ContentAbstract {

	/* First String */

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "0";
		String noio = "";
		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String sKeyword = msgObject.getKeyword();
			String inv_telco = "Hien tai dich vu chua ho tro mang cua ban, moi ban quay lai sau.DTHT 1900571566";

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			try {
				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				ismtadd = getString(_option, "ismtadd", ismtadd);

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
			String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";

			if (sTokens.length == 2) {
				msgObject
						.setUsertext("Tin ban gui ko hop le.Soan, NAM/NU nick noio gui 8751. Nick viet lien khong dau, toi da 20 ky tu.DTHT 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				
				msgObject.setUsertext(mt2);
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {

				List<String> nick = null;
				
				if (sTokens[1].length() > 20) {
					msgObject.setUsertext("Nick ban dang ky khong hop le. Vui long chon nick khac hoac goi 1900571566 de duoc huong dan chi tiet.");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
				} else {
					for(int i = 0; i<checkNick().size(); i++){
						if (sTokens[1].contains(checkNick().get(i))) {
							msgObject.setUsertext("Nick ban dang ky khong hop le. Vui long chon nick khac hoac goi 1900571566 de duoc huong dan chi tiet.");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
							return null;
						}
					}
					nick = Getnickbysdt(msgObject.getUserid());
					if (nick.size() > 0) {
						Util.logger.error("status:" + nick.get(2));
						//TH da dang ky roi
						if("1".equals(nick.get(2).trim())) {	
							msgObject
							.setUsertext("SDT nay da dky nick "
									+ nick.get(1)	
									+ " tren HNS. De huy nick, soan: HUYN gui 8151. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						} else {
							String checkNick = "";
							checkNick = Checknick(sTokens[1]);
							if (!checkNick.equalsIgnoreCase("")) {
								msgObject
										.setUsertext("Nick ban chon da co nguoi su dung. Vui long chon nick khac. Chuc ban co nhung giay phut vui ve cung Hop Nhac So tu 13h30-16h30 hang ngay tren VTV6");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(500);
							} else {
								if(sTokens.length > 3) {
									for(int i =2; i< sTokens.length; i++) {
										noio += sTokens[i] + " ";
									}
									noio = noio.substring(0, noio.length()-1);
								} else {
									noio = sTokens[2];
								}
								
								int updateNick = 0;
								updateNick = UpdateNick(msgObject.getUserid(), sTokens[1], noio, sKeyword);
								if(updateNick ==1) {
									msgObject
									.setUsertext("Thong tin cua ban da gui toi Hop Nhac So thanh cong. Buoc 2, soan: IN namsinh sothich gui 8151. VD: IN 1990 doc bao, nghe nhac, du lich. DTHT: 1900571566");
									msgObject.setMsgtype(1);
									msgObject.setContenttype(0);
									DBUtil.sendMT(msgObject);
								}
							}
						}
					} else {
						String checkNick = "";
						checkNick = Checknick(sTokens[1]);
						if (!checkNick.equalsIgnoreCase("")) {
							msgObject
									.setUsertext("Nick ban chon da co nguoi su dung. Vui long chon nick khac. Chuc ban co nhung giay phut vui ve cung Hop Nhac So tu 13h30-16h30 hang ngay tren VTV6");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(500);
						} else {
							if(sTokens.length > 3) {
								for(int i =2; i< sTokens.length; i++) {
									noio += sTokens[i] + " ";
								}
								noio = noio.substring(0, noio.length()-1);
							} else {
								noio = sTokens[2];
							}
							
							msgObject
									.setUsertext("Thong tin cua ban da gui toi Hop Nhac So thanh cong. Buoc 2, soan: IN namsinh sothich gui 8151. VD: IN 1990 doc bao, nghe nhac, du lich. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							
							savenickqueue(msgObject.getUserid(), sTokens[1], noio, sKeyword);
						}
					}
				}
/*
				// mt2
				msgObject.setUsertext("Truy cap:…..de upload avatar cua ban. TK <nick>, mat khau: xxxxxx. DTHT:1900571566");
				msgObject.setMsgtype(0);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);*/
				
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

	private int UpdateNick(String user_id,String nickName, String address, String keyword) throws Exception {
		int result = 0;
		int sex = 0;
		if(keyword.equalsIgnoreCase("NAM")) {
			sex = 1;
		} else {
			sex = 2;
		}
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "UPDATE NickName SET NickName= '" +nickName + "', SexID='"+sex+"',Address='"+address+"', " +
				"Status = 1 , CheckStatus= 4, IsValid = 1, CheckActive=1, CheckNick= 1, CheckFavorites=1, UpdateDate= CURRENT_TIMESTAMP WHERE MSISDN = '"+user_id+"'";
		
		try {
			Util.logger.info(proceExe);
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
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
	
	private static boolean deleteUser(String user) {
		boolean result = true;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "DELETE FROM NickName WHERE MSISDN = '" + user +"'";
		Util.logger.error("[Delete User]:" + proceExe);
		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				return result;
			}
			
			return false;
		} catch (SQLException ex3) {
			Util.logger.error("[Delete User]:" + ex3.toString());
			return false;
		} catch (Exception ex2) {
			Util.logger.error("[Delete User]:" + ex2.toString());
			return false;
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}

	}
	private List<String> checkNick() {
		List<String> lst = new ArrayList<String>();
		lst.add(";");
		lst.add("%");
		lst.add("#");
		lst.add("<");
		lst.add(">");
		lst.add("(");
		lst.add(")");
		lst.add("[");
		lst.add("]");
		lst.add(",");
		lst.add("%");
		lst.add("{");
		lst.add("}");
		lst.add("/");
		lst.add("?");
		lst.add("$");
		lst.add("&");
		lst.add("@");
		lst.add("*");
		lst.add("'");
		lst.add("+");
		return lst;
	}
	private int savenickqueue(String user_id,String nickName, String address, String keyword) throws Exception {

		int result = 1;
		int sex = 0;
		if(keyword.equalsIgnoreCase("NAM")) {
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
				+ nickName
				+ "</NickName>"
				+ "<SexID>"
				+ sex
				+ "</SexID>"
				+ "<Address>"
				+ address
				+ "</Address>"
				/*+ "<Brithday>"
				+ birthday
				+ "</Brithday>"*/
				+ "<Status> 1 </Status>"
				+ "<CheckStatus> 4 </CheckStatus>"
				+ "<IsValid> 1 </IsValid>"
				+ "</Child>"
				+ "</Parent>";
		Util.logger.info("Param:"+param);
		String proceExe = "{ call [dbo].[Sp_Nick_Insert](?,?) }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
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

	private static List<String> Getnickbysdt(String sdt) {
		//String result = "";
		List<String> result = new ArrayList<String>();
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](2,'"+sdt+"') }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(proceExe);

			Util.logger.info("proceExe:"+proceExe);
			Vector result1 = DBUtil.getVectorTable(connection, proceExe);

			Util.logger.info("DBUtil.getCode: queryStatement:" + proceExe);

			if (result1.size() > 0) {
				Vector item = (Vector) result1.elementAt(0);
				String msisdn = item.elementAt(1).toString();
				String nick = item.elementAt(3).toString();
				String status = item.elementAt(14).toString();
				result.add(msisdn);
				result.add(nick);
				result.add(status);
				return result;
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
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT NickName FROM NickName where NickName='" + nick
					+ "'";
			Util.logger.info("SEARCH Nick: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
					return result;
				}
			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
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


	private static String[] getListblacknick() {
		int max = getmaxlist();
		String[] result = new String[max];

		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String lastresult = "";
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "select blacknick from vtv6_blacknick ";

			Util.logger.info("SEARCH List game : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				int i = 0;
				while (rs.next()) {
					result[i] = rs.getString(1);
					i++;
				}
				return result;

			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
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

	private static int getmaxlist() {
		int result = 1;

		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String lastresult = "";
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "select count(*) from vtv6_blacknick ";

			Util.logger.info("SEARCH List game : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
				}
				return result;

			}
			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
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
