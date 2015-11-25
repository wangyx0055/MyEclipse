package daugia;

import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import DAO.MTDAO;

import servicesPkg.MlistInfo;
import servicesPkg.ServiceMng;

public class PushMTManual extends Thread {

	String USER_ALERT = "093825xxx";
	String AMOUNT_MIN = "10000";
	String AMOUNT_MAX = "30000";
	int AMOUNT_ADD = 50000;

	public void run() {

		SanPhamDGManager spMng = SanPhamDGManager.getInstance();
		DGAmountManager amountMng = new DGAmountManager();
		ServiceMng serviceMng = new ServiceMng();
		DaugiaCommon commonObj = new DaugiaCommon();

		while (Sender.getData) {

			SanPhamDG spDG = spMng.getSanPhamDG();
			if (spDG != null) {

				String currTime = Util.getCurrentDate();
				//currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				DateFormat formatter = new SimpleDateFormat("HH:mm");
				java.util.Date today = new java.util.Date();
				String currHour = formatter.format(today);

				String[] arrTimeManual = spDG.getTimeManual().split(";");

				// Check Time Manual
				if (isTimePushMT(arrTimeManual, currHour)) {

					String endDate = spDG.getEndDate().trim();
					String endHour = endDate.substring(11, 16).trim();
					String endDay = endDate.substring(0, 10).trim();
					String currDay = getToday();

					if (currDay.equals(endDay)) {
						if (endHour.compareTo(currHour) <= 0) {
							Util.logger
									.info("Ket thuc phien ==> Khong push MT Manual!!");
							try {
								Thread.sleep(5 * 60 * 1000);
							} catch (InterruptedException e) {
							}
							continue;
						}
					}
					if (spMng.checkTimeDG(currTime)) {

						ArrayList<String> arrUserNotManual = getUserNotManual();

						String info = commonObj
								.getContentPush(DGConstants.CONTENT_TYPE_PUSH_MANUAL);

						if (info.trim().equals("")) {
							Util.logger.info("#########");
							Util.logger
									.info("DAUGIA -- CHUA NHAP NOI DUNG MT MANUAL");
							Util.logger.info("#########");
							try {
								Thread.sleep(5 * 60 * 1000);
							} catch (InterruptedException e) {
							}
							continue;
						}

						DGAmount dgWin = amountMng.getUserWin();
						if (dgWin != null) {
							String phoneNumber = dgWin.getUserId();
							if (phoneNumber.length() > 5) {
								phoneNumber = phoneNumber.substring(0,
										phoneNumber.length() - 4);
								phoneNumber = phoneNumber + "xxx";
							}

							info = info.replace("$X", phoneNumber);

							int amountWin = Integer.parseInt(dgWin
									.getDgAmount());

							Random random = new Random();
							int randomMin = 1000 + random.nextInt(20) * 1000;
							int randomMax = 10000 + random.nextInt(20) * 1000;

							int minAmount = amountWin - randomMin;
							if (minAmount < 0)
								minAmount = 1000;
							int maxAmount = amountWin + randomMax;

							info = info.replace("$T1", minAmount + "");
							info = info.replace("$T2", maxAmount + "");
							info = info.replace("$Y", spDG.getTenSP());
							info = info.replace(
									DGConstants.STRING_REGEX_REPLACE_GIA_SP,
									spDG.getGiaSP());
							info = info.replace(
									DGConstants.STRING_REGEX_REPLACE_END_DATE,
									this.getToday());
						} else {
							//info = DGConstants.MTINFO_DAILY_NO_WIN;
							try {
								info = MTDAO.getRandomMessage(
									DGProcess.hMessageReminder.get("93"),
									DGProcess.hMessageReminder.get("93").split(
											";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}
							try {
								info = MTDAO.getRandomMessage(
										DGProcess.hMessageReminder.get("93"),
										DGProcess.hMessageReminder.get("93")
												.split(";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}
							try {
								Thread.sleep(5 * 60 * 1000);
							} catch (InterruptedException e) {
							}
							continue;
						}

						ArrayList<MlistInfo> arrMlist = commonObj
								.getAllMlistInfo(DGConstants.TABLE_MLIST_DG);

						Util.logger.info("DAUGIA --- Push MT Manual");
						for (int i = 0; i < arrMlist.size(); i++) {

							MlistInfo mlistInfo = arrMlist.get(i);

							if (mlistInfo.getRegCount() <= DGConstants.NUMBER_REGCOUNT) {

								if (!commonObj.isExistDG(mlistInfo.getUserId())) {
									continue;
								}

							}

							MsgObject msgObj = getMsgObj(mlistInfo);
							// tuannq add
							if (msgObj.getCommandCode().equals("DAUGIA"))
								info = info.replace(DGConstants.COMMAND_CODE,
										"DG");
							else {
								info = info.replace(DGConstants.COMMAND_CODE,
										msgObj.getCommandCode());
							}

							if (DGConstants.DG_MODE == 0) {
								DBSelect dbSelect = new DBSelect();
								Boolean checkTest = dbSelect.isUserTest(msgObj
										.getUserid());
								if (!checkTest) {
									continue;
								}

							}

							Boolean checkManual = isUserNotManual(msgObj
									.getUserid(), arrUserNotManual);
							if (checkManual) {
								continue;
							}

							msgObj.setUsertext(info);
							commonObj.pushMT(msgObj);
							serviceMng.updateMlistActive1(
									DGConstants.TABLE_MLIST_DG, mlistInfo
											.getId());

						}
						Util.logger.info("PUSH MT MANUAL FINISH!");
					} // End if 2
				}// End if 1
			}

			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

	private MsgObject getMsgObj(MlistInfo mlistInfo) {

		MsgObject msgObj = new MsgObject();

		msgObj.setUserid(mlistInfo.getUserId());
		msgObj.setMobileoperator(mlistInfo.getMobiOperator());
		msgObj.setChannelType(mlistInfo.getChanelType());
		msgObj.setServiceid(mlistInfo.getServiceId());
		msgObj.setCommandCode(mlistInfo.getCommandCode());
		msgObj.setContenttype(0);
		msgObj.setMsgtype(mlistInfo.getMessageType());
		msgObj.setKeyword("DAUGIA");
		msgObj.setRequestid(new BigDecimal(mlistInfo.getRequestId()));
		return msgObj;
	}

	private String getToday() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();
		String sDay = formatter.format(today);
		return sDay;
	}

	private ArrayList<String> getUserNotManual() {

		ArrayList<String> arrUserManual = new ArrayList<String>();

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT user_id FROM dg201111";

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
					String userId = rs.getString("user_id");
					arrUserManual.add(userId);
				}
			} else {
				Util.logger
						.error("PushMTManual - getUserNotManual: execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("PushMTManual - getUserNotManual: SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("PushMTManual - getUserNotManual: SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return arrUserManual;
	}

	/**
	 * 
	 * @param userId
	 * @param arrNotManual
	 * @return true - is userNotManual => not pushMT Manual false - userManual
	 */
	private Boolean isUserNotManual(String userIdCheck,
			ArrayList<String> arrNotManual) {

		Boolean check = false;

		for (int i = 0; i < arrNotManual.size(); i++) {

			String userNotManual = arrNotManual.get(i);
			if (userIdCheck.trim().equals(userNotManual)) {
				check = true;
				break;
			}

		}

		return check;
	}

	private Boolean isTimePushMT(String[] arrTimes, String currTime) {

		Boolean check = false;
		for (int i = 0; i < arrTimes.length; i++) {
			if (currTime.equals(arrTimes[i].trim())) {
				check = true;
				break;
			}
		}
		return check;
	}

}
