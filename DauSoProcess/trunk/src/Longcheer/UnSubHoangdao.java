package Longcheer;

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

public class UnSubHoangdao extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();

		HashMap _option = new HashMap();

		String MLIST = "mlist_hoangdao";

		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		// System.out.println("option:" + options);
		// MLIST = ((String) _option.get("mlist"));
		// /mlist=mlist_cauxs&mtfree=7&companyid=81
		MLIST = getStringfromHashMap(_option, "mlist", "mlist_hoangdao");
		Util.logger.info("mlist: " + MLIST);
		String mtfree = getStringfromHashMap(_option, "mtfree", "5");
		int mtfreeInt = 0;
		mtfreeInt = Integer.parseInt(mtfree);
		String duration = getStringfromHashMap(_option, "duration", "5");
		String sservice = getStringfromHashMap(_option, "service", "HOANGDAO");
		String companyid = getStringfromHashMap(_option, "companyid", "1");
		String errMsg = getStringfromHashMap(_option, "error_msg",
				"Tin nhan sai cu phap.");

		String DBCONTENT = "gateway";

		String duplicate = getStringfromHashMap(_option, "duplicate",
				"Ban da dang ky dich vu truoc do");

		String info = msgObject.getUsertext().toUpperCase().trim();
		info = replaceAllWhiteWithOne(info.trim());

		String[] arrInfo = info.split(" ");

		boolean infodetails = false;
		Util.logger.info("info:" + info);

		String bongda = "HOANGDAO";

		// Kiem tra su ton tai cua khach hang
		if (isexist(msgObject.getUserid(), MLIST, sservice, bongda)) {

			insertData2cancel(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getKeyword(), MLIST, msgObject, mtfree + "", 0,
					sservice);
			String strdel = "delete from "+MLIST+" where user_id='"
					+ msgObject.getUserid() + "'";
			Subutil.executeSQL("gateway", strdel);
			Util.logger.info(strdel);

			msgObject
					.setUsertext("Ban huy thanh cong dich vu Hoang dao. Cam on ban da su dung dich vu. DTHT 1900571566");
			msgObject.setContenttype(0);
			msgObject.setMsgtype(0);
			messages.add(new MsgObject(msgObject));
			return messages;

		}
		return null;

	}

	private int insertData2cancel(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "_cancel(user_id, service_id,date, service,request_id, message_type,command_code,mobile_operator, mt_free,mt_count) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "','"
				+ msgObject.getKeyword() + "'," + mtfree + ",0)";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error(this.getClass().getName() + ""
						+ ": uppdate Statement: Insert  " + mlist + " Failed:"
						+ sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error(this.getClass().getName() + ":Insert  " + mlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
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