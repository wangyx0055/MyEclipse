package vovtv;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import services.textbases.LogValues;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Chatketnoi extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		HashMap _option = new HashMap();
		Collection messages = new ArrayList();
		String dtbase = "gateway";
		String operator = msgObject.getMobileoperator();
		String currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String logDate = currDate.substring(0, currDate.length()-2);

		try {
			int[] result = new int[2];
			String infoid = msgObject.getMobileoperator();
			String userid = msgObject.getUserid();
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
			
			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Cam on ban da su dung dich vu.Yeu cau cua ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571566 de duoc huong dan su dung dich vu.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			} else {
				
				String sttuser = Getnickbysdt(msgObject.getUserid());
				if (sttuser.equalsIgnoreCase("")) {
					msgObject
							.setUsertext("Ban chua dang ky Chat tren VOVTV. Soan, XX namsinh noio gui 8751. VD: XX 1992 HaNoi gui 8751. Neu la nam, thay XX bang XY, soan XY namsinh noio gui 8751. DTHT: 1900571566.");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;

				}
				String chatuser = Checknick(sTokens[1]);
				Util.logger.info("chatuser  : " + chatuser);
				
				String giff_telco = getMobileOperatorNew(chatuser, 2);

				if (chatuser.equalsIgnoreCase("")) {
					msgObject
							.setUsertext("Tin chat cua ban chua duoc gui di do sai ma so nguoi nhan. Vui long kiem tra chinh xac va gui lai. DTHT: 1900571566");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
				}
				String blacklist = Getblacklist(sTokens[1]);
				String[] black = blacklist.split(",");
				for (int i = 0; i < black.length; i++) {
					if (sttuser.equalsIgnoreCase(black[i])
							|| blacklist.equalsIgnoreCase("chanall")) {
						msgObject
								.setUsertext("Tin chat cua ban chua duoc gui di do thanh vien co ma so "
										+ sTokens[1]
										+ " tu choi nhan cac tin chat rieng. DTHT: 1900571566");
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					}
				}
				String subTokens = "";
				for (int k = 2; k < sTokens.length; k++) {
					subTokens = subTokens + sTokens[k] + " ";
				}

				msgObject
						.setUsertext("Tin chat cua ban da duoc gui thanh cong toi thanh vien mang ma so "
								+ sTokens[1]
								+ ". Chuc ban som nhan duoc hoi am.");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(21);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);

				int count_sms = 0;
				count_sms = isCountUserid(logDate, msgObject.getUserid(), msgObject.getKeyword(), sTokens[1], msgObject.getServiceid());
				if(count_sms==0) {
					sendGifMsg(
							msgObject.getServiceid(),
							chatuser,
							giff_telco,
							msgObject.getKeyword(),
							"Ban da nhan duoc tin chat cua CT VOVTV tu thanh vien co ma so "
							+ sttuser + ": " + subTokens
							, msgObject
							.getRequestid(), 0);
					sendGifMsg(msgObject.getServiceid(), chatuser, giff_telco,
							msgObject.getKeyword(), "De tra loi cho nguoi gui hay soan tin CR masonguoinhan Noidung gui 8551. DTHT: 1900571566.",
							msgObject.getRequestid(), 0);
					
					sendGifMsg(msgObject.getServiceid(), chatuser, giff_telco,
							msgObject.getKeyword(), "De tu choi nhan tin chat rieng tu mot thanh vien, soan: K masotuchoi gui 8151. De tu choi nhan tin chat tu tat ca cac thanh vien soan K M gui 8151. DTHT: 1900571566.",
							msgObject.getRequestid(), 0);
				} else {
					sendGifMsg(
							msgObject.getServiceid(),
							chatuser,
							giff_telco,
							msgObject.getKeyword(),
							"Ban da nhan duoc tin chat cua CT VOV tu thanh vien mang ma so "
							+ sttuser + ": " + subTokens
							, msgObject
							.getRequestid(), 0);
				}
				// insert

				saveChat(sttuser, sTokens[1], subTokens);

			}
			return null;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

		}

	}

	private int isCountUserid(String sdate, String userid, String commandcode,
			String info, String serviceid) {
		
		//SELECT COUNT(1) FROM sms_receive_log201204 WHERE USER_ID='84917872843' AND COMMAND_CODE ='gt' AND receive_date = CURDATE()
		String smsLog = "sms_receive_log"+ sdate;
		String info_count = "";
		int count = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String currDate = new SimpleDateFormat("yyyy-MM-dd")
		.format(new Date());
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select INFO from  "+smsLog+" where "
					+ " USER_ID='"
					+ userid
					+ "' and COMMAND_CODE= '"
					+ commandcode
					+ "' and SERVICE_ID='" + serviceid + "'"
					+ " and DATE(receive_date)='"
					+ currDate + "' order by receive_date desc";

			// query1 = "select db_name()";

			// System.out.println(query1);
			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {
				 for(int iIndex = 0;iIndex<result.size();iIndex++)
		          {
					 Vector item = (Vector) result.elementAt(iIndex);
					 info_count = String.valueOf(item.elementAt(0));
					 // System.out.println("AAAAA:" + (String) item.elementAt(0));
					 String maso = "";
					 String[] sTokens = replaceAllWhiteWithOne(info_count).split(" ");
					 maso = sTokens[1];
					 if(info.equalsIgnoreCase(maso)) {
						 count +=1;
						break;
					 }
		          }
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(),
					"count Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return count;
	}
	private static String Getnickbysdt(String sdt) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](2,'"+sdt+"') }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("VOVTV");
			}
