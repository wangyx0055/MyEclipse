package subscription;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

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

import subscription.DeliveryManager;

public class Infoservices extends DeliveryManager {

	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(String ssid, String option,
			String servicename, int notcharge) throws Exception {

		String dbcontent = "gateway";
		try {
			String INFO_ID = "x";
			String MLIST = "x";
			String CLASSNAME = "Infoservices";
			Util.logger.info(CLASSNAME + ": start:" + servicename);
			HashMap _option = new HashMap();
			_option = getParametersAsString(option);
			String x = "x";

			INFO_ID = getStringfromHashMap(_option, "infoid", "x");
			MLIST = getStringfromHashMap(_option, "mlist", "x");
			dbcontent = getStringfromHashMap(_option, "dbcontent", dbcontent);

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + servicename + "",
						"CongLT:0963536888");
				return null;
			}
			// 01/04/2009
			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());

			String strResult = getInfo(dbcontent, currDate, INFO_ID);
			if (strResult == null) {
				return null;
			}

			String sqlUpdateRunning = "update evn_services set result="
					+ Constantsub.DELIVER_RUNNING + " where id=" + ssid;
			Subutil.executeSQL(dbcontent, sqlUpdateRunning);

			String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator,mt_count,mt_free,duration,TIMESTAMPDIFF(day, GREATEST(date,'2009-06-01 00:00:01'),current_timestamp()) from "
					+ MLIST
					+ " where upper(command_code)='"
					+ servicename.toUpperCase() + "'";

			if (notcharge == Constantsub.MODE_RESENDFAIL) {
				sqlSelect = sqlSelect + " and failures=1";
			}

			Vector vtUsers = Subutil.getVectorTable(dbcontent, sqlSelect);
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

				int msgtype = Integer.parseInt(messagetype);

				int nday = Integer.parseInt((String) item.elementAt(11));

				if ((nday <= 10) && (mtfree > 0)) {
					msgtype = Integer.parseInt(Constantsub.MT_PUSH);
				}

				if (notcharge == Constantsub.MODE_NOTCHARGE) {
					msgtype = Integer.parseInt(Constantsub.MT_PUSH);
				}

				MsgObject msgObj = new MsgObject(serviceid, userid,
						commandcode, strResult, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 0);

				if (sendMT(msgObj, CLASSNAME) == 1) {

					if (duration > 0 && (mtcount > duration)) {
						// xoa thoi

						insertData2cancel(userid, serviceid, commandcode,
								MLIST, msgObj, mtfree + "", msgtype,
								commandcode, mtcount + "");

						Subutil.executeSQL(dbcontent, "delete from " + MLIST
								+ " where id =" + id);

					} else {
						Subutil
								.executeSQL(
										dbcontent,
										"update "
												+ MLIST
												+ " set last_code = '"
												+ lastcode
												+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where id ="
												+ id);
					}

				} else {
					Subutil
							.executeSQL(
									dbcontent,
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp,failures=1 where id ="
											+ id);
				}

			}

			// cap nhat tinh trang
			String sqlUpdate = "update evn_services set result="
					+ Constantsub.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			Subutil.executeSQL(dbcontent, sqlUpdate);
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update evn_services set result="
					+ Constantsub.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			Subutil.executeSQL(dbcontent, sqlUpdate);
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
		// long lmilisec = System.currentTimeMillis();
		String sqlInsert = "Insert into "
				+ mlist
				+ "_cancel(user_id, service_id, date,command_code,request_id, message_type,mobile_operator,mt_free,mt_count) values ('"
				+ user_id
				+ "','"
				+ service_id
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + Service_ss_id + "','"
				+ msgObject.getRequestid() + "','" + msgtype + "','"
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
					+ "ems_send_queue( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

	// Lấy nội dung trong icom_infoservice
	@SuppressWarnings("unchecked")
	private String getInfo(String dbcontent, String sDate, String infoid) {

		Connection connection = null;
		DBPool dbpool = new DBPool();

		try {

			connection = dbpool.getConnection(dbcontent);

			String query = "SELECT content FROM icom_infoservice where info_type ='"
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
}
