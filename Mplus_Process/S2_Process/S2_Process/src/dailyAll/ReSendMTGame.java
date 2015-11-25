package dailyAll;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.DBUpdate;
import icom.common.DBUtil;
import icom.common.TableConst;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import daily.GetLinkManager;
import daily.ListSendObj;
import daily.TitleMng;
import sub.DeliveryManager;

public class ReSendMTGame extends DeliveryManager{

	public String CLASSNAME = "dailyResend.ReSendMTGame";
	public String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
	String mtqueue_gamering = "1";
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
			String DBCONTENT = Util.getStringfromHashMap(_option,
					"dbcontentwp", "wap");

			// Nhom se lay thong tin VMS
			String GID = Util.getStringfromHashMap(_option, "gid", "21");

			// The loai se lay
			String typehd = "image";
			typehd = Util.getStringfromHashMap(_option, "typehd", "image");
			// kieu tra mt 1:tra truc tiep, insert mt_queue, 2: gian tiep ban
			// qua ws
			mtqueue_gamering = Util.getStringfromHashMap(_option,
					"mtqueue_gamering", mtqueue_gamering);
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
			// TODO: PhuongDT
			// String code = "A";

			String sSplit = Constants._prop.getProperty("numbermt_queue", "4");

			SendMT2UsersHasCharged(ssid, DBCONTENT, serviceName, MLIST, type,
					CLASSNAME, notcharge, INFO_ID, sSplit, typehd, GID);


		} catch (Exception e) {
			Util.logger.printStackTrace(e);			
		}

		return null;
	}
	
	private void SendMT2UsersHasCharged(String ssid, String DBCONTENT,
			String serviceName, String MLIST, String type, String CLASSNAME,
			int notcharge, String INFO_ID, String sSplit, String typehd,
			String GID) throws Exception {
		
		GetLinkManager getLinkMng = new GetLinkManager();
		TitleMng titleMng = TitleMng.getInstance();
		DBSelect dbSelect = new DBSelect();
		DBUpdate dbUpdate = new DBUpdate();
		
		int cateId = getCateid(serviceName);
		String userId = "";
		double price = 10000;
		

		ArrayList<ListSendObj> arrListResend = 
			dbSelect.getArrListSendReCharge(serviceName); 
						
		for(int i = 0;i<arrListResend.size();i++){
			if(!Sender.getData) break;
			
			ListSendObj obj = arrListResend.get(i);
			
			String linkGame = getLinkMng.getLinkGame(cateId,-1, now(), userId, price);
			String strTitle = titleMng.getTitle(obj.getCommandCode());
			String info = strTitle + ":" + linkGame;

			int check = insertMtqueueVMS(obj.getUserId(), info, "9209", obj.getCommandCode(), 
					obj.getMessageType() + "", obj.getRequestId(), 
					"1", "1", "0", "8", "VMS");
			
			if(check == 1){
				dbUpdate.updateStatusMT(obj.getId(), 1, TableConst.ListSendReCharge);
			}
			
		}
		
	}
	
	public int getCateid(String command_code) {
		int cateid = 0;
		if (command_code.equalsIgnoreCase("GAMEHD")) {
			cateid = 391;
		}
		if (command_code.equalsIgnoreCase("GAMECT")) {
			cateid = 392;
		}
		if (command_code.equalsIgnoreCase("GAMETT")) {
			cateid = 393;
		}
		if (command_code.equalsIgnoreCase("GAMEDV")) {
			cateid = 394;
		}
		if (command_code.equalsIgnoreCase("GAMEHH")) {
			cateid = 436;
		}
		if (command_code.equalsIgnoreCase("GAMEHOT")) {
			cateid = 435;
		}
		if (command_code.equalsIgnoreCase("GAMENM")) {
			cateid = 396;
		}
		if (command_code.equalsIgnoreCase("GAMETN")) {
			cateid = 437;
		}
		if (command_code.equalsIgnoreCase("GAMEVN")) {
			cateid = 397;
		}
		return cateid;
	}
	
	public  String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
	
	public int insertMtqueueVMS(String User_ID, String Message,
			String Service_ID, String Command_Code, String Message_Type,
			String Request_ID, String Total_Message, String Message_Index,
			String IsMore, String Content_Type, String Operator) {
		
		int r = -1;
		
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
				
				r = stmt.executeUpdate();
				
			} catch (SQLException e) {
				Util.logger.info("insertSendQueue Failed! " + e);
			} finally {
				try {
					stmt.close();
					connection.close();
				} catch (SQLException ex) {
				}
			}
		} else {
			Util.logger.info("insertSendQueue Connection Null!");
		}
		return r;
	}
	
	public double ParserDouble(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}

}
