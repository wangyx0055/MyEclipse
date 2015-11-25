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

public class ExeMTwinTmpLucky extends Thread {

	public void run() {

		while (Sender.processData) {

			WinnerTmpObj winLucky = this.getWinnerTmpLucky();
			if (winLucky != null) {

				DaugiaCommon commObj = new DaugiaCommon();

				WinnerTmpObj winBefore = this.getWinnerTmpBefore();
				if (winBefore != null) {
					String mtInfo = DGConstants.MT_WIN_TMP_LUCKY;
					try {
						mtInfo = MTDAO
								.getRandomMessage(DGProcess.hMessageReminder
										.get("69"), DGProcess.hMessageReminder
										.get("69").split(";").length);
					} catch (Exception e) {
						Util.logger.printStackTrace(e);
					}

					String userIdBefore = "0"
							+ winBefore.getUserId().substring(2, 6) + "xxx";

					mtInfo = mtInfo.replaceAll(
							DGConstants.STRING_REGEX_REPLACE_USER_ID,
							userIdBefore);
					mtInfo = mtInfo.replaceAll(
							DGConstants.STRING_REGEX_REPLACE_AMOUNT, winLucky
									.getAmount());

					MsgObject msgObj = getMsgObj(mtInfo, winLucky);
					if (msgObj.getCommandCode().equals("DAUGIA"))
						mtInfo = mtInfo.replaceAll(DGConstants.COMMAND_CODE,
								"DG");
					else {
						mtInfo = mtInfo.replaceAll(DGConstants.COMMAND_CODE,
								msgObj.getCommandCode());
					}
					msgObj.setUsertext(mtInfo);
					commObj.sendMT(msgObj);

					updateSendMT(winLucky.getId(), 1);

				}

				ArrayList<WinnerTmpObj> arrWinLucky = getArrayWinnerTmpLucky();
				for (int i = 0; i < arrWinLucky.size(); i++) {

					WinnerTmpObj winTmp = arrWinLucky.get(i);
					if (winTmp.getAmount().equals(winLucky.getAmount())) {
						continue;
					}
					// win before
					String mtInfo = "";
					if (getAmountCount(winTmp.getAmount()) > 1) {

						 mtInfo = DGConstants.MT_SAME_AMOUNT;
						try {
							mtInfo = MTDAO.getRandomMessage(
									DGProcess.hMessageReminder.get("75"),
									DGProcess.hMessageReminder.get("75").split(
											";").length);
						} catch (Exception e) {
							Util.logger.printStackTrace(e);
						}
					} else {
						// mtInfo = DGConstants.MT_UNDER_YOUR_AMOUNT;
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
					mtInfo = mtInfo.replaceAll(
							DGConstants.STRING_REGEX_REPLACE_AMOUNT, winTmp
									.getAmount());
					
					mtInfo = mtInfo.replaceAll(
							DGConstants.STRING_REGEX_REPLACE_GIA_SP, winTmp
									.getAmount());

					mtInfo = mtInfo.replaceAll(
							DGConstants.STRING_REGEX_REPLACE_TEN_SP, spDG
									.getTenSP());
					DaugiaCommon common = new DaugiaCommon();
					String commandCode = common.getCommandCodeByUserId(winTmp
							.getUserId());
					if (commandCode.equals("DAUGIA"))
						mtInfo = mtInfo.replace(DGConstants.COMMAND_CODE, "DG");
					else {
						mtInfo = mtInfo.replace(DGConstants.COMMAND_CODE,
								commandCode);
					}

					MsgObject msgObjBerfore = getMsgObj(mtInfo, winTmp);

					commObj.sendMT(msgObjBerfore);
					updateSendMT(winTmp.getId(), 2);

				}

			}

			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

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

	private String getRequestId() {
		Calendar now = Calendar.getInstance();
		return new java.text.SimpleDateFormat("yyMMHHmmss").format(now
				.getTime());
	}

	private WinnerTmpObj getWinnerTmpLucky() {

		WinnerTmpObj winTmp = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,amount,send_mt "
				+ " FROM daugia_winner_tmp WHERE send_MT = 0 AND type = 1 "
				+ " LIMIT 1";
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

					winTmp = new WinnerTmpObj();
					winTmp.setId(rs.getInt("id"));
					winTmp.setUserId(rs.getString("user_id"));
					winTmp.setAmount(rs.getString("amount"));
					winTmp.setIsSendMT(rs.getInt("send_mt"));

				}
			} else {
				Util.logger
						.error("ExeMTwinTmpLucky - getWinnerTmp : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("ExeMTwinTmpLucky - getWinnerTmp. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ExeMTwinTmpLucky - getWinnerTmp. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return winTmp;

	}

	private ArrayList<WinnerTmpObj> getArrayWinnerTmpLucky() {

		ArrayList<WinnerTmpObj> arrWin = new ArrayList<WinnerTmpObj>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,amount,send_mt "
				+ " FROM daugia_winner_tmp WHERE send_MT = 1 AND type = 1 ";
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
					WinnerTmpObj winTmp = null;
					winTmp = new WinnerTmpObj();
					winTmp.setId(rs.getInt("id"));
					winTmp.setUserId(rs.getString("user_id"));
					winTmp.setAmount(rs.getString("amount"));
					winTmp.setIsSendMT(rs.getInt("send_mt"));

					arrWin.add(winTmp);

				}
			} else {
				Util.logger
						.error("ExeMTwinTmpLucky - getArrayWinnerTmpLucky : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("ExeMTwinTmpLucky - getArrayWinnerTmpLucky. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("ExeMTwinTmpLucky - getWinnerTmp. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrWin;

	}

	private WinnerTmpObj getWinnerTmpBefore() {

		WinnerTmpObj winTmp = null;

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT id,user_id,amount,send_mt "
				+ " FROM daugia_winner_tmp WHERE send_MT = 1 AND type = 0 "
				+ " ORDER BY id DESC LIMIT 1";
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

					winTmp = new WinnerTmpObj();
					winTmp.setId(rs.getInt("id"));
					winTmp.setUserId(rs.getString("user_id"));
					winTmp.setAmount(rs.getString("amount"));
					winTmp.setIsSendMT(rs.getInt("send_mt"));

				}
			} else {
				Util.logger
						.error("ExeMTwinTmpLucky - getWinnerTmpBefore : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger
					.error("ExeMTwinTmpLucky - getWinnerTmpBefore. SQLException:"
							+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger
					.error("ExeMTwinTmpLucky - getWinnerTmpBefore. SQLException:"
							+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return winTmp;

	}

	private int updateSendMT(int id, int numberMT) {

		int ireturn = 1;
		Connection connection = null;
		DBPool dbpool = new DBPool();

		String sqlUpdate = "UPDATE daugia_winner_tmp " + "SET send_mt = "
				+ numberMT + " WHERE id = " + id;

		Util.logger.info("ExeMTwinTmpLucky @ updateSendMT @ SQL UPDATE: "
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
			Util.logger.error("ExeMTwinTmpLucky @ updateSendMT @: UPDATE  "
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

}
