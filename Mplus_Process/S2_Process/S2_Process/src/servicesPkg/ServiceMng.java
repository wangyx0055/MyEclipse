package servicesPkg;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import vmg.itrd.ws.MTSenderVMS;

public class ServiceMng {

	/**********
	 * Kiem tra thue bao da dang ky dich vu chua?
	 * 
	 * @return true - da dang ky <br/>
	 *         false - chua dang ky
	 *@author DanND
	 *@Date 2011-01-11
	 */
	public Boolean isRegistry(MsgObject msg, String tableName) {

		int test = 0;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT COUNT(1) FROM " + tableName
				+ " WHERE USER_ID = '" + msg.getUserid() + "'"
				+ " AND COMMAND_CODE = '" + msg.getCommandCode() + "'";
		// System.out.println("==== SERVICE PACKAGE: isRegistry - QUERRY = "
		// + sqlQuery);
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
					test = rs.getInt(1);
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		if (test >= 1)
			return true;
		else
			return false;
	}

	/******
	 * Kiem tra xem co trong thoi gian Khuyen mai ko?
	 * 
	 * @return true: dang trong thoi gian khuyen mai<br/>
	 *         false: Het thoi gian khuyen mai
	 *@author DanND
	 *@Date 2011-01-11
	 */
	public Boolean isInTimePromotion(MsgObject msg) {

		String fromDateFree = "";
		String toDateFree = "";
		int activeFree = 0;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT from_date_free, to_date_free, active_free FROM services "
				+ "WHERE services = '" + msg.getCommandCode() + "'";
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
					fromDateFree = rs.getString("from_date_free");
					toDateFree = rs.getString("to_date_free");
					activeFree = rs.getInt("active_free");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		if (activeFree == 0) {
			fromDateFree = "";
			toDateFree = "";
		}

		if (fromDateFree.equals("") || toDateFree.equals("")) {
			return false;
		} else {
			Date todayTest = new Date();
			DateFormat formatTestLog = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String strToday = formatTestLog.format(todayTest);

			// System.out.println("KM FROM DATE: " + fromDateFree);
			// System.out.println("KM T0 DATE: " + toDateFree);
			// System.out.println("KM TODAY: " + strToday);

			if (fromDateFree.compareTo(strToday) <= 0
					&& toDateFree.compareTo(strToday) >= 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	/***
	 * 
	 * @param msg
	 *            : MsgObject
	 * @param tableName
	 *            : mlist_serviceName_Cancel Table
	 * @return Date Retry date retry: Da tung huy dich vu <br/>
	 *         "": Chua huy dich vu lan nao
	 *@author DanND
	 *@Date 2011-01-11
	 */
	public String isDestroy(MsgObject msg, String tableCancel) {

		String dateRetry = "";
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT DATE_RETRY FROM " + tableCancel
				+ " WHERE USER_ID = '" + msg.getUserid() + "'"
				+ " AND COMMAND_CODE = '" + msg.getCommandCode() + "'";
		// System.out.println("isDestroy QUERRY: " + sqlQuery);
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
					dateRetry = rs.getString("DATE_RETRY");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return dateRetry;

	}

	/***
	 * 
	 * @param msg
	 *            MsgObject
	 * @return 1: Insert successful <br/>
	 *         -1: failure
	 * @author DanND
	 */
	public int InsertVmsChargePkg(MsgObject msg) {

		String tableName = "vms_charge_packet";

		int isThePacket = 1; // 1: VMS, -1: ICOM
		if (msg.getSubCP() == 1) {
			isThePacket = -1; // ICOM
		}

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ tableName
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, "
				+ "DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, RETRIES_NUM, "
				+ "NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, RESULT_CHARGE, IS_THE_PACKET ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, msg.getUserid());
			stmt.setString(2, msg.getServiceid());
			stmt.setString(3, msg.getMobileoperator());
			stmt.setString(4, msg.getCommandCode());
			stmt.setInt(5, msg.getContenttype());
			stmt.setString(6, msg.getUsertext());
			stmt.setTimestamp(7, msg.getTSubmitTime());
			stmt.setTimestamp(8, msg.getTDoneTime());
			stmt.setInt(9, msg.getProcess_result());
			stmt.setInt(10, msg.getMsgtype());
			stmt.setBigDecimal(11, msg.getRequestid());
			stmt.setLong(12, msg.getMsg_id());
			stmt.setInt(13, msg.getRetries_num());
			stmt.setString(14, "");
			stmt.setString(15, msg.getServiceName());
			stmt.setInt(16, msg.getChannelType());
			stmt.setInt(17, msg.getContentId());
			stmt.setLong(18, msg.getAmount());
			stmt.setInt(19, 0);
			stmt.setInt(20, isThePacket);

			if (stmt.executeUpdate() != 1) {
				Util.logger.crisis("@Insert vms_charge_package: Error@userid="
						+ msg.getUserid() + "@serviceid=" + msg.getServiceid()
						+ "@usertext=" + msg.getUsertext() + "@messagetype="
						+ msg.getMsgtype() + "@requestid="
						+ msg.getRequestid().toString());
				return -1;
			}

			Util.logger.info("InsertVmsChargePkg SUCCESSFUL !!! \n @ Query = "
					+ sqlQuery);

			return 1;
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

	/******
	 * Kiem tra xem tin DK service co duoc KM ko <br/>
	 * 1. Kiem tra userID da dang ky lan nao chua <br/>
	 * 2. Neu chua dang ky, kiem tra xem da huy lan nao chua <br/>
	 * 3. Neu Chua DK, chua Huy => Kiem tra xem co trong thoi gian khuyen mai
	 * ko? <br/>
	 * 
	 * @param msgObject
	 */
	public void checkKM(MsgObject msgObject) {

		String tableName = this.getMlistTableName(msgObject.getCommandCode());
		msgObject.setMlistTableName(tableName);

		String tableCancel = tableName + "_cancel";

		Boolean isReg = this.isRegistry(msgObject, tableName);
		if (isReg) {// Da Dang Ky
			// Neu Dang ky roi => Insert vao bang mtQueue de tra tin MT cho KH
			Util.logger.info(" checkKM: Da DK : , USER_ID = "
					+ msgObject.getUserid() + " COMMAND_CODE = "
					+ msgObject.getCommandCode());
			msgObject.setChargingPackage(0);

		} else { // Chua thay dang ky
			String dateRetry = this.isDestroy(msgObject, tableCancel);
			// System.out.println("checkKM@ Date_RETRY = "+dateRetry);
			if (!dateRetry.equals("")) {
				// Da Huy Dich vu truoc do
				String today = this.getDate(0);
				if (dateRetry.compareTo(today) >= 0) {
					// Dang ky lai nhung thoi gian retry van con
					// System.out.println(" checkKM: Registry Again - van duoc KM");
					msgObject.setChargingPackage(3);
				} else {
					// System.out.println(" checkKM: Registry Again - het KM");
					msgObject.setChargingPackage(2);
				}
				// System.out.println(" checkKM: Da HUY");
			} else {
				// Chua dang ky va cung chua huy lan nao => Check co con nam
				// trong thoi gian KM ko?
				if (this.isInTimePromotion(msgObject)) {
					// Trong thoi gian khuyen mai => Thue bao duoc KM
					// => Insert mlist_serviceName voi so ngay bat dau dang ky
					// va so ngay duoc KM
					// => insert mtQueue de tra MT cho KH
					msgObject.setChargingPackage(1);
					// System.out.println(" checkKM: KM");

				} else {
					// Dang ky lan dau nhung het thoi gian KM => can Charging (
					// As (1))
					msgObject.setChargingPackage(2);
					// System.out.println(" TVBT: HET KM");
				}

			}

		}

	}

	/*****
	 * Tra ve mlis table tuong ung voi command code
	 * 
	 * @param commandCode
	 * @return
	 */
	public String getMlistTableName(String commandCode) {

		String strObtionDB = "";
		String tableName = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT options FROM keywords WHERE service_name = '"
				+ commandCode + "' limit 1";
		// System.out.println("getMlistTableName: " + sqlQuery);
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
					strObtionDB = rs.getString("options");
				}
			} else {
				Util.logger
						.error("Service Package - getMlistTableName : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - getMlistTableName. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - getMlistTableName. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		String[] arrTmp = strObtionDB.split("&");

		tableName = arrTmp[0].split("=")[1];

		// System.out.println(" ===== TABLE NAME: " + tableName);

		return tableName;

	}

	public String getCompanyId(Keyword keyword) {
		String companyId = "";
		String[] arrTmp = keyword.getOptions().split("&");
		if (arrTmp.length >= 3) {
			companyId = arrTmp[2].split("=")[1];
		}
		return companyId;
	}

	public String getCompanyId(String commandCode) {
		String companyId = "";
		String strObtionDB = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT options FROM keywords WHERE service_name = '"
				+ commandCode + "' limit 1";
		// System.out.println("getMlistTableName: " + sqlQuery);
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
					strObtionDB = rs.getString("options");
				}
			} else {
				Util.logger
						.error("Service Package - getMlistTableName : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - getMlistTableName. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - getMlistTableName. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		String[] arrTmp = strObtionDB.split("&");

		if (arrTmp.length >= 3) {
			companyId = arrTmp[2].split("=")[1];
		}
		return companyId;
	}

	/*****
	 * Xu ly khi co SMS dang ky dich vu
	 * 
	 * @param msg
	 * @param keyword
	 *            - luu cac thong tin tu bang key word
	 * @return
	 */
	public Collection<MsgObject> RegisterHandle(MsgObject msg, Keyword keyword) {
		Collection<MsgObject> messages = new ArrayList<MsgObject>();

		msg.setAmount(keyword.getAmount());
		msg.setCompany_id(this.getCompanyId(keyword));

		String tableVmsChargePacket = "vms_charge_packet";

		// HET KHUYEN MAI INSERT INTO VMS_CHARGE_PACKET
		if (msg.getChargingPackage() == 2) {
			this.InsertVmsChargePkg(msg);
			if (msg.getSubCP() == 1) { // ICOM send to WS
				ChargeResultInfo objInfo = this.getChargeInfo(
						tableVmsChargePacket, msg.getUserid(), msg
								.getServiceid(), msg.getCommandCode());
				if (objInfo != null) {
					int testSend = MTSenderVMS.insertVmsChargePackage(objInfo);
					if (testSend == 1) {
						Util.logger
								.info("ICOM RegisterHandle@insertVmsChargePackage"
										+ " Send to VMS Success: USER_ID = "
										+ msg.getUserid()
										+ ", COMMAND_CODE: ="
										+ msg.getCommandCode()
										+ ", insertToMlist = "
										+ msg.getMlistTableName());
						this.deleteInTable(tableVmsChargePacket, msg
								.getUserid(), msg.getCommandCode(), msg
								.getServiceid());
					} else {
						Util.logger
								.info("ICOM RegisterHandle@insertVmsChargePackage"
										+ " Send to VMS FAILURE: USER_ID = "
										+ msg.getUserid()
										+ ", COMMAND_CODE: ="
										+ msg.getCommandCode()
										+ ", insertToMlist = "
										+ msg.getMlistTableName());
					}
				}
			}
		}

		// DA DANG KY => update MT CONTENT (INFO)
		if (msg.getChargingPackage() == 0) {
			msg.setUsertext(keyword.getExistMsg());
		}

		// DANG KY LAN DAU VA TRONG THOI GIAN KM => UPDATE MT CONTENT (INFO)
		// => INSERT TABLE MLIST_DV
		// Insert in List_send to response MT Service
		if (msg.getChargingPackage() == 1) {

			msg.setUsertext(keyword.getPromoMsg());
			int numberFreeDate = this.getNumberFreeDate(msg.getCommandCode());
			String strDateRetry = this.getDate(numberFreeDate);
			this.insertMlistService(msg, keyword, strDateRetry);

			MlistInfo objInfo = this.getMlistInfoObject(
					msg.getMlistTableName(), msg.getUserid(), msg
							.getServiceid(), msg.getCommandCode());

			if (objInfo != null) {
				this.insertToMlistSubcriber(Constants.MLIST_SUBCRIBER, objInfo);
				// insert into List_Send
				this.insertToListSend(objInfo, "0", Constants.PACKET_VMS);
			}

			if (msg.getSubCP() == 1) {
				// ICOM => insert mlist in VMS
				if (objInfo != null) {
					int testSend = MTSenderVMS.insertMlist(msg
							.getMlistTableName(), objInfo);
					if (testSend == 1) {
						Util.logger.info("ICOM RegisterHandle@insertMlist"
								+ " Send to VMS Success: USER_ID = "
								+ msg.getUserid() + ", COMMAND_CODE: ="
								+ msg.getCommandCode() + ", insertToMlist = "
								+ msg.getMlistTableName());
					} else {
						Util.logger.info("ICOM RegisterHandle@insertMlist"
								+ " Send to VMS FAILURE: USER_ID = "
								+ msg.getUserid() + ", COMMAND_CODE: ="
								+ msg.getCommandCode() + ", insertToMlist = "
								+ msg.getMlistTableName());
					}
				}
			}

		}

		// Dang ky lai nhung van duoc KM
		// get from mlist_service_cancel and insert into mlist_service
		// delete in mlist_service
		// insert in list_send to response MT service
		if (msg.getChargingPackage() == 3) {
			msg.setUsertext(keyword.getSubMsg());
			this.InsertMlistCancel2Mlist(msg);

			// Delete in MLIST_CANCEL
			String tableCancel = msg.getMlistTableName() + "_cancel";
			this.deleteInTable(tableCancel, msg.getUserid(), msg
					.getCommandCode(), msg.getServiceid());

			// Delete in SUBCRIBER_CANCEL and Insert in SUBCRIBER
			MlistInfo mlistInfo = this.getMlistInfoObject(msg
					.getMlistTableName(), msg.getUserid(), msg.getServiceid(),
					msg.getCommandCode());
			this.deleteInTable(Constants.MLIST_SUBCRIBER_CANCEL, msg
					.getUserid(), msg.getCommandCode(), msg.getServiceid());

			this.insertToMlistSubcriber(Constants.MLIST_SUBCRIBER, mlistInfo);
			// insert to List_Send
			this.insertToListSend(mlistInfo, "0", Constants.PACKET_VMS);

			if (msg.getSubCP() == 1) { // ICOM
				// send to VMS
				int testSend = MTSenderVMS.insertToMlistFromMlist(msg
						.getMlistTableName(), msg.getMlistTableName()
						+ "_cancel", Constants.REGISTRY_AGAIN, msg.getUserid(),
						msg.getCommandCode(), msg.getServiceid());

				if (testSend == 1) {
					Util.logger
							.info("ICOM RegisterHandle@insertToMlistFromMlist"
									+ " Send to VMS Success: USER_ID = "
									+ msg.getUserid() + ", COMMAND_CODE: ="
									+ msg.getCommandCode()
									+ ", insertToMlist = "
									+ msg.getMlistTableName());
				} else {
					Util.logger
							.info("ICOM RegisterHandle@insertToMlistFromMlist"
									+ " Send to VMS FAILURE: USER_ID = "
									+ msg.getUserid() + ", COMMAND_CODE: ="
									+ msg.getCommandCode()
									+ ", insertToMlist = "
									+ msg.getMlistTableName());
				}
			}
		}

		messages.add(msg);
		return messages;
	}

	public Collection<MsgObject> UnRegisterHandle(MsgObject msg, Keyword keyword) {
		Collection<MsgObject> messages = new ArrayList<MsgObject>();

		// delete in mlist_service
		// insert in mlist_cancel
		String mlistTable = this.getMlistTableName(msg.getCommandCode());
		String mlistTableCancel = mlistTable + "_cancel";
		msg.setUsertext(keyword.getUnsubMsg());
		MlistInfo mlistInfo = this.getMlistInfoObject(mlistTable, msg
				.getUserid(), msg.getServiceid(), msg.getCommandCode());

		if (mlistInfo == null) {
			// Chua dang ky => ko the huy
			msg.setUsertext(keyword.getWarMsg());
		} else {

			insertMlistCancelFromMlistInfo(mlistTableCancel, mlistInfo);
			deleteInMlist(mlistTable, mlistInfo.getUserId(), mlistInfo
					.getCommandCode(), mlistInfo.getServiceId());

			// Huy trong Subcriber
			this.deleteInTable(Constants.MLIST_SUBCRIBER,
					mlistInfo.getUserId(), mlistInfo.getCommandCode(),
					mlistInfo.getServiceId());
			// Insert in Mlist_Sucriber_Cancel
			this.insertToMlistSubcriber(Constants.MLIST_SUBCRIBER_CANCEL,
					mlistInfo);

			if (msg.getSubCP() == 1) {
				// Dong bo du lieu ben VMS
				int testSend = MTSenderVMS.insertToMlistFromMlist(
						mlistTableCancel, mlistTable,
						Constants.DESTROY_SUBCRIBER, mlistInfo.getUserId(),
						mlistInfo.getCommandCode(), mlistInfo.getServiceId());
				if (testSend == 1) {
					Util.logger
							.info("ICOM RegisterHandle@insertToMlistFromMlist"
									+ " Send to VMS Success: USER_ID = "
									+ msg.getUserid() + ", COMMAND_CODE: ="
									+ msg.getCommandCode()
									+ ", insertToMlist = "
									+ msg.getMlistTableName());
				} else {
					Util.logger
							.info("ICOM RegisterHandle@insertToMlistFromMlist"
									+ " Send to VMS FAILURE: USER_ID = "
									+ msg.getUserid() + ", COMMAND_CODE: ="
									+ msg.getCommandCode()
									+ ", insertToMlist = "
									+ msg.getMlistTableName());
				}
			}
		}

		messages.add(msg);
		return messages;
	}

	/***
	 * GET Noi dung tin MT khi user da dang ky truoc do
	 * 
	 * @param commandCode
	 * @return
	 */
	public String getExistMessage(String commandCode) {
		// SELECT existmessage FROM s2mplus.keywords WHERE service_name = 'TVBT'
		// limit 1;
		String existMsg = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT existmessage FROM keywords WHERE service_name = '"
				+ commandCode + "' limit 1";

		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
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
					existMsg = rs.getString("existmessage");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return existMsg;
	}

	/***
	 * GET Noi dung tin MT khuyen mai
	 * 
	 * @param commandCode
	 * @return
	 */
	public String getPromotionMsg(String commandCode) {
		// SELECT existmessage FROM s2mplus.keywords WHERE service_name = 'TVBT'
		// limit 1;
		String promotionMsg = "";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT promomessage FROM keywords WHERE service_name = '"
				+ commandCode + "' limit 1";

		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
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
					promotionMsg = rs.getString("promomessage");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return promotionMsg;
	}

	/***
	 * get so ngay/tin nhan khuyen mai
	 * 
	 * @param commandCode
	 * @return
	 */
	public int getNumberFreeDate(String commandCode) {
		// SELECT existmessage FROM s2mplus.keywords WHERE service_name = 'TVBT'
		// limit 1;
		int numberFreeDate = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "SELECT number_free FROM services WHERE services = '"
				+ commandCode + "' limit 1";

		// System.out.println("getExistMessage QUERRY: " + sqlQuery);
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
					numberFreeDate = rs.getInt("number_free");
				}
			}
		} catch (SQLException ex3) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - Check Registry. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return numberFreeDate;
	}

	/***
	 * insert into mlist_service
	 * 
	 * @param msg
	 */
	public int insertMlistService(MsgObject msg, Keyword keyword,
			String dateRetry) {

		String tableName = msg.getMlistTableName();
		this.deleteInMlist(tableName, msg.getUserid(), msg.getCommandCode(),
				msg.getServiceid());

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		int msg1mt = 1; // Message Type
		String mtfree = "0";

		String sqlInsert = "INSERT INTO "
				+ tableName
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,ACTIVE,CHANNEL_TYPE,REG_COUNT,DATE_RETRY) values ('"
				+ msg.getUserid()
				+ "','"
				+ msg.getServiceid()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ msg.getOption()
				+ "',"
				+ 0
				+ ",'"
				+ msg.getLast_code()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + msg.getCommandCode()
				+ "','" + msg.getLongRequestid() + "','" + msg1mt + "','"
				+ msg.getMobileoperator() + "','" + 0 + "'," + mtfree + ","
				+ keyword.getDuration() + "," + keyword.getAmount() + ","
				+ msg.getContentId() + ",'" + msg.getCommandCode() + "','"
				+ msg.getCompany_id() + "'," + 0 + "," + msg.getChannelType()
				+ "," + 1 + "," + "'" + dateRetry + "'" + ")";

		Util.logger.info("@insertMlistService@SQL Insert: " + sqlInsert);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlInsert) < 0) {
				Util.logger.error("insertMlistService@"
						+ ": uppdate Statement: Insert  " + tableName
						+ " Failed:" + sqlInsert);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("insertMlistService@:Insert  " + tableName
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	/****
	 * 
	 * @param msg
	 * @param keyword
	 * @param dateRetry
	 * @return
	 */
	public int insertMlistFromChargeResult(ChargeResultInfo rslInfo) {

		String tableName = this.getMlistTableName(rslInfo.getCommandCode());

		this.deleteInMlist(tableName, rslInfo.getUserId(), rslInfo
				.getCommandCode(), rslInfo.getServiceId());

		String dateRetry = "";
		if (rslInfo.getDayNumber() == 30) {
			dateRetry = this.getDateAddMonth(1);
		} else {
			dateRetry = this.getDate(rslInfo.getDayNumber());
		}

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		int msg1mt = 1; // Message Type
		String mtfree = "0";

		String sqlQuery = "INSERT INTO "
				+ tableName
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,ACTIVE,CHANNEL_TYPE,REG_COUNT,DATE_RETRY) values ('"
				+ rslInfo.getUserId()
				+ "','"
				+ rslInfo.getServiceId()
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date())
				+ "','"
				+ 0
				+ "',"
				+ 0
				+ ",'"
				+ 0
				+ "','"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()) + "','" + rslInfo.getCommandCode()
				+ "','" + rslInfo.getRequestID() + "','" + msg1mt + "','"
				+ rslInfo.getMobileOperator() + "','" + 0 + "'," + mtfree + ","
				+ 0 + "," + rslInfo.getAmount() + "," + rslInfo.getContendID()
				+ ",'" + rslInfo.getCommandCode() + "','" + 1 + "'," + 0 + ","
				+ rslInfo.getChannelType() + "," + 1 + "," + "'" + dateRetry
				+ "'" + ")";

		Util.logger.info("@insertMlistService@SQL Insert: " + sqlQuery);

		try {

			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}

			if (DBUtil.executeSQL(connection, sqlQuery) < 0) {
				Util.logger.error("insertMlistService@"
						+ ": uppdate Statement: Insert  " + tableName
						+ " Failed:" + sqlQuery);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("insertMlistService@:Insert  " + tableName
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public MlistInfo convertChargeInfoToMlistInfo(ChargeResultInfo rslInfo) {

		String companyId = this.getCompanyId(rslInfo.getCommandCode());
		String dateRetry = "";
		if (rslInfo.getDayNumber() == 30) {
			dateRetry = this.getDateAddMonth(1);
		} else {
			dateRetry = this.getDate(rslInfo.getDayNumber());
		}
		
		PacketLimitedObj limitedObj = this.getPacketLimitedObj(rslInfo.getCommandCode());
		if(limitedObj != null){
			dateRetry = this.getDate(limitedObj.getDayNumb());
		}

		MlistInfo mlistInfo = new MlistInfo();

		mlistInfo.setUserId(rslInfo.getUserId());
		mlistInfo.setServiceId(rslInfo.getServiceId());
		mlistInfo.setToday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()));
		mlistInfo.setOptions("");
		mlistInfo.setFailures("0");
		mlistInfo.setLastCode("0");
		mlistInfo.setAutoTimeStamps(Timestamp.valueOf(mlistInfo.getToday()));
		mlistInfo.setCommandCode(rslInfo.getCommandCode());
		mlistInfo.setRequestId(rslInfo.getRequestID().toString());
		mlistInfo.setMessageType(rslInfo.getMsgType());
		mlistInfo.setMobiOperator(rslInfo.getMobileOperator());
		mlistInfo.setMtCount(0);
		mlistInfo.setMtFree(0);
		mlistInfo.setDuration(0);
		mlistInfo.setAmount(rslInfo.getAmount());
		mlistInfo.setContentId(Integer.parseInt(rslInfo.getContendID()));
		mlistInfo.setService(rslInfo.getServiceName());
		mlistInfo.setCompanyId(companyId);
		mlistInfo.setActive(1);
		mlistInfo.setChanelType(rslInfo.getChannelType());
		mlistInfo.setRegCount(1);
		mlistInfo.setDateRetry(dateRetry);

		return mlistInfo;
	}

	public int updateMlistDateRetry(ChargeResultInfo rslInfo) {

		String tableName = this.getMlistTableName(rslInfo.getCommandCode());

		String dateRetry = "";
		if (rslInfo.getDayNumber() == 30) {
			dateRetry = this.getDateAddMonth(1);
		} else {
			dateRetry = this.getDate(rslInfo.getDayNumber());
		}

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableName + " SET DATE_RETRY = '"
				+ dateRetry + "'" + " WHERE USER_ID = '" + rslInfo.getUserId()
				+ "'" + " AND COMMAND_CODE = '" + rslInfo.getCommandCode()
				+ "'" + " AND SERVICE_ID = '" + rslInfo.getServiceId() + "'";

		Util.logger.info("@updateMlistDateRetry@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateMlistDateRetry@"
						+ ": uppdate Statement: UPDATE  " + tableName
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistDateRetry@: UPDATE  " + tableName
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public int updateMlistActive1(String tableMlist, int id) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE " + tableMlist + " SET ACTIVE = 1 "
				+ " WHERE ID = " + id;

		Util.logger.info("@updateMlistActive1@SQL UPDATE: " + sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateMlistDateRetry@"
						+ ": uppdate Statement: UPDATE  " + tableMlist
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("updateMlistDateRetry@: UPDATE  " + tableMlist
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	public String getDate(int addDay) {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date today = new java.util.Date();
		Calendar gc = new GregorianCalendar();
		gc.setTime(today);
		gc.set(Calendar.DAY_OF_MONTH, gc.get(Calendar.DAY_OF_MONTH) + addDay);
		today = gc.getTime();
		return formatter.format(today);
	}

	public String getDateAddMonth(int addMonth) {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date today = new java.util.Date();
		Calendar gc = new GregorianCalendar();
		gc.setTime(today);
		gc.set(Calendar.MONTH, gc.get(Calendar.MONTH) + addMonth);
		today = gc.getTime();
		return formatter.format(today);
	}

	public String getDateAddDay(String strDate, int addDay) {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(Calendar.DAY_OF_MONTH, gc.get(Calendar.DAY_OF_MONTH) + addDay);
		date = gc.getTime();
		return formatter.format(date);
	}

	public int InsertMlistCancel2Mlist(MsgObject msg) {

		String sqlQuery = "insert into "
				+ msg.getMlistTableName()
				+ "(USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,AUTOTIMESTAMPS,COMMAND_CODE,REQUEST_ID"
				+ ",MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,MT_FREE,DURATION,AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID"
				+ ",ACTIVE,CHANNEL_TYPE,REG_COUNT,DATE_RETRY)"
				+ " select USER_ID,SERVICE_ID,DATE,OPTIONS,FAILURES,LAST_CODE,current_timestamp()"
				+ ",COMMAND_CODE,REQUEST_ID,MESSAGE_TYPE,MOBILE_OPERATOR,MT_COUNT,0,DURATION"
				+ ",AMOUNT,CONTENT_ID,SERVICE,COMPANY_ID,0,CHANNEL_TYPE,REG_COUNT +1,DATE_RETRY from "
				+ msg.getMlistTableName() + "_cancel WHERE USER_ID='"
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

	/***
	 * get all data from table vms_charge_package_result/recharge_packet_result
	 * and delete in table
	 */
	public void loadChargeResult(ChargeResultQueue rslQueue,
			String chargePacketResult) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR,"
				+ " COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, "
				+ "PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, "
				+ "RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, "
				+ "AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET FROM "
				+ chargePacketResult;

		ChargeResultInfo resultInfo;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// Util.logger.info("LoadChargeResult - SQL:" + sqlSelect);

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					resultInfo = null;
					resultInfo = new ChargeResultInfo();

					resultInfo.setId(rs.getInt("ID"));
					resultInfo.setUserId(rs.getString("USER_ID"));
					resultInfo.setServiceId(rs.getString("SERVICE_ID"));
					resultInfo.setMobileOperator(rs
							.getString("MOBILE_OPERATOR"));
					resultInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					resultInfo.setContentType(rs.getInt("CONTENT_TYPE"));
					resultInfo.setInfo(rs.getString("INFO"));
					resultInfo.setSubmitDate(rs.getTimestamp("SUBMIT_DATE"));
					resultInfo.setDoneDate(rs.getString("DONE_DATE"));
					resultInfo.setProcessResult(rs.getInt("PROCESS_RESULT"));
					resultInfo.setMsgType(rs.getInt("MESSAGE_TYPE"));
					resultInfo.setRequestID(rs.getBigDecimal("REQUEST_ID"));
					resultInfo.setMsgID(rs.getString("MESSAGE_ID"));
					resultInfo.setTotalSegments(rs.getInt("TOTAL_SEGMENTS"));
					resultInfo.setRetriesNumber(rs.getInt("RETRIES_NUM"));
					resultInfo.setInsertDate(rs.getTimestamp("INSERT_DATE"));
					resultInfo.setNotes(rs.getString("NOTES"));
					resultInfo.setServiceName(rs.getString("SERVICE_NAME"));
					resultInfo.setChannelType(rs.getInt("CHANNEL_TYPE"));
					resultInfo.setContendID(rs.getString("CONTENT_ID"));
					resultInfo.setAmount(rs.getInt("AMOUNT"));
					resultInfo.setDayNumber(rs.getInt("DAY_NUM"));
					resultInfo.setReslultCharge(rs.getInt("RESULT_CHARGE"));
					resultInfo.setIsThePacket(rs.getInt("IS_THE_PACKET"));

					rslQueue.add(resultInfo);
				}
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

	}

	public int deleteChargeResultByID(int ID, String tableChargePacket) {

		String sqlQuery = "DELETE FROM " + tableChargePacket + " WHERE ID = "
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

	public int sendMT(ChargeResultInfo msgObject) {
		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;

		DBPool dbpool = new DBPool();

		Util.logger.info("ServicePackage@sendMT\tuser_id:"
				+ msgObject.getUserId() + "\tservice_id:"
				+ msgObject.getServiceId() + "\tuser_Info:"
				+ msgObject.getInfo() + "\t message_Type:"
				+ msgObject.getMsgType() + "\t RequestID:"
				+ msgObject.getRequestID().toString() + "\t Chanel_Type:"
				+ msgObject.getChannelType() + "\tcommand_code:"
				+ msgObject.getCommandCode());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserId()
								+ "\tTO"
								+ msgObject.getServiceId()
								+ "\t"
								+ msgObject.getInfo()
								+ "\trequest_id:"
								+ msgObject.getRequestID().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, "
					+ "CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID,CHANNEL_TYPE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserId());
			statement.setString(2, msgObject.getServiceId());
			statement.setString(3, msgObject.getMobileOperator());
			statement.setString(4, msgObject.getCommandCode());
			statement.setInt(5, msgObject.getContentType());
			statement.setString(6, msgObject.getInfo());
			statement.setInt(7, msgObject.getMsgType());
			statement.setBigDecimal(8, msgObject.getRequestID());
			statement.setInt(9, msgObject.getChannelType());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ServicePackge@sendMT: Error@userid="
						+ msgObject.getUserId() + "@service_id="
						+ msgObject.getServiceId() + "@user_text="
						+ msgObject.getInfo() + "@message_type="
						+ msgObject.getMsgType() + "@request_id="
						+ msgObject.getRequestID().toString()
						+ "@channel_type=" + msgObject.getChannelType());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("ServicePackge@sendMT: Error@userid="
					+ msgObject.getUserId() + "@service_id="
					+ msgObject.getServiceId() + "@user_text="
					+ msgObject.getInfo() + "@message_type="
					+ msgObject.getMsgType() + "@request_id="
					+ msgObject.getRequestID().toString() + "@channel_type="
					+ msgObject.getChannelType());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ServicePackge@sendMT: Error@userid="
					+ msgObject.getUserId() + "@service_id="
					+ msgObject.getServiceId() + "@user_text="
					+ msgObject.getInfo() + "@message_type="
					+ msgObject.getMsgType() + "@request_id="
					+ msgObject.getRequestID().toString() + "@channel_type="
					+ msgObject.getChannelType());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public int deleteInMlist(String tableMlist, String userId,
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

	public int deleteInTable(String tableName, String userId,
			String commandCode, String serviceId) {

		String sqlQuery = "DELETE FROM " + tableName + " WHERE USER_ID = "
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

	public ArrayList<MlistInfo> getMlistInfo(String tableName) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT, DATE_RETRY"
				+ " FROM " + tableName;

		ArrayList<MlistInfo> arrMlistInfo = new ArrayList<MlistInfo>();
		MlistInfo mlistInfo;

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
					mlistInfo = new MlistInfo();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
					mlistInfo.setDateRetry(rs.getString("DATE_RETRY"));

					arrMlistInfo.add(mlistInfo);
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrMlistInfo;

	}

	public MlistInfo getMlistInfoObject(String tableName, String userId,
			String serviceId, String commandCode) {

		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, DATE, OPTIONS, "
				+ "FAILURES, LAST_CODE, AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, "
				+ "MESSAGE_TYPE, MOBILE_OPERATOR, MT_COUNT, MT_FREE, DURATION, AMOUNT, "
				+ "CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, CHANNEL_TYPE, REG_COUNT, DATE_RETRY"
				+ " FROM " + tableName + " WHERE USER_ID = '" + userId + "'"
				+ " AND SERVICE_ID = '" + serviceId + "' AND COMMAND_CODE = '"
				+ commandCode + "'";

		MlistInfo mlistInfo = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		Util.logger.info("getMlistInfoObject - SQL:" + sqlSelect);
		// System.out.println("getMlistInfoObject QUERRY: " + sqlSelect);
		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					mlistInfo = null;
					mlistInfo = new MlistInfo();
					mlistInfo.setId(rs.getInt("ID"));
					mlistInfo.setUserId(rs.getString("USER_ID"));
					mlistInfo.setServiceId(rs.getString("SERVICE_ID"));
					mlistInfo.setToday(rs.getString("DATE"));
					mlistInfo.setOptions(rs.getString("OPTIONS"));
					mlistInfo.setFailures(rs.getString("FAILURES"));
					mlistInfo.setLastCode(rs.getString("LAST_CODE"));
					mlistInfo.setAutoTimeStamps(rs
							.getTimestamp("AUTOTIMESTAMPS"));
					mlistInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					mlistInfo.setRequestId(rs.getString("REQUEST_ID"));
					mlistInfo.setMessageType(rs.getInt("MESSAGE_TYPE"));
					mlistInfo.setMobiOperator(rs.getString("MOBILE_OPERATOR"));
					mlistInfo.setMtCount(rs.getInt("MT_COUNT"));
					mlistInfo.setMtFree(rs.getInt("MT_FREE"));
					mlistInfo.setDuration(rs.getInt("DURATION"));
					mlistInfo.setAmount(rs.getInt("AMOUNT"));
					mlistInfo.setContentId(rs.getInt("CONTENT_ID"));
					mlistInfo.setService(rs.getString("SERVICE"));
					mlistInfo.setCompanyId(rs.getString("COMPANY_ID"));
					mlistInfo.setActive(rs.getInt("ACTIVE"));
					mlistInfo.setChanelType(rs.getInt("CHANNEL_TYPE"));
					mlistInfo.setRegCount(rs.getInt("REG_COUNT"));
					mlistInfo.setDateRetry(rs.getString("DATE_RETRY"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - getMlistInfo. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return mlistInfo;

	}

	public int insertMlistCancelFromMlistInfo(String mlistTable,
			MlistInfo mlistInfo) {

		this.deleteInTable(mlistTable, mlistInfo.getUserId(), mlistInfo
				.getCommandCode(), mlistInfo.getServiceId());

		int response = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ mlistTable
				+ " ( USER_ID, SERVICE_ID, DATE, OPTIONS, FAILURES, LAST_CODE, "
				+ "AUTOTIMESTAMPS, COMMAND_CODE, REQUEST_ID, MESSAGE_TYPE, MOBILE_OPERATOR, "
				+ "MT_COUNT, MT_FREE, DURATION, AMOUNT, CONTENT_ID, SERVICE, COMPANY_ID, ACTIVE, "
				+ "CHANNEL_TYPE, REG_COUNT, DATE_RETRY ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, mlistInfo.getUserId());
			stmt.setString(2, mlistInfo.getServiceId());
			stmt.setString(3, mlistInfo.getToday());
			stmt.setString(4, mlistInfo.getOptions());
			stmt.setString(5, mlistInfo.getFailures());
			stmt.setString(6, mlistInfo.getLastCode());
			stmt.setTimestamp(7, mlistInfo.getAutoTimeStamps());
			stmt.setString(8, mlistInfo.getCommandCode());
			stmt.setString(9, mlistInfo.getRequestId());
			stmt.setInt(10, mlistInfo.getMessageType());
			stmt.setString(11, mlistInfo.getMobiOperator());
			stmt.setInt(12, mlistInfo.getMtCount());
			stmt.setInt(13, mlistInfo.getMtFree());
			stmt.setInt(14, mlistInfo.getDuration());
			stmt.setInt(15, mlistInfo.getAmount());
			stmt.setInt(16, mlistInfo.getContentId());
			stmt.setString(17, mlistInfo.getService());
			stmt.setString(18, mlistInfo.getCompanyId());
			stmt.setInt(19, mlistInfo.getActive());
			stmt.setInt(20, mlistInfo.getChanelType());
			stmt.setInt(21, mlistInfo.getRegCount());
			stmt.setString(22, mlistInfo.getDateRetry());

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@insertMlistCancelFromMlistInfo: Error@userid="
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
			response = 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - insertMlistCancelFromMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			response = -1;
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - insertMlistCancelFromMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			response = -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return response;
	}

	public int insertInRechargePacket(MlistInfo mlistInfo, int isThePacket) {

		mlistInfo.setAutoTimeStamps(Timestamp.valueOf(this.getDate(0)));

		String tableName = "recharge_packet";

		this.deleteInTable(tableName, mlistInfo.getUserId(), mlistInfo
				.getCommandCode(), mlistInfo.getServiceId());

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ tableName
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, "
				+ "DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, RETRIES_NUM, "
				+ "NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, RESULT_CHARGE, IS_THE_PACKET ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, mlistInfo.getUserId());
			stmt.setString(2, mlistInfo.getServiceId());
			stmt.setString(3, mlistInfo.getMobiOperator());
			stmt.setString(4, mlistInfo.getCommandCode());
			stmt.setInt(5, mlistInfo.getContentId());
			stmt.setString(6, mlistInfo.getCommandCode());
			stmt.setTimestamp(7, mlistInfo.autoTimeStamps);
			stmt.setTimestamp(8, mlistInfo.autoTimeStamps);
			stmt.setInt(9, 1);
			stmt.setInt(10, mlistInfo.getMessageType());
			stmt.setBigDecimal(11, BigDecimal.valueOf(Integer
					.parseInt(mlistInfo.getRequestId())));
			stmt.setLong(12, mlistInfo.getMessageType());
			stmt.setInt(13, 1); // getRetries_num
			stmt.setString(14, "");
			stmt.setString(15, mlistInfo.getService());
			stmt.setInt(16, mlistInfo.getChanelType());
			stmt.setInt(17, mlistInfo.getContentId());
			stmt.setLong(18, mlistInfo.getAmount());
			stmt.setInt(19, 0);
			stmt.setInt(20, isThePacket);

			if (stmt.executeUpdate() != 1) {
				Util.logger.crisis("@insertInRechargePacket: Error@userid="
						+ mlistInfo.getUserId() + "@serviceid="
						+ mlistInfo.getServiceId() + "@usertext="
						+ mlistInfo.getCommandCode() + "@messagetype="
						+ mlistInfo.getMessageType() + "@requestid="
						+ mlistInfo.getRequestId());
				return -1;
			}
			return 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - insertInRechargePacket. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - insertInRechargePacket. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

	public ChargeResultInfo getChargeInfo(String tableCharge, String userId,
			String serviceId, String commandCode) {
		String sqlSelect = "SELECT ID, USER_ID, SERVICE_ID, MOBILE_OPERATOR,"
				+ " COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, DONE_DATE, "
				+ "PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, TOTAL_SEGMENTS, "
				+ "RETRIES_NUM, INSERT_DATE, NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, "
				+ "AMOUNT, DAY_NUM, RESULT_CHARGE, IS_THE_PACKET FROM "
				+ tableCharge + " WHERE USER_ID = '" + userId
				+ "' AND SERVICE_ID = '" + serviceId + "'"
				+ " AND COMMAND_CODE = '" + commandCode + "'";

		ChargeResultInfo resultInfo = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		// Util.logger.info("LoadChargeResult - SQL:" + sqlSelect);

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlSelect);

			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					resultInfo = new ChargeResultInfo();

					resultInfo.setId(rs.getInt("ID"));
					resultInfo.setUserId(rs.getString("USER_ID"));
					resultInfo.setServiceId(rs.getString("SERVICE_ID"));
					resultInfo.setMobileOperator(rs
							.getString("MOBILE_OPERATOR"));
					resultInfo.setCommandCode(rs.getString("COMMAND_CODE"));
					resultInfo.setContentType(rs.getInt("CONTENT_TYPE"));
					resultInfo.setInfo(rs.getString("INFO"));
					resultInfo.setSubmitDate(rs.getTimestamp("SUBMIT_DATE"));
					resultInfo.setDoneDate(rs.getString("DONE_DATE"));
					resultInfo.setProcessResult(rs.getInt("PROCESS_RESULT"));
					resultInfo.setMsgType(rs.getInt("MESSAGE_TYPE"));
					resultInfo.setRequestID(rs.getBigDecimal("REQUEST_ID"));
					resultInfo.setMsgID(rs.getString("MESSAGE_ID"));
					resultInfo.setTotalSegments(rs.getInt("TOTAL_SEGMENTS"));
					resultInfo.setRetriesNumber(rs.getInt("RETRIES_NUM"));
					resultInfo.setInsertDate(rs.getTimestamp("INSERT_DATE"));
					resultInfo.setNotes(rs.getString("NOTES"));
					resultInfo.setServiceName(rs.getString("SERVICE_NAME"));
					resultInfo.setChannelType(rs.getInt("CHANNEL_TYPE"));
					resultInfo.setContendID(rs.getString("CONTENT_ID"));
					resultInfo.setAmount(rs.getInt("AMOUNT"));
					resultInfo.setDayNumber(rs.getInt("DAY_NUM"));
					resultInfo.setReslultCharge(rs.getInt("RESULT_CHARGE"));
					resultInfo.setIsThePacket(rs.getInt("IS_THE_PACKET"));

				}
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

		return resultInfo;

	}

	public int insertToMlistSubcriber(String subcriberTable, MlistInfo mlistInfo) {

		String subcriberTableOther = "";
		if (subcriberTable.equals(Constants.MLIST_SUBCRIBER)) {
			subcriberTableOther = Constants.MLIST_SUBCRIBER_CANCEL;
		} else if (subcriberTable.equals(Constants.MLIST_SUBCRIBER_CANCEL)) {
			subcriberTableOther = Constants.MLIST_SUBCRIBER;
		}

		deleteInTable(subcriberTableOther, mlistInfo.getUserId(), mlistInfo
				.getCommandCode(), mlistInfo.getServiceId());
		deleteInTable(subcriberTable, mlistInfo.getUserId(), mlistInfo
				.getCommandCode(), mlistInfo.getServiceId());

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ subcriberTable
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
			stmt.setString(1, mlistInfo.getUserId());
			stmt.setString(2, mlistInfo.getServiceId());
			stmt.setString(3, mlistInfo.getToday());
			stmt.setString(4, mlistInfo.getOptions());
			stmt.setString(5, mlistInfo.getFailures());
			stmt.setString(6, mlistInfo.getLastCode());
			stmt.setTimestamp(7, mlistInfo.getAutoTimeStamps());
			stmt.setString(8, mlistInfo.getCommandCode());
			stmt.setString(9, mlistInfo.getRequestId());
			stmt.setInt(10, mlistInfo.getMessageType());
			stmt.setString(11, mlistInfo.getMobiOperator());
			stmt.setInt(12, mlistInfo.getMtCount());
			stmt.setInt(13, mlistInfo.getMtFree());
			stmt.setInt(14, mlistInfo.getDuration());
			stmt.setInt(15, mlistInfo.getAmount());
			stmt.setInt(16, mlistInfo.getContentId());
			stmt.setString(17, mlistInfo.getService());
			stmt.setString(18, mlistInfo.getCompanyId());
			stmt.setInt(19, mlistInfo.getActive());
			stmt.setInt(20, mlistInfo.getChanelType());
			stmt.setInt(21, mlistInfo.getRegCount());

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@insertMlistCancelFromMlistInfo: Error@userid="
								+ mlistInfo.getUserId()
								+ "@serviceid="
								+ mlistInfo.getServiceId()
								+ "@usertext="
								+ mlistInfo.getCommandCode()
								+ "@messagetype="
								+ mlistInfo.getMessageType()
								+ "@requestid="
								+ mlistInfo.getRequestId().toString());
				return -1;
			}
			return 1;
		} catch (SQLException ex3) {
			Util.logger
					.error("Service Package - insertMlistCancelFromMlistInfo. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package - insertMlistCancelFromMlistInfo. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

	}

	public int insertToListSend(MlistInfo mlistInfo, String timeDelivery,
			int vmsOrIcom) {

		String tableListSend = "list_send";

		int response = -1;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		String sqlQuery = "INSERT INTO "
				+ tableListSend
				+ " ( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, OPTIONS, "
				+ "MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, INSERT_DATE, SERVICE_NAME, CHANNEL_TYPE, "
				+ "CONTENT_ID, AMOUNT, TIME_DELIVERY, COMPANY_ID, IS_THE_SEND, LAST_CODE ) "
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
			stmt.setInt(16, vmsOrIcom);
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
					.error("Service Package VMS - insertToListSend. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			response = -1;
		} catch (Exception ex2) {
			Util.logger
					.error("Service Package VMS - insertToListSend. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			response = -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return response;
	}

	public String getContentPacketAmount(String commandCode, int amount) {
		
		String content = "";
		String tableName = "amount_content_packet";
		String sqlSelect = "SELECT  ID, COMMAND_CODE, AMOUNT, CONTENT "
				+ " FROM " + tableName + " WHERE COMMAND_CODE = '" 
				+ commandCode + "' AND AMOUNT = " + amount;

		Util.logger.info("SERVICE-PACKET@ getContentPacketAmount@ SQL =  " + sqlSelect);
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
					content = rs.getString("CONTENT");
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Service Package - getContentPacketAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - getContentPacketAmount. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return content;
	}
	
	public PacketLimitedObj getPacketLimitedObj(String commandCode){
		
		PacketLimitedObj limitObj = null;

		String tableName = "packet_limited";
		String sqlSelect = "SELECT  ID, COMMAND_CODE, NUMBER_MT, DAY_NUMB "
				+ " FROM " + tableName + " WHERE COMMAND_CODE = '" 
				+ commandCode + "'";

		Util.logger.info("SERVICE-PACKET@ getPacketLimitedObjt@ SQL =  " + sqlSelect);
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
					limitObj = new PacketLimitedObj();
					limitObj.setId(rs.getInt("ID"));
					limitObj.setCommandCode(rs.getString("COMMAND_CODE"));
					limitObj.setNumberMT(rs.getInt("NUMBER_MT"));
					limitObj.setDayNumb(rs.getInt("DAY_NUMB"));
				}
			}

		} catch (SQLException ex3) {
			Util.logger.error("Service Package - getPacketLimitedObjt. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("Service Package - getPacketLimitedObjt. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		
		return limitObj;
		
	}
	


}
