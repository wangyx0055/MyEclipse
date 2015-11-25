package daily;

import icom.Constants;
import icom.DBPool;
import icom.LoadConfig;
import icom.MsgObject;
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
import java.util.Vector;

import sub.DeliveryManager;

public class Xosotructiep extends DeliveryManager {
	
	String CLASSNAME = "daily.Xosott";

	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {
			Util.logger.info(CLASSNAME + "@companyid=" + serviceName
					+ ": start");

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";
			String MLIST = "x";
			String sCompanyId = Util.getStringfromHashMap(_option, "companyid",
					"x");
			String sCompanyName = Util.getStringfromHashMap(_option,
					"companyname", "x");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			if ("x".equalsIgnoreCase(sCompanyId) || "x".equalsIgnoreCase(MLIST)
					|| "x".equalsIgnoreCase(sCompanyName)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName
								+ ",companyId, companyName, mlist", "");
				return null;
			}
			int companyId = Integer.parseInt(sCompanyId);

			String currDate = new SimpleDateFormat("yyyyMMdd")
					.format(new Date());

			CLASSNAME = CLASSNAME + sCompanyId;

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");

			SendMT2UsersHasCharged(serviceName,notcharge, MLIST, companyId);

			icom.Services service  = new icom.Services(); 
			try{
			service = icom.Services.getService(serviceName, LoadConfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("Xosoructiep: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"Xosoructiep",
						"major",
						"Xosoructiep.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			
			// gui mt den nhung thue bao vua moi dang ky
			SendMT2UserHaveJustRegister(serviceName, notcharge, MLIST, companyId, service);
			// cap nhat tinh trang
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
			DBUtil.Alert("DeliveryDaily", "RUNING", "major",
					"Kiem tra dich vu:" + serviceName + "", "");
		}
		return null;
	}
	
	private void SendMT2UsersHasCharged(String serviceName,int notcharge, String MLIST, int companyId) throws Exception {
		Vector vtUsers = DBUtil.getListUserFromListSendForXoso(serviceName,companyId);

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

			int msgtype = Integer.parseInt(messagetype);

			if (notcharge == Constants.MODE_NOTCHARGE) {
				// phan biet viec charge theo goi va charge binh thuong
				msgtype = Integer.parseInt(Constants.MT_PUSH);
			}
			// Amount
			long amount = Long.parseLong((String) item.elementAt(8));
			int content_id = Integer.parseInt((String) item.elementAt(9));
			int channel_type  = Integer.parseInt((String) item.elementAt(10));
			
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					commandcode, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0);
			
			msgObj.setChannelType(channel_type);
			
			// Moi dung se gui cho khach hang
			// String content = "";
			if (insertMO2lotterygw(msgObj) == 1) {
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
	}
	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	private void SendMT2UserHaveJustRegister(String serviceName, int notcharge, String MLIST, int companyId, icom.Services service) throws Exception {
		Vector vtUsers = DBUtil.getListUserFromMlistForXoso(serviceName, MLIST, companyId);

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

			int msgtype = Integer.parseInt(messagetype);

			//if (notcharge == Constants.MODE_NOTCHARGE) {
				// phan biet viec charge theo goi va charge binh thuong
			//	msgtype = Integer.parseInt(Constants.MT_PUSH);
			//}

			// Amount
			long amount = Long.parseLong((String) item.elementAt(8));
			int content_id = Integer.parseInt((String) item.elementAt(9));

			int mtcount = Integer.parseInt((String) item.elementAt(10));
			int mtfree = Integer.parseInt((String) item.elementAt(11));
			int duration = Integer.parseInt((String) item.elementAt(12));
			//int nday = Integer.parseInt((String) item.elementAt(13));

			/*
			 * if ((nday >= 0) && (nday <= 10) && (mtfree > 0)) { msgtype =
			 * Integer.parseInt(Constants.MT_PUSH);
			 * Util.logger.info("WapPic@free:" + userid + "@" + serviceName +
			 * "@nday=" + nday); }
			 */
			//if(mtfree > 0 && mtcount <= mtfree)
			//{
			//	msgtype = 0;
			//}
			//if (notcharge == Constants.MODE_NOTCHARGE) {
			//	msgtype = Integer.parseInt(Constants.MT_PUSH);
			//}
			String sChannel_Type = (String) item.elementAt(14);
			String sCommand_Code = (String) item.elementAt(15);
			String sDate = (String)item.elementAt(16);
			String sReg_Count = (String)item.elementAt(17);
			int iReg_Count = Integer.parseInt(sReg_Count);
			
			/*******
			 * Kien tra xem co trong dich vu free hay khong
			 * neu dich vu la free va la dang ky lan dau (reg_count=1: dang ky lan dau)
			 * ****/
			if(Util.IsUserIdInFreeCharge(iReg_Count, sDate, sCommand_Code, service))
			{
				msgtype = 0;
			}
			
			MsgObject msgObj = new MsgObject(1, serviceid, userid,
					commandcode, commandcode, new BigDecimal(requestid),
					DateProc.createTimestamp(), mobileoperator, msgtype, 0);
			
			msgObj.setChannelType(Integer.parseInt(sChannel_Type));
			msgObj.setCommandCode(sCommand_Code);
			
			// Moi dung se gui cho khach hang
			//String content = "";		
			if (insertMO2lotterygw(msgObj) == 1) {
				if (duration > 0 && (mtcount >= duration)) {
					DBUtil.insertData2cancel(userid, serviceid, commandcode,
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
	}
	public int insertMO2lotterygw(MsgObject msgObject) throws Exception {
		Util.logger.info("insertMO2lottery:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "sms_receive_queue";
		sSQLInsert = "insert into "
				+ tablename
				+ "(ID,USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,  INFO, TIMESTAMP,REQUEST_ID)"
				+ " values(s_sms_receive_queue.nextval,?,?,?,?,?,sysdate,?)";

		try {
			connection = dbpool.getConnection("lotterygw");

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setString(5, msgObject.getUsertext());
			statement.setBigDecimal(6, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("insertMO2lottery:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return 0;
			}
			statement.close();
			return 1;
		} catch (SQLException e) {
			Util.logger.error("insertMO2lottery:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive queue:" + e.toString());
			return 0;
		} catch (Exception e) {
			Util.logger.error("insertMO2lottery:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive queue:" + e.toString());
			return 0;
		}

		finally {
			dbpool.cleanup(connection);

		}
	}
}
