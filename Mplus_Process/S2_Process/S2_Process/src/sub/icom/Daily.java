package sub.icom;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
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
import java.util.StringTokenizer;
import java.util.Vector;

import sub.DeliveryManager;

public class Daily extends DeliveryManager {
	String CLASSNAME = "Daily";
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

	@Override
	protected Collection getMessages(String ssid, String option,
			String servicename,int notcharge) throws Exception {
		// TODO Auto-generated method stub
		try {
			HashMap _option = new HashMap();
			_option = getParametersAsString(option);
			// String x = "x";
			// String MLIST = "mlist_cauxs";
			String sservice = servicename;
			String scompanyid = getStringfromHashMap(_option, "companyid", "1");
			String MLIST = getStringfromHashMap(_option, "mlist",
					"mlist_stk");
			String textbasetype = getStringfromHashMap(_option, "textbasetype",
					TB_RANDOM);
			String infoid = getStringfromHashMap(_option, "infoid", "0");

			int companyid = Integer.parseInt(scompanyid);

			Util.logger.info(CLASSNAME + "@service=" + sservice + ": start");
			CLASSNAME = CLASSNAME + scompanyid;

			// textbasetype: kieu dich vu textbase
			// 0: textbase tra ngau nhien hang ngay
			// 1: textbase tra theo thu tu hang ngay
			// 2: text base tra theo ngay
			// 3: xo so

			String strResult = null;
			if (TB_DAILY.equalsIgnoreCase(textbasetype)) {
				String currDate = new SimpleDateFormat("dd/MM/yyyy")
						.format(new Date());

				strResult = getInfo(currDate, infoid);
				if (strResult == null) {
					return null;
				}
			} else if (TB_CAU.equalsIgnoreCase(textbasetype)) {
				strResult = getDataCAU(companyid);
				if (strResult == null) {
					return null;
				}
			} else if (TB_XOSO.equalsIgnoreCase(textbasetype)) {
				String currDate = new SimpleDateFormat("yyyyMMdd")
						.format(new Date());

				strResult = getResultXS(companyid, currDate);
				if (strResult == null) {
					return null;
				}
			}

			Vector vtUsers = DBUtil
					.getVectorTable(
							"gateway",
							"select id, user_id, service_id, last_code,command_code,request_id, message_type,mobile_operator,mt_count,mt_free,duration,options,TIMESTAMPDIFF(day, date," + Constants.PROMO_DATE  + ")  from "
									+ MLIST
									+ " where upper(service)='"
									+ sservice.toUpperCase() + "'");

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

				int duration = Integer.parseInt((String) item.elementAt(10));
				String soption = (String) item.elementAt(11);

				int msgtype = Integer.parseInt(messagetype);

				int nday = Integer.parseInt((String) item.elementAt(12));
				if (( nday <= 10) && (mtfree > 0)) {
					msgtype = Integer.parseInt(Constants.MT_PUSH);
				}

				// /////
				if (TB_RANDOM.equals(textbasetype)) {

					String[] strResulttmp = getContent0(lastcode, infoid);
					strResult = strResulttmp[1];
					lastcode = lastcode + "," + strResulttmp[0];

				} else if (TB_ORDER.equals(textbasetype)) {
					String[] strResulttmp = getContent1(mtcount + 1, infoid);
					strResult = strResulttmp[1];
					lastcode = lastcode + "," + strResulttmp[0];
				} else if (TB_CK.equals(textbasetype)) {
					strResult = soption;
				}

				// /////////
				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, strResult, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 0);

				if (sendMT(msgObj, CLASSNAME, textbasetype) == 1) {

					if (duration > 0 && (mtcount > duration)) {
						// xoa thoi

						insertData2cancel(sservice, userid, serviceid,
								commandcode, MLIST, msgObj, mtfree + "",
								msgtype, commandcode, mtcount + "");

						DBUtil.executeSQL("gateway", "delete from " + MLIST
								+ " where id =" + id);

					} else {
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

			// cap nhat tinh trang
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);

		}

		return null;
	}

	private static int sendMT(MsgObject msgObject, String classname,
			String textbasetype) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(classname + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		if ("5".equals(textbasetype)) {
			return sendMO(msgObject);
		}

		DBPool dbpool = new DBPool();

		Util.logger.info(classname + "@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(classname
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
				Util.logger.crisis(classname + "@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis(classname + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} catch (Exception e) {
			Util.logger.crisis(classname + "@sendMT: Error:@userid="
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

	String[] getContent(String lastcode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String gameid = "truyencuoi";
		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid + "'";

		query = query + " and id not in(" + lastcode + ") "
				+ "  order by rand() limit 1";

		try {
			connection = dbpool.getConnection("content");
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

	private int insertData2cancel(String service, String user_id,
			String service_id, String command_code, String mlist,
			MsgObject msgObject, String mtfree, int msgtype,
			String Service_ss_id, String mt_count) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "_cancel(service,user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,mt_count) values ('"
				+ service.toUpperCase()
				+ "','"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getLongRequestid() + "','" + msgtype + "','"
				+ msgObject.getMobileoperator() + "'," + mtfree + ","
				+ mt_count + ")";

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
			// TODO: handle exception
			return _defaultval;
		}
	}

	@SuppressWarnings("unchecked")
	private String getInfo(String sDate, String infoid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection("gateway");

			String query = "select content from icom_infoservice where info_type ='"
					+ infoid
					+ "' and DATE_FORMAT(info_date,'%d/%m/%Y')='"
					+ sDate + "'";

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

	public static String getDataCAU(long companyid) {
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
			// TODO: handle exception
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(stmt);
			dbpool.cleanup(connection);
		}
		return null;
	}

	public String getResultXS(int companyId, String currDate) {
		Connection conn = null;
		String strResult = "";
		String sSql = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			conn = dbpool.getConnection("servicelottery");

			sSql = "Select RESULT_TEXT,RESULT_DATE From LOTTERY_RESULTS_FULL where RESULT_COMPANY_ID = "
					+ companyId
					+ " and to_char(result_date,'yyyymmdd')='"
					+ currDate + "' order by result_date desc";

			stmt = conn.prepareStatement(sSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (stmt.executeUpdate() != -1) {
				rs = stmt.getResultSet();
				if (rs.next()) {
					strResult = rs.getString("RESULT_TEXT");

				}
			}
			// if (arrResult[i].indexOf("DB:") > 0 &&
			// arrResult[i].indexOf("?") < 0) {
			Util.logger.info("strResult " + strResult);
			if (strResult.indexOf("DB:") >= 0 && strResult.indexOf("?") < 0) {
				return strResult;
			}

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

	String[] getContent0(String lastcode, String textbaseid) {

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
			connection = dbpool.getConnection("content");
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

	String[] getContent1(int lastcode, String textbaseid) {

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
			connection = dbpool.getConnection("content");
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

	public static int sendMO(MsgObject msgObject) {
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
			return -1;

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
				return -1;
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
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("getContent5: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("getContent5: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

}
