package daugia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import DAO.MTDAO;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

import servicesPkg.MlistInfo;
import servicesPkg.ServiceMng;
import icom.DBPool;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBSelect;
import icom.common.Util;

public class DGResponseMTDaily extends Thread {

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

				String timeDaily = spDG.getTimeDaily();
				if (currHour.equals(timeDaily)) {
					if (spMng.checkTimeDG(currTime)) {

						Util.logger.info("end day start push mt day winner...");
						String info = DGConstants.MT_WIN_DAILY;
						try {
							info = MTDAO.getRandomMessage(
								DGProcess.hMessageReminder.get("92"),
								DGProcess.hMessageReminder.get("92").split(
										";").length);
						} catch (Exception e) {
							Util.logger.printStackTrace(e);
						}

						DGAmount dgWin = amountMng.getUserWin();
						dgWin = amountMng.getUserWin();
						MsgObject msgObj = new MsgObject();
						if (dgWin != null) {
							DateFormat formatter1 = new SimpleDateFormat(
									"dd/MM/yyyy");
							java.util.Date today1 = new java.util.Date();
							String dt = formatter1.format(today1);

							info = info.replace("USER_ID", dgWin.getUserId());
							info = info.replace("CUR_DAY", dt);

							String tenSp = getTenSp(dgWin.getMaSP());
							info = info.replace("TEN_SP", tenSp);

							MlistInfo mlist = commonObj.getAllMlistInfoByUser(
									DGConstants.TABLE_MLIST_DG, dgWin
											.getUserId());
							msgObj.setUserid(mlist.getUserId());
							msgObj.setMobileoperator(mlist.getMobiOperator());
							msgObj.setChannelType(mlist.getChanelType());
							msgObj.setServiceid(mlist.getServiceId());
							msgObj.setCommandCode(mlist.getCommandCode());
							msgObj.setContenttype(0);
							msgObj.setMsgtype(mlist.getMessageType());
							msgObj.setKeyword(mlist.getCommandCode());
							msgObj.setRequestid(new BigDecimal(mlist
									.getRequestId()));
							msgObj.setUsertext(info);
							BigDecimal bd = new BigDecimal(mlist.getRequestId());
							msgObj.setRequestid(bd);
							// msgObj.setM
							commonObj.pushMT(msgObj);
							

							// insert vao winner_by_days

							DaugiaCommon.InsertWinnerByDays(dgWin.getUserId(),
									dgWin.getDgAmount(), msgObj.getUsertext(),
									dgWin.getMaSP());
						}
						info = commonObj
								.getContentPush(DGConstants.CONTENT_TYPE_PUSH_DAILY);

						if (info.trim().equals("")) {
							Util.logger.info("#########");
							Util.logger
									.info("DAUGIA -- CHUA NHAP NOI DUNG MT Daily");
							Util.logger.info("#########");
							try {
								Thread.sleep(5 * 60 * 1000);
							} catch (InterruptedException e) {
							}
							continue;
						}

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
						}

						ArrayList<MlistInfo> arrMlist = commonObj
								.getAllMlistInfoDaily(DGConstants.TABLE_MLIST_DG);

						// ArrayList<MlistInfo> arrSub =
						// commonObj.getAllMlistInfo(DGConstants.TABLE_MLIST_DG_TMP);
						// arrMlist.addAll(arrSub);

						for (int i = 0; i < arrMlist.size(); i++) {

							MlistInfo mlistInfo = arrMlist.get(i);
							Util.logger.info("DGResponseMTDaily arrMlist.size:"
									+ arrMlist.size() + " @pushmt2user:"
									+ mlistInfo.getUserId());
							// if(mlistInfo.getRegCount()<=
							// DGConstants.NUMBER_REGCOUNT){
							//								
							// continue;
							//							
							// }

							msgObj = getMsgObj(mlistInfo);

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

							msgObj.setUsertext(info);
							commonObj.pushMT(msgObj);
							serviceMng.updateMlistActive1(
									DGConstants.TABLE_MLIST_DG, mlistInfo
											.getId());

						}

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
		try {
			msgObj.setRequestid(new BigDecimal(mlistInfo.getRequestId()));
		} catch (Exception e) {
			Util.logger.error("DGResponseMTDaily @reqId error:" + e
					+ " @ setdefault reqId =123 @user:" + msgObj.getUserid());
			msgObj.setRequestid(new BigDecimal(123));
		}
		return msgObj;
	}

	private String getToday() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();
		String sDay = formatter.format(today);
		return sDay;
	}

	private static String getTenSp(String maSp) {
		String sReturn = "";
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		String sqlQuery = "SELECT TEN_SP FROM daugia_sanpham WHERE MA_SP = '"
				+ maSp + "'";
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
					Util.logger.info("DAUGIA - getTenSp :sql:" + sqlQuery);
					sReturn = rs.getString("TEN_SP");
				}
			} else {
				Util.logger.error("DAUGIA - getTenSp : execute Error!!");
			}
		} catch (SQLException ex3) {
			Util.logger.error("DAUGIA - getTenSp. SQLException:"
					+ ex3.toString());
			Util.logger.printStackTrace(ex3);
		} catch (Exception ex2) {
			Util.logger.error("DAUGIA - getTenSp. SQLException:"
					+ ex2.toString());
			Util.logger.printStackTrace(ex2);
		} finally {
			dbpool.cleanup(rs, stmt);
			dbpool.cleanup(connection);
		}

		return sReturn;

	}

}
