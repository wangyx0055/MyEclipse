package daily;

import icom.Constants;
import icom.DBPool;
import icom.LoadConfig;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class Infoservices_backup extends DeliveryManager {

	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {

			String INFO_ID = "x";
			String MLIST = "x";
			String CLASSNAME = "Infoservices";
			Util.logger.info(CLASSNAME + ": start:" + serviceName);
			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";

			INFO_ID = Util.getStringfromHashMap(_option, "infoid", "x");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent",
					"content");

			// Lay du lieu cua cai nao
			String type = Util.getStringfromHashMap(_option, "type", "2");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "",
						"");
				return null;
			}
			// 01/04/2009
			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());

			Util.logger.info("Ngay gio: " + currDate);

			// String strResult = getInfo(currDate, INFO_ID);
			String strResult = getContent2(DBCONTENT, type, INFO_ID, currDate);

			if (strResult == null) {
				return null;
			} else {
				Util.logger.info("Noi dung gui toi cho khach hang: "
						+ strResult);
			}

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);
			
			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			
			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, sSplit,strResult);
			
			icom.Services service  = new icom.Services(); 
			try{
			service = icom.Services.getService(serviceName, LoadConfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("infoservices: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"infoservices",
						"major",
						"infoservices.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			
			// gui mt den nhung thue bao vua moi dang ky
			SendMT2UserHaveJustRegister(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, strResult, service);
					
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
			DBUtil
					.Alert("DeliveryDaily", "RUNING", "major",
							"Kiem tra dich vu:" + serviceName + "",
							"");
		}
		return null;
	}

	private void SendMT2UsersHasCharged(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit,String strInfo) throws Exception {
		Util.logger.info("@Infoservices@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send,service name="+ serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("@Infoservices@SendMT2UsersHasCharged: vtUsers="+ cUser);
		for (int i = 0; i <cUser; i++) {
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
			int channel_type  = Integer.parseInt((String) item.elementAt(11));
			String content = strInfo;

			Util.logger.info("Infoservices@Noi dung tra ve cho khach hang: "
					+ content);
			// failures: khong gui tin cho KH vi content bi null
			if ("".equalsIgnoreCase(content)) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
				// return null;
				DBUtil.Alert("DeliveryDaily@Infoservices", "RUNING", "major",
						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
								+ serviceName + "", "");
			}			
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			
			msgObj.setChannelType(channel_type);
			if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, i,1) == 1) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set last_code = '"
										+ lastcode
										+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
										+ userid + "'");
			} else {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
			}
		}
	}

	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	private void SendMT2UserHaveJustRegister(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String strInfo, icom.Services service) throws Exception {
		Util.logger.info("@Infoservices@SendMT2UserHaveJustRegister: lay danh sach thue bao charge online- table mlist="  + MLIST + ",service name=" + serviceName);
	
		Vector vtUsers = DBUtil.getListUserFromMlist(serviceName, MLIST);
		int cUser = vtUsers.size();
		Util.logger.info("@Infoservices@SendMT2UserHaveJustRegister: vtUsers="+ cUser);
		
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
			
			String sChannel_Type = (String) item.elementAt(14);
			String sCommand_Code = (String) item.elementAt(15);
			String sDate = (String)item.elementAt(16);
			String sReg_Count = (String)item.elementAt(17);
			int iReg_Count = Integer.parseInt(sReg_Count);
			
			/*******
			 * Kien tra xem co trong dich vu free hay khong
			 * Neu dich vu la free va la dang ky lan dau (reg_count=1: dang ky lan dau)
			 * ****/
			if(Util.IsUserIdInFreeCharge(iReg_Count, sDate, sCommand_Code, service))
			{
				msgtype = 0;
			}
			
			String content = strInfo;

			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			
			msgObj.setChannelType(Integer.parseInt(sChannel_Type));
			msgObj.setCommandCode(sCommand_Code);
			
			if (DBUtil.sendVMSChargeOnline(type, msgObj, CLASSNAME, serviceName) == 1) {
				if (duration > 0 && (mtcount > duration)) {
					// xoa thoi
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
	
	@SuppressWarnings("unchecked")
	private String getContent2(String dbcontent, String type, String infoid,
			String sDate) {
//if(1==1) return "test infoservice";
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
				+ "' AND CONVERT(varchar(25), [isdate], 103) ='" + sDate
				+ "' order by id desc)x";

			Util.logger.info("Infoservices: Sql select content : sql =" + query);

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
}
