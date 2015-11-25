package sub;

import icom.Constants;
import icom.DBPool;
import icom.MsgObject;
import icom.common.DBUtil;
import icom.common.Util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Constant.PromotionConstant;
import DAO.MTDAO;
import DAO.PromotionDAO;
import DTO.PromotionCodeDTO;

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
public abstract class DeliveryManager {

	@SuppressWarnings("unchecked")
	public int start(String id, String services, String option, int notcharge)
			throws Exception {
		try {

			Util.logger.info("DeliveryManager@start:" + services + "");

			// cac bien khai bao ma du thuong
			Hashtable<String, String> hServicePromotion = new Hashtable<String, String>();
			hServicePromotion = PromotionDAO.getServicePromotion();
			String sNumCode = "null";
			sNumCode = hServicePromotion.get(services);
			int iActive = Integer.parseInt(Constants._prop.getProperty(
					"active_mdt", "0"));
			int iModeTest = Integer.parseInt(Constants._prop.getProperty(
					"mode_test_mdt", "1"));

			Vector vtUsers = DBUtil.getListUserFromListSend(services);
			int cUser = vtUsers.size();
			// phan khuyen mai MDT
			if (iActive == 1 && sNumCode != null) {
				for (int i = 0; i < cUser; i++) {
					MsgObject obj = new MsgObject();
					// send mt

					
					Vector item = (Vector) vtUsers.elementAt(i);

					String userid = (String) item.elementAt(1);
					String serviceid = (String) item.elementAt(2);
					item.elementAt(3);
					String commandcode = (String) item.elementAt(4);
					String requestid = (String) item.elementAt(5);
					String messagetype = (String) item.elementAt(6);
					String mobileoperator = (String) item.elementAt(7);
					int content_id = Integer.parseInt((String) item
							.elementAt(9));
					int msgtype = Integer.parseInt(messagetype);
					obj.setUserid(userid);
					obj.setServiceid(serviceid);
					obj.setKeyword(commandcode);
					obj.setMobileoperator(mobileoperator);
					obj.setContenttype(content_id);
					obj.setRequestid(new BigDecimal(requestid));
					obj.setMsgtype(msgtype);
					obj.setCommandCode(serviceid);
					
					boolean checkUserTest = true;

					// lam cho mode test
					if (iModeTest == 1) {
						// mode test phai check user co trong bang user_test
						// hay khong
						if (PromotionDAO.isExistUser(userid) == 0)
							checkUserTest = false;
					} else {
						checkUserTest = true;
					}

					if (checkUserTest) {
						// lay MDT cua user do
						Vector<PromotionCodeDTO> vProCode = PromotionDAO
								.getPromotionCodeDTO(userid, commandcode);
						String sMDT = "";
						boolean bCheckActive = false;
						/*
						 * TH1: Duyet MDT cua user do Neu chua active -> active
						 * Neu chua send MT -> sendMT
						 */
						for (int j = 0; j < vProCode.size(); j++) {
							PromotionCodeDTO proCode = vProCode.get(j);
							if (proCode == null)
								break;
							// neu MDT chua dc active thi active ma do len
							if (proCode.getActive() == 0) {
								PromotionDAO.activeProtionCode(proCode
										.getPromotionCode());
								bCheckActive = true;
							}
							// neu chua tra MT -> send MT
							if (proCode.getMtStatus() == 0) {
								if (j != vProCode.size() - 1)
									sMDT = sMDT + proCode.getPromotionCode()
											+ ",";
								else
									sMDT = sMDT + proCode.getPromotionCode();
							}
						}

						if (!sMDT.equals("")) {
							Util.logger
									.info("@DeliveryManager @promotion @User: "
											+ userid + " @had " + sMDT
											+ " promotion code, send MT");
							String sUserText = PromotionConstant.REGISTER_BL;
							sUserText = sUserText.replaceAll("PROCODE", sMDT);

							obj.setUsertext(sUserText);

							obj.setCommandCode(serviceid);
							sendMT(obj);
							// update lai sendMT
							PromotionDAO.activeSendMT(userid, commandcode);
						}
						int iSize = vProCode.size();

						/*
						 * TH2: Neu user chua co MDT se insert them cho user do
						 * TH3: Neu user co MDT nhung da active all cung insert new
						 */
						if (iSize == 0 || (!bCheckActive &&sMDT.equals(""))) {
							Util.logger.info("User:" + userid
									+ " insert new promotion code.");
							int iNumCode = Integer.parseInt(hServicePromotion
									.get(commandcode.toUpperCase()));
							// insert vao bang? service_promotion_code
							for (int j = 0; j < iNumCode; j++)
								PromotionDAO.insertPromotionActiceAll(userid, serviceid
										.toUpperCase());
							// lay lai promotion_code de tra mt
							Vector<String> vProCodeNew = PromotionDAO
									.getPromotionCodeSendMt(userid, serviceid
											.toUpperCase(), iNumCode);
							String sProCode = "";
							for (int k = 0; k < vProCodeNew.size(); k++) {
								if (k != vProCodeNew.size() - 1)
									sProCode = sProCode + vProCodeNew.get(k)
											+ ", ";
								else
									sProCode = sProCode + vProCodeNew.get(k);
							}

							Util.logger.info("User:" + userid
									+ " insert new prometion code @sProCode:"
									+ sProCode);

							String sTextSend;
							if (!serviceid.toUpperCase().equals("YTE"))
								sTextSend = PromotionConstant.REGISTER_YTE;
							else
								sTextSend = PromotionConstant.REGISTER_CUOI;

							sTextSend = sTextSend.replaceAll("PROCODE",
									sProCode);
							obj.setUsertext(sTextSend);
							if (!sProCode.equals(""))
								MTDAO.sendMT(obj);
							else {
								Util.logger
										.error("Get  promotion code error!!!!plz check:"
												+ obj.getUserid()
												+ " @commandCode:"
												+ obj.getCommandCode());
							}
						}
					}
				}
			}
			Collection messages = getMessages(id, option, services, notcharge);

			if (messages != null) {
				Iterator iter = messages.iterator();
				for (int i = 1; iter.hasNext(); i++) {
					MsgObject msgMT = (MsgObject) iter.next();
					sendMT(msgMT);

				}
			} else {
				Util.logger.info("DeliveryManager@start:" + services + "  ok");

			}
		} catch (Exception e) {
			Util.logger.info("DeliveryManager@start:" + services + "@"
					+ e.toString());
			Util.logger.printStackTrace(e);

		}
		return 1;

	}

