package icom;

import icom.common.DBUtil;
import icom.common.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import servicesPkg.ProcessReCharge;

import ws.vmscharge.VMSChargeSender;

/**
 * ExcecuteCharging class.<br>
 * 
 * <pre>
 *   Excute insert into table vms_charge for INgw charge money Subcriber
 *   Total All of subcriber in all of table start with mlist_ and charge
 * </pre>
 * 
 * @author VietNamNet ICom TrungVD
 * @version 2.0
 */
public class ExcecuteChargingICOM extends Thread {
	// private String sMlist =
	// mlist_hinhdong;mlist_hinhnen;mlist_game;mlist_rings";
	// private String sTimerun = "8:00";
	// private String sTimeFinish = "12:30";
	// private String lastTimeRequest = Util.getBeforeOneDay();
	Timer timer;
	long repeat = 1000 * 60;

	@Override
	public void run() {
		timer = new Timer();
		schedule();
	}

	void schedule() {
		timer.schedule(new ExceChargingICOMTask(), 10000, repeat);
	}

	class ExceChargingICOMTask extends TimerTask {
		public void run() {
			if (!Sender.processData) {
				timer.cancel();
				return;
			}
			java.util.Calendar calendar = java.util.Calendar.getInstance();

			int sDay = calendar.get(Calendar.DAY_OF_WEEK);
			int sHour = calendar.get(Calendar.HOUR_OF_DAY);
			// Util.logger.info("icom@ExcecuteCharging@ DAY OF WEEK: " +
			// sDay);

			String sqlSelect = "SELECT id, services, hours, options, timesendcharge, packet_or_mt FROM "
				+ "services WHERE (dayofweek like '%"
				+ sDay
				+ "%' or upper(dayofweek)='X') and active=0 and timesendcharge<= "
				+ sHour + " and packet_or_mt = " + Constants.SERVICE_MT;
		
			String sqlSelectPacket = "SELECT id, services, hours, options, timesendcharge, packet_or_mt FROM "
				+ "services WHERE active=0 AND time_rechage_packet = " + sHour
				+ " and packet_or_mt = " + Constants.SERVICE_PAGKET;

			Vector vtService = null;
			Vector vtServicePacket = null;

			try {
				vtService = DBUtil.getVectorTable("gateway", sqlSelect);
				vtServicePacket = DBUtil.getVectorTable("gateway", sqlSelectPacket);

			} catch (Exception e) {
				Util.logger
						.error("icom@ExcecuteChargingICOM@ error when get service names (check table services).ex="
								+ e.getMessage());
				DBUtil.Alert("Process.icom", "ExcecuteChargingICOM", "major",
						"ExcecuteChargingICOM.Exception(check table services):"
								+ e.toString(), "processAdmin");
			}
			processData(vtService);
			processData(vtServicePacket);
		}
	}

