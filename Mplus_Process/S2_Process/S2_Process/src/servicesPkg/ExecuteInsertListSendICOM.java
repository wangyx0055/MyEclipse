package servicesPkg;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


/**
 * ExcecuteCharging class.<br>
 * 
 * <pre>
 *   Excute insert into table vms_charge for INgw charge money Subcriber
 *   Total All of subcriber in all of table start with mlist_ and charge
 * </pre>
 * 
 * @author VietNamNet ICom DanND
 * @version 2.0
 */
public class ExecuteInsertListSendICOM extends Thread {
	// private String sMlist =
	// mlist_hinhdong;mlist_hinhnen;mlist_game;mlist_rings";
	// private String sTimerun = "3:00";
	// private String sTimeFinish = "12:30";
	// private String lastTimeRequest = Util.getBeforeOneDay();
	Timer timer;
	long repeat = 10000 * 60;

	@Override
	public void run() {
		timer = new Timer();
		schedule();
	}

	void schedule() {
		timer.schedule(new InsertListSendTask(), 10000, repeat);
	}

	class InsertListSendTask extends TimerTask {
		@Override
		public void run() {
			if (!Sender.processData) {
				// Util.logger.info("destroy timer exce charging!");
				timer.cancel();
				return;
			}
			
			Util.logger.info("ExecuteInsertListSendVMS@ Start Check services and INSERT INTO LIST_SEND!");
			
			java.util.Calendar calendar = java.util.Calendar.getInstance();

			int sDay = calendar.get(Calendar.DAY_OF_WEEK);
			
			int sHour = calendar.get(Calendar.HOUR_OF_DAY);
			int timeRun = 3;
			try{
				timeRun = Integer.parseInt(Constants._prop.getProperty("PACKET_TIME_INSERT_LIST_SEND", ""));
			}catch(Exception ex){
				ex.printStackTrace();
			}
						
			
			if(timeRun != sHour) {			
				return;
			}
			
			
			// Lay tat ca cac dich vu se chua chay trong ngay hom nay
						
			String sqlSelectPacket = "SELECT id, services, hours, options, timesendcharge, packet_or_mt FROM "
					+ "services WHERE ( dayofweek like '%"
					+ sDay
					+ "%' or upper(dayofweek)='X' ) and result = 0  and run_insert_list_send = 0 "
					+ " and packet_or_mt = " + Constants.SERVICE_PAGKET;
			
			Util.logger.info("InsertListSendTask@SELECT PACKET" + sqlSelectPacket);
			
			Vector<Object> vtServicePacket = null;

			try {
				vtServicePacket = DBUtil.getVectorTable("gateway", sqlSelectPacket);
			} catch (Exception e) {
				Util.logger
						.error("icom:ExcecuteCharging: error when get name of service (check table services).ex="
								+ e.getMessage());
				DBUtil.Alert("Process.VMS", "ExcecuteCharging", "major",
						"ExcecuteCharging.Exception(check table services):"
								+ e.toString(), "processAdmin");
			}
			if(vtServicePacket != null){
				processData(vtServicePacket);
			}
		}
	}

	private void processData(Vector vtService) {
		for (int i = 0; i < vtService.size(); i++) {
			Vector item = (Vector) vtService.elementAt(i);

			HashMap _option = new HashMap();

			String id = (String) item.elementAt(0);
			String sService = (String) item.elementAt(1);
			String sHours = (String) item.elementAt(2); // time to delivery message
			String sOptions = (String) item.elementAt(3);
			_option = Util.getParametersAsString(sOptions);
			String sTimeSendCharge = (String) item.elementAt(4);
			
			int packetOrMT = Integer.parseInt((String) item.elementAt(5));

			String sTable = Util.getStringfromHashMap(_option, "mlist", "x");
			Util.logger.info("InsertListSendTask@ mlist = " + sTable);

			String sCompany_id = Util.getStringfromHashMap(_option,
					"companyid", "0");
			int iCompany_id = Util.PaseInt(sCompany_id);
			String area = Util.getStringfromHashMap(_option, "area", "x");
			
			int iarea = 0;
			if (!"x".equalsIgnoreCase(area)) {
				iarea = Integer.parseInt(area);
			}
			Util.logger.info("icom:InsertListSendTask:processData{Mlist:"
					+ sTable + ",service name=" + sService + ",hours=" + sHours
					+ ",options=" + _option + ",timesendcharge="
					+ sTimeSendCharge);

			if ("x".equalsIgnoreCase(sTable)) {
				Util.logger
						.error("ICOM@ExcecuteCharging@processData@Xem lai cau hinh dich vu.");
			}
						
			Util.logger.info("InsertListSendTask@PacketOrMT = " + packetOrMT);
			// Charging with Service is Package
			if(packetOrMT == Constants.SERVICE_PAGKET){		
				Util.logger.info("InsertListSendTask@ Handle MlistObject@ PacketOrMT = " + packetOrMT);
				handleMlist(sTable, sHours, sService);	
				updateRunInsertService(id);
			}
		}
	}
	
