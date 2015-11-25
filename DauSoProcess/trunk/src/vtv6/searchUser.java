package vtv6;

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

public class searchUser extends ContentAbstract{

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		String ismtadd = "0";
		String isforwardMO = "0";
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
			userText = userText.replace("-", "");
			userText = userText.replace(";", "");
			userText = userText.replace("%", "");
			userText = userText.replace("#", "");
			userText = userText.replace("<", "");
			userText = userText.replace(">", "");
			userText = userText.replace("(", "");
			userText = userText.replace(")", "");
			userText = userText.replace("[", "");
			userText = userText.replace("]", "");
			userText = userText.replace("-", "");
			userText = userText.replace(",", "");
			userText = userText.replace(".", "");
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

			if (sTokens.length == 1) {
				msgObject
						.setUsertext("Tin ban gui khong hop le.De tim ban theo nam sinh, soan: VE namsinh gui 8551.De tim ban theo noi o, soan: VE tinh/thanh gui 8551. DTHT: 1900571566");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				List<String> nick = new ArrayList<String>();
					if(sTokens[1].matches("[0-9]+$")) {
						if(sTokens.length==2) {
							//tim ban theo nam sinh
							nick = findBirth(sTokens[1]);
							if(nick.size() >0) {
								String allNick = "";
								int size = 0;
								for(int i =0; i<nick.size(); i++) {
									allNick +=nick.get(i)+",";
									size +=1;
									if(size ==5) {
										allNick = allNick.substring(0, allNick.length()-1);
										break;
									}
								}
								msgObject
								.setUsertext("Thanh vien sinh nam "+sTokens[1]+": " + allNick);
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							} else {
								msgObject
								.setUsertext("Khong tim thay nick nao theo yeu cau cua ban. Ban co the tim theo ngay sinh khac. DTHT: 1900571566");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							}
						} else {
							if(sTokens.length > 3) {
								for(int i =2; i< sTokens.length; i++) {
									noio += sTokens[i] + " ";
								}
								noio = noio.substring(0, noio.length()-1);
							} else {
								noio = sTokens[2];
							}
							
							nick = findBirth(sTokens[1], noio);
							if(nick.size() >0) {
								String allNick = "";
								int size = 0;
								for(int i =0; i<nick.size(); i++) {
									allNick +=nick.get(i)+",";
									size +=1;
									if(size ==2) {
										allNick = allNick.substring(0, allNick.length()-1);
										break;
									}
								}
								msgObject
								.setUsertext("Thanh vien sinh nam "+sTokens[1]+", o tinh "+noio+": "+ allNick);
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
								
							} else {
								msgObject
								.setUsertext("Khong tim thay nick nao theo yeu cau cua ban. Ban co the tim theo noi o va ngay sinh khac. DTHT: 1900571566");
								msgObject.setMsgtype(1);
								msgObject.setContenttype(0);
								DBUtil.sendMT(msgObject);
								Thread.sleep(1000);
							}
						}
					} else {
						//tim ban theo noi o
						String address="";
						if(sTokens.length > 2) {
							for(int i =1; i< sTokens.length; i++) {
								address += sTokens[i] + " ";
							}
							address = address.substring(0, address.length()-1);
						} else {
							address = sTokens[1];
						}
						nick = findAddress(address);
						if(nick.size() >0) {
							String allNick = "";
							int size = 0;
							for(int i =0; i<nick.size(); i++) {
								allNick +=nick.get(i)+",";
								size +=1;
								if(size ==5) {
									allNick = allNick.substring(0, allNick.length()-1);
									break;
								}
							}
							msgObject
							.setUsertext("Thanh vien o tinh "+address+": "+allNick);
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
						} else {
							msgObject
							.setUsertext("Khong tim thay nick nao theo yeu cau cua ban. Ban co the tim theo tinh khac. DTHT: 1900571566");
							msgObject.setMsgtype(1);
							msgObject.setContenttype(0);
							DBUtil.sendMT(msgObject);
							Thread.sleep(1000);
						}
					}
				
				msgObject
				.setUsertext("De noi chuyen voi thanh vien khac tren Hop Nhac So, soan: ICC nicknguoinhan noidung gui 8351. DTHT: 1900571566");
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
	
	private static List<String> findBirth(String birthday, String address) {
		List<String> lstresult = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return lstresult;
			}

			String sqlSelect = "SELECT NickName FROM [dbo].NickName WHERE Brithday LIKE '"
					+ birthday + "' and Address LIKE '"+address+"'";

			Util.logger.info("SEARCH THEO BIRTHDAY AND ADDRESS  : " + sqlSelect);
			
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);

			Util.logger.info("DBUtil.getCode: queryStatement:" + sqlSelect);

			for(int iIndex = 0;iIndex<result.size();iIndex++)
	          {
			  	Vector vtRow = (Vector) result.elementAt(iIndex);
				String nick = (String) vtRow.elementAt(0); 
				lstresult.add(nick);
	          }
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return lstresult;
	}
	
	private static List<String> findBirth(String birthday) {
		List<String> lstresult = new ArrayList<String>();
		int code = 0;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return lstresult;
			}

			String sqlSelect = "SELECT NickName FROM [dbo].NickName WHERE Brithday LIKE '"
					+ birthday + "'";

			Util.logger.info("SEARCH THEO BIRTHDAY: " + sqlSelect);
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);

			Util.logger.info("DBUtil.getCode: queryStatement:" + sqlSelect);

			for(int iIndex = 0;iIndex<result.size();iIndex++)
	          {
			  	Vector vtRow = (Vector) result.elementAt(iIndex);
				String nick = (String) vtRow.elementAt(0); 
				lstresult.add(nick);
	          }
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return lstresult;
	}

	/* tim ma so cac bai hat */
	private static List<String> findAddress(String address) {
		List<String> lstresult = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection("DAIV6");
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return lstresult;
			}

			String sqlSelect = "SELECT NickName FROM [dbo].NickName WHERE Address LIKE '"+address+"'";

			Util.logger.info("SEARCH THEO ADDRESS: " + sqlSelect);
			
			Vector result = DBUtil.getVectorTable(connection, sqlSelect);

			Util.logger.info("DBUtil.getCode: queryStatement:" + sqlSelect);

			for(int iIndex = 0;iIndex<result.size();iIndex++)
	          {
			  	Vector vtRow = (Vector) result.elementAt(iIndex);
				String nick = (String) vtRow.elementAt(0); 
				lstresult.add(nick);
	          }
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return lstresult;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return lstresult;
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