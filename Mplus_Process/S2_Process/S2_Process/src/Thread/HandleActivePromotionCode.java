package Thread;

import icom.Constants;
import icom.MsgObject;
import icom.Sender;
import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import Constant.PromotionConstant;
import DAO.MTDAO;
import DAO.PromotionDAO;
import DTO.PromotionCodeDTO;

import sub.CServices;
import sub.DeliveryManager;

public class HandleActivePromotionCode extends Thread {

	String className = "HandleActivePromotionCode";

	public static long String2MilisecondNew(String strInputDate) {
		// System.err.println("String2Milisecond.strInputDate:" + strInputDate);
		String strDate = strInputDate.trim();
		int i, nYear, nMonth, nDay, nHour, nMinute, nSecond;
		String strSub = null;
		if (strInputDate == null || "".equals(strInputDate)) {
			return 0;
		}
		strDate = strDate.replace('-', '/');
		strDate = strDate.replace('.', '/');
		strDate = strDate.replace(' ', '/');
		strDate = strDate.replace('_', '/');
		strDate = strDate.replace(':', '/');
		i = strDate.indexOf("/");

		// System.err.println("String2Milisecond.strDate:" + strDate);
		if (i < 0) {
			return 0;
		}
		try {
			// Get Nam
			String[] arrDate = strDate.split("/");
			nYear = (new Integer(arrDate[0].trim())).intValue();
			nMonth = (new Integer(arrDate[1].trim())).intValue() - 1;
			nDay = (new Integer(arrDate[2].trim())).intValue();
			nHour = (new Integer(arrDate[3].trim())).intValue();
			nMinute = (new Integer(arrDate[4].trim())).intValue();
			nSecond = (new Integer(arrDate[5].trim())).intValue();

			// System.err.println("nYear: " + nYear + "@"+ nMonth + "@" +
			// nDay+"@"+ nHour + "@" + nMinute + "@" + nSecond);
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			calendar.set(nYear, nMonth, nDay, nHour, nMinute, nSecond);

			return calendar.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void run() {

		while (Sender.processData) {

			// Connection connection = null;
			// PreparedStatement stmt = null;
			// ResultSet rs = null;
			CServices[] arrServices = null;
			try {

				Vector vtServices = DBUtil
						.getVectorTable(
								"s2vmsicom",
								"select id, services, minutes, hours, dayofmonth, month, "
										+ "dayofweek, weekofyear, result, retries, lasttime, class,"
										+ "options,alertmin,notcharge from services ");
				arrServices = new CServices[vtServices.size()];

				for (int i = 0; i < vtServices.size(); i++) {
					Vector item = (Vector) vtServices.elementAt(i);

					// id, services, minutes, hours, dayofmonth, month,
					// dayofweek, weekofyear, result, retries, lasttime, class
					String id = (String) item.elementAt(0);
					String services = (String) item.elementAt(1);
					String minutes = (String) item.elementAt(2);
					String hours = (String) item.elementAt(3);
					String dayofmonth = (String) item.elementAt(4);
					String month = (String) item.elementAt(5);
					String dayofweek = (String) item.elementAt(6);
					String weekofyear = (String) item.elementAt(7);
					int result = Integer.parseInt((String) item.elementAt(8));
					int retries = Integer.parseInt((String) item.elementAt(9));
					String slasttime = (String) item.elementAt(10);
					Timestamp lasttime = new Timestamp(
							String2MilisecondNew(slasttime));
					String classname = (String) item.elementAt(11);
					String option = (String) item.elementAt(12);
					int alertmin = Integer
							.parseInt((String) item.elementAt(13));

					int notcharge = Integer.parseInt((String) item
							.elementAt(14));

					CServices cservices = new CServices(services, minutes,
							hours, dayofmonth, month, dayofweek, weekofyear,
							result, retries, lasttime, classname, option);

					if (cservices.istime2run()) {
						// thoi diem thread chay
						Hashtable<String, String> hServicePromotion = new Hashtable<String, String>();
						hServicePromotion = PromotionDAO.getServicePromotion();
						String sNumCode = "null";
						sNumCode = hServicePromotion.get(services);
						int iActive = Integer.parseInt(Constants._prop
								.getProperty("active_mdt", "0"));
						int iModeTest = Integer.parseInt(Constants._prop
								.getProperty("mode_test_mdt", "1"));

						Vector vtUsers = getListUserFromListSend(services);
						int cUser = vtUsers.size();
						// phan khuyen mai MDT
						if (iActive == 1 && sNumCode != null) {
							for (int j = 0; j < cUser; j++) {

								Vector item1 = (Vector) vtUsers.elementAt(j);

								String userid = (String) item1.elementAt(1);
								String serviceid = (String) item1.elementAt(2);
								item1.elementAt(3);
								String commandcode = (String) item1
										.elementAt(4);
								String requestid = (String) item1.elementAt(5);
								String messagetype = (String) item1
										.elementAt(6);
								String mobileoperator = (String) item1
										.elementAt(7);
								int content_id = Integer
										.parseInt((String) item1.elementAt(9));
								int msgtype = Integer.parseInt(messagetype);
								boolean checkUserTest = true;
								if (iModeTest == 1) {
									// mode test phai check user co trong bang
									// user_test
									// hay khong
									if (PromotionDAO.isExistUser(userid) == 0)
										checkUserTest = false;
								} else {
									checkUserTest = true;
								}

								if (checkUserTest) {

									// lay MDT cua user do
									Vector<PromotionCodeDTO> vProCode = PromotionDAO
											.getPromotionCodeDTO(userid,
													commandcode);

									String sMDT = "";

									for (int h = 0; h < vProCode.size(); h++) {
										PromotionCodeDTO proCode = vProCode
												.get(h);
										if (proCode == null)
											break;
										// neu MDT chua dc active thi active ma
										// do len
										if (proCode.getActive() == 0) {
											PromotionDAO
													.activeProtionCode(proCode
															.getPromotionCode());
										}
										// neu chua tra MT -> send MT
										if (proCode.getMtStatus() == 0) {
											if (j != vProCode.size() - 1)
												sMDT = sMDT
														+ proCode
																.getPromotionCode()
														+ ",";
											else
												sMDT = sMDT
														+ proCode
																.getPromotionCode();
										}
									}

									Util.logger
											.info("@DeliveryManager @promotion @User: "
													+ userid
													+ " @had "
													+ sMDT
													+ " promotion code");
									if (!sMDT.equals("")) {
										String sUserText = PromotionConstant.REGISTER_BL;
										sUserText = sUserText.replaceAll(
												"PROCODE", sMDT);

										MsgObject obj = new MsgObject();
										obj.setUserid(userid);
										obj.setServiceid(serviceid);
										obj.setKeyword(commandcode);
										obj.setMobileoperator(mobileoperator);
										obj.setContenttype(content_id);
										obj.setUsertext(sUserText);
										obj.setRequestid(new BigDecimal(
												requestid));
										obj.setMsgtype(msgtype);

										MTDAO.sendMT(obj);
										// update lai sendMT
										PromotionDAO.activeSendMT(userid,
												commandcode);
									}
								}
							}

						}

					}

				}

			} catch (Exception e) {
				Util.logger.error(className + " @error:" + e);
				Util.logger.printStackTrace(e);
			} finally {

				// dbpool.cleanup(connection);
			}

			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static Vector getListUserFromListSend(String serviceName)
			throws Exception {
		String sqlSelect = "select id, user_id, service_id, last_code,command_code,request_id, message_type, mobile_operator "
				+ " , amount, content_id, options,channel_type from list_send "
				+ " where upper(command_code) like '"
				+ serviceName.toUpperCase() + "'";
		Util.logger.info(sqlSelect);
		Vector vtUsers = DBUtil.getVectorTable("s2vmsicom", sqlSelect);
		return vtUsers == null ? new Vector() : vtUsers;
	}
}