	@SuppressWarnings("unchecked")
	protected abstract Collection getMessages(String services, String option,
			String servicename, int notcharge) throws Exception;

	private static int sendMT(MsgObject msgObject) {

		Connection connection = null;
		PreparedStatement statement = null;
		String sqlString = null;
		if ("".equalsIgnoreCase(msgObject.getUsertext().trim())
				|| msgObject.getUsertext() == null) {
			// Truong hop gui ban tin loi
			Util.logger.error("ContentAbstract@sendMT@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@requestid="
					+ msgObject.getRequestid().toString()
					+ "@message is null - LOST MESSAGE");
			return 1;

		}

		DBPool dbpool = new DBPool();

		Util.logger.info("ContentAbstract@sendMT@userid="
				+ msgObject.getUserid() + "@serviceid="
				+ msgObject.getServiceid() + "@usertext="
				+ msgObject.getUsertext() + "@messagetype="
				+ msgObject.getMsgtype() + "@requestid="
				+ msgObject.getRequestid().toString());
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger
						.crisis("ContentAbstract@sendMT: Error connection == null"
								+ msgObject.getUserid()
								+ "@TO"
								+ msgObject.getServiceid()
								+ "@"
								+ msgObject.getUsertext()
								+ "@requestid="
								+ msgObject.getRequestid().toString());
				return -1;
			}

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE,
			// INFO, MESSAGE_TYPE, REQUEST_ID
			sqlString = "INSERT INTO "
					+ Constants.tblMTQueue
					+ "( USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE, CONTENT_TYPE, INFO,  MESSAGE_TYPE, REQUEST_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			// USER_ID, SERVICE_ID, MOBILE_OPERATOR, COMMAND_CODE,
			// CONTENT_TYPE, INFO, MESSAGE_TYPE, REQUEST_ID
			statement = connection.prepareStatement(sqlString);
			statement.setString(1, msgObject.getUserid());
			statement.setString(2, msgObject.getServiceid());
			statement.setString(3, msgObject.getMobileoperator());
			statement.setString(4, msgObject.getKeyword());
			statement.setInt(5, msgObject.getContenttype());
			statement.setString(6, msgObject.getUsertext());
			statement.setInt(7, msgObject.getMsgtype());
			statement.setBigDecimal(8, msgObject.getRequestid());

			if (statement.executeUpdate() != 1) {
				Util.logger.crisis("ContentAbstract@sendMT: Error@userid="
						+ msgObject.getUserid() + "@serviceid="
						+ msgObject.getServiceid() + "@usertext="
						+ msgObject.getUsertext() + "@messagetype="
						+ msgObject.getMsgtype() + "@requestid="
						+ msgObject.getRequestid().toString());
				return -1;
			}
			return 1;
		} catch (SQLException e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} catch (Exception e) {
			Util.logger.crisis("ContentAbstract@sendMT: Error:@userid="
					+ msgObject.getUserid() + "@serviceid="
					+ msgObject.getServiceid() + "@usertext="
					+ msgObject.getUsertext() + "@messagetype="
					+ msgObject.getMsgtype() + "@requestid="
					+ msgObject.getRequestid().toString() + "@" + e.toString());
			return -1;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);

		}
	}

}