/*			callStmt = connection.prepareCall(proceExe);

			Util.logger.info("proceExe:"+proceExe);
			if (callStmt.execute()) {
				Util.logger.info("execute");
				rs = callStmt.getResultSet();
				rs.next();
				result = rs.getString(1);
				return result;
			}*/
			Vector result1 = DBUtil.getVectorTable(connection, proceExe);

			Util.logger.info("DBUtil.getCode: queryStatement:" + proceExe);

			if (result1.size() > 0) {
				Vector item = (Vector) result1.elementAt(0);
				String code = item.elementAt(3).toString();
				return code;
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
			Util.logger.sysLog(2, this.getClass().getName(), "Send gif Failed");
		}
	}

	private static int getPhien() {
		int result = -1;
		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "SELECT phien FROM ketnoiphien  where isprocess=1 ";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
					return result;
				}
			}
			return result;
		} catch (SQLException ex2) {
			Util.logger.error("GetSequence. Ex:" + ex2.toString());
			return result;
		} catch (Exception ex3) {
			Util.logger.error("GetSequece. Ex3:" + ex3.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(cnn);
		}
	}

	private static int findCode() {
		int result = 0;
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT stt FROM ketnoi order by stt desc limit 1";

			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getInt(1);
					Util.logger.info("Code: " + result);
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
				rs.next();
				result = rs.getString(2);
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

	private static String Getblacklist(String nick) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
				if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT blacklist FROM vovtv_nick_chan where nick='"
					+ nick + "'";
			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
					Util.logger.info("Code: " + result);
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

	private static String Getmobile(String stt) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}
			int tam = -1;
			try {
				tam = Integer.parseInt(stt);
			} catch (Exception ex) {

			}
			String sqlSelect = "SELECT userid FROM ketnoi where stt=" + stt;

			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
					Util.logger.info("Code: " + result);
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

	private static String Getstt(String userid) {
		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT stt FROM ketnoi where userid='" + userid
					+ "'";

			Util.logger.info("SEARCH CODE  : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
					Util.logger.info("Code: " + result);
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

	private static boolean saveChat(String fromnick, String tonick,
			String noidung) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO vtv6_chatketnoi (fromnick,tonick,content)"
					+ "VALUES(?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, fromnick);
			statement.setString(2, tonick);
			statement.setString(3, noidung);

			statement.executeUpdate();
			Util.logger.info("sqlInsert  : " + sqlInsert);
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

	private static int getValidPhone(String[] sTokens) {
		int place = 0;
		for (int i = 0; i < sTokens.length; i++) {
			if (!"-".equalsIgnoreCase(ValidISDNNew(sTokens[i]))) {
				place = i;
				return place;
			}
		}
		return place;
	}

	public static String ValidISDNNew(String sISDN) {

		if (sISDN.startsWith("+")) {
			sISDN = sISDN.substring(2);
		}
		String tempisdn = "-";
		try {
			long itemp = Long.parseLong(sISDN);

			tempisdn = "" + itemp;
			if (tempisdn.startsWith("84")) {
				tempisdn = "" + tempisdn;
			} else {
				tempisdn = "84" + tempisdn;
			}

		} catch (Exception e) {
			Util.logger.info("Utils.ValidISDN" + "Exception?*" + e.toString()
					+ "*");
			return "-";
		}
		return tempisdn;
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
