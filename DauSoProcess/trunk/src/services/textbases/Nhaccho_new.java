package services.textbases;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import common.DBUtils;
import cs.ExecuteADVCR;

public class Nhaccho_new extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {
			Collection messages = new ArrayList();

			String infoid = "";
			String gameid = "nhaccho";
			String info = msgObject.getUsertext();
			String[] sTokens = replaceAllWhiteWithOne(info).split(" ");
		    if (sTokens.length!=2 )
		    {
		    	msgObject
				.setUsertext("Ban da nt sai cu phap.DTHT 1900571566");
		msgObject.setMsgtype(1);
		msgObject.setContenttype(0);
		messages.add(new MsgObject(msgObject));
		return messages;
		    }
			if ("VIETTEL".equalsIgnoreCase(msgObject.getMobileoperator())
					|| "VIETEL".equalsIgnoreCase(msgObject.getMobileoperator())) {

				infoid = "viettel";
			} else if ("VMS".equalsIgnoreCase(msgObject.getMobileoperator())) {

				infoid = "mobifone";

			} else if ("GPC".equalsIgnoreCase(msgObject.getMobileoperator())) {

				infoid = "vinaphone";

			} else {

				infoid = "other";
			}

			String content = getAllcontent(gameid, infoid, "gateway");

			String[] mt2send = content.split("###");

			for (int i = 0; i < mt2send.length; i++) {
				if (!"".equalsIgnoreCase(mt2send[i])) {
					msgObject.setUsertext(mt2send[i]);
					if (i == 0) {
						msgObject.setMsgtype(1);

					} else {
						msgObject.setMsgtype(0);
					}
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
				}
			}

			return messages;
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());

			return null;
 
		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
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
	public String getAllcontent(String gameid, String subcode1, String dbcontent) {

		// Lay noi dung tra luon
		String lastid = "";
		String cnttemp = "";
		String sqlSELECT = "SELECT CONTENT,ID FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid + "'";

		if (!"".equalsIgnoreCase(subcode1)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode1)='"
					+ subcode1.toUpperCase() + "'";
		}

		String sqlORDER = "  order by rand() limit 1";

		String sqltemp = sqlSELECT;
		sqltemp = sqltemp + sqlORDER;
		String[] temp = getContent(sqltemp, dbcontent);
		cnttemp = temp[0];

		return cnttemp;
	}

	String[] getContent(String query, String dbcontent) {
		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnection(dbcontent);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.sysLog(1, this.getClass().getName(),
					"getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.sysLog(2, this.getClass().getName(),
					"getContent: Failed" + ex.getMessage());
			ex.printStackTrace();
			return record;
		} finally {
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

	private static boolean checkblacklist(String sname) {
		Connection connection;
		PreparedStatement statement;
		DBPool dbpool;
		connection = null;
		statement = null;
		dbpool = new DBPool();
		try {

			connection = dbpool.getConnection("content");

			String query3 = "select * from [Spammer].[dbo].[BlackList] where Name='"
					+ sname + "'";

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
}
