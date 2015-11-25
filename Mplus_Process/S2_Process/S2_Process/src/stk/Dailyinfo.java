package stk;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class Dailyinfo extends QuestionManager {

	String INFO_ID = "BAO";
	// String MLIST = "mlist_bao";
	String CLASSNAME = "Textbases";

	// textbasetype: kieu dich vu textbase
	// 0: textbase tra ngau nhien hang ngay
	// 1: textbase tra theo thu tu hang ngay
	// 2: text base tra theo ngay
	// 3: xo so
	// 4: soi cau
	// 5: chung khoan
	String TB_RANDOM = "0";
	String TB_ORDER = "1";
	String TB_DAILY = "2";
	String TB_XOSO = "3";
	String TB_CAU = "4";
	String TB_CK = "5";
	String TB_SUBCODE1 = "6";
	String TB_SUBCODE2 = "7";

	// subcode1 theo chi so
	String TB_SUBCODE1_IDX = "8";
	// subcode2 theo chi so
	String TB_SUBCODE2_IDX = "9";

	public static String Timestamp2DDMMYYYY(java.sql.Timestamp ts) {
		if (ts == null) {
			return "";
		} else {
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.setTime(new java.util.Date(ts.getTime()));

			String strTemp = Integer.toString(calendar
					.get(calendar.DAY_OF_MONTH));
			if (calendar.get(calendar.DAY_OF_MONTH) < 10) {
				strTemp = "0" + strTemp;
			}
			if (calendar.get(calendar.MONTH) + 1 < 10) {
				return strTemp + "/0" + (calendar.get(calendar.MONTH) + 1)
						+ "/" + calendar.get(calendar.YEAR);
			} else {
				return strTemp + "/" + (calendar.get(calendar.MONTH) + 1) + "/"
						+ calendar.get(calendar.YEAR);
			}
		}
	}

	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		try {

			int msg1mt = Integer.parseInt(Constants.MT_PUSH);
			int msg2mt = Integer.parseInt(Constants.MT_PUSH);

			if (keyword.getService_type() == Constants.PACKAGE_SERVICE) {
				msg1mt = Integer.parseInt(Constants.MT_CHARGING);
				msg2mt = Integer.parseInt(Constants.MT_PUSH);

			} else if (keyword.getService_type() == Constants.DAILY_SERVICE) {
				msg2mt = Integer.parseInt(Constants.MT_CHARGING);
				msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
			}
			HashMap _option = new HashMap();

			String MLIST = "mlist";

			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			// System.out.println("option:" + options);
			// MLIST = ((String) _option.get("mlist"));
			MLIST = getStringfromHashMap(_option, "mlist", "mlist_stk");
			//

			// infoid=GV&type=0&pool=content&index=1&typedb=2
			String infoid = getStringfromHashMap(_option, "infoid", "0");
			String stype = getStringfromHashMap(_option, "type", "0");
			String poolname = getStringfromHashMap(_option, "pool", "content");
			String indexsubcode = getStringfromHashMap(_option, "index", "1");

			// Loai du lieu can lay
			// 1: ngan can split
			// 2: dai
			// 3: tieng viet
			String typedb = getStringfromHashMap(_option, "typedb", "1");
			// textbasetype: kieu dich vu textbase
			// 0: textbase tra ngau nhien hang ngay
			// 1: textbase tra theo thu tu hang ngay
			// 2: text base tra theo ngay
			// 3: xo so
			// 4: soi cau
			// 5: chung khoan

			// subcodetype
			// 0: binh thuong
			// 1: date
			boolean firstmo = false;
			String lastcode = "0";
			int mtcount = 0;
			String id = "0";

			String strResult = null;

			// textbasetype: kieu dich vu textbase
			// 0: textbase tra ngau nhien hang ngay
			// 1: textbase tra theo thu tu hang ngay
			// 2: text base tra theo ngay
			// 3: xo so

			// strResult = getContent2(infoid);
			// 01/04/2009
			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());

			String usertext = msgObject.getUsertext();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");
			int isub1 = Integer.parseInt(indexsubcode);
			// String ssub1 = "";
			try {
				currDate = sTokens[isub1];
			} catch (Exception e) {
				// ssub1 = "";
				long milliSecond = System.currentTimeMillis();
				java.util.Calendar calendar = java.util.Calendar.getInstance();
				if (calendar.get(calendar.HOUR_OF_DAY) < 7) {
					currDate = Timestamp2DDMMYYYY(new Timestamp(milliSecond
							- 24 * 60 * 60 * 1000));
				}
			} finally {

			}

			Util.logger.info("Ngay gio: " + currDate);

			int msgtype = 1;
			if (currDate.length() != 10) {
				strResult = "Sai dinh dang ngay thang. Nhap day du thong tin theo dang DD/MM/YYYY. Ho tro 199";
				msgtype = 0;
			}

			strResult = getContent2(poolname, typedb, infoid, currDate);

			if ((strResult == null) || "".equalsIgnoreCase(strResult)) {

				strResult = "Thong tin ngay "
						+ currDate
						+ " chua duoc cap nhat, ban hay tra cuu voi ngay khac. DTHT: 199";
				msgtype = 0;
			}

			MsgObject msgObj = new MsgObject(1, msgObject.getServiceid(),
					msgObject.getUserid(), msgObject.getKeyword(), strResult,
					msgObject.getRequestid(), DateProc.createTimestamp(),
					msgObject.getMobileoperator(), msgtype, 0);

			sendMT(typedb, msgObj, CLASSNAME);

		}

		catch (Exception e) {
			Util.logger.printStackTrace(e);

		}

		return null;
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

	@SuppressWarnings("unchecked")
	private String getContent2(String dbcontent, String type, String infoid,
			String sDate) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection(dbcontent);
			String content = "content_short";
			// Xac dinh bang nao can lay du lieu
			if ("3".equalsIgnoreCase(type)) {
				content = "content_vi";
			} else if ("2".equalsIgnoreCase(type)) {
				content = "content";
			}

			String query = "SELECT "
					+ content
					+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=1 AND upper(newstypecode) = '"
					+ infoid.toUpperCase()
					+ "' AND CONVERT(varchar(25), [isdate], 103) <='" + sDate
					+ "' order by [isdate] desc )x";

			Util.logger.info("Query:" + query);
			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				return null;
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

	// Vi du truyen cuoi
	public String[] getContent0(String database, String type, String lastcode,
			String newstypecode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		Util.logger.info("content = " + content);

		String query = "SELECT ID,"
				+ content
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE upper(newstypecode) = '"
				+ newstypecode.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode + ") )x ";

		try {
			connection = dbpool.getConnection(database);

			Util.logger.info("SQL Query get content: " + query);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + result.size() + "@"
					+ query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);

				Util.logger.info(this.getClass().getName()
						+ "getContent: record[0]:" + record[0]);
				Util.logger.info(this.getClass().getName()
						+ "getContent: record[1]:" + record[1]);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public String getContent6(String database, String type, String subcode1,
			String subcode2, String newstypecode) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";
		String strResult = "";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		// String query =
		// "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
		// + textbaseid.toUpperCase() + "'";
		String query = "SELECT "
				+ content
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND  upper(newstypecode) = '"
				+ newstypecode.toUpperCase() + "'";

		// query = query + " and order =" + lastcode + " ";
		if (!"".equalsIgnoreCase(subcode1)) {
			query += " AND upper(subcode1)='" + subcode1.toUpperCase() + "'";
		}
		if (!"".equalsIgnoreCase(subcode2)) {
			query += " AND upper(subcode2)='" + subcode2.toUpperCase() + "'";
		}
		query += ")x";

		Util.logger.info("Query Content 6: " + query);

		try {
			connection = dbpool.getConnection(database);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				strResult = (String) item.elementAt(0);
				return strResult;
			}

			return strResult;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return strResult;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public String convertsubcode(String subcode, String typesubcode, int numcnt) {
		if ("1".equalsIgnoreCase(typesubcode)) {
			return subcode.toUpperCase();
		} else if ("2".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				sum += getNumber(subcode, i);
			}
			if (numcnt == 0) {
				return sum + "";
			} else {

				while (sum > numcnt) {
					String ssum = sum + "";
					sum = 0;
					for (int i = 0; i < ssum.length(); i++) {
						sum += getNumber(ssum, i);
					}
				}
				return sum + "";
			}
		} else if ("3".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				if ((subcode.substring(i, i + 1).equalsIgnoreCase("A"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("J"))) {
					sum += 1;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("B"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("K"))) {
					sum += 2;
				}

				else if ((subcode.substring(i, i + 1).equalsIgnoreCase("C"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("L"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("U"))) {
					sum += 3;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("D"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("M"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("V"))) {
					sum += 4;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("E"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("N"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("W"))) {
					sum += 5;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("F"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("O"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("X"))) {
					sum += 6;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("G"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("P"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Y"))) {
					sum += 7;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("H"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Q"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Z"))) {
					sum += 8;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("I"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("R"))) {
					sum += 9;
				} else {
					sum += 0;
				}

			}
			if (numcnt == 0) {
				return sum + "";
			}

			while (sum > numcnt) {
				String ssum = sum + "";
				sum = 0;
				for (int i = 0; i < ssum.length(); i++) {
					sum += getNumber(ssum, i);
				}
			}
			return sum + "";
		}
		return subcode.toUpperCase();
	}

	public int getNumber(String subcode, int index) {
		try {
			return Integer.parseInt(subcode.substring(index, index + 1));
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}

	}

	public String getsubode(int idsubcode, String subcode1, String subcode2,
			String subcodetype, int numcnt) {
		if (idsubcode == 1) {
			if (!"".equalsIgnoreCase(subcode2)) {
				return subcode1.toUpperCase();
			} else {
				return convertsubcode(subcode1, subcodetype, numcnt);
			}

		} else {
			return convertsubcode(subcode2, subcodetype, numcnt);
		}
	}

	// getContent6(poolname, typedb, ssub1, "",
	// infoid);
	public String getContent7(String database, String type, String subcode1,
			String subcode2, String newstypecode, String subcodetype) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";
		String strResult = "";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		// String query =
		// "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
		// + textbaseid.toUpperCase() + "'";
		String query = "SELECT "
				+ content
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND  upper(newstypecode) = '"
				+ newstypecode.toUpperCase() + "'";

		// query = query + " and order =" + lastcode + " ";
		if (!"".equalsIgnoreCase(subcode1)) {
			query += " AND upper(subcode1)='" + subcode1.toUpperCase() + "'";
		}
		if (!"".equalsIgnoreCase(subcode2)) {
			query += " AND upper(subcode2)='" + subcode2.toUpperCase() + "'";
		}
		query += ")x";

		Util.logger.info("Query Content 6: " + query);

		try {
			connection = dbpool.getConnection(database);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				strResult = (String) item.elementAt(0);
				return strResult;
			}

			return strResult;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return strResult;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public String[] getContent1(String database, String type, int lastcode,
			String newstypecode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		// String query =
		// "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
		// + textbaseid.toUpperCase() + "'";
		String query = "SELECT ID,"
				+ content
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE upper(newstypecode) = '"
				+ newstypecode.toUpperCase() + "'";

		query += " and [order] =" + lastcode + ")x ";

		Util.logger.info("Query Content 1: " + query);

		try {
			connection = dbpool.getConnection(database);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	private int insertData(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, long lduration) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "(user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,duration) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getLongRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ lduration + ")";

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

	private static int sendMT(MsgObject msgObject, String sclassname) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(sclassname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info(sclassname + "@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis(sclassname + "@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());

				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());

			Util.logger.printStackTrace(e);

			return -1;
		} catch (Exception e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

	@SuppressWarnings("unchecked")
	private String getContent2(String infoid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection("gateway");

			String query = "select content from icom_infoservice where info_type ='"
					+ infoid
					+ "' and  info_date <= current_timestamp() order by info_date desc limit 1";

			Vector result = DBUtil.getVectorTable(connection, query);
			if (result.size() == 0) {
				return null;
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

	// Gui content theo cach nao
	// Neu content ngan thi phai split ra
	private static int sendMT(String type, MsgObject msgObject,
			String sclassname) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(sclassname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info(sclassname + "@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(sclassname
						+ "@sendMT: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID

			if ("1".equalsIgnoreCase(type)) {

				// Truoc khi gui phai split
				String[] content = msgObject.getUsertext().split("###");

				for (int i = 0; i < content.length; i++) {
					if (!"".equalsIgnoreCase(content[i])) {
						statement = connection.prepareStatement(sqlString);
						statement.setString(1, msgObject.getUserid());
						statement.setString(2, msgObject.getServiceid());
						statement.setString(3, msgObject.getMobileoperator());
						statement.setString(4, msgObject.getKeyword());
						statement.setInt(5, msgObject.getContenttype());
						statement.setString(6, content[i]);
						if (i == 0) {
							statement.setInt(7, 1);
						} else {
							statement.setInt(7, 0);
						}
						statement.setBigDecimal(8, msgObject.getRequestid());

						if (statement.executeUpdate() != 1) {
							Util.logger.crisis(sclassname
									+ "@sendMT: Error@userid="
									+ msgObject.getUserid() + "@serviceid="
									+ msgObject.getServiceid() + "@usertext="
									+ msgObject.getUsertext() + "@messagetype="
									+ msgObject.getMsgtype() + "@requestid="
									+ msgObject.getRequestid().toString());

							return -1;
						}

					}
				}
				return 1;
			} else {
				statement = connection.prepareStatement(sqlString);
				statement.setString(1, msgObject.getUserid());
				statement.setString(2, msgObject.getServiceid());
				statement.setString(3, msgObject.getMobileoperator());
				statement.setString(4, msgObject.getKeyword());
				statement.setInt(5, msgObject.getContenttype());
				statement.setString(6, msgObject.getUsertext());
				statement.setInt(7, msgObject.getMsgtype());
				statement.setBigDecimal(8, msgObject.getRequestid());

				if (statement.executeUpdate() != 1) {
					Util.logger.crisis(sclassname + "@sendMT: Error@userid="
							+ msgObject.getUserid() + "@serviceid="
							+ msgObject.getServiceid() + "@usertext="
							+ msgObject.getUsertext() + "@messagetype="
							+ msgObject.getMsgtype() + "@requestid="
							+ msgObject.getRequestid().toString());

					return -1;
				}
				return 1;
			}
		} catch (SQLException e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());

			Util.logger.printStackTrace(e);

			return -1;
		} catch (Exception e) {
			Util.logger.crisis(sclassname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

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

	String[] getContent0(String lastcode, String textbaseid, String poolname) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ textbaseid.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode
				+ ")  order by rand() limit 1";

		try {
			connection = dbpool.getConnection(poolname);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + result.size() + "@"
					+ query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);

				Util.logger.info(this.getClass().getName()
						+ "getContent: record[0]:" + record[0]);
				Util.logger.info(this.getClass().getName()
						+ "getContent: record[1]:" + record[1]);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	String[] getContent1(int lastcode, String textbaseid, String poolname) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ textbaseid.toUpperCase() + "'";

		query = query + " and dayid =" + lastcode + " "
				+ "  order by rand() limit 1";

		try {
			connection = dbpool.getConnection(poolname);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				record[0] = (String) item.elementAt(0);
				record[1] = (String) item.elementAt(1);
				return record;
			}

			return record;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return record;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	public String[] getContent3(int companyId, String poolname) {
		Connection conn = null;
		String[] strResult = new String[2];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection(poolname);

			Util.logger.info("Conn: " + conn);
			Util.logger.info("poolname: " + poolname);
			// "Select
			// RESULT_COMPANY_ID,RESULT_TEXT,to_char(RESULT_DATE,'dd/mm/yyyy'),lotocap_text
			// from LASTEST_RESULT_FULL ORDER BY RESULT_COMPANY_ID ASC",
			sSql = "Select RESULT_TEXT,to_char(RESULT_DATE,'dd/mm') RESULT_DATE From LASTEST_RESULT_FULL where RESULT_COMPANY_ID = "
					+ companyId;
			Util.logger.info("SQL Select: " + sSql);

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult[0] = rs.getString("RESULT_TEXT");
					strResult[1] = rs.getString("RESULT_DATE");

				}
			}

			Util.logger.info("strResult " + strResult);

			return strResult;

		} catch (SQLException ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		} catch (Exception ex) {
			Util.logger.error("Error: getLatestResultText " + ex.toString());
		}

		finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(conn);
		}
		return null;
	}

	public static String getContent4(long companyid, String poolname) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp = "";
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection(poolname);

			stmt = connection.prepareStatement(
					"SELECT  RESULT_TEXT FROM LASTEST_CAU_FULL "
							+ "WHERE result_company_id = " + companyid,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();

				while (rs.next()) {

					temp = rs.getString(1);
				}
				String ret = temp;

				int i = ret.indexOf("*So ban thich");
				int j = ret.indexOf("8551###");

				ret = ret.subSequence(0, i) + ret.substring(j + 4);

				return ret;
			}

		} catch (Exception e) {
			Util.logger.error("Error: getLatestResultText " + e.toString());
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}
		return null;
	}

	public static String getContent5(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("getContent5@userid=" + msgObject.getUserid()
					+ "@serviceid=" + msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return null;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("getContent5@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnection("smscnt");
			if (connection == null) {
				Util.logger.crisis("getContent5: Error connection == null"
						+ msgObject.getUserid() + "@TO"
						+ msgObject.getServiceid() + "@"
						+ msgObject.getUsertext() + "@requestid="
						+ msgObject.getRequestid().toString());
				return null;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO mo_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, INFO,   REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setBigDecimal(6, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("getContent5: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return null;
			}
			return null;
		} catch (SQLException e) {
			Util.logger.crisis("getContent5: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} catch (Exception e) {
			Util.logger.crisis("getContent5: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return null;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

}
