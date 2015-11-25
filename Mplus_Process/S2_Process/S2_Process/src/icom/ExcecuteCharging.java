package icom;

import icom.common.DBUtil;
import icom.common.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import servicesPkg.ProcessReCharge;

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
public class ExcecuteCharging extends Thread {

	Timer timer;
	long repeat = 1000 * 60;

	@Override
	public void run() {
		timer = new Timer();
		schedule();
	}

	void schedule() {
		timer.schedule(new ExceChargingTask(), 10000, repeat);
	}

	class ExceChargingTask extends TimerTask {
		public void run() {
			if (!Sender.processData) {
				// Util.logger.info("destroy timer exce charging!");
				timer.cancel();
				return;
			}
			// Util.logger.info("exce charging chay tiep ko");
			java.util.Calendar calendar = java.util.Calendar.getInstance();

			int sDay = calendar.get(calendar.DAY_OF_WEEK);
			int sHour = calendar.get(calendar.HOUR_OF_DAY);

			// Lay tat ca cac dich vu se chua chay trong ngay hom nay
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
						.error("icom:ExcecuteCharging: error when get name of service (check table services).ex="
								+ e.getMessage());
				DBUtil.Alert("Process.VMS", "ExcecuteCharging", "major",
						"ExcecuteCharging.Exception(check table services):"
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
			Util.logger.info("icom:ExcecuteCharging:processData{Mlist:"
					+ sTable + ",service name=" + sService + ",hours=" + sHours
					+ ",options=" + _option + ",timesendcharge="
					+ sTimeSendCharge);

			if ("x".equalsIgnoreCase(sTable)) {
				Util.logger
						.error("VMS@ExcecuteCharging@processData@Xem lai cau hinh dich vu.");
			}

			if (packetOrMT == Constants.SERVICE_MT) {
				Util.logger.info("VMS:ExcecuteCharging:processData: Dich vu "
						+ sService + " co thoi gian charge =" + sTimeSendCharge);
				
				// Lay danh sach KH bi charge tien
				String sSQL = "";
				if (iCompany_id == 0) {
					sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id, message_type"
							+ ", mobile_operator, company_id, content_id, amount,mt_count, mt_free, channel_type, command_code,date,reg_count,is_icom FROM "
							+ sTable
							+ " WHERE upper(command_code)='"
							+ sService.toUpperCase() + "' and active = 0";
				} else {
					sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id, message_type"
							+ ", mobile_operator, company_id, content_id, amount,mt_count, mt_free, channel_type, command_code,date,reg_count,is_icom FROM "
							+ sTable
							+ " WHERE company_id = '"
							+ sCompany_id
							+ "' and active = 0";
					if (iarea > 0) {
						sSQL = "SELECT id, service, user_id, service_id, options, last_code, request_id "
								+ ", message_type, mobile_operator, company_id, content_id, amount, mt_count, "
								+ "mt_free, channel_type, command_code,date,reg_count,is_icom FROM "
								+ sTable
								+ " WHERE (company_id = '"
								+ sCompany_id
								+ "' or company_id="
								+ iarea
								+ ") and active = 0";
					}
				}

				Util.logger
						.info("VMS@ExcecuteCharging@processData@SQL String: "
								+ sSQL);

				Vector vtUsers = null;

				try {
					vtUsers = DBUtil.getVectorTable("gateway", sSQL);
				} catch (Exception e) {
					Util.logger
							.error("VMS@ExcecuteCharging@ Loi trong qua trinh lay thue bao Khach hang.ex="
									+ e.getMessage());
					DBUtil
							.Alert(
									"Process.VMS",
									"icom@ExcecuteCharging",
									"major",
									"ExcecuteCharging.Exception: Loi khi lay thue bao de ban qua vms_charge, table=:"
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
				processReCharge.handleRecharge(sTable,sService,Constants.PACKET_VMS);				
				updateStatusServices(id);
			}
		}
	}

	private static int insertToVms_Charge(Vector vtUsers, String sHours,
			String sTable, icom.Services service) {
		try {
			if (vtUsers == null) {
				Util.logger
						.info("VMS@ExcecuteCharging@insertToVms_Charge@ Khong co thue bao nao can charge, table="
								+ sTable);
				return 0;
			}
			Util.logger
					.info("VMS@ExcecuteCharging@insertToVms_Charge@ Tong thue bao cua dich vu can charge:"
							+ vtUsers.size());

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
				String sIsIcom = (String) item1.elementAt(18);
				int iIsIcom = Integer.parseInt(sIsIcom);

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
					int iAmount = 0;
					if(sServices.equals("CUOI") || sServices.equals("SAO")){
						iAmount = Integer.parseInt(sAmount) * k;
					}else
						iAmount = Integer.parseInt(sAmount);
					
					Util.logger
							.info("VMS@ExcecuteCharging:Amount doi voi dich vu tra tin "
									+ k
									+ " lan trong ngay (table "
									+ sTable
									+ "):" + iAmount);
					sAmount = iAmount + "";
				}
				// Tao MsgObject
				MsgObject msgObject = new MsgObject(0, sUser_Id, sService_Id,
						sMobile_Operator, sCommand_Code, sOption,
						sMessage_Type, sRequest_Id, sId, sServices,
						sChannel_Type, sContent_Id, sAmount, sCompany_Id,
						sLast_Code);
				msgObject.setIsIcom(iIsIcom);
				
				// Insert VMS_Charge
				if (sendMT(msgObject, sHours) == 1) {
					// Update thue bao da duoc gui sang vms_charge
					updateSendList(sTable, iID);
				}
			}
			return 1;
		} catch (Exception ex) {
			Util.logger
					.error("VMS@ExcecuteCharging@insertToVms_Charge: error, ex="
							+ ex.getMessage());
			return 0;
		}
	}

