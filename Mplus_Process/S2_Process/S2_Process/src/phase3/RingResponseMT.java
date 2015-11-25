package phase3;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import daily.GetLinkManager;
import daily.LinkQueueMng;
import daily.ListSendObj;
import daily.QueueListSendMng;

import sub.DeliveryManager;

public class RingResponseMT extends DeliveryManager {
	
	String DomainServer = Constants._prop.getProperty("domainServer", "");
	String DomainMobinet = Constants._prop.getProperty("domainmobinet", "");
	
	String CLASSNAME = "daily.RingResponseMT";
	
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
//			// cap nhat tinh trang
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
				DeliveryRingPacket thDelivery = new DeliveryRingPacket(serviceName);
				thDelivery.start();
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
	
	class DeliveryRingPacket extends Thread {

		QueueListSendMng queue = null;
		LinkQueueMng linkQueue = null;

		String serviceName = "";

		public DeliveryRingPacket(String _serviceName) {
			queue = QueueListSendMng.getInstance();
			linkQueue = LinkQueueMng.getInstance();
			serviceName = _serviceName;
		}

		public void run() {

			int count = 0;
			while (Sender.processData) {
								
				ListSendObj obj = queue.getObjResponseMT(serviceName);
				if(obj == null){
					linkQueue.removeAllLink(serviceName);
					Util.logger.info("DELIVERY RING PACKET: "+ serviceName +" FINISH!!!");
					break;
					
				}
				
				queue.updateStatusMT(obj.getId(), 1);
				
				String temp = linkQueue.getLink(obj.getCommandCode(), obj.getUserId());
				if (temp.trim().equals("")) {
					
					GetLinkManager getLinkMng = new GetLinkManager();

					int cateId = GateCateid(obj.getCommandCode());
					int mediaId = getCodeStore(cateId, obj.getLastCode());
					String result = getLinkMng.getLinkRing(mediaId, now(), obj.getUserId(), 1000);

					if ((result.startsWith("0"))
							&& (!"".equalsIgnoreCase(result.substring(2)))) {
						temp = result.substring(2);

					} else {
						queue.updateStatusMT(obj.getId(), 0);
						temp = "";
					}
				}

				if (!temp.equals("")) {

					String namecode = GateCateName(obj.getCommandCode());
					String info = namecode + ":" + temp;

					insertMtqueue(obj.getUserId(), info, obj.getUserId(), obj
							.getCommandCode(), obj.getMessageType() + "", obj
							.getRequestId(), "1", "1", "0", "8", obj.getMobileOperator());

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
