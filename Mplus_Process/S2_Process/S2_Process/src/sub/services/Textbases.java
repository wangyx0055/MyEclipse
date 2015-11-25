package sub.services;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class Textbases extends QuestionManager {

	String INFO_ID = "BAO";
	String MLIST = "mlist_bao";
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

	// Có subcode hay không subcode
	// Có subcode 1
	String TB_SUBCODE1 = "6";
	// Có subcode 2
	String TB_SUBCODE2 = "7";

	// Lấy nội dung dài hay ngắn ?
	// 1: Ngắn content_short
	// 2: Dài content
	// 3: Tiếng Việt content_vi
	
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
			_option = Util.getParametersAsString(options);
			// System.out.println("option:" + options);
			MLIST = ((String) _option.get("mlist"));
			String companyid = Util.getStringfromHashMap(_option, "companyid", "1");
			String textbasetype = Util.getStringfromHashMap(_option, "textbasetype",TB_RANDOM);
			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent",
					"content");

			// Lay du lieu cua cai nao
			String type = Util.getStringfromHashMap(_option, "type", "1");
			String infoid = Util.getStringfromHashMap(_option, "infoid", "0");
			
			// textbasetype: kieu dich vu textbase
			// 0: textbase tra ngau nhien hang ngay
			// 1: textbase tra theo thu tu hang ngay
			// 2: text base tra theo ngay
			// 3: xo so
			// 4: soi cau
			// 5: chung khoan

			String strQuery = "select id, user_id, service_id, last_code,command_code,request_id, message_type,mobile_operator,mt_count,mt_free,duration,options,TIMESTAMPDIFF(day, date,"
					+ Constants.PROMO_DATE
					+ ")  from "
					+ MLIST
					+ " where user_id='" + msgObject.getUserid() + "'";

			Util.logger.info("sub.services.TextBase.SQL Query: " + strQuery);
			Util.logger.info("sub.services.TextBase.textbasetype=: " + textbasetype);
			if (textbasetype.equalsIgnoreCase(TB_XOSO)) {
				strQuery = strQuery + " and company_id=" + companyid;
			}

			if (textbasetype.equalsIgnoreCase(TB_CAU)) {
				strQuery = strQuery + " and company_id=" + companyid;
			}

			Vector vtUsers = DBUtil.getVectorTable("gateway", strQuery);

			Util.logger.info("VtUSers Size: " + vtUsers.size());

			if (vtUsers.size() == 0) {
				// khong tim thay
				MsgObject msgObj = new MsgObject(1, msgObject.getServiceid(),
						msgObject.getUserid(), msgObject.getKeyword(),
						"Tin sai cu phap. Dien thoai ho tro 199", msgObject
								.getRequestid(), DateProc.createTimestamp(),
						msgObject.getMobileoperator(), Integer
								.parseInt(Constants.MT_NOCHARGE), 0);
				sendMT(type, msgObj, CLASSNAME);
				return null;
			}

			for (int i = 0; i < vtUsers.size(); i++) {
				Vector item = (Vector) vtUsers.elementAt(i);
				String id = (String) item.elementAt(0);
				String userid = (String) item.elementAt(1);
				String serviceid = (String) item.elementAt(2);
				String lastcode = (String) item.elementAt(3);
				String commandcode = (String) item.elementAt(4);
				String requestid = (String) item.elementAt(5);
				String messagetype = (String) item.elementAt(6);
				String mobileoperator = (String) item.elementAt(7);

				int mtcount = Integer.parseInt((String) item.elementAt(8));
				int mtfree = Integer.parseInt((String) item.elementAt(9));

				int msgtype = Integer.parseInt(messagetype);

				String soptions = (String) item.elementAt(11);

				// int nday = Integer.parseInt((String) item.elementAt(12));
				// if ((nday>=0)&&(nday <= 10) && (mtfree > 0)) {
				if (TB_CK.equals(textbasetype)) {
					requestid = "0";
				} else {
					msgtype = Integer.parseInt(Constants.MT_PUSH);
				}
				// }

				if (mtcount > 0) {
					Util.logger.info("Den day roi mt count");
					return null;

				}

				String strResult = null;

				// textbasetype: kieu dich vu textbase
				// 0: textbase tra ngau nhien hang ngay
				// 1: textbase tra theo thu tu hang ngay
				// 2: text base tra theo ngay
				// 3: xo so

				if ("0".equals(textbasetype)) {

					Util.logger.info("Den day roi");
					// Lay noi dung
					String[] strResulttmp = getContent0(DBCONTENT, type,
							lastcode, infoid);
					strResult = strResulttmp[1];
					lastcode = lastcode + "," + strResulttmp[0];

				} else if ("1".equals(textbasetype)) {

					// Tra tin theo ngay
					Util.logger.info("Den day roi 1");
					String[] strResulttmp = getContent1(DBCONTENT, type,
							mtcount + 1, infoid);
					strResult = strResulttmp[1];
					lastcode = lastcode + "," + strResulttmp[0];
				} else if ("2".equals(textbasetype)) {
					Util.logger.info("Den day roi 2");
					
					// 01/04/2009
					String currDate = new SimpleDateFormat("dd/MM/yyyy")
							.format(new Date());

					Util.logger.info("Ngay gio: " + currDate);
					
					strResult = getContent2(DBCONTENT, type, infoid, currDate);

				} else if ("3".equals(textbasetype)) {
					String[] strResulttmp = getContent3(Integer
							.parseInt(companyid));
					strResult = strResulttmp[0];
					strResult = Constants.getNameOfLottery(Integer
							.parseInt(companyid))
							+ " " + strResulttmp[1] + "\n" + strResult;
				} else if ("4".equals(textbasetype)) {
					strResult = getContent4(Integer.parseInt(companyid));

				} else if (TB_CK.equals(textbasetype)) {
					// Thong tin chung khoan, fw sang doi tac

					MsgObject msgObjck = new MsgObject(1, serviceid, userid,
							commandcode, soptions, new BigDecimal(requestid),
							DateProc.createTimestamp(), mobileoperator,
							msgtype, 0);
					strResult = getContent5(msgObjck);

					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set last_code = '"
											+ lastcode
											+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where id ="
											+ id);

				}

				if (strResult == null) {
					return null;
				}

				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, strResult, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 0);

				if (sendMT(type, msgObj, CLASSNAME) == 1) {

					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set last_code = '"
											+ lastcode
											+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where id ="
											+ id);

				} else {
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp,failures=1 where id ="
											+ id);
				}

			}

		} catch (Exception e) {
			Util.logger.printStackTrace(e);

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
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
						statement.setInt(7, msgObject.getMsgtype());
						statement.setBigDecimal(8, msgObject.getRequestid());
						statement.setInt(9, msgObject.getChannelType());

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
				statement.setInt(9, msgObject.getChannelType());
				if (statement.executeUpdate() != 1) {
					Util.logger.crisis(sclassname + "@sendMT: Error@userid="
							+ msgObject.getUserid() + "@serviceid="
							+ msgObject.getServiceid() + "@usertext="
							+ msgObject.getUsertext() + "@messagetype="
							+ msgObject.getMsgtype() + "@requestid="
							+ msgObject.getRequestid().toString() + "@channel_type=" + msgObject.getChannelType());

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

	@SuppressWarnings("unchecked")
	private String getContent2(String dbcontent, String type, String infoid, String sDate) {

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
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND upper(newstypecode) = '"
				+ infoid.toUpperCase()
				+ "' AND CONVERT(varchar(25), [isdate], 103) ='" + sDate + "')x";

			
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

	// Truyen cuoi
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

		query = query + " and status=1 and id not in (" + lastcode + ") )x ";

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

		query = query + " and order =" + lastcode + " ";
		// + "  order by rand() limit 1";

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

	public String[] getContent3(int companyId) {
		Connection conn = null;
		String[] strResult = new String[2];
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("servicelottery");
			// "Select
			// RESULT_COMPANY_ID,RESULT_TEXT,to_char(RESULT_DATE,'dd/mm/yyyy'),lotocap_text
			// from LASTEST_RESULT_FULL ORDER BY RESULT_COMPANY_ID ASC",
			sSql = "Select RESULT_TEXT,to_char(RESULT_DATE,'dd/mm') RESULT_DATE From LASTEST_RESULT_FULL where RESULT_COMPANY_ID = "
					+ companyId;

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

	public static String getContent4(long companyid) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp = "";
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection("servicelottery");

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
