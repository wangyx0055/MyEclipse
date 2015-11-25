package multiService;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class LoadMultiService {
	
	public static String icommplus = "s2vmsicom";
	public static String VMSMPLUS_POOL = "s2mplus";
	public static String ICOM_MULTI = "ICOM MULTI";
	public static String tblMapMulti = "map_multi_service";
	
	public ArrayList<MsgObject> getMO() {

		ArrayList<MsgObject> arrMO = null;
		MsgObject msgObject = null;
		DBPool dbpool = new DBPool();

		String serviceId = "";
		String userId = "";
		String info = "";
		Timestamp tTime;
		String operator = "";
		BigDecimal requestId = new BigDecimal(-1);

		String tblName = "mo_queue_multi";

		String SQL_LOAD = "select * from " + tblName + " limit 100 ";

		arrMO = new ArrayList<MsgObject>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(SQL_LOAD,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {

				rs = stmt.getResultSet();

				while (rs.next() && Sender.getData) {

					serviceId = rs.getString("SERVICE_ID");
					userId = rs.getString("USER_ID");
					info = rs.getString("INFO").toUpperCase();
					tTime = new Timestamp(System.currentTimeMillis());
					operator = rs.getString("MOBILE_OPERATOR");
					//requestId = rs.getBigDecimal("REQUEST_ID");
					requestId = rs.getBigDecimal("ID");

					msgObject = new MsgObject(0, serviceId, userId, "INV",
							info, requestId, tTime, operator, 0, 0);
					
					msgObject.setMoId(rs.getBigDecimal("REQUEST_ID"));
					
					msgObject.setMsg_id(rs.getLong("ID"));
					msgObject.setUserid(rs.getString("USER_ID"));
					msgObject.setServiceid(rs.getString("SERVICE_ID"));
					msgObject
							.setMobileoperator(rs.getString("MOBILE_OPERATOR"));
					msgObject.setKeyword(rs.getString("COMMAND_CODE"));
					msgObject.setCommandCode(rs.getString("COMMAND_CODE"));
					msgObject.setUsertext(rs.getString("INFO").toUpperCase());
					msgObject.setRequestid(requestId);
					msgObject.setChannelType(rs.getInt("CHANNEL_TYPE"));
					msgObject.setObjtype(0);					
					msgObject.setMultiService(1);
					
					try {
						rs.deleteRow();
						arrMO.add(msgObject);

					} catch (SQLException ex) {
						Util.logger.error("{Load MO}{Ex:" + ex.toString());
					} catch (Exception ex1) {
						Util.logger.error("Load MO. ex1:" + ex1.toString());
						// queue.remove();
						Util.logger.printStackTrace(ex1);
					}
				}

			}
		} catch (SQLException ex3) {
			Util.logger.error("Load MO Multi Service. SQLException:" + ex3.toString());
			DBUtil.Alert("Process.LoadMO Multi Service ", "LoadMO.SQLException", "major",
					"LoadMO.SQLException:" + ex3.toString(), "S2Admin");
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO Multi Service Exception:" + ex2.toString());
			DBUtil.Alert("Process.LoadMO Multi Service", "LoadMO.Exception", "major",
					"LoadMO.Exception:" + ex2.toString(), "processAdmin");
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return arrMO;
	}
	
	
	
	public ArrayList<MTQueueObj> getMTMulti(){
		
		ArrayList<MTQueueObj> arrMTMulti = new ArrayList<MTQueueObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "mt_queue_multi";
		
		String sqlQuery = " SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, " +
				" COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, " +
				" PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, " +
				" TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, AMOUNT, " +
				" CHANNEL_TYPE, MO_ID, NUMBER_SERVICE " +
				" FROM " + tableName
				+ " limit 100 ";
		
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
					MTQueueObj mtObj = new MTQueueObj();
					
					mtObj.setID(rs.getInt("ID"));
					mtObj.setUserId(rs.getString("USER_ID"));
					mtObj.setServiceId(rs.getString("SERVICE_ID"));
					mtObj.setMobileOperator(rs.getString("MOBILE_OPERATOR"));
					mtObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtObj.setContentType(rs.getInt("CONTENT_TYPE"));
					mtObj.setInfo(rs.getString("INFO"));
					mtObj.setProcessResult(rs.getInt("PROCESS_RESULT"));
					mtObj.setMsgType(rs.getInt("MESSAGE_TYPE"));
					mtObj.setRequestId(rs.getString("REQUEST_ID"));
					mtObj.setMsgId(rs.getString("MESSAGE_ID"));
					mtObj.setTotalSegment(rs.getInt("TOTAL_SEGMENTS"));
					mtObj.setRetrieNumber(rs.getInt("RETRIES_NUM"));
					mtObj.setInsertDate(rs.getString("INSERT_DATE"));
					mtObj.setNotes(rs.getString("NOTES"));
					mtObj.setAmount(rs.getInt("AMOUNT"));
					mtObj.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mtObj.setMoId(rs.getInt("MO_ID"));
					mtObj.setNumberService(rs.getInt("NUMBER_SERVICE"));
					
					arrMTMulti.add(mtObj);

				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
				
		return arrMTMulti;
	}
	
public ArrayList<MTQueueObj> getMTMultiByMOId(int moId){
		
		ArrayList<MTQueueObj> arrMTMulti = new ArrayList<MTQueueObj>();
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "mt_queue_multi";
		
		String sqlQuery = " SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR, " +
				" COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, " +
				" PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, " +
				" TOTAL_SEGMENTS, RETRIES_NUM, INSERT_DATE, NOTES, AMOUNT, " +
				" CHANNEL_TYPE, MO_ID, NUMBER_SERVICE " +
				" FROM " + tableName
				+ " WHERE MO_ID = " + moId;
		
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
					MTQueueObj mtObj = new MTQueueObj();
					
					mtObj.setID(rs.getInt("ID"));
					mtObj.setUserId(rs.getString("USER_ID"));
					mtObj.setServiceId(rs.getString("SERVICE_ID"));
					mtObj.setMobileOperator(rs.getString("MOBILE_OPERATOR"));
					mtObj.setCommandCode(rs.getString("COMMAND_CODE"));
					mtObj.setContentType(rs.getInt("CONTENT_TYPE"));
					mtObj.setInfo(rs.getString("INFO"));
					mtObj.setProcessResult(rs.getInt("PROCESS_RESULT"));
					mtObj.setMsgType(rs.getInt("MESSAGE_TYPE"));
					mtObj.setRequestId(rs.getString("REQUEST_ID"));
					mtObj.setMsgId(rs.getString("MESSAGE_ID"));
					mtObj.setTotalSegment(rs.getInt("TOTAL_SEGMENTS"));
					mtObj.setRetrieNumber(rs.getInt("RETRIES_NUM"));
					mtObj.setInsertDate(rs.getString("INSERT_DATE"));
					mtObj.setNotes(rs.getString("NOTES"));
					mtObj.setAmount(rs.getInt("AMOUNT"));
					mtObj.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mtObj.setMoId(rs.getInt("MO_ID"));
					mtObj.setNumberService(rs.getInt("NUMBER_SERVICE"));
					
					arrMTMulti.add(mtObj);

				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Load MO Multi Service *** getMTMultiByMOId.SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO Multi Service *** getMTMultiByMOId.SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
				
		return arrMTMulti;
	}
	
	
	public String getOptionsICOM(String commandCode){
		String options = "";
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String tableName = "services";
		
		String sqlQuery = "SELECT services, options FROM " + tableName
				+ " WHERE services = '" + commandCode + "'";
		
		try {
			if (connection == null) {
				connection = dbpool.getConnection(icommplus);
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
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		
		return options;
	}
	
	public String getOptionsVMS(String commandCode){
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
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Load MO Multi Service SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		
		return options;
	}
	
	public int insertToMlistICOM(String mlistTable, MsgObject msgObj){

		if(this.searchInMlistICOM(mlistTable, msgObj.getUserid(), msgObj.getCommandCode(), msgObj.getServiceid()) == 1){
			return 1;
		}
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ mlistTable
				+ " ( USER_ID, SERVICE_ID, DATE, OPTIONS, FAILURES, LAST_CODE, "
				+ "AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, MESSAGE_TYPE, MOBILE_OPERATOR, "
				+ "MT_COUNT, MT_FREE, DURATION, AMOUNT, CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, "
				+ "CHANNEL_TYPE, REG_COUNT) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnection(icommplus);
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, msgObj.getUserid());
			stmt.setString(2, msgObj.getServiceid());
			stmt.setString(3, Util.getCurrentDate());
			stmt.setString(4, msgObj.getOption());
			stmt.setString(5, "0");
			stmt.setString(6, msgObj.getLast_code());
			stmt.setString(7, Util.getCurrentDate());
			stmt.setString(8, msgObj.getCommandCode());
			stmt.setString(9, String.valueOf(msgObj.getRequestid()));
			stmt.setInt(10, msgObj.getMsgtype());
			stmt.setString(11, msgObj.getMobileoperator());
			stmt.setInt(12, 0);
			stmt.setInt(13, 0);
			stmt.setInt(14, 0);
			stmt.setInt(15, 0);
			stmt.setInt(16, msgObj.getContentId());
			stmt.setString(17, msgObj.getServiceName());
			stmt.setString(18, msgObj.getCompany_id());
			stmt.setInt(19, 0);
			stmt.setInt(20, 0);
			stmt.setInt(21, 0);

			if (stmt.executeUpdate() != 1) {
				
				return -1;
			}
			return 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("MultiService - insertMlist. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger
					.error("MultiService - insertMlist. SQLException:. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}
	
	public int insertToMlistVMS(String mlistTable, MsgObject msgObj){
		
		if(this.searchInMlistVMS(mlistTable, msgObj.getUserid(), msgObj.getCommandCode(), msgObj.getServiceid()) == 1){
			return 1;
		}
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ mlistTable
				+ " ( USER_ID, SERVICE_ID, DATE, OPTIONS, FAILURES, LAST_CODE, "
				+ "AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, MESSAGE_TYPE, MOBILE_OPERATOR, "
				+ "MT_COUNT, MT_FREE, DURATION, AMOUNT, CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, "
				+ "CHANNEL_TYPE, REG_COUNT) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, msgObj.getUserid());
			stmt.setString(2, msgObj.getServiceid());
			stmt.setString(3, Util.getCurrentDate());
			stmt.setString(4, msgObj.getOption());
			stmt.setString(5, "0");
			stmt.setString(6, msgObj.getLast_code());
			stmt.setString(7, Util.getCurrentDate());
			stmt.setString(8, msgObj.getCommandCode());
			stmt.setString(9, String.valueOf(msgObj.getRequestid()));
			stmt.setInt(10, msgObj.getMsgtype());
			stmt.setString(11, msgObj.getMobileoperator());
			stmt.setInt(12, 0);
			stmt.setInt(13, 0);
			stmt.setInt(14, 0);
			stmt.setInt(15, 0);
			stmt.setInt(16, msgObj.getContentId());
			stmt.setString(17, msgObj.getServiceName());
			stmt.setString(18, msgObj.getCompany_id());
			stmt.setInt(19, 0);
			stmt.setInt(20, 0);
			stmt.setInt(21, 0);

			if (stmt.executeUpdate() != 1) {
				
				return -1;
			}
			return 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("MultiService - insertMlist. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger
					.error("MultiService - insertMlist. SQLException:. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}
	
	public int InsertMlistCancel2MlistVMS(MsgObject msg, String tblName) {

		String sqlQuery = "insert into "
				+ tblName
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,DATE_RETRY)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ ",AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,0,CHANNEL_TYPE,REG_COUNT +1,DATE_RETRY from "
				+ tblName + "_cancel WHERE USER_ID='"
				+ msg.getUserid() + "' and upper(COMMAND_CODE)='"
				+ msg.getCommandCode().toUpperCase() + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// System.out.println("InsertMlistCancel2Mlist QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() < 0) {
				return -1;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - InsertMlistCancel2Mlist. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - InsertMlistCancel2Mlist. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return 1;

	}
	
	public int InsertMlistCancel2MlistICOM(MsgObject msg,String tblName) {

		String sqlQuery = "insert into "
				+ tblName
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,DATE_RETRY)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ ",AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,0,CHANNEL_TYPE,REG_COUNT +1,DATE_RETRY from "
				+ tblName + "_cancel WHERE USER_ID='"
				+ msg.getUserid() + "' and upper(COMMAND_CODE)='"
				+ msg.getCommandCode().toUpperCase() + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// System.out.println("InsertMlistCancel2Mlist QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection(icommplus);
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() < 0) {
				return -1;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - InsertMlistCancel2Mlist. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - InsertMlistCancel2Mlist. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return 1;

	}
	
	public int insertToMOLog(MsgObject msgObj){
		
		int iReturn = 1;
		
		String tblMOLog = "mo";
		Calendar now = Calendar.getInstance();
		tblMOLog = tblMOLog +(new java.text.SimpleDateFormat("yyyyMM").format(now.getTime()));
		
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO " + tblMOLog + " ( USER_ID, SERVICE_ID," +
				" MOBILE_OPERATOR, COMMAND_CODE, INFO, RECEIVE_DATE, RESPONDED, REQUEST_ID, CHANNEL_TYPE) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?)";
		
		
		Util.logger.info("INSERT INTO MO LOG: Table Log = "
				+ tblMOLog + "### \t USER_ID =  " + msgObj.getUserid()
				+ " ; \t Service_Id = " + msgObj.getServiceid() 
				+ "; \t Chanel_Type = " + msgObj.getChannelType()
				+ "; \t INFO = " + msgObj.getUsertext());
		
		
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1,msgObj.getUserid());
			stmt.setString(2, msgObj.getServiceid());
			stmt.setString(3, msgObj.getMobileoperator());
			stmt.setString(4, msgObj.getCommandCode());
			stmt.setString(5, msgObj.getUsertext());
			stmt.setString(6, Util.getCurrentDate());
			stmt.setString(7, "0");
			stmt.setString(8, String.valueOf(msgObj.getRequestid()));
			stmt.setInt(9, msgObj.getChannelType());

			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			} else {
				iReturn = -1;
				Util.logger
						.error("MULTI-SERVICE @ INSERT INTO MO LOG : execute Error!!");
			}
		} catch (SQLException ex3) {
			iReturn = -1;
			Util.logger
					.error("MULTI-SERVICE @ INSERT INTO MO LOG. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			iReturn = -1;
			Util.logger
					.error("MULTI-SERVICE @ INSERT INTO MO LOG. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
	}
	
	public int deleteInMlistVMS(String tableMlist, String userId,
			String commandCode, String serviceId) {

		String sqlQuery = "DELETE FROM " + tableMlist + " WHERE USER_ID = "
				+ userId + " AND COMMAND_CODE = '" + commandCode
				+ "' AND SERVICE_ID = '" + serviceId + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// System.out.println("deleteChargeResultByID QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() < 0) {
				return -1;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - deleteChargeResultByID9. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - deleteChargeResultByID9. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return 1;
	}

	public int deleteInMlistICOM(String tableMlist, String userId,
			String commandCode, String serviceId) {

		String sqlQuery = "DELETE FROM " + tableMlist + " WHERE USER_ID = "
				+ userId + " AND COMMAND_CODE = '" + commandCode
				+ "' AND SERVICE_ID = '" + serviceId + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// System.out.println("deleteChargeResultByID QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection(icommplus);
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() < 0) {
				return -1;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - deleteChargeResultByID9. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - deleteChargeResultByID9. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return 1;
	}
	
	public int sendMT(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@sendMT\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@sendMT\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@sendMT: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	
	public int searchInMlistVMS(String tableMlist, String userId,
			String commandCode, String serviceId) {
		
		int iReturn = -1;
		
		String sqlQuery = "Select * FROM " + tableMlist + " WHERE USER_ID = "
				+ userId + " AND COMMAND_CODE = '" + commandCode
				+ "' AND SERVICE_ID = '" + serviceId + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		// System.out.println("deleteChargeResultByID QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			if(stmt.execute()){
				rs = stmt.getResultSet();
				while(rs.next()){
					iReturn = 1;
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("searchInMlistVMS. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("searchInMlistVMS. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int searchInMlistICOM(String tableMlist, String userId,
			String commandCode, String serviceId) {
		
		int iReturn = -1;
		
		String sqlQuery = "Select * FROM " + tableMlist + " WHERE USER_ID = "
				+ userId + " AND COMMAND_CODE = '" + commandCode
				+ "' AND SERVICE_ID = '" + serviceId + "'";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		
		// System.out.println("deleteChargeResultByID QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnection(icommplus);
			}
			stmt = connection.prepareStatement(sqlQuery);
			
			if(stmt.execute()){
				rs = stmt.getResultSet();
				while(rs.next()){
					iReturn = 1;
				}
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("searchInMlistVMS. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("searchInMlistVMS. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;
	}
	
	public int insertMTQueue(MTQueueObj mtObj, String tblName){
		
		int iReturn = 1;
		
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("Multi Serivice insertMTQueue  ###@## insertMTQueue \tuser_id:"
				+ mtObj.getUserId() + "\tservice_id:"
				+ mtObj.getServiceId() + "\tuser_Info:"
				+ mtObj.getInfo() + "\t message_Type:"
				+ mtObj.getMsgType() + "\t RequestID:"
				+ mtObj.getRequestId().toString() + "\t Chanel_Type:"
				+ mtObj.getChanelType() + "\tcommand_code:"
				+ mtObj.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("Multi Serivice insertMTQueue  ###@## insertMTQueue: Error connection == null"
								+ mtObj.getUserId()
								+ "\tTO"
								+ mtObj.getServiceId()
								+ "\t"
								+ mtObj.getInfo()
								+ "\trequest_id:"
								+ mtObj.getRequestId().toString());
				return -1;
			}

			sqlString = "INSERT INTO "
					+ tblName
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, "
					+ "CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, mtObj.getUserId());
			statement.setString(2, mtObj.getServiceId());
			statement.setString(3, mtObj.getMobileOperator());
			statement.setString(4, mtObj.getCommandCode());
			statement.setInt(5, mtObj.getContentType());
			statement.setString(6, mtObj.getInfo());
			statement.setInt(7, mtObj.getMsgType());
			statement.setString(8, mtObj.getRequestId());
			statement.setInt(9, mtObj.getChanelType());

			Util.logger.info("Multi Serivice insertMTQueue");
			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("Multi Serivice insertMTQueue  ###@## insertMTQueue: Error@ tableName = "
						+ tblName+ " @userid="
						+ mtObj.getUserId() + "@service_id="
						+ mtObj.getServiceId() + "@user_text="
						+ mtObj.getInfo() + "@message_type="
						+ mtObj.getMsgType() + "@request_id="
						+ mtObj.getRequestId().toString()
						+ "@channel_type=" + mtObj.getChanelType());
				iReturn = -1;
			}else{
				Util.logger.info("Multi Serivice insertMTQueue  ###@## insertMTQueue Success; " +
						"@ tableName = " + tblName
						+ "@userid="+ mtObj.getUserId() + "@service_id="
						+ mtObj.getServiceId() + "@user_text="
						+ mtObj.getInfo() + "@message_type="
						+ mtObj.getMsgType() + "@request_id="
						+ mtObj.getRequestId().toString()
						+ "@channel_type=" + mtObj.getChanelType());
			}
			
		} catch (SQLException e) {
			Util.logger.crisis("Multi Serivice insertMTQueue  ###@## insertMTQueue: Error@ tableName = "
					+ tblName+ " @userid="
					+ mtObj.getUserId() + "@service_id="
					+ mtObj.getServiceId() + "@user_text="
					+ mtObj.getInfo() + "@message_type="
					+ mtObj.getMsgType() + "@request_id="
					+ mtObj.getRequestId().toString() + "@channel_type="
					+ mtObj.getChanelType());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("Multi Serivice insertMTQueue  ###@## insertMTQueue: Error@ tableName = "
					+ tblName+ " @userid="
					+ mtObj.getUserId() + "@service_id="
					+ mtObj.getServiceId() + "@user_text="
					+ mtObj.getInfo() + "@message_type="
					+ mtObj.getMsgType() + "@request_id="
					+ mtObj.getRequestId().toString() + "@channel_type="
					+ mtObj.getChanelType());
			iReturn = -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		
		return iReturn;
	}
	
	
	public int deleteByID(int ID, String tableName) {

		String sqlQuery = "DELETE FROM " + tableName + " WHERE ID = "
				+ ID;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// System.out.println("deleteChargeResultByID QUERRY: " + sqlQuery);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() < 0) {
				return -1;
			}

		} catch (SQLException ex3) {
			Util.logger
					.error("LoadMultiService - deleteByID. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("LoadMultiService - deleteByID. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return 1;

	}
	
	public ArrayList<MapMultiSerivce> getMapMultiService() {

		ArrayList<MapMultiSerivce> arrMap = null;
		DBPool dbpool = new DBPool();
		
		String SQL_LOAD = "select * from " + tblMapMulti + " limit 100 ";

		arrMap = new ArrayList<MapMultiSerivce>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(SQL_LOAD,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			if (stmt.execute()) {

				rs = stmt.getResultSet();

				while (rs.next() && Sender.getData) {
					
					MapMultiSerivce mapObj = new MapMultiSerivce();
					
					mapObj.setID(rs.getInt("ID"));
					mapObj.setUserId(rs.getString("USER_ID"));
					mapObj.setMoId(rs.getInt("MO_ID"));
					mapObj.setNumberService(rs.getInt("NUMBER_SERVICE"));
					mapObj.setKeyword(rs.getString("KEYWORD"));
					
					arrMap.add(mapObj);

				}

			}
		} catch (SQLException ex3) {
			Util.logger.error("getMapMultiService. SQLException:" + ex3.toString());
			DBUtil.Alert("getMapMultiService ", "LoadMO.SQLException", "major",
					"getMapMultiService.SQLException:" + ex3.toString(), "S2Admin");
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("getMapMultiService Exception:" + ex2.toString());
			DBUtil.Alert("getMapMultiService ", "LoadMO.Exception", "major",
					"getMapMultiService.Exception:" + ex2.toString(), "processAdmin");
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		return arrMap;
	}
	
	public int insertMapMulti(String userId,int moId, int numberService, String keyword ){
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("LoadMultiService@insertMapMulti: Error connection == null");
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ tblMapMulti
					+ "( USER_ID, MO_ID, NUMBER_SERVICE, KEYWORD) VALUES (?, ?, ?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, userId);
			statement.setInt(2, moId);
			statement.setInt(3, numberService);
			statement.setString(4, keyword);

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("LoadMultiService@insertMapMulti: Error Execute, Sql = " + sqlString +
						"@userid="
						+ userId + "@mo_id="
						+ moId + "@numberService="
						+ numberService);
				return -1;
			}
						
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("LoadMultiService@insertMapMulti: Error = " + e.getMessage() +
					"@userid="
					+ userId + "@mo_id="
					+ moId + "@numberService="
					+ numberService);
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("LoadMultiService@insertMapMulti: Error = " + e.getMessage() +
					"@userid="
					+ userId + "@mo_id="
					+ moId + "@numberService="
					+ numberService);
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	
	public static int insertMTMulti(MsgObject msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("LOAD MULTI SERVICE ******** insertMTMulti\tuser_id:"
					+ msgObject.getUserid() + "\tservice_id:"
					+ msgObject.getServiceid() + "\trequest_id:"
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return -1;

		}
		
//		String commandCode = searchMTMulti(msgObject);
//		if(commandCode != null){
//			if(!commandCode.equals(""))
//			return updateMTMulti(msgObject, commandCode);
//		}

		DBPool dbpool = new DBPool();

		Util.logger.info("LOAD MULTI SERVICE ******** insertMTMulti\tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tuser_text:"
				+ msgObject.getUsertext() + "\tmessage_type:"
				+ msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("LOAD MULTI SERVICE ******** insertMTMulti: Error connection == null"
								+ msgObject.getUserid()
								+ "\tTO"
								+ msgObject.getServiceid()
								+ "\t"
								+ msgObject.getUsertext()
								+ "\trequest_id:"
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ " mt_queue_multi "
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE, MO_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getServiceName());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());
			statement.setInt(9, msgObject.getChannelType());
			statement.setBigDecimal(10, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("LOAD MULTI SERVICE ******** insertMTMulti: Error@userid="
						+ msgObject.getUserid() + "@service_id="
						+ msgObject.getServiceid() + "@user_text="
						+ msgObject.getUsertext() + "@message_type="
						+ msgObject.getMsgtype() + "@request_id="
						+ msgObject.getRequestid().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("LOAD MULTI SERVICE ******** insertMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("LOAD MULTI SERVICE ******** insertMTMulti: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}
	
	public String getMTInfoReg(String services){
		String mtReturn = "Ban da dang ky thanh cong dich vu " + services +
				".Cam on ban da su dung dich vu cua MobiFone.Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu " +
				"khac cua Dich vu.Dien thoai ho tro 9244.Chi tiet tai http://mplus.vn";
		return mtReturn;
	}
	
	public String getMTInfoDestry(String services){
		String mtReturn = "Ban da huy thanh cong dich vu " + services +
				".Cam on ban da su dung dich vu cua MobiFone.Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu " +
				"khac cua Dich vu.Dien thoai ho tro 9244.Chi tiet tai http://mplus.vn";
		return mtReturn;
	}

}
