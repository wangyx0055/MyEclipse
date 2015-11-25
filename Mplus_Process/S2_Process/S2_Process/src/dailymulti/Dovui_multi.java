package dailymulti;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class Dovui_multi extends DeliveryManager_multi {

	String CLASSNAME = "dailymulti.Dovui_multi";

	@Override
	protected Collection getMessages(String ssid, String option,
			String servicename, int notcharge, int idx, int num)
			throws Exception {
		try {
			Util.logger.info(CLASSNAME + ": start:" + servicename + "@idx="
					+ idx + "@num=" + num);
			String MLIST = "x";
			String INFO_ID = "x";

			HashMap _option = new HashMap();
			_option = getParametersAsString(option);
			String x = "x";

			INFO_ID = getStringfromHashMap(_option, "infoid", "x");
			MLIST = getStringfromHashMap(_option, "mlist", "x");
			String STYPE = getStringfromHashMap(_option, "type", "1");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + servicename + "",
						"CongLT:0963536888");
				return null;
			}

			String sqlUpdateRunning = "update services_daily set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type,mobile_operator,mt_count,mt_free,duration,TIMESTAMPDIFF(day, date,"
					+ Constants.PROMO_DATE
					+ "),last_id  from "
					+ MLIST
					+ " where upper(command_code)='"
					+ servicename.toUpperCase()
					+ "' and (mod(id,"
					+ num
					+ ")="
					+ idx + ")";

			if (notcharge == Constants.MODE_RESENDFAIL) {
				sqlSelect = sqlSelect + " and failures=1";
			}

			Vector vtUsers = DBUtil.getVectorTable("gateway", sqlSelect);
			for (int i = 0; i < vtUsers.size(); i++) {
				if (!Sender.processData) {
					break;
				}

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

				int msgtype = Integer.parseInt(messagetype);

				int nday = Integer.parseInt((String) item.elementAt(11));

				if ((nday >= 0) && (nday <= 10) && (mtfree > 0)) {
					msgtype = Integer.parseInt(Constants.MT_PUSH);
					Util.logger.info("free:" + userid + "@" + servicename
							+ "@nday=" + nday);
				}

				if (notcharge == Constants.MODE_NOTCHARGE
						|| "0".equalsIgnoreCase(STYPE)) {
					msgtype = Integer.parseInt(Constants.MT_PUSH);
				}

				String lastidquest = (String) item.elementAt(12);

				String[] arrContents = getContent(INFO_ID, lastcode, STYPE,
						lastidquest);

				String lastid = arrContents[0];
				String content = arrContents[1];

				if ("".equalsIgnoreCase(lastid) || "".equalsIgnoreCase(content)) {
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp,failures=1 where id ="
											+ id);
					// return null;
					DBUtil.Alert("DeliveryDaily", "RUNING", "major",
							"Kiem tra dich vu:" + servicename + "",
							"CongLT:0963536888");
				}

				if (lastcode.length() > 1500) {

					String templastcode = lastcode.substring(500);
					if (templastcode.startsWith(",")) {
						lastcode = "0" + templastcode;
					}
				} else {
					if (!"".equalsIgnoreCase(lastid)) {
						lastcode = lastcode + "," + lastid;
					}
				}

				MsgObject msgObj = new MsgObject(1, serviceid, userid,
						commandcode, content, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 0);

				if (sendMT(msgObj, CLASSNAME) == 1) {
					if (duration > 0 && (mtcount > duration)) {
						// xoa thoi

						insertData2cancel(userid, serviceid, commandcode,
								MLIST, msgObj, mtfree + "", msgtype,
								commandcode, mtcount + "");

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
												+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0,last_id='"
												+ lastid + "' where id =" + id);
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
			String sqlUpdate = "update services_daily set result="
					+ Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services_daily set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
			DBUtil
					.Alert("DeliveryDaily", "RUNING", "major",
							"Kiem tra dich vu:" + servicename + "",
							"CongLT:0963536888");

		}

		return null;
	}

	private int insertData2cancel(String user_id, String service_id,
			String command_code, String mlist, MsgObject msgObject,
			String mtfree, int msgtype, String Service_ss_id, String mt_count) {
		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlInsert = "Insert into "
				+ mlist
				+ "_cancel(user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,mt_count) values ('"
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

	private static int sendMT(MsgObject msgObject, String class_name) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error(class_name + "@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info(class_name + "@sendMT@userid=" + msgObject.getUserid()
				+ "@serviceid=" + msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.crisis(class_name
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
				Util.logger.crisis(class_name + "@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis(class_name + "@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			Util.logger.printStackTrace(e);
			return -1;
		} catch (Exception e) {
			Util.logger.crisis(class_name + "@sendMT: Error:@userid="
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

	String[] getContent(String gameid, String lastcode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode + ") "
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

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));
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

	String[] getContent(String gameid, String lastcode, String stype,
			String lastid) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode + ") "
				+ "  order by rand() limit 1";

		if ("0".equalsIgnoreCase(stype)) {
			query = "SELECT ID,NOTES FROM icom_textbase_data WHERE id = "
					+ lastid.trim() + " limit 1";

		}
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
}
