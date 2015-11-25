package promotion;

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

public class SendPromoDaily extends Thread {
	
	
	public void run(){
		
		PromoCommon commObj = new PromoCommon();
		Boolean isTest = commObj.isTest();
		
		String[] arrPhoneTest = null;
		
		if(isTest){
			arrPhoneTest = commObj.getPhoneTest();
			Util.logger.info("DANG CHAY HE THONG TEST GAME IPHONE 4");
		}
		
		
		while(Sender.getData){
			
			if(!PromoProcess.isPromo){
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
				
			}
			
			
			ArrayList<ListSendObj> arrListSend = 
				this.getArrResponseMT(commObj.promoService);
			
			for(int i = 0;i<arrListSend.size();i++){
				
				if(!Sender.getData) break;
				
				ListSendObj obj = arrListSend.get(i);
				
				// Neu he thong dang chay test
				if(arrPhoneTest != null){
					
					if(!commObj.isPhoneTest(obj.getUserId(), arrPhoneTest)){
						updateSendPromo(obj.getId(), 1);
						continue;
					}
					
				}
								
				ArrayList<PromotionCodeObj> arrCode = commObj.getPromoCode(5);
				
				if(arrCode.size() <5) break;
				
				for(int k = 0;k<arrCode.size();k++){
					
					commObj.updateIsUsed(arrCode.get(k).getId(), 1);
					
				}
				
				String info = getInfo(arrCode);
												
				insertMtqueue(obj.getUserId(), info, "9209", obj.getCommandCode(), 
						obj.getMessageType() + "", obj.getRequestId(), 
						"1", "1", "0", "0", "VMS");
				
				updateSendPromo(obj.getId(), 1);
				
				for(int k = 0;k<arrCode.size();k++){
					
					commObj.insertUserCode(obj.getUserId(), 
							arrCode.get(k).getPromotionCode(),
							obj.getCommandCode(),1);
					
				}
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	private ArrayList<ListSendObj> getArrResponseMT(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where  upper(command_code)  like '" + serviceName +"%' AND STATUS_MT = 1 AND IS_SEND_PROMOCODE = 0 limit 100 ";

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
			Util.logger.error("SendPromoDaily - getArrResponseMT. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("SendPromoDaily - getArrResponseMT. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	
	
	private int updateSendPromo(int id, int isSend){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE list_send SET IS_SEND_PROMOCODE = "
				+ isSend + " WHERE ID = " + id;
		
		
		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateListSendStatus@"
						+ ": uppdate Statement: UPDATE  "
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("SendPromoDaily@ updateSendPromo: UPDATE  "
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}
	
	private String getInfo(ArrayList<PromotionCodeObj> arrCode){
		
		String info = "Ma so du thuong CKTM mPlus cua ban la ";
		String infoLast = ". Tu 23/9-10/10, hay tiep tuc nhan game de"
				+ " them co hoi so huu xe Air Blade"
				+ " va 620 giai thuong khac tu MobiFone."
				+ " Chi tiet tai http://m.mplus.vn hoac goi 9244.";
		
		String strCode = arrCode.get(0).getPromotionCode();;
		
		for(int i = 1;i< arrCode.size();i++){
			strCode = strCode + ", " + arrCode.get(i).getPromotionCode();
		}
		
		return info + strCode + infoLast;
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
	
	
}


