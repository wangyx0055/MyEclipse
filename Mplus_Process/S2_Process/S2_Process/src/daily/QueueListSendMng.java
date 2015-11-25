package daily;

import icom.DBPool;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

public class QueueListSendMng {
	
	// String1 = service Name -- Hash get link
	private Hashtable<String, ArrayList<ListSendObj> > hListSend = new Hashtable<String, ArrayList<ListSendObj>>();
	
	// Hash responseMT
	private Hashtable<String, ArrayList<ListSendObj>> hResponseMT = new Hashtable<String, ArrayList<ListSendObj>>();
	
	private static QueueListSendMng queue = null;
	
	private QueueListSendMng(){}
	
	public static synchronized QueueListSendMng getInstance(){
				
		if(queue == null){
			queue = new QueueListSendMng();
		}
				
		return queue;		
	}
	
	/**
	 * Get Object to get link
	 * @param serviceName
	 * @return Object to get link
	 */
	public synchronized ListSendObj getListSendObj(String serviceName){
		
		try {
			// wait
			//this.wait();
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListSendObj obj = null;
		
		ArrayList<ListSendObj> arrObj = null;
		arrObj = hListSend.get(serviceName);
		
		if(arrObj == null){
			arrObj = new ArrayList<ListSendObj>();
		}
		
		if(arrObj.size() == 0){
			arrObj = this.getArrListSendObj(serviceName);			
		}
		
		if(arrObj.size()>0){				
			obj = arrObj.get(0);
			arrObj.remove(0);
			hListSend.put(serviceName, arrObj);
		}
		
		arrObj = null;
		//this.notify();
		
		return obj;
		
	}
	
	/**
	 * Get Object to get response MT
	 * @param serviceName
	 * @return Object to response MT
	 */
	public synchronized ListSendObj getObjResponseMT(String serviceName){
		
		try {
			// wait
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListSendObj obj = null;
		
		ArrayList<ListSendObj> arrObj = null;
		arrObj = hResponseMT.get(serviceName);
		
		if(arrObj == null){
			arrObj = new ArrayList<ListSendObj>();
		}

		if (arrObj.size() == 0) {
			arrObj = this.getArrResponseMT(serviceName);			
		}

		if (arrObj.size() > 0) {
			obj = arrObj.get(0);
			arrObj.remove(0);
			hResponseMT.put(serviceName, arrObj);
		}
		
		return obj;
		
	}
	
	public synchronized ListSendObj getObjResponseMT(String serviceName, int statusMT){
		
		try {
			// wait
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListSendObj obj = null;
		
		ArrayList<ListSendObj> arrObj = null;
		arrObj = hResponseMT.get(serviceName);
		
		if(arrObj == null){
			arrObj = new ArrayList<ListSendObj>();
		}

		if (arrObj.size() == 0) {
			arrObj = this.getArrResponseMT(serviceName,statusMT);			
		}

		if (arrObj.size() > 0) {
			obj = arrObj.get(0);
			arrObj.remove(0);
			hResponseMT.put(serviceName, arrObj);
		}
		
		return obj;
		
	}
	
	private ArrayList<ListSendObj> getArrListSendObj(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where ( upper(command_code)  like '" + serviceName +"%' ) AND STATUS_GETLINK = 0 limit 100 ";

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
					obj.setMobileOperator(rs.getString("mobile_operator"));
					arrListSend.add(obj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("QueueListSendMng - getArrListSend. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("QueueListSendMng - getArrListSend. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	

	private ArrayList<ListSendObj> getArrResponseMT(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where  upper(command_code)  like '" + serviceName +"%' AND STATUS_MT = 0 limit 100 ";

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
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	
	private ArrayList<ListSendObj> getArrResponseMT(String serviceName, int statusMT){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send "
			+ " where  upper(command_code)  like '" + serviceName +"%' " +
				"AND STATUS_MT = " + statusMT + " limit 100 ";

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
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
	}
	
	public int getNumberResponseMT(String serviceName){
		
		int numberReturn = 0;
							
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select count(*) "
			+ " where  upper(command_code)  like '" + serviceName +"%' AND STATUS_MT = 0 ";

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
					numberReturn = rs.getInt(1);
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("QueueListSendMng - getArrResponseMT. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return numberReturn;
	}
	
	
	public int updateListSendStatus(int id, int status){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE list_send SET STATUS_GETLINK = "
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
	
	public int updateRetry(int id){

		int ireturn = 1;
			
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE list_send SET GETLINK_RETRY = GETLINK_RETRY + 1"
				 + " WHERE ID = " + id;

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("QueueListSendMng.updateRetry@"
						+ ": uppdate Statement: UPDATE  "
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("QueueListSendMng.updateRetry@: UPDATE  "
					+ " Failed; SQL STATEMENT = " + sqlUpdate);
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		
		return ireturn;

	}
	
	public int insertMTQueue(ListSendObj obj, String info){
		int r = 0;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		connection = (Connection) dbpool.getConnection("s2mplus");

		PreparedStatement stmt = null;
		if (connection != null) {
			try {
				String strQuery = "INSERT INTO mt_queue (USER_ID, SERVICE_ID, " +
						"	MOBILE_OPERATOR, " +
						" COMMAND_CODE, CONTENT_TYPE, "
						+ " INFO, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, CHANNEL_TYPE)"
						+ " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
				stmt = connection.prepareStatement(strQuery);
				stmt.setString(1, obj.getUserId());
				stmt.setString(2, obj.getServiceId());
				stmt.setString(3, obj.getMobileOperator());
				stmt.setString(4, obj.getCommandCode());
				stmt.setDouble(5, 0);
				stmt.setString(6, info);
				stmt.setDouble(7, ParserDouble(obj.getMessageType()));
				stmt.setString(8, obj.getRequestId());
				stmt.setString(9, obj.getMessageId()+"");
				stmt.setDouble(10, 1);
				stmt.setInt(11, obj.getChannelType());
				
				r = stmt.executeUpdate();
			} catch (SQLException e) {
				Util.logger.info("insertSendQueue Failed! " + e);
				r = -1;
			} finally {
				try {
					stmt.close();
					connection.close();
				} catch (SQLException ex) {
				}
			}
		} else {
			Util.logger.info("insertSendQueue Connection Null!");
			r = -1;
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
