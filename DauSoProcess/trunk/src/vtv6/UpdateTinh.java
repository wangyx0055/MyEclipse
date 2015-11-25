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

public class UpdateTinh extends ContentAbstract {

	/* First String */

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		String ismtadd = "0";
		String tinh = "";
		try {
			Collection messages = new ArrayList();

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
			userText = userText.replace("-", "");
			userText = userText.replace("%", "");
			userText = userText.replace("#", "");
			userText = userText.replace("<", "");
			userText = userText.replace(">", "");
			userText = userText.replace("(", "");
			userText = userText.replace(")", "");
			userText = userText.replace("[", "");
			userText = userText.replace("]", "");
			userText = userText.replace("-", "");
			userText = userText.replace("%", "");
			userText = userText.replace("{", "");
			userText = userText.replace("}", "");
			userText = userText.replace("/", "");
			userText = userText.replace("?", "");
			userText = userText.replace("$", "");
			userText = userText.replace("&", "");
			userText = userText.replace("@", "");
			userText = userText.replace("*", "");
			userText = userText.replace("'", "");
			userText = userText.replace("+", "");
			
			String[] sTokens = replaceAllWhiteWithOne(userText).split(" ");
			String mt2 = "Tang ban 3 game online HOT nhat hien nay.DTHT 1900571566:http://s.mobinet.vn/d/list_gf.htm";

			if (sTokens.length == 1) {
				msgObject
						.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
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
				String content = userText.substring(msgObject.getKeyword()
						.length(), userText.length());
				content=content.replaceAll("\n", "");
				
				List<String> infor = Getnickbysdt(msgObject.getUserid());
				
				if (infor.size() == 0) {
					msgObject.setUsertext("Ban chua dang ky thanh vien cua Hop Nhac So VTV6, soan: NAM/NU nick noio gui 8751 de tham gia KBBP. DTHT: 1900571566");
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					DBUtil.sendMT(msgObject);
					Thread.sleep(500);
				} else {
					//
					if(sTokens.length > 2) {
						for(int i =1; i< sTokens.length; i++) {
							tinh += sTokens[i] + " ";
						}
						tinh = tinh.substring(0, tinh.length()-1);
					} else {
						tinh = sTokens[1];
					}
					if(checkUser(userid)) {
						int update = UpdateNick(tinh, userid);
						if(update==1) {
							msgObject
							.setUsertext("Noi o cua "+ infor.get(1)+" da duoc thay doi. De thong tin duoc hien thi tren Hop Nhac So mot lan nua, soan IC gui 8551. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
						}
					}
					
				}
				
			/*// mt2
			msgObject.setUsertext("De chat rieng voi nick khac tren Hop Nhac So, soan: ICC nicknguoinhan noidungchat gui 8351. DTHT: 1900571566");
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

	public static boolean checkUser(String userId){
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		boolean check=false;
		String sqlQuery = "SELECT * From NickName Where MSISDN='"+userId+"'";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(sqlQuery);

			callStmt.execute();
			rs = callStmt.getResultSet();
			while (rs.next()) {
				check=true;  
			}
			
			
		} catch (SQLException ex3) {
			Util.logger.error("get mlist info: SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("get mlist info: SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, callStmt);
			dbpool.cleanup(connection);
		}
		return check;
	}

	private int UpdateNick(String tinh, String sdt) throws Exception {
		int result = 0;
		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String proceExe = "UPDATE NickName SET Address= '" +tinh + "' WHERE MSISDN = '"+sdt+"'";
		
		try {
			Util.logger.info(proceExe);
			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}
			callStmt = connection.prepareCall(proceExe);

			if (callStmt.execute()) {
				Util.logger.info("UPDATE NickName");
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
				String sexid = item.elementAt(6).toString();
				String address = item.elementAt(16).toString();
				result.add(msisdn);
				result.add(nick);
				result.add(sexid);
				result.add(address);
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
