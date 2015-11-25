package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import DAO.MTDAO;



import icom.Constants;
import icom.DBPool;
import icom.common.Util;

public class DGProcess extends Thread {

	public static Hashtable<String, String> hMessageReminder =  new Hashtable<String, String>();
	@Override
	public void run() {
		
		Util.logger.info("### START DAUGIA PROCESS..... #####");
		
		hMessageReminder =  MTDAO.getMessageReminder();
		try {

			AmountQueue amountQueue = AmountQueue.getInstance();
			amountQueue.inithAmount();

			DGConstants.DG_MODE = Integer.parseInt(Constants._prop.getProperty(
					"DG_MODE").trim());

			Util.logger.info("DAUGIA_MODE = " + DGConstants.DG_MODE);

			try {
				DGConstants.DAUGIA_DURATION_TIME_CHECK_BLACK_LIST = Integer
						.parseInt(Constants._prop.getProperty(
								"DAUGIA_DURATION_TIME_CHECK_BLACK_LIST").trim());
				DGConstants.DAUGIA_NUMBERMO_INVALID = Integer
						.parseInt(Constants._prop.getProperty(
								"DAUGIA_NUMBERMO_INVALID").trim());
			} catch (Exception ex) {

			}

			int numberDK = Integer.parseInt(Constants._prop.getProperty(
					"mtqueue_daugia_dkhuy").trim());
			int numberPUSH = Integer.parseInt(Constants._prop.getProperty(
					"mtqueue_daugia_pushtudong").trim());

			int numberPushWin = Integer.parseInt(Constants._prop.getProperty(
					"mtqueue_push_trungthuong").trim());

			if (numberDK > 0) {
				DGConstants.MTQUEUE_DKHUY = Constants.tblMTQueue + numberDK;
			}

			Util.logger.info("DAUGIA - TABLE MT_QUEUE DK + HUY + DAUGIA = "
					+ DGConstants.MTQUEUE_DKHUY);

			if (numberPUSH > 0) {
				DGConstants.MTQUEUE_PUSH = Constants.tblMTQueue + numberPUSH;
			}

			Util.logger.info("DAUGIA - TABLE MT_QUEUE PUSH Tu dong = "
					+ DGConstants.MTQUEUE_PUSH);

			if (numberPushWin > 0) {
				DGConstants.MTQUEUE_WIN = Constants.tblMTQueue + numberPushWin;
			}

			Util.logger.info("DAUGIA - TABLE MT_QUEUE PUSH TRUNG THUONG = "
					+ DGConstants.MTQUEUE_WIN);

			DGConstants.DG_TIME_RESET = Integer.parseInt(Constants._prop
					.getProperty("DAUGIA_TIME_RESET").trim());

			Util.logger
					.info("DAUGIA TIME RESET = " + DGConstants.DG_TIME_RESET);

		} catch (Exception ex) {

		}

		try {
			DGConstants.NUMBER_REGCOUNT = Integer.parseInt(Constants._prop
					.getProperty("DAUGIA_NUMBER_REGCOUNT_PUSHMT").trim());

			DGConstants.DG_PROCESS_RESULTS_NUM = Integer
					.parseInt(Constants._prop.getProperty(
							"DG_PROCESS_RESULTS_NUM").trim());

		} catch (Exception ex) {
		}

		LoadSanPhamDG loadDG = new LoadSanPhamDG();
		loadDG.start();

		HandleChargeResult chargeResult = new HandleChargeResult();
		chargeResult.initThread();

		ResetDGMList resetMlist = new ResetDGMList();
		resetMlist.start();

		ResetDGAmount resetAmount = new ResetDGAmount();
		resetAmount.start();

		DGResponseMTDaily responseDaily = new DGResponseMTDaily();
		responseDaily.start();

		DGResponseMTWeekly responseWeekly = new DGResponseMTWeekly();
		responseWeekly.start();

		ExecutorUserWin userWin = new ExecutorUserWin();
		userWin.start();

		DGPushMTAdvertise pushMTAd = new DGPushMTAdvertise();
		pushMTAd.start();

		DGMTAlert alertMT = new DGMTAlert();
		alertMT.start();

		ExeMTwinnerTmp exWinnerTmp = new ExeMTwinnerTmp();
		exWinnerTmp.start();

		ExeCheckMaxSession exeCheckMaxSession = new ExeCheckMaxSession();
		exeCheckMaxSession.start();

		ExeMTNewSession exeNewSession = new ExeMTNewSession();
		exeNewSession.start();

		FindWinTmpLucky findLucky = new FindWinTmpLucky();
		findLucky.start();

		ExeMTwinTmpLucky mtLucky = new ExeMTwinTmpLucky();
		mtLucky.start();

		PushMTManual pushMTManual = new PushMTManual();
		pushMTManual.start();

	}

	public int insertDGAmount(ArrayList<Integer> arrAmount) {

		int iReturn = -1;
		String tableName = "daugia_amount_empty";

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "INSERT INTO " + tableName + "(amount_empty) "
				+ " VALUES ";

		for (int i = 0; i < arrAmount.size(); i++) {
			int amount = arrAmount.get(i);
			if (i > 0) {
				sqlQuery = sqlQuery + ",";
			}
			sqlQuery = sqlQuery + "(" + amount + ")";
		}

		try {
			if (connection == null) {
				connection = dbpool.getConnectionGateway();
			}
			stmt = connection.prepareStatement(sqlQuery);

			if (stmt.executeUpdate() == 1) {
				iReturn = 1;
			}

		} catch (SQLException ex3) {
			Util.logger.error("@DGAmountManager insertDGAmount. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("@DGAmountManager insertDGAmount SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return iReturn;

	}
}
