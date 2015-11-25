package daily;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.DateProc;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class CunghoangdaoNew extends DeliveryManager {
	@SuppressWarnings("unchecked")
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {
			// Fix 11/05/2010 10:04
			String INFO_ID = "x";
			String MLIST = "x";
			String CLASSNAME = "Cunghoangdao";
			Util.logger.info(CLASSNAME + ": start:" + serviceName);
			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";

			String[] arrInfo = new String[12];
			String[] arrCung = { "BACHDUONG", "KIMNGUU", "SONGTU", "CUGIAI",
					"SUTU", "XUNU", "THIENBINH", "HOCAP", "NHANMA", "MAKET",
					"BAOBINH", "SONGNGU" };

			MLIST = Util.getStringfromHashMap(_option, "mlist", "mlist_hoangdao");
			String tblContent = Util.getStringfromHashMap(_option, "tblContent", "x");
			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent",
					"content");
			String type = Util.getStringfromHashMap(_option, "type", "2");

			if ("x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "",
						"");
				return null;
			}
			
			String currDate = new SimpleDateFormat("dd/MM/yyyy")
					.format(new Date());
			for (int i = 0; i < arrCung.length; i++) {
				// arrInfo[i] = getInfo(currDate, arrCung[i]);
				arrInfo[i] = getContent2(DBCONTENT, type, arrCung[i], currDate,tblContent,serviceName);
				if (arrInfo[i] == null) {
					Util.logger.error("@Cung hoang dao: " + arrCung[i] + " khong doc duoc content!. Kiem tra ket noi hoac noi dung?");
					DBUtil.Alert("DeliveryDaily", "RUNING", "major",
							"Cung hoang dao: khong doc duoc content!. Kiem tra ket noi hoac noi dung?",
							"");
					arrInfo[i] = "";
				}
			}
			// 01/04/2009
			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);
			
			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			
			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, sSplit, arrInfo, arrCung);
		
			icom.Services service  = new icom.Services(); 
			try{
				service = icom.Services.getService(serviceName, Sender.loadconfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("cunghoangdao: co loi khi get service");
				DBUtil.Alert(
						"Process.VMS",
						"cunghoangdao",
						"major",
						"cunghoangdao.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			// gui mt den nhung thue bao vua moi dang ky
			SendMT2UserHaveJustRegister(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, arrInfo,arrCung,service);
			
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
			DBUtil.Alert("DeliveryDaily", "RUNING", "major",
							"Kiem tra dich vu:" + serviceName + "", "");
		}
		return null;
	}
	
	private void SendMT2UsersHasCharged(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit, final String[] arrInfo, String[] arrCung) throws Exception {
		Util.logger.info("@Cunghoangdao@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send.service name="+ serviceName);
		Vector vtUsers = DBUtil.getListUserFromListSend(serviceName);
		int cUser = vtUsers.size();
		Util.logger.info("@Cunghoangdao@SendMT2UsersHasCharged: vtUsers="+ cUser);
						
		for (int i = 0; i < cUser; i++) {
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
			
			String options = (String) item.elementAt(10);
			
			String content = "";

			for (int j = 0; j < arrCung.length; j++) {
				if (arrCung[j].equalsIgnoreCase(options)) {
					content = arrInfo[j];
				}
			}
			
			if(content.trim().equals("")) continue;
			
			Util.logger.info("Textbaserandom@Noi dung tra ve cho khach hang: "
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
				DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
								+ serviceName + "", "");
			}	
			
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);

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
				/******
				 * Update last_code trong list send
				 * **/
				
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
			int notcharge, String INFO_ID, String[] arrInfo, String[] arrCung, icom.Services service) throws Exception {
		Util.logger.info("@Cunghoangdao@SendMT2UserHaveJustRegister: lay danh sach thue bao charge online- table mlist="  + MLIST + ".service name=" + serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromMlist(serviceName, MLIST);
		int cUser = vtUsers.size();
		Util.logger.info("@Cunghoangdao@SendMT2UserHaveJustRegister: vtUsers="+ cUser);
		//for(int i=0; i<arrInfo.length;i++)
		//{
		//	Util.logger.error("noi dung nao: i = " + i + ": " + arrInfo[i]);
		//}
		for (int i = 0; i < cUser; i++) {
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
			
			String options = (String) item.elementAt(13);
			
			String content = "";
			//Util.logger.error("Options: " + options);
			for (int j = 0; j < arrCung.length; j++) {
				if (arrCung[j].equalsIgnoreCase(options)) {
					content = arrInfo[j];
				}
			}
			//if(mtfree > 0 && mtcount <= mtfree)
			//{
			//	msgtype = 0;
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
				
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			
			msgObj.setChannelType(Integer.parseInt(sChannel_Type));
			msgObj.setCommandCode(sCommand_Code);
			
			//Util.logger.info("content hoang dao1:" + content);
			
			if (DBUtil.sendVMSChargeOnline(type, msgObj, CLASSNAME, serviceName) == 1) {
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
	@SuppressWarnings("unchecked")
	private String getContent2(String dbcontent, String type, String infoid,
			String sDate, String tblContent, String commandCode) {

		String content = null;
		
		Calendar calCur = Calendar.getInstance();
		
		Calendar beforCal = Calendar.getInstance();
		Calendar afterCal = Calendar.getInstance();
		
		beforCal.set(Calendar.MINUTE, calCur.get(Calendar.MINUTE) - 30);
		afterCal.set(Calendar.MINUTE, calCur.get(Calendar.MINUTE) + 30);
		
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strBefore = formatDate.format(beforCal.getTime());
		String strAfter = formatDate.format(afterCal.getTime());
		
		//if(1==1) return "test infoservice";
		Connection connection = null;
		DBPool dbpool = new DBPool();
				
		try {

			connection = dbpool.getConnection(dbcontent);

			String query = "SELECT content"
				+ " FROM  " + tblContent
				+ " WHERE ( ReturnDate > CAST('" + strBefore + "' AS DATETIME ) " 
				+  " AND ReturnDate < CAST('" + strAfter + "' AS DATETIME ) )" 
				+ " AND IsPublish=1 AND KeyWord='" + commandCode + " " + infoid + "'";
			
			System.out.println("CUNG HOANG DAO QUERY: " + query);
			
			Util.logger.info("Infoservices: Sql select content : sql =" + query);

			Vector result = DBUtil.getVectorTable(connection, query);

				for (int i = 0; i < result.size(); i++) {

					Vector item = (Vector) result.elementAt(i);
					content = (String) item.elementAt(0);
				}

		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getInfo: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);

		} finally {
			dbpool.cleanup(connection);
		}
		
		return content;

	}
}
