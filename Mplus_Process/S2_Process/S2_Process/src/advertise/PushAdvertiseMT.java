package advertise;

import icom.Constants;
import icom.DBPool;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import daily.ListSendObj;


public class PushAdvertiseMT extends Thread{
	
	public void run(){
		
		while(Sender.processData){
						
			ArrayList<AdvertiseObj> arrAdvertiseObjs = this.getAdvertiseObj();
			
			for(int i = 0;i<arrAdvertiseObjs.size();i++){
				
				AdvertiseObj adObj = arrAdvertiseObjs.get(i);
				ArrayList<ListSendObj> arrListSend = 
					this.getArrListSend(adObj.getServiceName());
				
				for(int j = 0;j<arrListSend.size();j++){
					
					ListSendObj obj = arrListSend.get(j);
					String info = adObj.getMtAdvertise();
					// send MT
					insertMtqueue(obj.getUserId(), info, "9209", obj.getCommandCode(), 
							obj.getMessageType() + "", obj.getRequestId(), 
							"1", "1", "0", "0", "VMS");
					
					updateStatusMT(obj.getId(), 2);
					
				}
				
			}
			
			arrAdvertiseObjs.removeAll(arrAdvertiseObjs);
			arrAdvertiseObjs = null;
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private ArrayList<AdvertiseObj> getAdvertiseObj(){
		
		ArrayList<AdvertiseObj> arrAdObj = new ArrayList<AdvertiseObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "SELECT t1.ID,t1.service_name, t1.mt_advertisement"
				+ " FROM service_advertisement t1,services t2"
				+ " WHERE t1.service_name = t2.services AND t2.result = "
				+ Constants.DELIVER_OK;
				
//		 System.out.println("getAdvertiseObj QUERRY: " + sqlSelect);
		 
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
					AdvertiseObj adObj = new AdvertiseObj();
					adObj.setId(rs.getInt("ID"));
					adObj.setServiceName(rs.getString("service_name"));
					adObj.setMtAdvertise(rs.getString("mt_advertisement"));
					
					arrAdObj.add(adObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("PushAdvertiseMT - getAdvertiseObj. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("PushAdvertiseMT - getAdvertiseObj. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrAdObj;
		
	}
	
	private ArrayList<ListSendObj> getArrListSend(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where command_code = '" + serviceName +"' AND STATUS_MT = 1 ";
		
//		 System.out.println("getArrListSend QUERRY: " + sqlSelect);

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
			Util.logger.error("PushAdvertiseMT - getArrListSend. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("PushAdvertiseMT - getArrListSend. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	
	private int insertMtqueue(String User_ID, String Message,
			String Service_ID, String Command_Code, String Message_Type,
			String Request_ID, String Total_Message, String Message_Index,
			String IsMore, String Content_Type, String Operator) {
		
		int r = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = (Connection) dbpool.getConnection("s2mplus");
		PreparedStatement stmt = null;
		
		Util.logger.info("Push Advertise MT: user_id = "
				+ User_ID + " ; command_code = "
				+ Command_Code + " ; info = "
				+ Message );
		
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
	
	public static double ParserDouble(Object o) {
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception ex) {
			return 0;
		}
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
			Util.logger.error("updateMlistDateRetry@: UPDATE  "
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}

	
}