	private void processData(Vector vtService) {
		for (int i = 0; i < vtService.size(); i++) {
			Vector item = (Vector) vtService.elementAt(i);

			HashMap _option = new HashMap();

			String id = (String) item.elementAt(0);
			String sService = (String) item.elementAt(1);
			String sHours = (String) item.elementAt(2); // time to delivery
			// message
			String sOptions = (String) item.elementAt(3);
			_option = Util.getParametersAsString(sOptions);
			String sTimeSendCharge = (String) item.elementAt(4);
			int packetOrMT = Integer.parseInt((String) item.elementAt(5));

			String sTable = Util.getStringfromHashMap(_option, "mlist", "x");

			String sCompany_id = Util.getStringfromHashMap(_option,
					"companyid", "0");
			int iCompany_id = Util.PaseInt(sCompany_id);

			String area = Util.getStringfromHashMap(_option, "area", "x");
			int iarea = 0;
			if (!"x".equalsIgnoreCase(area)) {
				iarea = Integer.parseInt(area);
			}

			Util.logger.info("icom@ExcecuteChargingICOM@processData{Mlist:"
					+ sTable + "\tservice name:" + sService + "\thours:"
					+ sHours + "\toptions:" + _option + "\ttimesendcharge:"
					+ sTimeSendCharge);

			if ("x".equalsIgnoreCase(sTable)) {
				Util.logger
						.error("icom@ExcecuteChargingICOM@processData@Xem lai cau hinh dich vu.");
			}

			if (packetOrMT == Constants.SERVICE_MT) {
				Util.logger.info("icom@ExcecuteChargingICOM@processData: Dich vu "
						+ sService + " co thoi gian charge=" + sTimeSendCharge);
				String sSQL = "";
				if (iCompany_id == 0) {
					sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id "
							+ ", message_type, mobile_operator, company_id, content_id, amount, mt_count, mt_free, channel_type, command_code,date,reg_count FROM "
							+ sTable
							+ " WHERE upper(command_code)='"
							+ sService.toUpperCase() + "' and active = 0";
				} else {
					sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id "
							+ ", message_type, mobile_operator, company_id, content_id, amount, mt_count, mt_free, channel_type, command_code,date,reg_count FROM "
							+ sTable
							+ " WHERE upper(command_code)='"
							+ sService.toUpperCase()
							+ "' and company_id = '"
							+ sCompany_id + "' and active = 0";
					if (iarea > 0) {
						sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id "
								+ ", message_type, mobile_operator, company_id, content_id, amount, mt_count, mt_free, channel_type, command_code,date,reg_count FROM "
								+ sTable
								+ " WHERE upper(command_code)='"
								+ sService.toUpperCase()
								+ "' and (company_id = '"
								+ sCompany_id
								+ "' or company_id="
								+ iarea
								+ ") and active = 0";
					}
				}

				Util.logger
						.info("icom@ExcecuteChargingICOM@processData@SQL String: "
								+ sSQL);

				Vector vtUsers = null;

				try {
					vtUsers = DBUtil.getVectorTable("gateway", sSQL);
				} catch (Exception e) {
					Util.logger
							.error("icom@ExcecuteChargingICOM@ Loi trong qua trinh lay thue bao Khach hang.ex="
									+ e.getMessage());
					DBUtil
							.Alert(
									"Process.icom",
									"icom@ExcecuteChargingICOM",
									"major",
									"ExcecuteChargingICOM.Exception: Loi khi lay thue bao de ban qua vms_charge, table=:"
											+ sTable + ".ex=" + e.toString(),
									"processAdmin");
					continue;
				}
				icom.Services service = new icom.Services();
				try {
					service = icom.Services.getService(sService,
							Sender.loadconfig.hServices);
				} catch (Exception ex) {
					Util.logger
							.error("ExcecuteCharging: co loi khi get service");
					DBUtil.Alert("Process.VMS", "icom@ExcecuteCharging",
							"major",
							"ExcecuteCharging.Exception: co loi khi get service.ex="
									+ ex.toString(), "");
				}
				if (insertToVms_Charge(vtUsers, sHours, sTable, service) == 1) {
					// update status services
					updateStatusServices(id);
				}
			}
			
			// Charging with Service is Package
			if(packetOrMT == Constants.SERVICE_PAGKET){				
				ProcessReCharge processReCharge = new ProcessReCharge();
				processReCharge.handleRecharge(sTable,sService,Constants.PACKET_ICOM);
				updateStatusServices(id);
			}
		}
	}

