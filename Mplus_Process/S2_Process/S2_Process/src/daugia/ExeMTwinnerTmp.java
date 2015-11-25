package daugia;

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
import java.util.ArrayList;
import java.util.Calendar;

import DAO.MTDAO;

public class ExeMTwinnerTmp extends Thread {

	public void run() {

		while (Sender.getData) {

			ArrayList<WinnerTmpObj> arrWinnerTmp = getWinnerTmp();

			if (arrWinnerTmp.size() == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			WinnerTmpObj minTmp = arrWinnerTmp.get(0);

			handleTmpWinner(minTmp.getAmount(), minTmp, true);

			for (int i = 1; i < arrWinnerTmp.size(); i++) {

				WinnerTmpObj winTmp = arrWinnerTmp.get(i);
				String amountTmp = winTmp.getAmount();
				if (winTmp.getUserId().equals(minTmp.getUserId())) {

					updateSendMT(winTmp.getId());

				} else {

					handleTmpWinner(amountTmp, winTmp, false);

				}

			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private ArrayList<WinnerTmpObj> getWinnerTmp() {

		ArrayList<WinnerTmpObj> arrWinTmp = new ArrayList<WinnerTmpObj>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,amount,send_mt "
				+ " FROM daugia_winner_tmp WHERE send_MT = 0 AND type = 0 "
				+ " ORDER BY id DESC";
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
					WinnerTmpObj tmpObj = new WinnerTmpObj();
					tmpObj.setId(rs.getInt("id"));
					tmpObj.setUserId(rs.getString("user_id"));
					tmpObj.setAmount(rs.getString("amount"));
					tmpObj.setIsSendMT(rs.getInt("send_mt"));
					arrWinTmp.add(tmpObj);
				}
			} else {
				Util.logger
						.error("ExeMTwinnerTmp - getWinnerTmp : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("ExeMTwinnerTmp - getWinnerTmp. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ExeMTwinnerTmp - getWinnerTmp. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrWinTmp;

	}

	private int updateSendMT(int id) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE daugia_winner_tmp "
				+ "SET send_mt = 1 WHERE id = " + id;

		Util.logger.info("ExeMTwinnerTmp @ updateSendMT @ SQL UPDATE: "
				+ sqlUpdate);

		try {

			connection = dbpool.getConnectionGateway();

			if (DBUtil.executeSQL(connection, sqlUpdate) < 0) {
				Util.logger.error("updateSendWeeklyMT@"
						+ ": uppdate Statement: UPDATE  Winner Tmp"
						+ " Failed:" + sqlUpdate);
				ireturn = -1;
			}
		} catch (Exception ex) {
			Util.logger.error("ExeMTwinnerTmp @ updateSendMT @: UPDATE  "
					+ " Failed");
			ireturn = -1;
			Util.logger.printStackTrace(ex);
		} finally {
			dbpool.cleanup(connection);
		}
		return ireturn;
	}

	private int getAmountCount(String amount) {

		int count = 0;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT COUNT(*) AS number FROM "
				+ "daugia_amount WHERE DG_AMOUNT = '" + amount + "'";

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
					count = rs.getInt("number");
				}
			} else {
				Util.logger
						.error("ExeMTwinnerTmp - getAmountCount : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("ExeMTwinnerTmp - getAmountCount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ExeMTwinnerTmp - getAmountCount. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return count;

	}

	private MsgObject getMsgObj(String info, WinnerTmpObj winTmp) {

		MsgObject msgObj = new MsgObject();
		DaugiaCommon common = new DaugiaCommon();
		String commandCode = common.getCommandCodeByUserId(winTmp.getUserId());

		msgObj.setUserid(winTmp.getUserId());
		msgObj.setMobileoperator("VMS");
		msgObj.setChannelType(0);
		msgObj.setServiceid("9209");
		msgObj.setRequestid(new BigDecimal(getRequestId()));
		msgObj.setCommandCode(commandCode);
		msgObj.setContenttype(0);
		msgObj.setUsertext(info);
		msgObj.setKeyword(commandCode);
		msgObj.setServiceName(commandCode);

		return msgObj;
	}

	public static String getRequestId() {
		Calendar now = Calendar.getInstance();
		return new java.text.SimpleDateFormat("yyMMHHmmss").format(now
				.getTime());
	}

	private void handleTmpWinner(String amountTmp, WinnerTmpObj winTmp,
			Boolean isMin) {

		String mtInfo = "";

		if (getAmountCount(amountTmp) > 1) {

			// mtInfo = DGConstants.MT_SAME_AMOUNT;
			try {
				mtInfo = MTDAO.getRandomMessage(DGProcess.hMessageReminder
						.get("75"), DGProcess.hMessageReminder.get("75").split(
						";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}

		} else {
			if (isMin)
				return;
			//mtInfo = DGConstants.MT_UNDER_YOUR_AMOUNT;
			
			try {
				mtInfo = MTDAO.getRandomMessage(
					DGProcess.hMessageReminder.get("68"),
					DGProcess.hMessageReminder.get("68").split(
							";").length);
			} catch (Exception e) {
				Util.logger.printStackTrace(e);
			}

		}

		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		SanPhamDG spDG = spMng.getSanPhamDG();
		if (spDG == null)
			return;

		mtInfo = mtInfo.replaceAll(DGConstants.STRING_REGEX_REPLACE_GIA_SP,
				amountTmp);

		mtInfo = mtInfo.replaceAll(DGConstants.STRING_REGEX_REPLACE_TEN_SP,
				spDG.getTenSP());
		// tuannq add
		DaugiaCommon common = new DaugiaCommon();
		String commandCode = common.getCommandCodeByUserId(winTmp.getUserId());

		mtInfo = mtInfo.replaceAll(DGConstants.COMMAND_CODE, commandCode);

		DaugiaCommon dgComm = new DaugiaCommon();
		dgComm.sendMT(getMsgObj(mtInfo, winTmp));

		updateSendMT(winTmp.getId());

	}

}
