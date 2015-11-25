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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import sub.DeliveryManager;

public class Textbaserandom extends DeliveryManager {

	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {

		String CLASSNAME = "daily.Textbaserandom";

		try {

			Util.logger.info(CLASSNAME + ": start:" + serviceName);

			String MLIST = "x";
			String INFO_ID = "x";

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);

			INFO_ID = Util.getStringfromHashMap(_option, "infoid", "x");
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontent", "content");

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");

			// Lay du lieu cua cai nao
			String type = Util.getStringfromHashMap(_option, "type", "2");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu, options=?:" + serviceName
								+ "", "");
				return null;
			}

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			// gui mt den nhung thue bao da charge tu truoc
			SendMT2UsersHasCharged(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, sSplit);
			// gui mt den nhung thue bao vua moi dang ky
			
			icom.Services service  = new icom.Services();
			
			try{
				service = icom.Services.getService(serviceName, Sender.loadconfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("Textbaserandom: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"Textbaserandom",
						"major",
						"Textbaserandom.Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			SendMT2UserHaveJustRegister(ssid, serviceName, MLIST, DBCONTENT, type,
					CLASSNAME, notcharge, INFO_ID, service);
			// cap nhat tinh trang
			String sqlUpdate = "update services set result=" + Constants.DELIVER_OK
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);

		} catch (Exception e) {
			Util.logger.printStackTrace(e);
			String sqlUpdate = "update services set result="
					+ Constants.DELIVER_FAILED
					+ ", lasttime=current_timestamp() where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdate);
			DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
					"Kiem tra dich vu:" + serviceName + "", "");

		}
		return null;
	}
	
	private void SendMT2UsersHasCharged(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit) throws Exception {
		Util.logger.info("@Textbaserandom@SendMT2UsersHasCharged: lay danh sach thue bao charge offline - table list send,service name="+ serviceName);
		
		Vector vtUsers;
//		
//		vtUsers = DBUtil.getListUserFromListSend(serviceName);
//		int cUser = vtUsers.size();		
//		Util.logger.info("@Textbaserandom@SendMT2UsersHasCharged: vtUsers="+ cUser);
		
		// DanNd Edit
		// Nhom Group co chung lastcode
		ArrayList<SameLastCodeObject> arrLastCode = getGroupLastCode(serviceName);
		ArrayList<SameLastCodeObject> arrContent = new ArrayList<SameLastCodeObject>();
		
		// get All User From ListSend
		ArrayList<Vector> arrUser = new ArrayList<Vector>();
		for(int i = 0;i < arrLastCode.size();i++){
			String lastCode = arrLastCode.get(i).lastCode;
			arrUser.add(DBUtil.getListUserFromListSendByLastCode(serviceName, lastCode));
		}
		
		
		for(int i = 0; i< arrLastCode.size(); i++){
			SameLastCodeObject obj = arrLastCode.get(i);
			String[] arrContents = getContent0(DBCONTENT, type, obj.lastCode,
					INFO_ID);

			obj.lastId = arrContents[0];
			obj.content = arrContents[1];
			arrContent.add(obj);
			
			String lastcode = "";
			vtUsers = null;
			vtUsers = arrUser.get(i);
			for(int j = 0;j<vtUsers.size();j++){
				Vector item = (Vector) vtUsers.elementAt(j);

				String id = (String) item.elementAt(0);
				String userid = (String) item.elementAt(1);
				String serviceid = (String) item.elementAt(2);
				lastcode = (String) item.elementAt(3);
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
				
				String content = obj.content;
				String lastid = obj.lastId;
				
				Util.logger.info("Textbaserandom@Noi dung tra ve cho khach hang: " + content);
				Util.logger.info("ServiceName = " + serviceName
						+ "; user_id = " + userid
						+ "; next code = " + lastid);
				// failures: khong gui tin cho KH vi content bi null
				if ("".equalsIgnoreCase(lastid) || "".equalsIgnoreCase(content)) {
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
											+ userid + "' and command_code = '" + commandcode + "'");
					// return null;
					DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
							"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
									+ serviceName + "", "");
				}
				if (lastcode.length() > 3500) {
					String templastcode = lastcode.substring(500);
					if (templastcode.startsWith(",")) {
						lastcode = "0" + templastcode;
					}
				} else {
					if (!"".equalsIgnoreCase(lastid)) {
						lastcode = lastcode + "," + lastid;
					}
				}
				
				MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
						content, new BigDecimal(requestid), DateProc
								.createTimestamp(), mobileoperator, msgtype, 0,
						amount, content_id);
				
				msgObj.setChannelType(channel_type);
				
				if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, j,1) == 1) {
				
				} else {
					DBUtil
							.executeSQL(
									"gateway",
									"update "
											+ MLIST
											+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
											+ userid + "' and command_code = '" + commandcode + "'");
				}
			}
			
			
			Util.logger.info("Textbaserandom: charge offline: update last code trong mlist. \tmlist:" + MLIST + "\tlastcode:" + lastcode+ "\tcommand_code:" + serviceName);
			
			String sqlUpdateLastCode = "update "
				+ MLIST
				+ " set last_code = '"
				+ lastcode
				+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where "
				+ " upper(command_code) like '"
				+ serviceName.toUpperCase() + "%' and last_code = '" + obj.lastCode + "'";
			
			Util.logger.info("SQL Update Last Code:: " + sqlUpdateLastCode);
			
			DBUtil.executeSQL("gateway", sqlUpdateLastCode);
							
			/**********
			 * update last code trong list_send
			 * *****/
