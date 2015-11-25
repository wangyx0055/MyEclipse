package vtv6;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;


import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Chan1nick extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");

			if ("SFONE".equalsIgnoreCase(msgObject.getMobileoperator())) {
				msgObject
						.setUsertext("Dich vu chua ho tro mang cua ban. DTHT 1900571566");
				msgObject.setMsgtype(2);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
				return null;
			}

			String nickId = "";
			if (sTokens.length < 2) {
				String mo = "";
				mo = sTokens[0].toUpperCase();
				if(mo.startsWith("ICT")) {
					nickId = mo.substring(0, 3);
				}
					String sttuser = Getnickbysdt(msgObject.getUserid());
					if (sttuser.equalsIgnoreCase("")) {
						msgObject
								.setUsertext("Ban chua dang ky Chat tren Hop Nhac So VTV6. Soan: NAM/NU nick noio gui 8751. DTHT 1900571566.");
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;

					}
					String chatuser = Checknick(sTokens[1]);
					Util.logger.info("chatuser  : " + chatuser);

					if (chatuser.equalsIgnoreCase("")) {
						msgObject
								.setUsertext("Ten nick ban vua chon khong ton tai. Vui long kiem tra lai. DTHT: 1900571566");
						msgObject.setMsgtype(1);
						DBUtil.sendMT(msgObject);
						Thread.sleep(1000);
						return null;
					}
					String blacklist = Getblacklist(msgObject.getUserid());
					if("".equalsIgnoreCase(blacklist)) {
						blacklist = nickId;
					} else {
						blacklist = blacklist + "," + nickId;
					}
					if(isExistPhoneNumber(msgObject.getUserid())) {
						updateBlacklist(msgObject.getUserid(), blacklist);
					} else {
						insertBlacklist(msgObject.getUserid(), sttuser, blacklist);
					}
					msgObject
							.setUsertext("Ban da khoa tin chat rieng tu nick "+nickId+" thanh cong. De mo khoa, soan: ICM <tennicktuchoi> gui 8151");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
			} else {
				String sttuser = Getnickbysdt(msgObject.getUserid());
				if (sttuser.equalsIgnoreCase("")) {
					msgObject
							.setUsertext("Ban chua dang ky Chat tren Hop Nhac So VTV6. Soan: NAM/NU nick noio gui 8751. DTHT 1900571566.");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;

				}
				String chatuser = Checknick(sTokens[1]);
				Util.logger.info("chatuser  : " + chatuser);

				if (chatuser.equalsIgnoreCase("")) {
					msgObject
							.setUsertext("Thanh vien co ma so ban vua chon khong ton tai. Vui long kiem tra lai. DTHT: 1900571566.");
					msgObject.setMsgtype(1);
					DBUtil.sendMT(msgObject);
					Thread.sleep(1000);
					return null;
				}
				String blacklist = Getblacklist(msgObject.getUserid());
				
				if("".equalsIgnoreCase(blacklist)) {
					blacklist = sTokens[1];
				} else {
					blacklist = blacklist + "," + sTokens[1];
				}
				
				if(isExistPhoneNumber(msgObject.getUserid())) {
					updateBlacklist(msgObject.getUserid(), blacklist);
				} else {
					insertBlacklist(msgObject.getUserid(), sttuser, blacklist);
				}
				
				msgObject
						.setUsertext("Ban da khoa tin chat rieng tu nick "+sTokens[1]+" thanh cong. De mo khoa, soan: ICM <tennicktuchoi> gui 8151");
				msgObject.setMsgtype(1);
				DBUtil.sendMT(msgObject);
				Thread.sleep(1000);
			}
			return null;
		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

		}

	}
	
	private static boolean insertBlacklist(String userid, String nick, String blacklist) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			String sqlInsert = "INSERT INTO vtv6_nick(user_id,nick,blacklist,status)"
					 + " VALUES(?,?,?,?)";
			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, nick);
			statement.setString(3, blacklist);
			statement.setString(4, "1");
			statement.executeUpdate();
			Util.logger.info(" INSERT INTO vtv6_nick: " + sqlInsert);
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
	private boolean isExistPhoneNumber(String phoneNumber) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			String query1 = "select * from  vtv6_nick where user_id = '"+ phoneNumber+ "'";

			Vector result = DBUtil.getVectorTable(connection, query1);
			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.info("isExistPhoneNumber Failed" + e.getMessage());
		} finally {
			dbpool.cleanup(connection);

		}
		return false;
	}

	private static boolean updateBlacklist(String userid, String blacklist) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}

			// Update cac danh sach bai hat da gui cho khach hang
			String sqlUpdate = "UPDATE vtv6_nick SET blacklist = '" + blacklist
					+ "' WHERE upper(user_id)='" + userid.toUpperCase()
					+ "' and status=1";
			Util.logger.info(" UPDATE: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger
						.info("Update vtv6_nick " + userid + " to dbcontent");
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

	private static String Getnickbysdt(String sdt) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](2,'"+sdt+"') }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
			}

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


	private static String Checknick(String nick) {
		String result = "";

		Connection connection = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String proceExe = "{ call [dbo].[Sp_Nick_Select](3,'"+nick+"') }";

		try {

			if (connection == null) {
				connection = dbpool.getConnection("DAIV6");
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

	private static String Getblacklist(String userid) {
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

			String sqlSelect = "SELECT blacklist FROM vtv6_nick where nick='"
					+ userid + "'";
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