	private static int insertToVms_Charge(Vector vtUsers, String sHours,
			String sTable, icom.Services service) {
		try {
			if (vtUsers == null) {
				Util.logger
						.info("icom@ExcecuteChargingICOM@insertToVms_Charge@ Khong co thue bao nao can charge, table="
								+ sTable);
				return 0;
			}
			Util.logger
					.info("icom@ExcecuteChargingICOM@insertToVms_Charge@ Tong thue bao cua dich vu can charge:"
							+ vtUsers.size() + " table:" + sTable);

			for (int j = 0; j < vtUsers.size(); j++) {
				Vector item1 = (Vector) vtUsers.elementAt(j);
				String sId = (String) item1.elementAt(0);
				int iID = Integer.parseInt(sId);
				String sServices = (String) item1.elementAt(1);
				String sUser_Id = (String) item1.elementAt(2);
				String sService_Id = (String) item1.elementAt(3);
				String sOption = (String) item1.elementAt(4);
				String sLast_Code = (String) item1.elementAt(5);
				String sRequest_Id = (String) item1.elementAt(6);
				String sMessage_Type = (String) item1.elementAt(7);
				String sMobile_Operator = (String) item1.elementAt(8);
				String sCompany_Id = (String) item1.elementAt(9);
				String sContent_Id = (String) item1.elementAt(10);
				String sAmount = (String) item1.elementAt(11);
				String sMt_Count = (String) item1.elementAt(12);
				String sMt_Free = (String) item1.elementAt(13);
				String sChannel_Type = (String) item1.elementAt(14);
				String sCommand_Code = (String) item1.elementAt(15);
				String sDate = (String) item1.elementAt(16);
				String sReg_Count = (String) item1.elementAt(17);
				int iReg_Count = Integer.parseInt(sReg_Count);
				int mt_count = Integer.parseInt(sMt_Count);
				int mt_free = Integer.parseInt(sMt_Free);

				if (Util.IsUserIdInFreeCharge(iReg_Count, sDate, sCommand_Code,
						service)) {
					sMessage_Type = "0";
				}
				if (sHours.indexOf(";") != -1) {
					int k = 0;
					try {
						String[] _time = sHours.split(";");
						int l = _time.length;

						for (int q = 0; q < l; q++) {
							String s = _time[q];
							boolean b = "".equalsIgnoreCase(s) || null == s;
							if (!b) {
								k++;
							}
						}
					} catch (Exception ex) {

					}
					int iAmount = Integer.parseInt(sAmount) * k;
					Util.logger.info("Amount doi voi dich vu tra tin " + k
							+ " lan trong ngay (table " + sTable + "):"
							+ iAmount);
					sAmount = iAmount + "";
				}
				// Tao MsgObject
				/*******
				 * 
				 *(int objtype, String sUser_Id,String sService_Id,String
				 * sMobile_Operator, String sCommand_Code, String sOption,String
				 * sMessage_Type,String sRequest_Id, String sMessage_Id, String
				 * sService_Name, String sChannel_Type,String sContent_Id,String
				 * sAmount, String sCompany_Id, String sLast_Code)
				 * *****/
				MsgObject msgObject = new MsgObject(0, sUser_Id, sService_Id,
						sMobile_Operator, sCommand_Code, sOption,
						sMessage_Type, sRequest_Id, sId, sServices,
						sChannel_Type, sContent_Id, sAmount, sCompany_Id,
						sLast_Code);
				// Ban qua ws
				String result = "";
				try {
					// if (msgObject.getObjtype() == 0) {
					Util.logger.info("Bat dau send XML to WS VMS");
					/*******
					 * 2010-11-08: PhuongDT Gui thue bao can charge tu ICOM sang
					 * VMS vao table vms_charge:charge offline
					 * ****/
					result = VMSChargeSender.SendToVmsCharge(msgObject, sHours)
							+ "";
					// }

				} catch (Exception e) {
					Util.logger.error("Loi khi sendXML:" + e.getMessage());
				}
				if ("1".equalsIgnoreCase(result)) {
					// Update thue bao da duoc gui sang vms_charge
					updateSendList(sTable, iID);
				} else {
					Util.logger.error("Gui sang VMS bi loi thue bao: "
							+ "\tuser_id:" + msgObject.getUserid()
							+ "\t command_code:" + msgObject.getCommandCode());
				}
			}
			return 1;
		} catch (Exception ex) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@insertToVms_Charge: error, ex="
							+ ex.getMessage());
			return 0;
		}
	}

	// Update list user sended
	private static boolean updateSendList(String tblName, int sId) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.error("icom@ExcecuteChargingICOM@updateSendList@Impossible to connect to DB");
				return false;
			}

			// Update
			String sSQL = "UPDATE " + tblName + " SET active = 1"
					+ " WHERE id = " + sId;

			Util.logger
					.info("icom@ExcecuteChargingICOM@updateSendList@SQL UPDATE: "
							+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger.error("icom@ExcecuteChargingICOM@updateSendList@"
						+ sId + " has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@updateSendList@: Error:"
							+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@updateSendList@: Error:"
							+ e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	private static boolean updateStatusServices(String serviceId) {

		Connection connection = null;
		PreparedStatement statement = null;
		DBPool dbpool = new DBPool();
		try {

			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.error("icom@ExcecuteChargingICOM@updateStatusServices@connection is null.");
				return false;
			}

			// Update
			String sSQL = "UPDATE services SET active = 1" + " WHERE id = "
					+ serviceId;

			Util.logger
					.info("icom@ExcecuteChargingICOM@updateStatusServices@SQL UPDATE: "
							+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger
						.error("icom@ExcecuteChargingICOM@updateStatusServices@"
								+ serviceId + " has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@updateStatusServices@: Error:"
							+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@updateStatusServices@: Error:"
							+ e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public boolean isHasServicesRun(int sDay, int sHour) {
		String sqlSelect = "SELECT id, services, hours, options, timesendcharge FROM "
				+ "services WHERE (dayofweek like '%"
				+ sDay
				+ "%' or upper(dayofweek)='X') and active=0 and timesendcharge<="
				+ sHour;
		// Util.logger.info("icom@ExcecuteChargingICOM@isServicesRun@SQL Select: "
		// + sqlSelect);

		Vector vtService = null;

		try {
			vtService = DBUtil.getVectorTable("gateway", sqlSelect);
			return vtService.size() > 0;
		} catch (Exception e) {
			Util.logger
					.error("icom@ExcecuteChargingICOM@isServicesRun@error when get service names (check table services).ex="
							+ e.getMessage());
			DBUtil.Alert("Process.icom", "ExcecuteChargingICOM", "major",
					"ExcecuteChargingICOM@isServicesRun.Exception(check table services):"
							+ e.toString(), "processAdmin");
		}
		return false;
	}
}