//			Util.logger.info("Textbaserandom: charge offline: update last code trong list_send. \tlastCode:" + lastcode+ "\tcommand_code:" + serviceName);
//			DBUtil.executeSQL(
//					"gateway",
//					"update list_send "								
//							+ " set last_code ='"
//							+ lastcode
//							+ "' where "
//							+ " upper(command_code) like '"
//							+ serviceName.toUpperCase() + 
//							"%' AND last_code = '" + obj.lastCode + "'");
			
		}
				
				
//		for (int i = 0; i < cUser; i++) {
//
//			Vector item = (Vector) vtUsers.elementAt(i);
//
//			String id = (String) item.elementAt(0);
//			String userid = (String) item.elementAt(1);
//			String serviceid = (String) item.elementAt(2);
//			String lastcode = (String) item.elementAt(3);
//			String commandcode = (String) item.elementAt(4);
//			String requestid = (String) item.elementAt(5);
//			String messagetype = (String) item.elementAt(6);
//			String mobileoperator = (String) item.elementAt(7);
//
//			int msgtype = Integer.parseInt(messagetype);
//
//			if (notcharge == Constants.MODE_NOTCHARGE) {
//				// phan biet viec charge theo goi va charge binh thuong
//				msgtype = Integer.parseInt(Constants.MT_PUSH);
//			}
//			// Amount
//			long amount = Long.parseLong((String) item.elementAt(8));
//			int content_id = Integer.parseInt((String) item.elementAt(9));
//			int channel_type  = Integer.parseInt((String) item.elementAt(11));
//			
////			String[] arrContents = getContent0(DBCONTENT, type, lastcode,
////					INFO_ID);
////
////			String lastid = arrContents[0];
////			String content = arrContents[1];
//			
//			String lastid = "";
//			String content = "";
//			for(int j = 0;j< arrContent.size();j++){
//				SameLastCodeObject obj = arrContent.get(j);
//				if(lastcode.equals(obj.lastCode)){
//					lastid = obj.lastId;
//					content = obj.content;
//					break;
//				}
//			}
//
//			Util.logger.info("Textbaserandom@Noi dung tra ve cho khach hang: " + content);
//			// failures: khong gui tin cho KH vi content bi null
//			if ("".equalsIgnoreCase(lastid) || "".equalsIgnoreCase(content)) {
//				DBUtil
//						.executeSQL(
//								"gateway",
//								"update "
//										+ MLIST
//										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
//										+ userid + "'");
//				// return null;
//				DBUtil.Alert("DeliveryDaily@Textbaserandom", "RUNING", "major",
//						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
//								+ serviceName + "", "");
//			}
//			if (lastcode.length() > 3500) {
//				String templastcode = lastcode.substring(500);
//				if (templastcode.startsWith(",")) {
//					lastcode = "0" + templastcode;
//				}
//			} else {
//				if (!"".equalsIgnoreCase(lastid)) {
//					lastcode = lastcode + "," + lastid;
//				}
//			}
//			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
//					content, new BigDecimal(requestid), DateProc
//							.createTimestamp(), mobileoperator, msgtype, 0,
//					amount, content_id);
//			
//			msgObj.setChannelType(channel_type);
//			
//			if (DBUtil.sendMTQueue(type, msgObj, CLASSNAME, serviceName, sSplit, i,1) == 1) {
//				Util.logger.info("Textbaserandom: charge offline: update last code trong mlist. \tmlist:" + MLIST + "\tuser_id:" + userid+ "\tcommand_code:" + serviceName);
//				DBUtil.executeSQL(
//								"gateway",
//								"update "
//										+ MLIST
//										+ " set last_code = '"
//										+ lastcode
//										+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
//										+ userid + "' and upper(command_code) like '"
//										+ serviceName.toUpperCase() + "%'");
//				/**********
//				 * update last code trong list_send
//				 * *****/
//				Util.logger.info("Textbaserandom: charge offline: update last code trong list_send. \tuser_id:" + userid+ "\tcommand_code:" + serviceName);
//				DBUtil.executeSQL(
//						"gateway",
//						"update list_send "								
//								+ " set last_code ='"
//								+ lastcode
//								+ "' where user_id ='"
//								+ userid + "'" + " and upper(command_code) like '"
//								+ serviceName.toUpperCase() + "%'");
//			} else {
//				DBUtil
//						.executeSQL(
//								"gateway",
//								"update "
//										+ MLIST
//										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
//										+ userid + "'");
//			}
//		}
	}
	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	private void SendMT2UserHaveJustRegister(String ssid, String serviceName,
			String MLIST, String DBCONTENT, String type, String CLASSNAME,
			int notcharge, String INFO_ID, icom.Services service) throws Exception {
		Util.logger.info("@Textbaserandom@SendMT2UserHaveJustRegister: lay danh sach thue bao charge online- table mlist="  + MLIST + ",service name=" + serviceName);
		Vector vtUsers = DBUtil.getListUserFromMlist(serviceName, MLIST);
		
		int cUser = vtUsers.size();
		Util.logger.info("@Textbaserandom@SendMT2UserHaveJustRegister: vtUsers="+ cUser);
		
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
			//	// phan biet viec charge theo goi va charge binh thuong
			//	msgtype = Integer.parseInt(Constants.MT_PUSH);
			//}

			// Amount
			long amount = Long.parseLong((String) item.elementAt(8));
			int content_id = Integer.parseInt((String) item.elementAt(9));
			
			int mtcount = Integer.parseInt((String) item.elementAt(10));
			int mtfree = Integer.parseInt((String) item.elementAt(11));
			int duration = Integer.parseInt((String) item.elementAt(12));
			//int nday = Integer.parseInt((String) item.elementAt(13));
			
			String[] arrContents = getContent0(DBCONTENT, type, lastcode,
					INFO_ID);

			String lastid = arrContents[0];
			String content = arrContents[1];

			// failures: khong gui tin cho KH vi content bi null
			Util.logger.info("Textbaserandom@SendMT2UserHaveJustRegister@latsid=" + lastid + ",content=" + content);
			if ("".equalsIgnoreCase(lastid) || "".equalsIgnoreCase(content)) {
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set autotimestamps = current_timestamp, failures=1 where user_id = '"
										+ userid + "'");
				// return null;
				DBUtil.Alert("DeliveryDaily@Textbaserandom@SendMT2UserHaveJustRegister", "RUNING", "major",
						"Kiem tra dich vu: content bi null. de nghi nhap content ngay."
								+ serviceName + "", "");
			}
			if (lastcode.length() > 3500) {

				String templastcode = lastcode.substring(500);
				if (templastcode.startsWith(",")) {
					lastcode = "0" + templastcode;
				}
			} else {
				if (!"".equalsIgnoreCase(lastid)) {
					lastcode = lastcode + "," + lastid;
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
			
			Util.logger.info("Textbaserandom\tSendMT2UserHaveJustRegister\tSend vms_charge_online");
			if (DBUtil.sendVMSChargeOnline(type, msgObj, CLASSNAME, serviceName) == 1) {
				Util.logger.info("Textbaserandom: charge online: update last code trong mlist. \tmlist:" + MLIST + "\tuser_id:" + userid+ "\tcommand_code:" + serviceName);
				DBUtil
						.executeSQL(
								"gateway",
								"update "
										+ MLIST
										+ " set last_code = '"
										+ lastcode
										+ "' ,autotimestamps = current_timestamp,mt_count=mt_count+1,failures=0 where user_id = '"
										+ userid + "'" + " and upper(command_code) like '"
										+ sCommand_Code.toUpperCase() + "%'");
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
			+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND upper(newstypecode) = '"
			+ newstypecode.toUpperCase() + "'";

		query = query + " and id not in (" + lastcode + ") )x ";

		Util.logger.info("Textbaserandom: Sql select content: Sql = " + query);

		try {
			connection = dbpool.getConnection(database);

			Util.logger.info("SQL Query get content: " + query);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "@getContent: queryStatement:" + result.size() + "@"
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
	
	public ArrayList<SameLastCodeObject> getGroupLastCode(String commandCode){
		
		ArrayList<SameLastCodeObject> arrLastCode = new ArrayList<SameLastCodeObject>();
		
		String sqlSelect = "select last_code  from list_send where command_code='" + commandCode + "' " +
				"group by last_code order by length(last_code) desc";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					SameLastCodeObject obj = new SameLastCodeObject();
					obj.lastCode = rs.getString("last_code");
					arrLastCode.add(obj);					
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Textbaserandom - getGroupLastCode. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Textbaserandom - getGroupLastCode. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrLastCode;
	}
}
