package daugia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import DAO.MTDAO;

import icom.Constants;
import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.Sender;
import icom.common.ResultCode;
import icom.common.Util;

public class HandleChargeResult {

	public void initThread() {

		for (int i = 0; i < DGConstants.DG_PROCESS_RESULTS_NUM; i++) {
			HandleThread handleThread = new HandleThread();
			handleThread.start();
		}
	}

	private class HandleThread extends Thread {

		List<Integer> lTop20 = new ArrayList<Integer>();

		public List<Integer> initTop20() {

			List<Integer> lTop20 = new ArrayList<Integer>();
			int amount = 0;

			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			DBPool dbpool = new DBPool();

			String sqlQuery = "SELECT amount_empty FROM daugia_amount_empty where status =0 ORDER BY amount_empty ASC LIMIT 0,20 ";

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
						amount = rs.getInt("amount_empty");
						lTop20.add(new Integer(amount));
					}
				} else {
					
					Util.logger
							.error("DAUGIA - isExistAmount : execute Error!!");
				}
			} catch (SQLException ex3) {
				Util.logger.error("DAUGIA - isExistAmount. SQLException:"
						+ ex3.toString());
				Util.logger.printStackTrace(ex3);
			} catch (Exception ex2) {
				Util.logger.error("DAUGIA - isExistAmount. SQLException:"
						+ ex2.toString());
				Util.logger.printStackTrace(ex2);
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);
			}

			return lTop20;
		}

		public void run() {
			lTop20 = initTop20();
			final DaugiaCommon dgCommon = new DaugiaCommon();
			DGQueueCharge queueCharge = DGQueueCharge.getInstance();
			int amountFar = Integer.parseInt(Constants._prop
					.getProperty("amount_far"));
			while (Sender.processData) {

				final SanPhamDG spDg = SanPhamDGManager.getInstance()
						.getSanPhamDG();
				// neu co san pham ddang dau gia moi xu ly
				if (spDg == null) {

					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {

					}

					continue;
				}
				// lay ra lap daugia_charge_result
				DGChargeInfo resultInfo = queueCharge.getDGChargeObj();

				if (resultInfo == null) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}

					continue;
				}
				// delete trong TABLE_DG_CHARGE_RESULT tuong ung voi obj lay ra
				int resultDelete = dgCommon.deleteTableByID(resultInfo.getId(),
						DGConstants.TABLE_DG_CHARGE_RESULT);

				if (resultDelete < 1) {
					continue;
				}

				DGAmount dgAmountObj = this.getDGAmountObj(resultInfo, spDg);
				DGAmountManager dgAmountMng = new DGAmountManager();

				int processResult = 0;
				// charge thanh cong
				if (resultInfo.getReslultCharge() == ResultCode.OK
						|| resultInfo.getReslultCharge() == ResultCode.NOK_ACCOUNT_NOT_FOUND) {

					// check in daugia_amount_empty
					int iStatus = dgAmountMng.getEmptyStatus(dgAmountObj
							.getDgAmount());

					EmptyMng emptyMng = EmptyMng.getInstance();
					// neu gia nay da co nguoi dat
					if (iStatus == 1) {
						
						// xem gia nay co phai chinh user dat khong
						if (dgCommon.isExistAmount(resultInfo.getUserId(),
								dgAmountObj.getDgAmount(), spDg.getMaSP())) {

							String userText = DGConstants.MT_AMOUNT_EXIST;
							try {
								userText = MTDAO.getRandomMessage(
									DGProcess.hMessageReminder.get("75"),
									DGProcess.hMessageReminder.get("75").split(
											";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}

							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_AMOUNT,
									dgAmountObj.getDgAmount());
							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDg.getTenSP());

							MsgObject msgObj = this.getMsgObj(resultInfo);
							userText = userText.replaceAll(
									DGConstants.COMMAND_CODE, msgObj
											.getCommandCode());
							msgObj.setUsertext(userText);
							dgCommon.sendMT(msgObj);
							continue;

						} else {
							// neu khong phai dat gia trung se insert vao
							// daugia_amount
							processResult = dgAmountMng.insertDGAmount(
									dgAmountObj, DGConstants.TABLE_DG_AMOUNT);

							AmountQueue amountQueue = AmountQueue.getInstance();
							amountQueue.updateAmount(dgAmountObj.getDgAmount());
							// thap nhat va hok duy nhat
							if (dgAmountObj.getDgAmount().equals("1000")) {

								MsgObject msgObj = this.getMsgObj(resultInfo);
								// String userText =
								// DGConstants.MTINFO_MIN_AND_NO_ONLY;
								String userText = "";
								try {
									userText = MTDAO
											.getRandomMessage(
													DGProcess.hMessageReminder
															.get("61"),
													DGProcess.hMessageReminder
															.get("61").split(
																	";").length);
								} catch (Exception e) {
									Util.logger.printStackTrace(e);
								}
								userText = userText
										.replaceAll(
												DGConstants.STRING_REGEX_REPLACE_AMOUNT,
												dgAmountObj.getDgAmount());
								userText = userText
										.replaceAll(
												DGConstants.STRING_REGEX_REPLACE_TEN_SP,
												spDg.getTenSP());

								if (msgObj.getCommandCode().equals("DAUGIA"))
									userText = userText.replace(
											DGConstants.COMMAND_CODE, "DG");
								else {
									userText = userText.replace(
											DGConstants.COMMAND_CODE, msgObj
													.getCommandCode());
								}
								// tuannq add for replace min - max_emply

								int amountEmpty = dgAmountMng.findAmountEmpty();
								Random random = new Random();
								int minEmpty = amountEmpty
										- (10000 + random.nextInt(20) * 1000);
								int maxEmpty = amountEmpty
										+ (10000 + random.nextInt(50) * 1000);
								if (minEmpty < 1000)
									minEmpty = 1000;

								userText = userText.replace("MIN_EMPTY",
										minEmpty + "");
								userText = userText.replace("MAX_EMPTY",
										maxEmpty + "");

								msgObj.setUsertext(userText);
								dgCommon.sendMT(msgObj);
								continue;

							} else {
								// thap nhat va hok duy nhat
								int amountEmpty = emptyMng.getAmountEmpty();
								String info = "";
								/*
								 * if (amountEmpty > 0) { //info =
								 * DGConstants.MTINFO_NO_MIN_AND_NO_ONLY_EMPTYNUMBER
								 * ;
								 * 
								 * } else { //info =
								 * DGConstants.MTINFO_NO_MIN_AND_NO_ONLY; }
								 */
								try {
									info = MTDAO
											.getRandomMessage(
													DGProcess.hMessageReminder
															.get("65"),
													DGProcess.hMessageReminder
															.get("65").split(
																	";").length);
								} catch (Exception e) {
									Util.logger.printStackTrace(e);
								}

								info = info
										.replaceAll(
												DGConstants.STRING_REGEX_REPLACE_AMOUNT,
												dgAmountObj.getDgAmount());
								info = info
										.replaceAll(
												DGConstants.STRING_REGEX_REPLACE_TEN_SP,
												spDg.getTenSP());
								// tuannq add

								Random random = new Random();
								int minEmpty = amountEmpty
										- (10000 + random.nextInt(20) * 1000);
								int maxEmpty = amountEmpty
										+ (50000 + random.nextInt(50) * 1000);
								if (minEmpty < 1000)
									minEmpty = 1000;

								info = info.replace("MIN_EMPTY", minEmpty + "");
								info = info.replace("MAX_EMPTY", maxEmpty + "");

								MsgObject msgObj = this.getMsgObj(resultInfo);

								if (msgObj.getCommandCode().equals("DAUGIA"))
									info.replaceAll(DGConstants.COMMAND_CODE,
											"DG");
								else {
									info.replaceAll(DGConstants.COMMAND_CODE,
											msgObj.getCommandCode());
								}

								msgObj.setUsertext(info);
								dgCommon.sendMT(msgObj);
								continue;

							}
						}
					} else if (iStatus == 0) {
						// amount is only
						processResult = dgAmountMng.insertDGAmount(dgAmountObj,
								DGConstants.TABLE_DG_AMOUNT);
						dgAmountMng.updateEmptyAmount(
								dgAmountObj.getDgAmount(), 1);
						AmountQueue amountQueue = AmountQueue.getInstance();
						amountQueue.updateAmount(dgAmountObj.getDgAmount());

						MsgObject msgObj = this.getMsgObj(resultInfo);

						if (dgAmountObj.getDgAmount().equals(
								emptyMng.getAmountEmpty() + "")) {
							emptyMng.resetAmountEmpty();
						}

						String strAmountWin = dgAmountMng
								.getAmountTrungThuong().trim();
						String userAmount = dgAmountObj.getDgAmount().trim();
						
						if (userAmount.equals(strAmountWin)) {
							dgCommon.insertWinnerTmp(resultInfo.getUserId(),
									resultInfo.getDgAmount() + "", spDg
											.getMaSP(),
									DGConstants.TYPE_WIN_TMP_FIRST_TIME);
							// DGConstants.MTINFO_MIN_AND_ONLY;
							String userText = "";

							try {
								userText = MTDAO.getRandomMessage(
										DGProcess.hMessageReminder.get("66"),
										DGProcess.hMessageReminder.get("66")
												.split(";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}

							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_AMOUNT,
									dgAmountObj.getDgAmount());
							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDg.getTenSP());

							if (msgObj.getCommandCode().equals("DAUGIA"))
								userText.replaceAll(DGConstants.COMMAND_CODE,
										"DG");
							else {
								userText.replaceAll(DGConstants.COMMAND_CODE,
										msgObj.getCommandCode());
							}

							msgObj.setUsertext(userText);

							dgCommon.sendMT(msgObj);
							continue;

						} else {
							// duy nhat nhung khong phai thap nhat
							// chia 3 truong hop
							// 1.thap nhat trong 20 gia
							// 2.thap nhat va >x
							// 3.truong hop con lai
							int numberUnder = dgAmountMng
									.findNumberEmptyUnder(dgAmountObj
											.getDgAmount());
							String info = "";
							if (numberUnder > 0) {
								//info = DGConstants.MTINFO_NO_MIN_AND_ONLY_NUMBERUNDER;

								try {
									info = MTDAO.getRandomMessage(
											DGProcess.hMessageReminder
													.get("610"),
											DGProcess.hMessageReminder.get(
													"610").split(";").length);
								} catch (Exception e) {
									Util.logger.printStackTrace(e);
								}
								info = info.replace("NUMBER_UNDER", numberUnder
										+ "");

							} else {
								/**
								 * tuannq
								 */
								String sOut = "Top 20 gia can chien thang:\t";
								for (Integer index : lTop20) {
									sOut = sOut + index.intValue() + " ";
								}
								Util.logger.info(sOut);
								Boolean bCheckaEqual = false;

								for (Integer index : lTop20) {
									if (index.intValue() == Integer
											.parseInt(dgAmountObj.getDgAmount()))
										bCheckaEqual = true;
								}

								if (Integer.parseInt(dgAmountObj.getDgAmount()) < lTop20
										.get(19).intValue()) {

									if (!bCheckaEqual) {
										// remote gia thu 19
										lTop20.remove(19);
										lTop20.add(new Integer(dgAmountObj
												.getDgAmount()));
										for (int i = 0; i < 19; i++) {
											for (int j = i; j < 20; j++) {
												if (lTop20.get(i).intValue() < lTop20
														.get(j).intValue()) {
													int temp = lTop20.get(i)
															.intValue();
													lTop20
															.set(
																	i,
																	new Integer(
																			lTop20
																					.get(
																							j)
																					.intValue()));
													lTop20.set(j, new Integer(
															temp));
												}
											}
										}
									}
									// set mt trong top 20
									// info =
									// DGConstants.MTINFO_NO_MIN_AND_ONLY_TOP20;

									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("62"),
														DGProcess.hMessageReminder
																.get("62")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								} else if (Integer.parseInt(dgAmountObj
										.getDgAmount()) > amountFar) {
									//info = DGConstants.MTINFO_NO_MIN_AND_ONLY_FAR;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("63"),
														DGProcess.hMessageReminder
																.get("63")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								} else {
									// info =
									// DGConstants.MTINFO_NO_MIN_AND_ONLY_NEAR;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("64"),
														DGProcess.hMessageReminder
																.get("64")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								}

								// info = DGConstants.MTINFO_NO_MIN_AND_ONLY;

							}
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_AMOUNT,
									dgAmountObj.getDgAmount());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDg.getTenSP());
							// tuannq add
							if (msgObj.getCommandCode().equals("DAUGIA"))
								info.replaceAll(DGConstants.COMMAND_CODE, "DG");
							else {
								info.replaceAll(DGConstants.COMMAND_CODE,
										msgObj.getCommandCode());
							}

							int amountEmpty = dgAmountMng.findAmountEmpty();
							Random random = new Random();
							int minEmpty = amountEmpty
									- (10000 + random.nextInt(20) * 1000);
							int maxEmpty = amountEmpty
									+ (10000 + random.nextInt(50) * 1000);
							if (minEmpty < 1000)
								minEmpty = 1000;
							info = info.replace("MIN_EMPTY", minEmpty + "");
							info = info.replace("MAX_EMPTY", maxEmpty + "");

							msgObj.setUsertext(info);
							dgCommon.sendMT(msgObj);
							continue;

						}

					} else {
						if (dgCommon.isExistAmount(resultInfo.getUserId(),
								dgAmountObj.getDgAmount(), spDg.getMaSP())) {

							String userText = DGConstants.MT_AMOUNT_EXIST;
							try {
								userText = MTDAO.getRandomMessage(
									DGProcess.hMessageReminder.get("75"),
									DGProcess.hMessageReminder.get("75").split(
											";").length);
							} catch (Exception e) {
								Util.logger.printStackTrace(e);
							}
							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_AMOUNT,
									dgAmountObj.getDgAmount());
							userText = userText.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDg.getTenSP());

							MsgObject msgObj = this.getMsgObj(resultInfo);
							userText = userText.replaceAll(
									DGConstants.COMMAND_CODE, msgObj
											.getCommandCode());
							msgObj.setUsertext(userText);
							dgCommon.sendMT(msgObj);
							continue;

						} else {
							processResult = dgAmountMng.insertDGAmount(
									dgAmountObj, DGConstants.TABLE_DG_AMOUNT);
							int number = dgAmountMng.getNumberDG(dgAmountObj
									.getDgAmount());

							MsgObject msgObj = this.getMsgObj(resultInfo);
							String info = "";
							if (number > 1) {
								// info = DGConstants.MTINFO_NO_MIN_AND_ONLY;
								boolean bCheckaEqual = false;
								for (Integer index : lTop20) {
									if (index.intValue() == Integer
											.parseInt(dgAmountObj.getDgAmount()))
										bCheckaEqual = true;
								}

								if (Integer.parseInt(dgAmountObj.getDgAmount()) < lTop20
										.get(19).intValue()) {

									if (!bCheckaEqual) {
										// remote gia thu 19
										lTop20.remove(19);
										lTop20.add(new Integer(dgAmountObj
												.getDgAmount()));
										for (int i = 0; i < 19; i++) {
											for (int j = i; j < 20; j++) {
												if (lTop20.get(i).intValue() < lTop20
														.get(j).intValue()) {
													int temp = lTop20.get(i)
															.intValue();
													lTop20
															.set(
																	i,
																	new Integer(
																			lTop20
																					.get(
																							j)
																					.intValue()));
													lTop20.set(j, new Integer(
															temp));
												}
											}
										}
									}
									// set mt trong top 20
									// info =
									// DGConstants.MTINFO_NO_MIN_AND_ONLY_TOP20;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("62"),
														DGProcess.hMessageReminder
																.get("62")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								} else if (Integer.parseInt(dgAmountObj
										.getDgAmount()) > amountFar) {
									//info = DGConstants.MTINFO_NO_MIN_AND_ONLY_FAR;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("63"),
														DGProcess.hMessageReminder
																.get("63")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								} else {
									// info =
									// DGConstants.MTINFO_NO_MIN_AND_ONLY_NEAR;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("64"),
														DGProcess.hMessageReminder
																.get("64")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}
								}
								msgObj.setUsertext(info);
							} else {
								if (dgAmountObj.getDgAmount().equals(
										dgAmountMng.getAmountTrungThuong())) {
									// info = DGConstants.MTINFO_MIN_AND_ONLY;

									try {
										info = MTDAO.getRandomMessage(
												DGProcess.hMessageReminder
														.get("9"),
												DGProcess.hMessageReminder.get(
														"9").split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}

								} else {
									// info =
									// DGConstants.MTINFO_NO_MIN_AND_NO_ONLY;
									try {
										info = MTDAO
												.getRandomMessage(
														DGProcess.hMessageReminder
																.get("65"),
														DGProcess.hMessageReminder
																.get("65")
																.split(";").length);
									} catch (Exception e) {
										Util.logger.printStackTrace(e);
									}

								}
							}

							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_AMOUNT,
									dgAmountObj.getDgAmount());
							info = info.replaceAll(
									DGConstants.STRING_REGEX_REPLACE_TEN_SP,
									spDg.getTenSP());
							// tuannq add
							if (msgObj.getCommandCode().equals("DAUGIA"))
								info = info.replace(DGConstants.COMMAND_CODE,
										"DG");
							else {
								info = info.replace(DGConstants.COMMAND_CODE,
										msgObj.getCommandCode());
							}
							msgObj.setUsertext(info);
							dgCommon.sendMT(msgObj);

						}
					}

				} else {
					MsgObject msgObj = this.getMsgObj(resultInfo);
					processResult = dgCommon.sendMT(msgObj);
					Util.logger.info("Insert Daugia_blackList; userid = "
							+ msgObj.getUserid());
					dgCommon.insertDaugiaBlackList(msgObj.getUserid(),
							resultInfo.getTimeSendMO());

					if (processResult < 1) {
						InsertDGCharge(resultInfo,
								DGConstants.TABLE_DG_CHARGE_RESULT);
					}
					continue;
				}

			}

		}

		private MsgObject getMsgObj(DGChargeInfo rslInfo) {

			MsgObject msgObj = new MsgObject();

			String info = "DG";
			Keyword keyword = Sender.loadconfig.getKeyword(info.toUpperCase(),
					rslInfo.getServiceId());

			if (rslInfo.reslultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT
					|| rslInfo.reslultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT) {
				msgObj.setUsertext(keyword.getNotEnoughMoneyMsg());
			} else {
				msgObj.setUsertext(keyword.getErrMsg());
			}

			msgObj.setUserid(rslInfo.getUserId());
			msgObj.setMobileoperator(rslInfo.getMobileOperator());
			msgObj.setChannelType(rslInfo.getChannelType());
			msgObj.setServiceid(rslInfo.getServiceId());
			msgObj.setRequestid(rslInfo.getRequestID());
			msgObj.setCommandCode(info);
			msgObj.setContenttype(rslInfo.getContentType());
			msgObj.setMsgtype(rslInfo.getMsgType());
			msgObj.setKeyword("DAUGIA");

			return msgObj;
		}

		private DGAmount getDGAmountObj(DGChargeInfo rslInfo, SanPhamDG spDG) {
			DGAmount dgAmountObj = new DGAmount();

			dgAmountObj.setUserId(rslInfo.getUserId());
			dgAmountObj.setServiceId(rslInfo.getServiceId());
			dgAmountObj.setTimeSendMO(rslInfo.getTimeSendMO());
			dgAmountObj.setDgAmount(rslInfo.getDgAmount().trim());

			dgAmountObj.setMaSP(spDG.getMaSP());

			return dgAmountObj;
		}

		public int InsertDGCharge(DGChargeInfo rslInfo, String tableName) {

			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			DBPool dbpool = new DBPool();

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
				stmt.setString(1, rslInfo.getUserId());
				stmt.setString(2, rslInfo.getServiceId());
				stmt.setString(3, rslInfo.getMobileOperator());
				stmt.setString(4, rslInfo.getCommandCode());
				stmt.setInt(5, rslInfo.getContentType());
				stmt.setString(6, rslInfo.getInfo());
				stmt.setTimestamp(7, rslInfo.getSubmitDate());
				stmt.setString(8, rslInfo.getDoneDate());
				stmt.setInt(9, rslInfo.getProcessResult());
				stmt.setInt(10, rslInfo.getMsgType());
				stmt.setBigDecimal(11, rslInfo.getRequestID());
				stmt.setString(12, rslInfo.getMsgID());
				stmt.setInt(13, rslInfo.getRetriesNumber());
				stmt.setString(14, rslInfo.getNotes());
				stmt.setString(15, rslInfo.getServiceName());
				stmt.setInt(16, rslInfo.getChannelType());
				stmt.setString(17, rslInfo.getContendID());
				stmt.setLong(18, rslInfo.getAmount());
				stmt.setInt(19, rslInfo.getReslultCharge());
				stmt.setString(20, rslInfo.getDgAmount());
				stmt.setString(21, rslInfo.getTimeSendMO());

				if (stmt.executeUpdate() != 1) {
					Util.logger
							.crisis("@HandleChargeResult InsertDGCharge: Error@userid="
									+ rslInfo.getUserId()
									+ "@serviceid="
									+ rslInfo.getServiceId()
									+ "@usertext="
									+ rslInfo.getInfo()
									+ "@messagetype="
									+ rslInfo.getMsgType()
									+ "@requestid="
									+ rslInfo.getRequestID());
					return -1;
				}

				Util.logger
						.info("@HandleChargeResult InsertDGCharge SUCCESSFUL !!! \n @ Query = "
								+ sqlQuery);

				return 1;
			} catch (SQLException ex3) {
				Util.logger
						.error("@HandleChargeResult InsertDGCharge. SQLException:"
								+ ex3.toString());
				Util.logger.printStackTrace(ex3);
				return -1;
			} catch (Exception ex2) {
				Util.logger
						.error("@HandleChargeResult InsertDGCharge. SQLException:"
								+ ex2.toString());
				Util.logger.printStackTrace(ex2);
				return -1;
			} finally {
				dbpool.cleanup(rs, stmt);
				dbpool.cleanup(connection);
			}

		}

	}

	/**
	 * Tuannq add for replace MIN_EMPTY and MAX_EMPTY
	 * 
	 * @param info
	 * @return
	 */
	public static String replaceInfoEmpty(String info) {

		DGAmountManager dgAmountMng = new DGAmountManager();
		int amountEmpty = dgAmountMng.findAmountEmpty();
		Random random = new Random();
		int minEmpty = amountEmpty - (10000 + random.nextInt(20) * 1000);
		int maxEmpty = amountEmpty + (10000 + random.nextInt(50) * 1000);
		if (minEmpty < 1000)
			minEmpty = 1000;
		info = info.replace("MIN_EMPTY", minEmpty + "");
		info = info.replace("MAX_EMPTY", maxEmpty + "");

		return info;
	}

}
