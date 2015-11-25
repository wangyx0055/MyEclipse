package icom.common;

import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import daily.ListSendObj;


public class DBSelect {
	
	public ArrayList<MTPushObject> getMTPush(int status, String tableName){
		ArrayList<MTPushObject> arrMTPush = new ArrayList<MTPushObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE, LINK_RING FROM " + tableName
				+ " WHERE status = " + status + " limit 100" ;
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLinkRing(rs.getString("LINK_RING"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMTPush;
	}
	
	public MTPushObject getMTPushByUserId(String userId, String tableName){
		MTPushObject mtPushObj = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID,  COMMAND_CODE FROM " + tableName
				+ " WHERE USER_ID = '" + userId + "'";
		Util.logger.info("DB Select SQL = " + sqlQuery);
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("gateway");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLinkRing(rs.getString("LINK_RING"));
					
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Icom.common - DBSelect.getMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Icom.common - DBSelect.getMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return mtPushObj;
	}
	
	
	public void addMTPush(int status, String tableName, ArrayList<MTPushObject> arrMTPush){

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE, LINK_RING FROM " + tableName
				+ " WHERE status = " + status + " limit 100";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLinkRing(rs.getString("LINK_RING"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
	}
	
	public ArrayList<MTPushObject> getMTPushRing(int status, String tableName){
		ArrayList<MTPushObject> arrMTPush = new ArrayList<MTPushObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE, LINK_RING FROM " + tableName
				+ " WHERE status = " + status + " AND LINK_RING != 'x' limit 100";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLinkRing(rs.getString("LINK_RING"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMTPush;
	}
	
	public ArrayList<MTPushObject> getMTPushAD(int status, String tableName){
		ArrayList<MTPushObject> arrMTPush = new ArrayList<MTPushObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE FROM " + tableName
				+ " WHERE status = " + status + " limit 100";
		// 
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushAD SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushAD SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMTPush;
	}
	
	public String getOptions(String commandCode){
		String options = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "services";
		
		String sqlQuery = "SELECT services, options FROM " + tableName
				+ " WHERE services = '" + commandCode + "'";
		// System.out.println("==== DBSelect: getOptions - QUERRY = " + sqlQuery);
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
					options = rs.getString("options");
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPush SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		
		return options;
	}
	
	public String getAdvertiseContent(String commandCode){
		
		String content = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "noidung_qc";
		
		String sqlQuery = "SELECT CONTENT FROM " + tableName
				+ " WHERE DICHVU = '" + commandCode + "'" ;

		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {					
					content = rs.getString("CONTENT");
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getAdvertiseContent SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getAdvertiseContent SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return content;
	}
	
	public String getAdvertiseAlarm(String commandCode, int type){
		
		String content = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "noidung_qc";
		
		String sqlQuery = "SELECT CONTENT FROM " + tableName
				+ " WHERE DICHVU = '" + commandCode + "' AND TYPE = " + type ;

		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {					
					content = rs.getString("CONTENT");
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getAdvertiseAlarm SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getAdvertiseAlarm SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return content;
	}
	
	

	
	public ArrayList<MTPushObject> getMTPushByLink(String linkRing, String tableName){
		ArrayList<MTPushObject> arrMTPush = new ArrayList<MTPushObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE,LAST_CODE FROM " + tableName
				+ " WHERE LINK_RING = '" + linkRing + "' limit 100";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLastCode(rs.getString("LAST_CODE"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushByLink SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushByLink SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMTPush;
	}
	
	public int getCodeStore(int CateID, String lastcode) {
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

			//Util.logger.info("DBSELECT.getCode: queryStatement:" + query);

			if (result.size() > 0) {
				Vector item = (Vector) result.elementAt(0);
				code = Integer.parseInt(item.elementAt(0).toString());
				return code;
			}

		} catch (Exception ex) {
			Util.logger.info("DBSELECT.getCode: getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return code;
		} finally {
			dbpool.cleanup(connection);
		}
		return code;
	}
	
	public ArrayList<MTPushObject> getMTPushAlarm(int numberPushAlarm, String tableName){
		ArrayList<MTPushObject> arrMTPush = new ArrayList<MTPushObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlQuery = "SELECT ID, USER_ID, status, COMMAND_CODE, LINK_RING FROM " + tableName
				+ " WHERE NUMBER_MT_ALARM = " + numberPushAlarm + " limit 100";
		// System.out.println("==== getMTPush: getMTPush - QUERRY = " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection("s2mtpush");
			}
			stmt = connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					
					MTPushObject mtPushObj = new MTPushObject();
					
					mtPushObj.setID(rs.getInt("ID"));
					mtPushObj.setUserId(rs.getString("USER_ID"));
					mtPushObj.setStatus(rs.getInt("status"));
					mtPushObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtPushObj.setLinkRing(rs.getString("LINK_RING"));
					
					arrMTPush.add(mtPushObj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushAlarm SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("mtPushSMS - DBSelect.getMTPushAlarm SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMTPush;
	}
	
	
	/*
	 * get title from service name
	 */
 	public String getTitle(String serviceName){
		
		String title = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "service_title";
		
		int count = 0;
		
		String sqlQuery = "SELECT title FROM " + tableName
				+ " WHERE service_name = '" + serviceName + "'" ;

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
					title = rs.getString("title");
					count = count + 1;
					break;
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("DBSelect.getTitle SQLException: "
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DBSelect.getTitle SQLException: "
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		if(count == 0){
			Util.logger.info("@@DBSelect : Chua nhap Title cho dich vu " + serviceName);
		}
		
		return title;
	}
	
	/* 
	 * get Object from List_Send_Recharge
	 * 
	 */
	public ArrayList<ListSendObj> getArrListSendReCharge(String serviceName){
		
		ArrayList<ListSendObj> arrListSend = new ArrayList<ListSendObj>();						
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,service_name,request_id, message_type, mobile_operator "
			+ " , amount, content_id, options,channel_type from list_send_recharge "
			+ " where ( upper(command_code)  like '" + serviceName +"%' ) AND STATUS_MT = 0 limit 100 ";

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
			Util.logger.error("DBSelect - getArrListSendReCharge. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("DBSelect - getArrListSendReCharge. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrListSend;
		
	}
	
	public ArrayList<MsgObject> getResendService(){
		
		ArrayList<MsgObject> arrMsg = new ArrayList<MsgObject>();
		
		Calendar cal = Calendar.getInstance();
		int today = cal.get(Calendar.DAY_OF_WEEK);
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "SELECT	id,services,dayofweek,notcharge," +
				" options,hours,minutes,class " +
				" FROM services WHERE ( dayofweek = 'x' " +
				" OR dayofweek LIKE '%" + today +"%' ) " +
				" AND ( result = 1 ) ";

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
					
					MsgObject obj = new MsgObject();
					
					obj.setMsg_id(rs.getLong("ID"));
					obj.setServiceName(rs.getString("services"));
					obj.setCommandCode(rs.getString("services"));
					obj.setNotcharge(rs.getInt("notcharge"));
					obj.setOption(rs.getString("options"));
					obj.setHours(rs.getString("hours"));
					obj.setMinutes(rs.getString("minutes"));
					obj.setClassSendMT(rs.getString("class"));
					
					arrMsg.add(obj);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("DBSelect - getResendService. SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("DBSelect - getResendService. SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return arrMsg;
	}
	
	public String getResendClass(String serviceName){
		
		String className = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "SELECT resend_class FROM ReSend_Class " +
				" WHERE services = '" + serviceName +"'";

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
					
					className = rs.getString("resend_class");
					
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("DBSelect - getResendClass, Services = " 
					+ serviceName + ". SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("DBSelect - getResendClass, Services = " 
					+ serviceName + ". SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return className;
		
	}
	
	/**
	 * 
	 * @param tableName
	 * @param active: 0 not send charge, 1: already send charge
	 * @return
	 */
	public ArrayList<MsgObject> getMlistInfoActive(String tableName,String serviceName, int active) {
		
		Calendar now = Calendar.getInstance();
		String strToday = new java.text.SimpleDateFormat("yyyy-MM-dd").format(now
				.getTime());
		
		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT"
				+ " FROM " + tableName 
				+ " WHERE COMMAND_CODE = '" + serviceName + "' AND ACTIVE = " + active
				+ " AND DATE like '" + strToday + "%'";

		ArrayList<MsgObject> arrMlistInfo = new ArrayList<MsgObject>();
		MsgObject mlistInfo;

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
					mlistInfo = null;
					mlistInfo = new MsgObject();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserid(rs.getString("USER_ID"));
					mlistInfo.setServiceid(rs.getString("SERVICE_ID"));
					mlistInfo.setTimeSendMO(rs.getString("DATE"));
					mlistInfo.setOption(rs.getString("OPTIONS"));
//					mlistInfo.set(rs.getString("FAILURES"));
					mlistInfo.setLast_code(rs.getString("LAST_CODE"));
					mlistInfo.setTTimes(rs.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestid(rs.getBigDecimal("REQUEST_ID"));
					mlistInfo.setMsgtype(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobileoperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setServiceName(rs.getString("SERVICE"));
					mlistInfo.setCompany_id(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChannelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
//					mlistInfo.setDateRetry(rs.getString("DATE_RETRY"));

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("DBSelect - getMlistInfoActive. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DBSelect Package - getMlistInfoActive. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}
	
	/**
	 * 
	 * @param userId
	 * @return true if userId is user which is testing<br>
	 * else return false;
	 */
	public Boolean isUserTest(String userId){
		
		int numberCount = 0;
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		String sqlSelect = "SELECT COUNT(*) AS numberCount FROM mlist_user_test "
				+ " WHERE user_id = '" + userId + "'";

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
					
					numberCount = rs.getInt("numberCount");
					
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("DBSelect - isUserTest,"
					+ ". SQLException: "
					+ ex3.toString());
		} catch (Exception ex2) {
			Util.logger.error("DBSelect - isUserTest,"
					+ ". SQLException: "
					+ ex2.toString());
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		if(numberCount>0) return true;
		return false;
		
	}

}
