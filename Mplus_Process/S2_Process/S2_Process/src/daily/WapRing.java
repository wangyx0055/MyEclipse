package daily;

import icom.COMObject;
import icom.Constants;
import icom.DBPool;
import icom.GameInfo;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import resend.ResendCommon;
import sub.DeliveryManager;
import vmg.itrd.ws.MTSenderVMS;

public class WapRing extends DeliveryManager {
	String DomainServer = Constants._prop.getProperty("domainServer", "");
	String DomainMobinet = Constants._prop.getProperty("domainmobinet", "");
	
	String CLASSNAME = "daily.WapPicture";
	
	@Override
	protected Collection getMessages(String ssid, String option,
			String serviceName, int notcharge) throws Exception {
		try { 

			Util.logger.info(CLASSNAME + ": start:" + serviceName);

			String MLIST = "x";
			String INFO_ID = "x";

			HashMap _option = new HashMap();
			_option = Util.getParametersAsString(option);
			String x = "x";

			// Noi dung can lay
			INFO_ID = Util.getStringfromHashMap(_option, "infoid", "x");

			// Ten bang luu danh sach khach hang
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");

			// Ket noi voi content wappush
			String DBCONTENT = Util.getStringfromHashMap(_option, "dbcontentwp", "wap");

			// Nhom se lay thong tin VMS
			String GID = Util.getStringfromHashMap(_option, "gid", "21");			

			// The loai se lay
			String typehd = "image";
			typehd = Util.getStringfromHashMap(_option, "typehd", "image");

			// Lay du lieu cua cai nao
			// 1 hinh dong, 2 hinh nen, 3:media
			String type = Util.getStringfromHashMap(_option, "type", "1");

			if ("x".equalsIgnoreCase(INFO_ID) || "x".equalsIgnoreCase(MLIST)) {
				Util.logger.info("Alert");
				DBUtil.Alert("DeliveryDaily", "RUNING", "major",
						"Kiem tra cau hinh dich vu:" + serviceName + "", "");
				return null;
			}

			String sqlUpdateRunning = "update services set result="
					+ Constants.DELIVER_RUNNING + " where id=" + ssid;
			DBUtil.executeSQL("gateway", sqlUpdateRunning);

			// Lay ma wap push
			//TODO: PhuongDT
			//String code = "A";
		
			
			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");
			
			// Handle Active = 0
			ResendCommon resendCmm = new ResendCommon();
			resendCmm.handleJustRegICOM(MLIST, serviceName);
			
			SendMT2UsersHasCharged(ssid, DBCONTENT, serviceName, MLIST, type, CLASSNAME,
					notcharge, INFO_ID, sSplit, typehd, GID);
		

			icom.Services service  = new icom.Services();
			
			try{
				service = icom.Services.getService(serviceName, Sender.loadconfig.hServices);
			}catch(Exception ex)
			{
				Util.logger.error("WapPicture: co loi khi get service");
				DBUtil
				.Alert(
						"Process.VMS",
						"WapPicture",
						"major",
						"WapPicture:Exception: co loi khi get service.ex=" + ex.toString(),"");
			}
			
			// gui mt den nhung thue bao vua moi dang ky
//			SendMT2UserHaveJustRegister(ssid, DBCONTENT, serviceName, MLIST,
//					type, CLASSNAME, notcharge, INFO_ID ,typehd, GID,service);
												
			
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
	
	private void SendMT2UsersHasCharged(String ssid, String DBCONTENT, String serviceName,
			String MLIST, String type, String CLASSNAME, int notcharge,
			String INFO_ID, String sSplit,  String typehd, String GID) throws Exception {		
		Util.logger
		.info("WapRing : SendMT2UsersHasCharged: Start Delivery Service="+ serviceName);
			
			int numberThread = 1;
			try {
				numberThread = Integer.parseInt(Constants._prop
						.getProperty("NUMBER_THREAD_GET_LINK_GAME", "1"));
			} catch (Exception ex) {
				Util.logger
						.error("Error Config file at NUMBER_THREAD_GET_LINK_GAME!!!!");
			}
			
			for(int i = 0;i<numberThread;i++){
				SubThreadDeliveryRing thDelivery = new SubThreadDeliveryRing(serviceName,i);
				thDelivery.start();
			}		
	}
	
	// send mt online : send to vms_mt_queue, INProcess read vms_mt_queue,
	// charge and then insert into mt_queue
	private void SendMT2UserHaveJustRegister(String ssid, String DBCONTENT, String serviceName,
			String MLIST, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String typehd, String GID, icom.Services service) throws Exception {
		Util.logger.info("@Wappicture@SendMT2UserHaveJustRegister: lay danh sach thue bao charge online- table mlist="  + MLIST + ",service name=" + serviceName);
		
		Vector vtUsers = DBUtil.getListUserFromMlist(serviceName, MLIST);
		int cUser = vtUsers.size();
		Util.logger.info("@Wappicture@SendMT2UserHaveJustRegister: vtUsers="+ cUser);
		
		TitleMng titleMng = TitleMng.getInstance();
		
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

			/*if ((nday >= 0) && (nday <= 10) && (mtfree > 0)) {
				msgtype = Integer.parseInt(Constants.MT_PUSH);
				Util.logger.info("WapPic@free:" + userid + "@" + serviceName
						+ "@nday=" + nday);
			}*/
			
			//if (notcharge == Constants.MODE_NOTCHARGE) {
			//	msgtype = Integer.parseInt(Constants.MT_PUSH);
			//}
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
			
			int cateid = GateCateid(sCommand_Code);
			int code = getCodeStore(cateid, lastcode);
			//String namecode = GateCateName(sCommand_Code);
			
			String namecode = titleMng.getTitle(serviceName);
			// TODO: PhuongDT
			// String code= "GGAMEHD";
			// String namecode= "Game hanh dong";
			/*******
			 * Kien tra xem co trong dich vu free hay khong neu dich vu la free
			 * va la dang ky lan dau (reg_count=1: dang ky lan dau)
			 * ****/
			if (Util.IsUserIdInFreeCharge(iReg_Count, sDate, sCommand_Code,
					service)) {
				msgtype = 0;
			}
			MsgObject msgObject = new MsgObject(1, serviceid, userid,
					commandcode, code + "", new BigDecimal(requestid), DateProc
							.createTimestamp(), mobileoperator, msgtype, 8);
			msgObject.setChannelType(Integer.parseInt(sChannel_Type));
			msgObject.setCommandCode(sCommand_Code);
			msgObject.setServiceName(serviceName);

			if (lastcode.length() > 1500) {
				String templastcode = lastcode.substring(500);
				if (templastcode.startsWith(",")) {
					lastcode = "0" + templastcode;
				}
			} else {
				if (code != 0) {
					lastcode = lastcode + "," + code;
				}
			}
			// Moi dung se gui cho khach hang
			String content = "";
			String temp = "";
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String RequestTime = sdf.format(date);
			
			GetLinkManager linkMng = new GetLinkManager();			
			String result = linkMng.getLinkRing(code, now(), userid, 1000);
			
			if (result.startsWith("0")) {
				temp = result.substring(2);
				Util.logger.info("@Lay link thanh cong:Ring[request_id:"
						+ msgObject.getRequestid()
						+ "] charge online : Duong link tai ring: " + temp);

			}
			Util.logger.info("@Ring[request_id:" + msgObject.getRequestid()
					+ "] charge online : Duong link tai ring: " + temp);
			MsgObject msgObj = new MsgObject(1, serviceid, userid, commandcode,
					namecode+":"+temp, new BigDecimal(requestid),
					DateProc.createTimestamp(), mobileoperator, msgtype, 8,
					amount, content_id);
			msgObj.setChannelType(Integer.parseInt(sChannel_Type));
			msgObj.setServiceName(serviceName);
			if (MTSenderVMS.insertVMSChargeOnline(msgObj) == 1) {
				Util.logger.info("@Game[request_id:" + msgObject.getRequestid()
						+ "] charge online : Update last code:\tlast_code: "
						+ lastcode + "\tuser_id:" + userid + "\tcode:" + code
						+ "\tlink:" + temp);
				Util.CallThread(new COMObject(MLIST, lastcode, userid,
						serviceName), 1);
				Util.logger
						.info("@Game[request_id:"
								+ msgObject.getRequestid()
								+ "] charge online : Luu thong tin game vao db - icom_game: user_id:"
								+ userid + "\tcode:" + code + "\tlink:" + temp);
				Util.CallThread(new COMObject(new GameInfo(namecode, code + "",
						temp, userid)), 4);
			} else {
				Util.logger
						.info("@Game[request_id:"
								+ msgObject.getRequestid()
								+ "] charge online: Insert sang mt queue failt! \tiser_id: "
								+ userid + "\tgame code:" + code);
				Util.CallThread(new COMObject(MLIST, lastcode, userid), 2);
			}

		}
	}
	
	public static int getGameid(String user_id, String Command_code) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT gameid FROM linkgame_queue WHERE upper(user_id) = '"
				+ user_id.toUpperCase()
				+ "' AND upper(Command_code)='"
				+ Command_code.toUpperCase() + "'";

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String getLink(String user_id, String Command_code) {
		// tach lastcode

		String code = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT link FROM linkgame_queue WHERE upper(user_id) = '"
				+ user_id.toUpperCase()
				+ "' AND upper(Command_code)='"
				+ Command_code.toUpperCase() + "'";

		try {
			connection = dbpool.getConnectionGateway();
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = (String) item.elementAt(0);
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}
	private static boolean deleteLink(String user,String Commandcode) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlUpdate = "DELETE FROM linkgame_queue  WHERE user_id='" + user+"' and upper(command_code)='"+Commandcode.toUpperCase()+"'" ;
			Util.logger.info(" DELETE USER: " + sqlUpdate);
			statement = connection.prepareStatement(sqlUpdate);
			if (statement.execute()) {
				Util.logger.info("Loi xoa link " + user
						+ "trong bang link game queue");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public static int GateCateid(String command_code) {
		int cateid = 0;
		if (command_code.equalsIgnoreCase("RINGHOT")) {
			cateid = 420;
		}

		if (command_code.equalsIgnoreCase("RINGQT")) {
			cateid = 423;
		}

		if (command_code.equalsIgnoreCase("RINGTRE")) {
			cateid = 421;
		}

		if (command_code.equalsIgnoreCase("RINGTT")) {
			cateid = 447;
		}

		if (command_code.equalsIgnoreCase("RINGROCK")) {
			cateid = 448;
		}
		
		if (command_code.equalsIgnoreCase("RINGMOI")) {
			cateid = 480;
		}
		
		if (command_code.equalsIgnoreCase("RINGDOCDAO")) {
			cateid = 434;
		}
		
		if (command_code.equalsIgnoreCase("RINGTHUC")) {
			cateid = 478;
		}

		if (command_code.equalsIgnoreCase("RINGQC")) {
			cateid = 479;
		}

		return cateid;
	}
	public static String GateCateName(String command_code) {
		String catename = "";
		if (command_code.equalsIgnoreCase("RINGHOT")) {
			catename = "Nhac chuong hot";
		}

		if (command_code.equalsIgnoreCase("RINGQT")) {
			catename = "Nhac chuong quoc te";
		}

		if (command_code.equalsIgnoreCase("RINGTT")) {
			catename = "Nhac tru tinh";
		}

		if (command_code.equalsIgnoreCase("RINGROCK")) {
			catename = "Nhac rock";
		}

		if (command_code.equalsIgnoreCase("RINGTRE")) {
			catename = "Nhac tre";
		}
		
		if (command_code.equalsIgnoreCase("RINGMOI")) {
			catename = "Nhac moi nhat";
		}
		
		if (command_code.equalsIgnoreCase("RINGDOCDAO")) {
			catename = "Nhac doc dao";
		}
		
		if (command_code.equalsIgnoreCase("RINGTHUC")) {
			catename = "Nhac am thanh thuc";
		}

		if (command_code.equalsIgnoreCase("RINGQC")) {
			catename = "Nhac quang cao";
		}
		return catename;
	}
	public static int getCodeStore(int CateID, String lastcode) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [RingtoneID]  FROM [dbo].[Ringtone]  where CateID ="
				+ CateID
				+ " and [RingtoneID]  not in ("
				+ lastcode
				+ ") order by newid()";

		try {
			connection = dbpool.getConnection("store");
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info("DBUtil.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}

	public static double ParserDouble(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public static int insertMtqueue(String User_ID, String Message,
			String Service_ID, String Command_Code, String Message_Type,
			String Request_ID, String Total_Message, String Message_Index,
			String IsMore, String Content_Type, String Operator) {
		int r = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = (Connection) dbpool.getConnection("s2mplus");

		PreparedStatement stmt = null;
		if (connection != null) {
			try {
				String strQuery = "INSERT INTO mt_queue (USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, "
						+ " INFO, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS)"
						+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
				stmt = connection.prepareStatement(strQuery);
				stmt.setString(1, User_ID);
				stmt.setString(2, Service_ID);
				stmt.setString(3, Operator);
				stmt.setString(4, Command_Code);
				stmt.setDouble(5, ParserDouble(Content_Type));
				stmt.setString(6, Message);
				stmt.setDouble(7, ParserDouble(Message_Type));
				stmt.setString(8, Request_ID);
				stmt.setString(9, Message_Index);
				stmt.setDouble(10, ParserDouble(Total_Message));
				r = (stmt.executeUpdate() == 1 ? 0 : 1);
			} catch (SQLException e) {
				Util.logger.info("insertSendQueue Failed! " + e);
				r = 1;
			} finally {
				try {
					stmt.close();
					connection.close();
				} catch (SQLException ex) {
				}
			}
		} else {
			Util.logger.info("insertSendQueue Connection Null!");
			r = 1;
		}
		return r;
	}
	
	class SubThreadDeliveryRing extends Thread {

		QueueListSendMng queue = null;
		LinkQueueMng linkQueue = null;
		int numberThread = 0;

		String serviceName = "";

		public SubThreadDeliveryRing(String _serviceName, int _numberThread) {
			queue = QueueListSendMng.getInstance();
			linkQueue = LinkQueueMng.getInstance();
			serviceName = _serviceName;
			numberThread = _numberThread;
		}

		public void run() {

			TitleMng titleMng = TitleMng.getInstance();
			
			int count = 0;
			while (Sender.processData) {
								
				ListSendObj obj = queue.getObjResponseMT(serviceName);
				
				if(obj == null){
					linkQueue.removeAllLink(serviceName);
					
					if(numberThread == 0){
						Util.logger.info("DELIVERY RING: "+ serviceName +" FINISH!!!");
						
						// cap nhat tinh trang
						String sqlUpdate = "update services set result= "
								+ Constants.DELIVER_OK
								+ ", lasttime=current_timestamp() " 
								+ "where services = '" + serviceName + "'";
						DBUtil.executeSQL("gateway", sqlUpdate);
					}
					break;
					
				}
				
				queue.updateStatusMT(obj.getId(), 1);
				
				String temp = linkQueue.getLink(obj.getCommandCode(), obj.getUserId());
				if (temp.trim().equals("")) {
					
					GetLinkManager getLinkMng = new GetLinkManager();

					int cateId = GateCateid(obj.getCommandCode());
					int mediaId = getCodeStore(cateId, obj.getLastCode());
					String result = getLinkMng.getLinkRing(mediaId, now(), obj.getUserId(), 1000);
					
					if(result == null){
						Util.logger.error("SubThreadDeliveryRing ERROR ## link result = null; " +
								"user_id = " + obj.getUserId() 
								+ "; last_code = " + obj.getLastCode() 
								+ " ;; command_code = " + obj.getCommandCode()
								+ ";; media_id = " + mediaId);
						
						mediaId = getCodeStore(cateId, "0");
						result = getLinkMng.getLinkRing(mediaId, now(), obj.getUserId(), 1000);
						if(result == null){
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						}
					}
					
					if ((result.startsWith("0"))
							&& (!"".equalsIgnoreCase(result.substring(2)))) {
						temp = result.substring(2);

					} else {
						queue.updateStatusMT(obj.getId(), 0);
						temp = "";
					}
				}

				if (!temp.equals("")) {

					//String namecode = GateCateName(obj.getCommandCode());
					String strTitle = titleMng.getTitle(obj.getCommandCode());
					String info = strTitle + ":" + temp;
					
					if(insertMtqueue(obj.getUserId(), info, "9209", obj
							.getCommandCode(), obj.getMessageType() + "", obj
							.getRequestId(), "1", "1", "0", "8", "VMS") == 0){
						
					}

				}
				
				count = count +1;
				if(count == 100){
					try {
						count = 0;
						Thread.sleep(100);
					} catch (Exception e) {
						
					}
				}
			}
		}
	}
	
}
