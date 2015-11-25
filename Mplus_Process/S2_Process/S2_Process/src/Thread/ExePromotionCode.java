package Thread;

import icom.Constants;
import icom.MsgObject;
import icom.Sender;
import icom.common.Util;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import DAO.MTDAO;
import DAO.PromotionDAO;
import DAO.ServiceDAO;

public class ExePromotionCode extends Thread {
	String className = "ExePromotionCode ";
	int iActive = 1;
	int iModeTest = 0;
	String dayPromotionCode = "";

	public ExePromotionCode() {
		this.dayPromotionCode = Constants._prop.getProperty("day_start",
				"2012-07-20");
		this.iActive = Integer.parseInt(Constants._prop.getProperty(
				"active_mdt", "0"));
		this.iModeTest = Integer.parseInt(Constants._prop.getProperty(
				"mode_test_mdt", "1"));
	}

	public void run() {

		Util.logger.info(className + "start");

		Vector<String> vServiceP = new Vector<String>();
		Vector<String> vServicePIcom = new Vector<String>();
		vServiceP = PromotionDAO.getServiceNameP("gateway");
		/* vServicePIcom = PromotionDAO.getServiceNameP("s2vmsicom"); */
		Hashtable<String, String> hServicePromotion = PromotionDAO
				.getServicePromotion();
		if (Sender.getData && iActive == 1) {
			try {
				// lay ra cac bang mlist

				String sServices = "";

				for (int i = 0; i < vServiceP.size(); i++) {
					if (i != vServiceP.size() - 1)
						sServices = sServices + "'" + vServiceP.get(i) + "',";
					else
						sServices = sServices + "'" + vServiceP.get(i) + "'";
				}
				if (sServices.equals(""))
					sServices = "''";

				Vector<String> vMlist = new Vector<String>();
				vMlist = ServiceDAO.getMlistByCommandCode(sServices, "gateway");
				// duyet toan bo mlist lay ra tap user_commandCode\
				Util.logger.info("Xu ly ben VMS :Mlist.size:" + vMlist.size());
				for (String sMlists : vMlist) {

					String sMlist = sMlists.split(";")[0];
					String sCommandCode = sMlists.split(";")[1];
					if (hServicePromotion.get(sCommandCode) == null)
						break;
					int iNumCode = Integer.parseInt(hServicePromotion
							.get(sCommandCode));

					Vector<String> vUser = new Vector<String>();
					vUser = ServiceDAO.getUserFromMlist(sMlist, "gateway");
					Util.logger.info(className + " process mlist VMS "
							+ sMlists + " @has " + vUser.size() + " user");
					for (String sUser : vUser) {

						String msisdn = sUser.split(";")[0];
						String commandCode = sUser.split(";")[1];

						if (PromotionDAO.hasPromotionCode(msisdn, commandCode) == 0) {
							for (int k = 0; k < iNumCode; k++)
								PromotionDAO.insertPromotionNoMT(msisdn,
										commandCode);
						}
					}

					Thread.sleep(100);
				}
				// Xu ly ben db icom

				Vector<String> vMlistIcom = new Vector<String>();
				vMlistIcom = ServiceDAO.getMlistByCommandCode(sServices,
						"s2vmsicom");
				// duyet toan bo mlist lay ra tap user_commandCode
				for (String sMlistIcom : vMlistIcom) {

					Util.logger.info(className + " process mlist icom "
							+ sMlistIcom);

					String sMlist = sMlistIcom.split(";")[0];
					String sCommandCode = sMlistIcom.split(";")[1];
					Vector<String> vUser = new Vector<String>();
					vUser = ServiceDAO.getUserFromMlist(sMlist, "s2vmsicom");

					for (String sUser : vUser) {

						String msisdn = sUser.split(";")[0];
						String commandCode = sUser.split(";")[1];
						if (hServicePromotion.get(sCommandCode) == null)
							break;

						int iNumCode = Integer.parseInt(hServicePromotion
								.get(sCommandCode));
						boolean checkUserTest = false;
						if (iModeTest == 1) {
							// mode test phai check user co trong bang user_test
							// hay khong
							if (PromotionDAO.isExistUser(msisdn) != 0) {
								checkUserTest = true;
							}
						} else {
							checkUserTest = true;
						}
						if (checkUserTest)
							if (PromotionDAO.hasPromotionCode(msisdn,
									commandCode) == 0) {
								for (int k = 0; k < iNumCode; k++)
									PromotionDAO.insertPromotionNoMT(msisdn,
											commandCode);
							}
					}
					Thread.sleep(100);
				}

				// notify
				MsgObject msgObject = new MsgObject();
				msgObject.setUserid("84934408198");
				msgObject.setServiceid("9209");
				msgObject.setMobileoperator("VMS");
				msgObject.setKeyword("CUOI");
				msgObject.setContenttype(0);

				msgObject
						.setUsertext("Xong roi kia`!!! off Thread di Hoc oi !!!!");
				msgObject.setMsgtype(1);
				msgObject.setRequestid(new BigDecimal("99999"));
				msgObject.setChannelType(0);
				MTDAO.sendMT(msgObject);
			} catch (Exception e) {
				Util.logger.error(className + "@error:" + e);
				
				Util.logger.printStackTrace(e);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
