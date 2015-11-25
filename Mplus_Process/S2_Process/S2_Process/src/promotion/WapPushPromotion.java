package promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;
import daily.GetLinkManager;
import daily.LinkQueueMng;
import daily.ListSendObj;
import daily.QueueListSendMng;

/**
 * Send MT Promotion for WAPPUSH.
 * @author DanND
 * @date 2012-01-09
 */

public class WapPushPromotion extends Thread{
	
	QueueListSendMng queue = null;
	LinkQueueMng linkQueue = null;
	int numberThread = 0;
	String serviceName = "";
	private Hashtable<String, String> hServicePromotion = null;
	
	public WapPushPromotion(){
						
	}
	
	public void run(){
		
		initServicePromotion();

		while (Sender.processData) {
			
			Enumeration<String> eProm = hServicePromotion.keys();
			while(eProm.hasMoreElements()){
				
				if(!Sender.processData) break;
				
				String serviceKey = eProm.nextElement();
				String element = hServicePromotion.get(serviceKey);
				String[] arrElement = element.split(";");
				String serviceSendMT = arrElement[0];
				String strTitle = arrElement[1];
								
				ArrayList<ListSendObj> arrListSend = getArrResponsePromotion(serviceKey);
				Util.logger.info("WapPushPromotion: " + serviceKey);
				for(int i = 0;i<arrListSend.size();i++){
					
					if(!Sender.processData) break;
					ListSendObj obj = arrListSend.get(i);
					
					String promotionCode = "0";
					Boolean check = isExistInPromotion(obj.getUserId(), serviceKey);
					if(check){
						promotionCode = getPromotionCode(obj.getUserId(), serviceKey);
					}
					
					String info = strTitle
							+ getLink(obj.getUserId(), serviceKey, serviceSendMT, promotionCode, check);
					
					if(info.equals("")) continue;
					
					insertMtqueue(obj.getUserId(), info, "9209", obj.getCommandCode(), 
							obj.getMessageType() + "", obj.getRequestId(), 
							"1", "1", "0", "8", "VMS");
					
					updateStatusMT(obj.getId(), 3);
																				
				}
				
			}// end while 2
			
			try {
				Thread.sleep(5*60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}// end while 1
	
	}// End Run
	
	public int insertMtqueue(String User_ID, String Message,
			String Service_ID, String Command_Code, String Message_Type,
			String Request_ID, String Total_Message, String Message_Index,
			String IsMore, String Content_Type, String Operator) {
		
		int r = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = (Connection) dbpool.getConnection("s2mplus");
		
		Util.logger.info("WapPushPromotion:: insertMtqueue."
				+ " poolName = s2mplus; "
				+ " user_id = " + User_ID
				+ "; command_code = " + Command_Code
				+ "; content_type = " + Content_Type
				+ "; message_type = " + Message_Type
				+ ";\n info = " + Message);
		
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
	
	public double ParserDouble(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}
	
	public String now() {
		String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	
	private ArrayList<ListSendObj> getArrResponsePromotion(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where  upper(command_code)  like '" + serviceName +"%' AND STATUS_MT = 2 limit 100 ";

		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					ListSendObj obj = new ListSendObj();
					obj.setId(rs.getInt("id"));
					obj.setUserId(rs.getString("user_id"));
					obj.setServiceId(rs.getString("service_id"));
					obj.setLastCode(rs.getString("last_code"));
					obj.setCommandCode(rs.getString("command_code"));
					obj.setServiceName(rs.getString("service_name"));
					obj.setRequestId(rs.getString("request_id"));
					obj.setAmount(rs.getInt("amount"));
					obj.setContentId(rs.getString("content_id"));
					obj.setOptions(rs.getString("options"));
					obj.setChannelType(rs.getInt("channel_type"));
					arrListSend.add(obj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("WapPushPromotion - getArrResponsePromotion. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("WapPushPromotion - getArrResponsePromotion. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	
	public int updateStatusMT(int id, int status){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE list_send SET STATUS_MT = "
				+ status + " WHERE ID = " + id;
		
		
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateListSendStatus@"
						+ ": uppdate Statement: UPDATE  "
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateStatusMT@: UPDATE  "
					+ " Failed" + ex.getMessage());
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}
	
	private void initServicePromotion(){
		hServicePromotion = new Hashtable<String, String>();
		// Key - Service khuyen mai, Value - Service gui link khuyen mai
		hServicePromotion.put("GAMEHOT", "RINGHOT;Link KM - Tang ban nhac chuong HOT voi nhung giai dieu tung bung chao Nam moi, click de tai qua tang:");
		hServicePromotion.put("GAMEVN", "RINGDOCDAO;Link KM - Tang ban Album nhac Doc dao voi nhung giai dieu tung bung chao don Nam moi, click de tai qua tang:");
		hServicePromotion.put("RINGDOCDAO", "HINHNEN;Link KM - Tang ban bo hinh nen dep lung linh va tran ngap khong khi Nam moi, click de tai qua tang:");
	}
	
	private String getLink(String userId,String serviceKey,
			String serviceSendMT, String lastcode, Boolean isExistPromotion){
		
		String linkWAP = "";
		int mediaId = 0;
		GetLinkManager linkMng = new GetLinkManager();
		if(serviceSendMT.startsWith("HINH")){
			int cateId = 1026;
			mediaId = getCodeStoreImage(cateId, lastcode);
			linkWAP = linkMng.getLinkImagePromotion(mediaId,now(), userId,0);
		}
		
		if(serviceSendMT.startsWith("RING")){
			int cateId = 1024;
			mediaId = getCodeStoreRing(cateId, lastcode);
			linkWAP = linkMng.getLinkRingPromotion(mediaId, now(), userId,0); 
		}
		
		Util.logger.info("WapPushPromotion -- Link tai " + serviceSendMT
				+ " ;; userId = " + userId + " ;; LINK = " + linkWAP);
		
		if ((linkWAP.startsWith("0"))
				&& (!"".equalsIgnoreCase(linkWAP.substring(2)))) {
			linkWAP = linkWAP.substring(2);
		}else{
			linkWAP = "";
		}

		if (lastcode.length() > 3900) {
			lastcode = "0";
		} else {
			if (mediaId != 0) {
				lastcode = lastcode + "," + mediaId;
			}							
		}
		
		if (isExistPromotion) {

			String sqlUpdatePromotion = "UPDATE mlist_promotion "
					+ " SET promotion_code = '" + lastcode + "'"
					+ " WHERE user_id ='" + userId + "' AND command_code = '"
					+ serviceKey + "'";
			
			DBUtil.executeSQL("gateway", sqlUpdatePromotion);
			
		}else{
			
			insertMlistPromotion(userId, serviceKey, lastcode);
			
		}
		
		
		return linkWAP;
		
	}
	
	private int getCodeStoreRing(int CateID, String lastcode) {
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
			}

		} catch (Exception ex) {
			Util.logger.info("DBUtil.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return code;
	}
	
	private int getCodeStoreImage(int CateID, String lastcode) {
		// tach lastcode

		int code = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String query = "SELECT top 1 [ImageID] FROM [dbo].[Image] where CateID ="
				+ CateID
				+ " and [ImageID] not in ("
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
	
//	public String getPromotionCode()
	private Boolean isExistInPromotion(String userId, String commandCode){
		
		Boolean check = false;
		int count = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM mlist_promotion" +
				" WHERE user_id ='" + userId + "' AND command_code = '"
				+ commandCode + "'";
//		System.out.println("isExistAmount: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					count = rs.getInt("number");
				}
			} else {
				Util.logger
						.error("WapPushPromotion - isExistInPromotion : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("WapPushPromotion - isExistInPromotion. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("WapPushPromotion - isExistInPromotion. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
//		System.out.println("count = " + count);
		if(count > 0){
			check = true;
		}
		
		return check;
	}
	
	private String getPromotionCode(String userId, String commandCode){
		
		String promotionCode = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT promotion_code FROM mlist_promotion "
				+ " WHERE user_id ='" + userId + "' AND command_code = '"
				+ commandCode + "'";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					promotionCode = rs.getString("promotion_code");
				}
			} else {
				Util.logger
						.error("WapPushPromotion - getPromotionCode . execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("WapPushPromotion - getPromotionCode. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("WapPushPromotion - getPromotionCode. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return promotionCode;
		
	}

	private int insertMlistPromotion(String userId,String commandCode, String promotionCode){


		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO mlist_promotion(user_id,command_code, promotion_code)" +
				" VALUES(?,?,?)";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, userId);
			stmt.setString(2, commandCode);
			stmt.setString(3, promotionCode);
			
			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@insertMlistPromotion: Error@userid=" 
								+ userId);
			}

			Util.logger
					.info("@insertMlistPromotion SUCCESSFUL !!! \n @ Query = "
							+ sqlQuery);

			return 1;
		} catch (SQLException ex3) {
			Util.logger.error("@insertMlistPromotion. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@insertMlistPromotion. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	
	}
	
	
}
