package subscription;

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

public class SubHoangDaoZF extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();

		HashMap _option = new HashMap();

		String MLIST = "mlist";

		String options = keyword.getOptions();
		_option = getParametersAsString(options);
		// System.out.println("option:" + options);
		// MLIST = ((String) _option.get("mlist"));
		// /mlist=mlist_cauxs&mtfree=7&companyid=81
		MLIST = getStringfromHashMap(_option, "mlist", "mlist_hoangdao");
		Util.logger.info("mlist: " + MLIST);
		String mtfree = getStringfromHashMap(_option, "mtfree", "2");

		int mtfreeInt = 0;
		mtfreeInt = Integer.parseInt(mtfree);
		String duration = getStringfromHashMap(_option, "duration", "7");
		String sservice = getStringfromHashMap(_option, "service", "HOANGDAO");
		String companyid = getStringfromHashMap(_option, "companyid", "1");
		String errMsg = getStringfromHashMap(_option, "error_msg",
				"Tin nhan sai cu phap.");

		String DBCONTENT = getStringfromHashMap(_option, "dbcontent",
				"textbase");

		String duplicate = getStringfromHashMap(_option, "duplicate",
				"Ban da dang ky dich vu truoc do");
		String success = getStringfromHashMap(
				_option,
				"success",
				"Ban da dang ky thanh cong dich vu HOANG DAO. Noi dung se dc gui cho ban vao luc 8h sang hang ngay.DTHT 19001745");

		String info = msgObject.getUsertext().toUpperCase().trim();

		info = info.replace('-', ' ');
		info = info.replace(';', ' ');
		info = info.replace('+', ' ');
		info = info.replace('.', ' ');
		info = info.replace(',', ' ');
		info = info.replace('_', ' ');

		info = replaceAllWhiteWithOne(info.trim());

		String[] arrInfo = info.split(" ");

		boolean infodetails = false;
		Util.logger.info("info:" + info);

		String HOROSCOPE = getStringfromHashMap(_option, "cung","SONGNGU");

	
		
		// Kiem tra su ton tai cua khach hang
		if (isexist(msgObject.getUserid(), MLIST, sservice, HOROSCOPE)) {

			// Cong them tai khoan cho nguoi dung
			updateData(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfreeInt, 0,
					msgObject.getKeyword(), companyid, sservice, HOROSCOPE);

			msgObject.setUsertext(duplicate);
			msgObject.setContenttype(0);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));
			return messages;

		} else {

			// Thong bao dang ky thanh cong insert vao csdl
			insertData(msgObject.getUserid(), msgObject.getServiceid(),
					msgObject.getServiceid(), MLIST, msgObject, mtfree, 0,
					msgObject.getKeyword(), companyid, sservice, HOROSCOPE);

			msgObject.setUsertext(success);
			msgObject.setContenttype(0);
			msgObject.setMsgtype(1);
			messages.add(new MsgObject(msgObject));

			// Kiem tra noi dung va thong bao cho khach hang
			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());

			String content = getInfo(DBCONTENT, currDate, HOROSCOPE);

			if (!"".equalsIgnoreCase(content)) {
				String[] mess = content.split("###");
				for (int i = 0; i < mess.length; i++) {
					if (!"".equalsIgnoreCase(mess[i])) {

						// Gui noi dung cho khach hang
						msgObject.setUsertext(mess[i]);
						msgObject.setContenttype(0);
						msgObject.setMsgtype(0);
						DBUtil.sendMT(msgObject);
						Thread.sleep(500);
					}
				}
			}
			return messages;
		}
	}

	// Lay noi dung ben EVN - Happy Info
	@SuppressWarnings("unchecked")
	private String getInfo(String dbcontent, String sDate, String infoid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String result1 = "";

		try {

			connection = dbpool.getConnection(dbcontent);

			String query = "SELECT content from icom_horoscope WHERE info_type ='"
					+ infoid
					+ "' AND DATE_FORMAT(info_date,'%d/%m/%Y')='"
					+ sDate + "'";

			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				// return null;
				return result1;
			} else {

				for (int i = 0; i < result.size(); i++) {

					Vector item = (Vector) result.elementAt(i);
					return (String) item.elementAt(0);

				}

			}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getInfo: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(connection);
		}
		return null;

	}

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String companyid,
			String service, String horoscope) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,company_id,options) values ('"
				+ service.toUpperCase()
				+ "','"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ companyid + ",'" + horoscope + "')";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
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

	private int updateData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			int mtfreeint, int msgtype, String Service_ss_id, String companyid,
			String service, String horoscope) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Update " + mlist + " set mt_free=mt_free + "
				+ mtfreeint + " WHERE upper(user_id)='" + user_id.toUpperCase()
				+ "' AND options='" + horoscope.toUpperCase() + "'";

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtils.executeSQL(connection, sqlInsert) < 0) {
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

	// Lay cung hoang dao
	String getHoroscope(long requesttime) {

		try {

			if (requesttime < 1)
				return "x";
			long bachduong1 = validdate("21", "3");
			long bachduong2 = validdate("19", "4");

			if (requesttime >= bachduong1 && requesttime <= bachduong2) {
				return "BACHDUONG";
			}

			long kimnguu1 = validdate("20", "4");
			long kimnguu2 = validdate("20", "5");

			if (requesttime >= kimnguu1 && requesttime <= kimnguu2) {
				return "KIMNGUU";
			}

			long songtu1 = validdate("21", "5");
			long songtu2 = validdate("21", "6");

			if (requesttime >= songtu1 && requesttime <= songtu2) {
				return "SONGTU";
			}

			long cugiai1 = validdate("22", "6");
			long cugiai2 = validdate("22", "7");

			if (requesttime >= cugiai1 && requesttime <= cugiai2) {
				return "CUGIAI";
			}

			long sutu1 = validdate("23", "7");
			long sutu2 = validdate("22", "8");

			if (requesttime >= sutu1 && requesttime <= sutu2) {
				return "SUTU";
			}

			long xunu1 = validdate("23", "8");
			long xunu2 = validdate("22", "9");

			if (requesttime >= xunu1 && requesttime <= xunu2) {
				return "XUNU";
			}

			long thienbinh1 = validdate("23", "9");
			long thienbinh2 = validdate("22", "10");

			if (requesttime >= thienbinh1 && requesttime <= thienbinh2) {
				return "THIENBINH";
			}

			long hocap1 = validdate("23", "10");
			long hocap2 = validdate("21", "11");

			if (requesttime >= hocap1 && requesttime <= hocap2) {
				return "HOCAP";
			}

			long nhanma1 = validdate("22", "11");
			long nhanma2 = validdate("21", "12");
			if (requesttime >= nhanma1 && requesttime <= nhanma2) {
				return "HOCAP";
			}

			long maket1 = validdate("22", "12");
			long maket2 = validdate("19", "1");
			if (requesttime >= maket1 && requesttime <= maket2) {
				return "MAKET";
			}

			long baobinh1 = validdate("20", "1");
			long baobinh2 = validdate("18", "2");

			if (requesttime >= baobinh1 && requesttime <= baobinh2) {
				return "BAOBINH";
			}

			long songngu1 = validdate("19", "2");
			long songngu2 = validdate("20", "3");

			if (requesttime >= songngu1 && requesttime <= songngu2) {
				return "SONGNGU";
			}
		} catch (Exception e) {
		}
		return "x";

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