	private void handleMlist(String mlisTable, String timeDelivery, String commandCode){
		ServiceMng serviceMng = new ServiceMng();
		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		
		arrMlistInfo = serviceMng.getMlistInfo(mlisTable);
		
		for(int j = 0; j< arrMlistInfo.size(); j++){
			
			MlistInfo mlistInfo = arrMlistInfo.get(j);
			if(!mlistInfo.getCommandCode().equals(commandCode)) continue;
			
			String today = serviceMng.getDate(0);
			
			System.out.println("Date RETRY = " + mlistInfo.getDateRetry());
			System.out.println("TODAY = " + today);
			
			if( mlistInfo.getDateRetry().compareTo(today) >= 0 ){
				mlistInfo.setToday(today);
				// insert in ListSend to send MT
				System.out.println("START INSERT......");
				insertToListSend(mlistInfo,timeDelivery);
			}
									
		}
	}
	
	private int insertToListSend(MlistInfo mlistInfo, String timeDelivery){
		
		String tableListSend = "list_send";
		
		int response = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ tableListSend
				+ " ( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, OPTIONS, " +
				   "MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, INSERT_DATE, SERVICE_NAME, CHANNEL_TYPE, " +
				   "CONTENT_ID, AMOUNT, TIME_DELIVERY, COMPANY_ID, IS_THE_SEND, LAST_CODE ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, mlistInfo.getUserId());
			stmt.setString(2, mlistInfo.getServiceId());
			stmt.setString(3, mlistInfo.getMobiOperator());
			stmt.setString(4, mlistInfo.getCommandCode());
			stmt.setString(5, mlistInfo.getOptions());
			stmt.setInt(6, mlistInfo.getMessageType());
			stmt.setString(7, mlistInfo.getRequestId());
			stmt.setString(8, "0");
			stmt.setString(9, mlistInfo.getToday());
			stmt.setString(10, mlistInfo.getService());
			stmt.setInt(11, mlistInfo.getChanelType());
			stmt.setInt(12, mlistInfo.getContentId());
			stmt.setInt(13, mlistInfo.getAmount());
			stmt.setString(14, timeDelivery);
			stmt.setString(15, mlistInfo.getCompanyId());
			stmt.setInt(16, Constants.PACKET_ICOM);
			stmt.setString(17, mlistInfo.getLastCode());			

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("SERVICE_PACKET VMS@insertToListSend: Error @userid="
								+ mlistInfo.getUserId()
								+ "@serviceid="
								+ mlistInfo.getServiceId()
								+ "@usertext="
								+ mlistInfo.getCommandCode()
								+ "@messagetype="
								+ mlistInfo.getMessageType()
								+ "@requestid="
								+ mlistInfo.getRequestId().toString());
				response = -1;
			}
			
			Util.logger
			.crisis("SERVICE_PACKET VMS@insertToListSend: Success @userid="
					+ mlistInfo.getUserId()
					+ "@serviceid="
					+ mlistInfo.getServiceId()
					+ "@usertext="
					+ mlistInfo.getCommandCode()
					+ "@messagetype="
					+ mlistInfo.getMessageType()
					+ "@requestid="
					+ mlistInfo.getRequestId().toString());
			
			
			response = 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package ICOM - insertToListSend. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			response = -1;
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package ICOM - insertToListSend. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			response = -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
		
		return response;
	}


	private static boolean updateRunInsertService(String serviceId) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.error("ICOM@ExecuteInsertListSendVMS@updateRunInsertService@connection is null.");
				return false;
			}

			// Update
			String sSQL = "UPDATE services SET run_insert_list_send = 1" + " WHERE id = "
					+ serviceId;

			Util.logger
					.info("ICOM@ExecuteInsertListSendVMS@updateRunInsertService@SQL UPDATE: "
							+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger.error("ICOM@ExecuteInsertListSendVMS@updateRunInsertService@"
						+ serviceId + " has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger
					.error("ICOM@ExecuteInsertListSendVMS@updateRunInsertService@: Error:"
							+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger
					.error("ICOM@ExecuteInsertListSendVMS@updateRunInsertService@: Error:"
							+ e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}
