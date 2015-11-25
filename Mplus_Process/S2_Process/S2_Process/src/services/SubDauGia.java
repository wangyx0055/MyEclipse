package services;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.QuestionManager;
import icom.common.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import daugia.DGConstants;

import DAO.MListDAO;
import DAO.WinnerDailyDAO;
import DTO.MlistInfoDTO;

public class SubDauGia extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		Util.logger.info("Starting Handle MO with user= "
				+ msgObject.getUserid() + ", info=" + msgObject.getUsertext());
		try {
			// xac dinh mlist
			HashMap _option = Util.getParametersAsString(keyword.getOptions());
			String MLIST = "x";
			MLIST = Util.getStringfromHashMap(_option, "mlist", "x");
			String dbContent = Util.getStringfromHashMap(_option, "dbcontent",
					"x").trim();
			if ("x".equalsIgnoreCase(MLIST) || "x".equalsIgnoreCase(dbContent)) {
				Util.logger
						.info("Khong xac dinh duoc MLIst hoac dbcontent duoc get ra tu param options:"
								+ " "
								+ msgObject.getUserid()
								+ ","
								+ keyword.getOptions());
				return null;
			} else {
				msgObject.setCommandCode(keyword.getService_ss_id());
				// kiem tra ton tai trong tap mlist
				if (!MListDAO.checkMlist(MLIST, msgObject.getUserid(),
						dbContent)) {

					MlistInfoDTO mlist = new MlistInfoDTO(msgObject);
					// kiem tra khach hang ton tai trong table mlist_cancel
					mlist = MListDAO.getUserDauGia(MLIST + "_cancel", msgObject
							.getUserid(), dbContent);
					boolean bCheckAgain = false;
					if (mlist != null) {
						mlist.setRegCount(mlist.getRegCount() + 1);
						bCheckAgain= true;
					} else {
						mlist = new MlistInfoDTO(msgObject);
						mlist.setRegCount(0);
						mlist.setIsPushAd(0);
						mlist.setTotalScore(0);
						mlist.setTotalScoreByDay(0);
						mlist.setTotalScoreByWeek(0);

					}
					MListDAO.insertMListDGTichDiem(mlist, MLIST, dbContent);
					// thuc hien xoa trong list_cancel
					MListDAO.deleteMlist(MLIST + "_cancel", msgObject
							.getUserid(), dbContent);
					msgObject.setIsIcom(keyword.getIsIcom());
					MListDAO.InsertSubcriber("mlist_subcriber", msgObject);
					// xoa trong mlist_subcriber_cancel
					MListDAO.deleteMlist("mlist_subcriber_cancel", msgObject
							.getUserid(), "gateway");

					// tra ve thong tin dang ky thanh cong cho khach hang

					msgObject.setUsertext(keyword.getSubMsg());
					if(bCheckAgain)
						msgObject.setUsertext((Constants._prop.getProperty("mtRegAgain")));
					Sender.msgPushMTQueue.add(msgObject);
					/*
					 * if (bCheckIsFirst) { // sinh gia may man Calendar cal =
					 * Calendar.getInstance(); cal.setTime(new Date());
					 * cal.set(Calendar.DATE, -1); String oldDate = new
					 * SimpleDateFormat("yyyy-MM-dd") .format(cal.getTime());
					 * 
					 * String dgAmountWinnerOld = WinnerDailyDAO
					 * .getDgAmountWinner(oldDate, dbContent); if
					 * (dgAmountWinnerOld.equals("")) { dgAmountWinnerOld =
					 * "1000"; } int lengthMsisdn =
					 * msgObject.getUserid().length(); int idgAmountWinnerOld =
					 * Integer .parseInt(dgAmountWinnerOld); int iBonus =
					 * Integer.parseInt(msgObject.getUserid()
					 * .substring(lengthMsisdn - 3, lengthMsisdn)); int
					 * iAmountLucky = idgAmountWinnerOld + iBonus; String
					 * dgAmountLucky = iAmountLucky + "000";
					 * 
					 * msgObject.setChannelType(6); msgObject.setAmount(5000);
					 * 
					 * insertDauGiaChargeResult(
					 * DGConstants.TABLE_DG_CHARGE_RESULT, msgObject,
					 * dgAmountLucky);
					 * 
					 * 
					 * }
					 */
				} else {
					// truong hop khong phai tra tin theo goi
					msgObject.setUsertext(keyword.getExistMsg());
					Sender.msgPushMTQueue.add(msgObject);
				}
			}

		} catch (Exception ex) {
			Util.logger.error("@Error at class sub mohandles: "
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
		}
		Util.logger.info("End  Handle MO with user " + msgObject.getUserid());
		return null;
	}

	private static int insertDauGiaChargeResult(String tableName,
			MsgObject msg, String strAmount) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();
		msg.setTSubmitTime(new Timestamp(System.currentTimeMillis()));
		msg.setTDoneTime(new Timestamp(System.currentTimeMillis()));
		msg.setContenttype(0);
		msg.setProcess_result(0);
		msg.setMsgtype(0);
		msg.setMsg_id(0);
		msg.setRetries_num(0);
		msg.setTimeSendMO(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()));
		String sqlQuery = "INSERT INTO "
				+ tableName
				+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, SUBMIT_DATE, "
				+ "DONE_DATE, PROCESS_RESULT, MESSAGE_TYPE, REQUEST_ID, MESSAGE_ID, RETRIES_NUM, "
				+ "NOTES, SERVICE_NAME, CHANNEL_TYPE, CONTENT_ID, AMOUNT, RESULT_CHARGE, DGAMOUNT, TIME_SEND_MO ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

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
			stmt.setInt(16, 6);
			stmt.setInt(17, msg.getContentId());
			stmt.setLong(18, msg.getAmount());
			stmt.setInt(19, 0);
			stmt.setString(20, strAmount);
			stmt.setString(21, msg.getTimeSendMO());

			if (stmt.executeUpdate() != 1) {
				Util.logger
						.crisis("@DaugiaCommon InsertDGCharge: Error@userid="
								+ msg.getUserid() + "@serviceid="
								+ msg.getServiceid() + "@usertext="
								+ msg.getUsertext() + "@messagetype="
								+ msg.getMsgtype() + "@requestid="
								+ msg.getRequestid().toString());
				return -1;
			}

			Util.logger
					.info("@DaugiaCommon InsertDGCharge SUCCESSFUL !!! \n @ Query = "
							+ sqlQuery);

			return 1;
		} catch (SQLException ex3) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
			return -1;
		} catch (Exception ex2) {
			Util.logger.error("@DaugiaCommon InsertDGCharge. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
			return -1;
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}
	}

}
