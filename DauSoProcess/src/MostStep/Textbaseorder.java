package MostStep;

import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.MsgObject;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.DateProc;
import com.vmg.sms.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import subscription.Constantsub;
import subscription.DeliveryManager;

public class Textbaseorder extends DeliveryManager {

	String CLASSNAME = "daily.Textbaseorder";

	protected Collection getMessages(String ssid, String option,
			String servicename, int notcharge) throws Exception {
		// TODO Auto-generated method stub
		try {
			Util.logger.info(CLASSNAME + ": start:" + servicename);
			String MLIST = "x";
			String INFO_ID = "x";
			HashMap _option = new HashMap();
			_option = getParametersAsString(option);
			String x = "x";
			

			INFO_ID = getStringfromHashMap(_option, "infoid", "x");
			MLIST = getStringfromHashMap(_option, "mlist", "x");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + servicename + "",
						"CongLT:0963536888");
				return null;
			}

			String sqlUpdateRunning = "update icom_services set result="
					+ Constantsub.DELIVER_RUNNING + " where id=" + ssid;
			Subutil.executeSQL("gateway", sqlUpdateRunning);

			String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type,mobile_operator,mt_count,mt_free,duration,0  from "
					+ MLIST
					+ " where upper(service) ='"
					+ servicename.toUpperCase() + "'";
			
			Util.logger.info(sqlSelect);

			if (notcharge == Constantsub.MODE_RESENDFAIL) {
				sqlSelect = sqlSelect + " and failures=1";
			}

			Vector vtUsers = Subutil.getVectorTable("gateway", sqlSelect);
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

				/*int nday = Integer.parseInt((String) item.elementAt(11));
				if ((nday <= 10) && (mtfree > 0)) {
					msgtype = Integer.parseInt(Constantsub.MT_PUSH);
				}*/

				if (notcharge == Constantsub.MODE_NOTCHARGE) {
					msgtype = Integer.parseInt(Constantsub.MT_PUSH);
				}
				// if (mtcount <= mtfree) {
				// msgtype = Integer.parseInt(Constants.MT_PUSH);
				// }

				String[] arrContents = getContent(INFO_ID, mtcount + 1);

				String lastid = arrContents[0];
				String content = arrContents[1];
				lastcode = lastcode + "," + lastid;

				MsgObject msgObj = new MsgObject(serviceid, userid,
						commandcode, content, new BigDecimal(requestid),
						DateProc.createTimestamp(), mobileoperator, msgtype, 0);

				String[] arrcontent = content.split("###");
				mtcount = mtcount+1;
				int sendok = 1;
				for ( int ii = 0; ii < arrcontent.length; ii ++) {
					msgObj.setUsertext(arrcontent[ii]);
					DBUtil.sendMT(msgObj);
					// so lan gui tin nhan cho so dien thoai
					// voi goi nay
					// requetiD co dang req_ac, reqab
					// a : lan thu a gui tin cho thue bao trong goi dang ky
					// b: so tin thu b trong lan gui tin a
					
					int si = ii +1;				
					String sMTcount ="_"+  mtcount+ si ;
					
					// log send MT		
					// userID,  info, serviceID, command_code, telcoid, rsqID
					String temp2 = SoapMOMT.SendMT2(msgObj,sMTcount);
					Util.logger.info("temp2____" + temp2);
				}
				
				
				if (sendok == 1) {
					if (duration > 0 && (mtcount > duration)) {
						// xoa thoi
						Util.logger.info("{delete from mlist:" + MLIST
								+ "}{classname=" + CLASSNAME + "}{userid="
								+ msgObj.getUserid() + "}{mtcount=" + mtcount
								+ "}{duration=" + duration + "}");
						Util.logger.info("delete from " + MLIST + " where id ="
								+ id);
						insertData2cancel(userid, serviceid, commandcode,
								MLIST, msgObj, mtfree + "", msgtype,
								commandcode, mtcount + "");

						Subutil.executeSQL("gateway", "delete from " + MLIST
								+ " where id =" + id);

					} else {
						Subutil
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
					Subutil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp,failures=1 where id ="
											+ id);
				}

			}

			// cap nhat tinh trang
			String sqlUpdate = "update icom_services set result="
					+ Constantsub.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			Subutil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			// TODO: handle exception
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update icom_services set result="
					+ Constantsub.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			Subutil.executeSQL("gateway", sqlUpdate);

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
				+ msgObject.getRequestid().toString() + "','" + msgtype + "','"
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

	String[] getContent(String gameid, int lastcode) {

		String[] record = new String[2];
		record[0] = "";
		record[1] = "";

		Connection connection = null;
		DBPool dbpool = new DBPool();

		String query = "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
				+ gameid.toUpperCase() + "'";

		query = query + " and dayid =" + lastcode + " "
				+ "  order by rand() limit 1";

		try {
			connection = dbpool.getConnection("gateway");
			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);
			Vector result = DBUtil.getVectorTable(connection, query);

			

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
}
