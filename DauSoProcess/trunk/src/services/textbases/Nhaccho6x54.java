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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

public class Nhaccho6x54 extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try { 
			Collection messages = new ArrayList();
  
			String infoid = "";
			String gameid = "nhaccho";
			String info = replaceAllWhiteWithOne(msgObject.getUsertext())
					.toUpperCase();

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

			String slastid = getBalance(msgObject.getUserid());

			String content = "";

			if (!"".equalsIgnoreCase(slastid)) {
				// tin nhan tiep theo
				String[] stokens = info.split(" ");
				String subcode = "";
				if (stokens.length > 1) {
					subcode = stokens[1];
				}

				content = getAllcontent(gameid, infoid, subcode, "gateway");

				if ("".equalsIgnoreCase(content)) {
					content = "Ban da nhan sai cu phap hoac TheLoaiNhacCho khong dung. Soan "
							+ msgObject.getKeyword()
							+ " gui "
							+ msgObject.getServiceid()
							+ " de nhan them MaSo nhac cho";
				}

			} else {
				String sDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
						.format(new Date());
				String sSQLupdate = "insert into icom_nhaccho(msisdn,lastid,date) values('"
						+ msgObject.getUserid() + "','0','" + sDate + "')";

				executeSQL(sSQLupdate);

				content = getAllcontent1MO(gameid, infoid, "gateway");

			}

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
					// messages.add(new MsgObject(msgObject));
					if (i < mt2send.length - 1) {
						DBUtil.sendMT(msgObject);
						Thread.sleep(4000);
					} else
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

	public static int executeSQL(String sql) {

		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		Connection obj = dbpool.getConnectionGateway();
		try {

			statement = obj.prepareStatement(sql);
			if (statement.executeUpdate() < 0) {
				return -1;
			}
			return 1;
		} catch (SQLException e) {

			return -1;
		} catch (Exception e) {
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(obj);

		}
	}

	private String getBalance(String userid) {
		String sequence = "";

		Connection cnn = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		ResultSet rs = null;
		String query = "select lastid from icom_nhaccho where msisdn='"
				+ userid + "'";

		try {
			cnn = dbpool.getConnectionGateway();
			statement = cnn.prepareStatement(query);

			String sequence_temp = "";
			if (statement.execute()) {
				rs = statement.getResultSet();

				while (rs.next()) {
					sequence_temp = rs.getString(1);
				}
			}

			sequence = sequence_temp;

		} catch (SQLException ex2) {
			Util.logger.error("getGameid. Ex:" + ex2.toString());

		} catch (Exception ex3) {
			Util.logger.error("GetGameid. Ex3:" + ex3.toString());

		} finally {
			dbpool.cleanup(rs, statement);
			dbpool.cleanup(cnn);
		}

		return sequence;
	}

	public String getAllcontent(String gameid, String subcode1,
			String subcode2, String dbcontent) {

		// Lay noi dung tra luon
		String lastid = "";
		String cnttemp = "";
		String sqlSELECT = "SELECT CONTENT,ID FROM icom_textbase_data WHERE dayid>0 and upper(gameid) = '"
				+ gameid + "'";

		if (!"".equalsIgnoreCase(subcode1)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode1)='"
					+ subcode1.toUpperCase() + "'";
		}

		if (!"".equalsIgnoreCase(subcode2)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode2)='"
					+ subcode2.toUpperCase() + "'";
		}

		String sqlORDER = "  order by rand() limit 1";

		String sqltemp = sqlSELECT;
		sqltemp = sqltemp + sqlORDER;
		String[] temp = getContent(sqltemp, dbcontent);
		cnttemp = temp[0];

		return cnttemp;
	}

	public String getAllcontent1MO(String gameid, String subcode1,
			String dbcontent) {

		// Lay noi dung tra luon
		String lastid = "";
		String cnttemp = "";
		String sqlSELECT = "SELECT CONTENT,ID FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid + "'";

		if (!"".equalsIgnoreCase(subcode1)) {
			sqlSELECT = sqlSELECT + " AND upper(subcode1)='"
					+ subcode1.toUpperCase() + "' and dayid=0";
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
			// TODO: handle exception
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
