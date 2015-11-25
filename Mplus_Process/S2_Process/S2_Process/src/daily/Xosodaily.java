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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class Xosodaily extends DeliveryManager {
	String CLASSNAME = "daily.Xosodaily";
	
	String XSMT = "XSMT";
	String XSMN = "XSMN";
	
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try {
			
			// Update 14.05 by TrungVD 11:10 AM
			
			Util.logger.info(CLASSNAME + ": serviceName :" + serviceName
					+ ": start");

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";
			String MLIST = "x";
			String scompanyid = Util.getStringfromHashMap(_option, "companyid", "x");
			String area = Util.getStringfromHashMap(_option, "area", "x");
			String scompanyname = Util.getStringfromHashMap(_option, "companyname","x");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			if ("x".equalsIgnoreCase(scompanyid) || "x".equalsIgnoreCase(MLIST)
					|| "x".equalsIgnoreCase(scompanyname)
					|| "x".equalsIgnoreCase(area)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "","");				
				return null;
			}
			int companyId = Integer.parseInt(scompanyid);
			int areaid = Integer.parseInt(area);

			String currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

			// Lay ket qua
			String strResult = getResult(companyId, currDate);
			//String strResult =" test xo so daily.";
			
			if ("x".equalsIgnoreCase(strResult) || strResult == null) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra dich vu xoso, content bi null:" + serviceName + "", "");
				return null;
			}

			String currDatexs = new SimpleDateFormat("dd/MM")
					.format(new Date());

			if (companyId == Constants.THUDO_COMPANY_ID) {
				strResult = "MB " + currDatexs + "\n" + strResult;
			} else {
				strResult = scompanyname + " " + currDatexs + "\n" + strResult;

			}
			Util.logger.info("Ket qua xoso daily:"  + strResult);
			CLASSNAME = CLASSNAME + "@" + scompanyid;
			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			String type = "2";
			
			String sqlUpdateRunning = "update services set result="
				+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);
			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged(notcharge, serviceName, type, MLIST, sSplit, companyId, areaid, strResult);
			
			icom.Services service  = new icom.Services(); 
			try{
				service = icom.Services.getService(serviceName, Sender.loadconfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("Xosodaily: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"Xosodaily",
						"major",
						"Xosodaily.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			// gui mt den nhung thue bao vua moi dang ky
			SendMT2UserHaveJustRegister(notcharge, serviceName, type, MLIST, companyId, areaid, strResult,service);
			
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
							"Kiem tra dich vu:" + serviceName + "", "");
		}
		return null;			
	}
	
	private void SendMT2UsersHasCharged(int notcharge, String serviceName,String type, String MLIST, String sSplit, int companyId, int areaid, String info) throws Exception {
		Util.logger.info("@Xosodaily@SendMT2UsersHasCharged: lay danh sach thue bao charge offline- table list send,service name="+ serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromListSendForXosoDaily(serviceName,companyId, areaid);
		int cUser = vtUsers.size();
		Util.logger.info("@Xosodaily@SendMT2UsersHasCharged: vtUsers="+ cUser);
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
			int channel_type  = Integer.parseInt((String) item.elementAt(10));
			// Moi dung se gui cho khach hang
			String content = info;
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
	private void SendMT2UserHaveJustRegister(int notcharge,String serviceName, String type, String MLIST, int companyId, int areaid, String info, icom.Services service) throws Exception {
		Util.logger.info("@Xosodaily@SendMT2UserHaveJustRegister: lay danh sach thue bao charge online- table mlist="  + MLIST + ",service name=" + serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromMlistForXosoDaily(serviceName, MLIST, companyId, areaid);
		int cUser = vtUsers.size();
		Util.logger.info("@Xosodaily@SendMT2UserHaveJustRegister: vtUsers="+ cUser);
		
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
			
			// DanNd ADD
			int iActive = 0;
			
			try{
				iActive = Integer.parseInt((String)item.elementAt(18));
			}catch(Exception ex){
				Util.logger.info(CLASSNAME + " ;; " + ex.getMessage());
			}
						
			
			
			/*******
			 * Kien tra xem co trong dich vu free hay khong
			 * neu dich vu la free va la dang ky lan dau (reg_count=1: dang ky lan dau)
			 * ****/
			if(Util.IsUserIdInFreeCharge(iReg_Count, sDate, sCommand_Code, service))
			{
				msgtype = 0;
			}
			// Moi dung se gui cho khach hang
			String content = info;		
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					content, new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 0,
					amount, content_id);
			
			msgObj.setChannelType(Integer.parseInt(sChannel_Type));
			msgObj.setCommandCode(sCommand_Code);
			
			// DanNd Add
//			if( iActive == 2 && (sCommand_Code.equals(XSMN) || sCommand_Code.equals(XSMT)) ){
//				//insert to mt_queue
//				String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
//				DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, i,1);
//				continue;
//			}
			
			if (DBUtil.sendVMSChargeOnline(type, msgObj, CLASSNAME, serviceName) == 1) {
				
				// DanNd Add
				if( sCommand_Code.equals(XSMN) || sCommand_Code.equals(XSMT) ){
					
					//update active = 1 in MLIST					
					DBUtil.executeSQL(
							"gateway",
							"update "
							+ MLIST
							+ " set last_code = '"
							+ lastcode
							+ "' active = 1 ,autotimestamps = current_timestamp,"
							+ " mt_count=mt_count+1,failures=0 where id ="
							+ id);
					
					continue;
				}
				
				
				if (duration > 0 && (mtcount > duration)) {
					// xoa thoi
					DBUtil.insertData2cancel(userid, serviceid, commandcode,
							MLIST, msgObj, mtfree + "", msgtype,
							commandcode, mtcount + "");

					DBUtil.executeSQL("gateway", "delete from " + MLIST
							+ " where id =" + id);
				} else {
					DBUtil.executeSQL( "gateway",
								"update "
								+ MLIST
								+ " set last_code = '"
								+ lastcode + "' active = 1,"
								+ "autotimestamps = current_timestamp,"
								+ "mt_count=mt_count+1,failures=0 "
								+ "where id =" + id);
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
	
	public String getResult(int companyId, String currDate) {
		//TODO: PhuongDT
		//if(1==1) return "test xo so dailty";
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

			Util.logger.info("Xosodaily: sql select content: sql="+sSql);
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

			// Khi nao co toan bo ket qua moi gui ve
			Util.logger.info("strResult:" + strResult);
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
		return "x";
	}

}
