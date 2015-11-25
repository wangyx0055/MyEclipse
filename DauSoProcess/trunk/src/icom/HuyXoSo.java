package icom;

import com.vmg.sms.process.DBPool;

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

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DBUtils;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class HuyXoSo extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();

		HashMap _option = new HashMap();

		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		insertsms_dk_996_cancel(msgObject.getUserid());
		deleteUser(msgObject.getUserid(), "sms_dk_996");
		insertsms_dk8x99_cancel(msgObject.getUserid());
		deleteUser(msgObject.getUserid(), "sms_dk8x99");

		msgObject
				.setUsertext("Ban huy thanh cong dich vu Xo so. Cam on ban da su dung dich vu. DTHT 1900571566");
		msgObject.setContenttype(0);
		msgObject.setMsgtype(0);
		messages.add(new MsgObject(msgObject));
		return messages;

	}

	private int insertsms_dk_996_cancel(String user_id) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Iinsert into sms_dk_996_cancel (service_id, user_id, info, company_id, mobile_operator, command_code, request_id,request_date) SELECT service_id, user_id, info, company_id, mobile_operator, command_code, request_id,CURRENT_date FROM sms_dk_996 where user_id='"
				+ user_id + "' ;commit";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger
						.error(this.getClass().getName()
								+ ""
								+ ": uppdate Statement: Insert  sms_dk_996_cancel Failed:"
								+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert  sms_dk_996_cancel Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private int insertsms_dk8x99_cancel(String user_id) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "insert into sms_dk8x99_cancel (user_id,service_id,msg,times_user,total,status,company_id,request_id,command_code,time_update) select user_id,service_id,msg,times_user,total,status,company_id,request_id,command_code, CURRENT_date from sms_dk8x99 where user_id='"
				+ user_id + "';commit ";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger
						.error(this.getClass().getName()
								+ ""
								+ ": uppdate Statement: Insert sms_dk8x99_cancel Failed:"
								+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName()
					+ ":Insert  sms_dk8x99_cancel Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private static boolean deleteUser(String user, String table) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnection("lotterydelete");
			Util.logger.info(" dbpool: lotterydelete");

			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM " + table + "  WHERE user_id='"
					+ user + "';commit";
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger
						.info("Loi xoa user " + user + "trong bang " + table);
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
				// tempMilisec = (String) item.elementAt(0);
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