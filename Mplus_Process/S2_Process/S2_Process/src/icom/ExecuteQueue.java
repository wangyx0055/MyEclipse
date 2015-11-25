package icom;

import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Constant.PromotionConstant;
import DAO.MTDAO;
import DAO.PromotionDAO;
import DAO.ServiceDAO;
import DTO.ServiceDTO;
import SyncSky.SyncAPI;

import phase4.BLMOHandle;

import daugia.DGMOHandle;
import daugia.DaugiaCommon;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class ExecuteQueue extends Thread {
	int threadID = 0;
	MsgQueue queue = null;
	MsgQueue queueLog = null;
	BigDecimal AM = new BigDecimal(-1);
	Hashtable<String, String> hServicePromotion;

	public ExecuteQueue(MsgQueue queue, MsgQueue queueLog, int threadID) {
		this.queue = queue;
		this.queueLog = queueLog;
		this.threadID = threadID;
		hServicePromotion = new Hashtable<String, String>();
	}

	public static String replaceAllPointWithSpace(String sInput) {
		String strTmp = sInput;
		for (int i = 0; i < sInput.length(); i++) {
			char ch = sInput.charAt(i);
			if (ch == '.') {
				strTmp = strTmp.replace(ch, ' ');
			}
		}
		return strTmp;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {

		MsgObject msgObject = null;
		String serviceId = "";
		String info = "";
		int process_result = 0;
		// cac bien dung cho dich vu khuyen mai
		hServicePromotion = PromotionDAO.getServicePromotion();
		String dayPromotionCode = Constants._prop.getProperty("day_start",
				"2012-07-20");
		int iActive = Integer.parseInt(Constants._prop.getProperty(
				"active_mdt", "0"));

		int iModeTest = Integer.parseInt(Constants._prop.getProperty(
				"mode_test_mdt", "1"));
		try {
			sleep(1000);
		} catch (InterruptedException ex1) {
		}

		while (Sender.processData) {
			try {
				msgObject = (MsgObject) queue.remove();
				if (msgObject == null) {
					Thread.sleep(500);
					continue;
				}
				info = msgObject.getUsertext();
				serviceId = msgObject.getServiceid();
				info = info.trim();
				/*// chitd add code process supersim
//				if(Constants.MODECONFIRM.equals("1"))
//				{
//					if("1".equals(info))  MOHandleSuperSim.handle(msgObject);
//				}
//				else
//					if(info.indexOf("1")==0) MOHandleSuperSim.handle(msgObject);
				// end chitd
*/
				// MO DAU GIA
				// TUANNQ ADD
				/*if (!(info.toUpperCase().startsWith("DADH") || info
						.toUpperCase().startsWith("DACD"))) {

					if ((info.toUpperCase().startsWith("DA") || info
							.toUpperCase().startsWith("DG"))) {

						handleDAUGIA(msgObject, info);
						sleep(100);

						continue;

					}
				}*/

				// DanND SKB
				if (info.toUpperCase().startsWith("SKB")
						|| info.toUpperCase().startsWith("DK SKB")) {

					int i = -1;

					try {

						String[] arrInfo = info.split(" ");
						String strNumber = arrInfo[arrInfo.length - 1].trim();

						i = Integer.parseInt(strNumber);

					} catch (Exception ex) {

					}

					if (i < 0 || i > 40) {
						msgObject.setLast_code("0");
					} else {
						msgObject.setLast_code(i + "");
					}

					msgObject.setUsertext("SKB");

				}

				if (msgObject.getObjtype() == 0) {
					info = info.replace('-', ' ');
					info = info.replace('.', ' ');
					info = info.replace(',', ' ');
					info = info.replace('_', ' ');
					info = Util.replaceAllWhiteWithOne(info.trim());
				}

				String sgetkeyword = info;

				if (msgObject.getObjtype() != 0) {
					sgetkeyword = msgObject.getKeyword();
				}

				Keyword keyword = Sender.loadconfig.getKeyword(sgetkeyword
						.toUpperCase(), serviceId);
				
				/*
				 * tuannq phan xu ly de fw mo sang doi tac sky
				 */

				Util.logger.info("keyword lay duoc :" + keyword.getKeyword()
						+ " \t@command_code:" + keyword.getService_ss_id());

				if (!keyword.getKeyword().equals("INV")
						&& info.toUpperCase().matches("(.*)SKY(.*)")) {
					Util.logger.info("Synchronization " + msgObject.getUserid()
							+ " \t@info:" + info + "\t@serviceId:" + serviceId);
					SyncAPI.SyncMO(msgObject);
				}
				/**
				 * xu ly cho khuyen mai
				 */
				boolean checkUserTest = false;
				if (iModeTest == 1) {
					// mode test phai check user co trong bang user_test
					// hay khong
					if (PromotionDAO.isExistUser(msgObject.getUserid()) != 0) {
						checkUserTest = true;
					}
				}else{
					checkUserTest = true;
				}

				String curDay = new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date());
				String sNumCode = hServicePromotion.get(keyword
						.getService_ss_id());
				msgObject.setCommandCode(keyword.getService_ss_id()
						.toUpperCase());
				if (checkUserTest) {
					if (!keyword.getKeyword().equals("INV") && sNumCode != null
							&& iActive == 1 && keyword.getCpmo() == 1
							&& dayPromotionCode.compareTo(curDay) <= 0) {

						// neu nhu nam trong free user thi ms dc tham gia
						// Check co phai tin dang ky hay khong
						if (!info.toUpperCase().startsWith("HUY")) {
							// check da co ma du thuong chua?
							if (PromotionDAO.hasPromotionCode(msgObject
									.getUserid(), keyword.getService_ss_id()
									.toUpperCase()) == 0) {
								Util.logger.info("User:"
										+ msgObject.getUserid()
										+ " insert new promotion code.");
								int iNumCode = Integer
										.parseInt(hServicePromotion.get(keyword
												.getService_ss_id()
												.toUpperCase()));
								// insert vao bang? service_promotion_code
								for (int j = 0; j < iNumCode; j++)
									PromotionDAO.insertPromotion(msgObject
											.getUserid(), keyword
											.getService_ss_id().toUpperCase());
								// lay lai promotion_code de tra mt
								Vector<String> vProCode = PromotionDAO
										.getPromotionCode(
												msgObject.getUserid(), keyword
														.getService_ss_id()
														.toUpperCase());
								String sProCode = "";
								for (int i = 0; i < vProCode.size(); i++) {
									if (i != vProCode.size() - 1)
										sProCode = sProCode + vProCode.get(i)
												+ ", ";
									else
										sProCode = sProCode + vProCode.get(i);
								}

								Util.logger.info("User:"
										+ msgObject.getUserid() + " @sProCode:"
										+ sProCode);

								// send mt
								String sUserTextTemp = "";
								if (msgObject.getUsertext() != null)
									sUserTextTemp = msgObject.getUsertext();

								String sTextSend = "";
								if (!keyword.getService_ss_id().toUpperCase()
										.equals("YTE"))
									sTextSend = PromotionConstant.REGISTER_YTE;
								else
									sTextSend = PromotionConstant.REGISTER_CUOI;

								sTextSend = sTextSend.replaceAll("PROCODE",
										sProCode);
								msgObject.setUsertext(sTextSend);
								msgObject.setCommandCode(keyword
										.getService_ss_id());
								if (!sProCode.equals(""))
									MTDAO.sendMT(msgObject);
								else {
									Util.logger
											.error("Get  promotion code error!!!!plz check:"
													+ msgObject.getUserid()
													+ " @commandCode:"
													+ msgObject
															.getCommandCode());
								}

								// xu ly xong gan lai
								msgObject.setUsertext(sUserTextTemp);

							} else {

							}
						} else {
							// truong hop huy? phai check xem co MDT tuong
							// ung
							// voi
							// commandCode da dc active hay chua
							Util.logger.info("User test send mo HUY:"
									+ msgObject.getUserid() + " @info:"
									+ msgObject.getUsertext());
							if (PromotionDAO.hasPromotionCodeNotActivated(
									msgObject.getUserid(), msgObject
											.getCommandCode()) != 0) {

								/*PromotionDAO.deletePromotionCode(msgObject
										.getUserid(), msgObject
										.getCommandCode());*/
								PromotionDAO.deactiveProtionCode(msgObject.getUserid(), msgObject
										.getCommandCode());
								String sUserTextTemp = "";
								if (msgObject.getUsertext() != null)
									sUserTextTemp = msgObject.getUsertext();

								String sTextSend = PromotionConstant.CANCEL_PROMOTION;
								msgObject.setUsertext(sTextSend);
								msgObject.setCommandCode(keyword
										.getService_ss_id());

								MTDAO.sendMT(msgObject);
								msgObject.setUsertext(sUserTextTemp);
							}
						}
					}
					/*if (sNumCode == null && keyword.getCpmo() ==1 ) {
						
						String sUserTextTemp = "";
						if (msgObject.getUsertext() != null)
							sUserTextTemp = msgObject.getUsertext();

						String sTextSend = PromotionConstant.REGISTER_NOT_PRO;
						msgObject.setUsertext(sTextSend);
						msgObject.setCommandCode(keyword.getService_ss_id());

						MTDAO.sendMT(msgObject);
						msgObject.setUsertext(sUserTextTemp);
					}*/
				}

				if (msgObject.getObjtype() == 0) {
					msgObject.setKeyword(keyword.getKeyword());
				}
				msgObject.setIsIcom(keyword.getIsIcom());
				msgObject.setCommandCode(keyword.getService_ss_id());
				msgObject.setContentId(keyword.getService_type());
				msgObject.setCp_mo(keyword.getCpmo());
				msgObject.setCp_mt(keyword.getCpmt());

				if (msgObject.getObjtype() == 1) {
					String[] sTokens = info.split("###");
					for (int i = 0; i < sTokens.length; i++) {
						String infonew = sTokens[i];
						MsgObject msgObj = new MsgObject(msgObject);
						msgObj.setUsertext(infonew);

						if (i != 0) {
							msgObj.setAmount(0);
							if (msgObj.getMsgtype() == Integer
									.parseInt(Constants.MT_CHARGING)) {
								msgObj.setMsgtype(Integer
										.parseInt(Constants.MT_NOCHARGE));
							}
						}

						long lamount = msgObj.getAmount();
						int msgtype = msgObj.getMsgtype();
						Util.logger.info("{ExecuteQueue}:" + "Q" + serviceId
								+ "{@user_id=" + msgObj.getUserid()
								+ "}{@info=" + info + "}{@amount = " + lamount
								+ " }{@msgtype=" + msgtype + "@channel_type="
								+ msgObj.getChannelType() + "}");
						process_result = processQueueMsg(msgObj, keyword,
								LoadConfig.hServices);
						if ((i + 1) < sTokens.length) {
							sleep(50);
						}
					}
				} else {
					process_result = processQueueMsg(msgObject, keyword,
							LoadConfig.hServices);
				}

				this.addLog(msgObject, info);
				sleep(100);

			} catch (Exception ex) {
				Util.logger.error("Execute queue. Ex:" + ex.toString());
				Util.logger.printStackTrace(ex);
				queue.add(msgObject);
			}
		}
	}

	private int processQueueMsg(MsgObject msgObject, Keyword keyword,
			Hashtable services) {
		try {
			QuestionManager delegate = null;
			String classname = "services.Invalid";

			if (msgObject.getObjtype() == 0) {
				classname = keyword.getClass_mo();
			} else {
				classname = keyword.getClass_mt();
			}

			Util.logger.info("{processQueueMsg}{"
					+ ((msgObject.getObjtype() == 0) ? "MO" : "MT")
					+ "}@user_id=" + msgObject.getUserid() + "@info="
					+ msgObject.getUsertext() + "@request_id="
					+ msgObject.getRequestid().toString() + "@channel_type="
					+ msgObject.getChannelType() + "@className=" + classname);

			if (msgObject.getObjtype() == 1) {
				if ("1".equalsIgnoreCase(Constants.BLACKLIST)) {
					if (Sender.loadrepeat.isRepeat(msgObject.getUserid())) {
						msgObject.setMsgNotes(1, "black_list");
						msgObject.setProcess_result(Constants.RET_BLACKLIST);
						ExecuteInsertSendLog.add2queueReceiveLog(msgObject);
						return 1;
					}
				}
			}

			/*
			 * if (Constants.INV_KEYWORD.equalsIgnoreCase(keyword.getKeyword()))
			 * { Util.logger.info("processQueueMsg:@INVKEYWORD==>Save into inv@"
			 * + msgObject.getUserid() + "@" + msgObject.getUsertext() + "@" +
			 * msgObject.getRequestid().toString() + "@" + classname); if
			 * (msgObject.getObjtype() == 0) { add2InvalidMO(msgObject); } else
			 * { add2InvalidMT(msgObject); }
			 * 
			 * return 1;
			 * 
			 * }
			 */

			Class delegateClass = Class.forName(classname);
			Object delegateObject = delegateClass.newInstance();
			delegate = (QuestionManager) delegateObject;

			delegate.start(Constants._prop, msgObject, keyword, services);
			return 1;

		} catch (Exception e) {
			Util.logger.crisis("processQueueMsg:@user_id="
					+ msgObject.getUserid() + "@info="
					+ msgObject.getUsertext() + "@request_id="
					+ msgObject.getRequestid().toString() + "@channel_type="
					+ msgObject.getChannelType() + "@ex=" + e.toString());

			String notes = e.toString();
			notes = (notes.length() > 100) ? notes.substring(1, 100) : notes;
			msgObject.setMsgNotes(notes);

			return 0;
		}

	}

	private static BigDecimal add2InvalidMO(MsgObject msgObject) {
		Util.logger.info("add2InvalidMO:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mo_invalid";
		sSQLInsert = "insert into "
				+ tablename
				+ "(REQUEST_ID,USER_ID,SERVICE_ID,MOBILE_OPERATOR,COMMAND_CODE,INFO,RECEIVE_DATE)"
				+ " values(?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);
			statement.setBigDecimal(1, msgObject.getRequestid());
			statement.setString(2, msgObject.getUserid());
			statement.setString(3, msgObject.getServiceid());
			statement.setString(4, msgObject.getMobileoperator());
			statement.setString(5, msgObject.getKeyword());
			statement.setString(6, msgObject.getUsertext());
			statement.setTimestamp(7, msgObject.getTTimes());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2InvalidMO:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2InvalidMO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2InvalidMO:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	private static BigDecimal add2InvalidMT(MsgObject msgObject) {
		Util.logger.info("add2InvalidMT:" + msgObject.getUserid() + "@"
				+ msgObject.getUsertext());
		PreparedStatement statement = null;
		String sSQLInsert = null;
		Connection connection = null;
		DBPool dbpool = new DBPool();
		String tablename = "mt_invalid";
		sSQLInsert = "insert into "
				+ tablename
				+ "(USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID)"
				+ " values(?,?,?,?,?,?,?,?)";

		try {
			connection = dbpool.getConnectionGateway();

			statement = connection.prepareStatement(sSQLInsert);

			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.error("add2InvalidMT:" + msgObject.getUserid()
						+ ":" + msgObject.getUsertext()
						+ ":statement.executeUpdate failed");
				return new BigDecimal(-1);
			}
			statement.close();
			return msgObject.getRequestid();
		} catch (SQLException e) {
			Util.logger.error("add2InvalidMT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		} catch (Exception e) {
			Util.logger.error("add2InvalidMT:" + msgObject.getUserid() + ":"
					+ msgObject.getUsertext()
					+ ":Error add row from sms receive log:" + e.toString());
			return new BigDecimal(-1);
		}

		finally {
			dbpool.cleanup(connection);

		}
	}

	/**
	 * Gop chung DG va DA.
	 * 
	 * @param msgObject
	 * @param info
	 */
	private void handleDAUGIA(MsgObject msgObject, String info) {

		String strAmount = "";
		if (info.startsWith("DG"))
			msgObject.setCommandCode("DAUGIA");
		else
			msgObject.setCommandCode("DA");
		this.addLog(msgObject, info);

		info = info.replace("(", "");
		info = info.replace(")", "");
		info = info.replace("<", "");
		info = info.replace(">", "");
		info = info.replace("+", "");
		info = info.replace(" ", "");
		String[] arrDG = info.split(" ");

		double amount = -1;
		if (arrDG.length > 1) {
			strAmount = arrDG[1].trim();
		} else {
			DaugiaCommon dgCommon = new DaugiaCommon();
			String strKeyword = dgCommon.getKeyword(info);
			strAmount = info.substring(strKeyword.length());
		}

		if (checkAmount(strAmount)) {
			amount = 1;
			strAmount = strAmount + "000";
		}

		if (strAmount.length() > 900) {
			amount = -1;
		}

		DGMOHandle dgMOHandle = new DGMOHandle(msgObject, amount, strAmount);
		dgMOHandle.handleMO();

		/*
		 * double amount = -1; if(arrDG.length == 1 ){ DaugiaCommon dgCommon =
		 * new DaugiaCommon(); String strKeyword = dgCommon.getKeyword(info);
		 * strAmount = info.substring(strKeyword.length());
		 * if(checkAmount(strAmount)){ amount = 1; strAmount = strAmount +
		 * "000"; } if(strAmount.length()>900){ amount = -1; } DGMOHandle
		 * dgMOHandle = new DGMOHandle(msgObject, amount, strAmount);
		 * dgMOHandle.handleMO(); }else{ for(int i= 1;i < arrDG.length;i++){
		 * 
		 * strAmount = arrDG[i].trim(); if(checkAmount(strAmount)){ amount = 1;
		 * strAmount = strAmount + "000"; } if(strAmount.length()>900){ amount =
		 * -1; }
		 * 
		 * if(i>6){ amount = -1; } DGMOHandle dgMOHandle = new
		 * DGMOHandle(msgObject, amount, strAmount); dgMOHandle.handleMO(); } }
		 */
	}

	/****
	 * 
	 * @param amount
	 * @return true if amount is correct format <br/>
	 *         false if amount is incorrect format!
	 */
	private Boolean checkAmount(String amount) {

		Pattern icomPattern = Pattern.compile("\\D");
		Matcher match = icomPattern.matcher(amount);

		if (match.find()) {
			return false;
		}

		return true;
	}

	private void addLog(MsgObject msgObject, String info) {
		if (msgObject.getObjtype() == 0) {
			if (msgObject.getMultiService() == 0) {
				queueLog.add(new MsgObject(msgObject.getObjtype(), msgObject
						.getServiceid(), msgObject.getUserid(), msgObject
						.getKeyword(), info, msgObject.getRequestid(),
						msgObject.getTTimes(), msgObject.getMobileoperator(),
						msgObject.getMsgtype(), msgObject.getContenttype(),
						msgObject.getCp_mo(), msgObject.getCp_mt(), msgObject
								.getMsgnotes(), 0, msgObject.getRetries_num(),
						msgObject.getMsg_id(), msgObject.getAmount(), msgObject
								.getChannelType(), msgObject.getCommandCode()));
			}
		}
	}

}