	// Chuyen thong tin vao bang vms_charge
	private static int sendMT(MsgObject msgObject, String sHours) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = "";

		DBPool dbpool = new DBPool();

		Util.logger.info("VMS@ExcecuteCharging: sendMT \tuser_id:"
				+ msgObject.getUserid() + "\tservice_id:"
				+ msgObject.getServiceid() + "\tmobile_operator:"
				+ msgObject.getMobileoperator() + "\tkeyword:"
				+ msgObject.getServiceName() + "\tcontent_type:"
				+ "\tmessage_type:" + msgObject.getMsgtype() + "\trequest_id:"
				+ msgObject.getRequestid().toString() + "\tcontent_id:"
				+ msgObject.getContentId() + "\tamount:"
				+ msgObject.getAmount() + "\tchannel_type:"
				+ msgObject.getChannelType() + "\tservice_name:"
				+ msgObject.getServiceName());

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("icom@ExcecuteCharging\tsendMT: connection is null"
								+ msgObject.getUserid()
								+ ":\tTO"
								+ msgObject.getServiceid()
								+ ":\t"
								+ msgObject.getUsertext()
								+ ":\trequest_id="
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblCharge
					+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, MESSAGE_TYPE, REQUEST_ID, SERVICE_NAME, CHANNEL_TYPE, "
					+ " CONTENT_ID, AMOUNT, TIME_DELIVERY, COMPANY_ID, IS_THE_SEND, LAST_CODE, OPTIONS, MESSAGE_ID, IS_ICOM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

			Util.logger.info("VMS@: \tSQL Insert:"
					+ sqlString);

			statement = connection.prepareStatement(sqlString);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setInt(5, msgObject.getMsgtype());
			statement.setBigDecimal(6, msgObject.getRequestid());
			statement.setString(7, msgObject.getServiceName());

			statement.setInt(8, msgObject.getChannelType());

			statement.setInt(9, msgObject.getContentId());
			statement.setLong(10, msgObject.getAmount());

			statement.setString(11, sHours);

			// Ten dich vu
			statement.setString(12, msgObject.getCompany_id());
			statement.setString(13, "1");
			statement.setString(14, msgObject.getLast_code());
			statement.setString(15, msgObject.getOption());
			statement.setLong(16, msgObject.getMsg_id());
			statement.setInt(17, msgObject.getIsIcom());

			Util.logger
					.info("VMS@ExcecuteCharging:SendMT \t send to vms_charge: \tuser_id:"
							+ msgObject.getUserid()
							+ "\tservice_id:"
							+ msgObject.getServiceid()
							+ "\tmobile_operator:"
							+ msgObject.getMobileoperator()
							+ "\tkeyword:"
							+ msgObject.getServiceName()
							+ "\tmessage_type:"
							+ msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:"
							+ msgObject.getContentId()
							+ "\tamount:"
							+ msgObject.getAmount()
							+ "\tservice_name:" + msgObject.getServiceName());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis(msgObject.getUserid() + "\tservice_id:"
						+ msgObject.getServiceid() + "\tmobile_operator:"
						+ msgObject.getMobileoperator() + "\tkeyword:"
						+ msgObject.getServiceName() + "\tmessage_type:"
						+ msgObject.getMsgtype() + "\trequest_id:"
						+ msgObject.getRequestid().toString() + "\tcontent_id:"
						+ msgObject.getContentId() + "\tamount:"
						+ msgObject.getAmount() + "\tservice_name:"
						+ msgObject.getServiceName());
				return -1;
			}
		} catch (SQLException e) {
			Util.logger
					.error("VMS@ExcecuteCharging:sendMT\tSql error\tuser_id="
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tmobile_operator:"
							+ msgObject.getMobileoperator() + "\tkeyword:"
							+ msgObject.getServiceName() + "\tcontent_type:"
							+ "\tmessage_type=" + msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:" + msgObject.getContentId()
							+ "\tamount:" + msgObject.getAmount()
							+ "\tservice_name:" + msgObject.getServiceName());

			Util.logger.error("VMS@ExcecuteCharging:sendMT\tSql ex:"
					+ e.getMessage());

			return -1;
		} catch (Exception e) {
			Util.logger
					.error("VMS@ExcecuteCharging:Error to: \tsendMT\tuser_id:"
							+ msgObject.getUserid() + "\tservice_id:"
							+ msgObject.getServiceid() + "\tmobile_operator:"
							+ msgObject.getMobileoperator() + "\tkeyword:"
							+ msgObject.getServiceName() + "\tcontent_type:"
							+ "\tmessage_type:" + msgObject.getMsgtype()
							+ "\trequest_id:"
							+ msgObject.getRequestid().toString()
							+ "\tcontent_id:" + msgObject.getContentId()
							+ "\tamount:" + msgObject.getAmount()
							+ "\tchannel_type:" + msgObject.getChannelType()
							+ "\tservice_name:" + msgObject.getServiceName());
			Util.logger.error("VMS@ExcecuteCharging:\tsendMTError to: ex:"
					+ e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
		return 1;
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
						.error("VMS@ExcecuteCharging@updateSendList@Impossible to connect to DB");
				return false;
			}

			// Update
			String sSQL = "UPDATE " + tblName + " SET active = 1"
					+ " WHERE id = " + sId;

			Util.logger.info("VMS@ExcecuteCharging@updateSendList@SQL UPDATE: "
					+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger.error("VMS@ExcecuteCharging@updateSendList@" + sId
						+ " has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger.error("VMS@ExcecuteCharging@updateSendList@: Error:"
					+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error("VMS@ExcecuteCharging@updateSendList@: Error:"
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
						.error("VMS@ExcecuteCharging@updateStatusServices@connection is null.");
				return false;
			}

			// Update
			String sSQL = "UPDATE services SET active = 1" + " WHERE id = "
					+ serviceId;

			Util.logger
					.info("VMS@ExcecuteCharging@updateStatusServices@SQL UPDATE: "
							+ sSQL);
			statement = connection.prepareStatement(sSQL);
			if (statement.execute()) {
				Util.logger.error("VMS@ExcecuteCharging@updateStatusServices@"
						+ serviceId + " has to sent");
				return false;
			}
			return true;
		} catch (SQLException e) {
			Util.logger
					.error("VMS@ExcecuteCharging@updateStatusServices@: Error:"
							+ e.toString());
			return false;
		} catch (Exception e) {
			Util.logger
					.error("VMS@ExcecuteCharging@updateStatusServices@: Error:"
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

		// Util.logger.info("VMS@ExcecuteCharging@isServicesRun@SQL Select: "
		// + sqlSelect);

		Vector vtService = null;

		try {
			vtService = DBUtil.getVectorTable("gateway", sqlSelect);

			return vtService.size() > 0;
		} catch (Exception e) {
			Util.logger
					.error("VMS@ExcecuteCharging@isServicesRun@error when get service names (check table services).ex="
							+ e.getMessage());
			DBUtil.Alert("Process.VMS", "ExcecuteCharging", "major",
					"ExcecuteCharging@isServicesRun.Exception(check table services):"
							+ e.toString(), "processAdmin");
		}
		return false;
	}
}
