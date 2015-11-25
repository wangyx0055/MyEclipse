package phase4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import servicesPkg.MlistInfo;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.DBUtil;
import icom.common.ResultCode;
import icom.common.Util;

public class ChatMTHandle extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		try {
			HashMap hoption = new HashMap();
			String options = keyword.getOptions();
			hoption = Util.getParametersAsString(options);
			String tblMlist = Util.getStringfromHashMap(hoption, "mlist", "");

			if (tblMlist.trim().equals("")) {
				Util.logger
						.error("CHAT --- Keyword.option not input mlist table!!!!!!");
				return null;
			}

			int resultCharge = msgObject.getChargeResult();
			ChatCommon chComm = new ChatCommon();
			Ph4Common cmmObj = new Ph4Common();
			DBInsert dbInsert = new DBInsert();
			
		
			
			if (msgObject.getUsertext().startsWith("CHAT_CHARGING")) {
				String[] arrInfo = msgObject.getUsertext().split(";");
				String[] arrId = arrInfo[1].split("=");
				int mlistId = Integer.parseInt(arrId[1]);

				if (resultCharge == ResultCode.OK
						|| resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
					chComm.updateMlistActive(tblMlist, mlistId, 1);
					chComm.increaseMlistNumberCharge(mlistId, tblMlist);
					
				} else {
					chComm.updateMlistActive(tblMlist, mlistId, 0);
				}

				return null;
			}

			String[] arrInfo = msgObject.getUsertext().trim().split(" ");

			String userReceive = cmmObj.validPhoneNumber(arrInfo[1]);
			
			String chatContent = msgObject.getUsertext().substring(arrInfo[0].length() + 1 + arrInfo[1].length());

			ChatBlockedObj blockedObj = chComm.getBlockObj(userReceive, msgObject.getUserid());
			if(blockedObj.getId()>0){
				return null;
			}
			
			ChatBlockedObj blockedObjUserSend = chComm.getBlockObj(msgObject.getUserid(), userReceive);
			if(blockedObjUserSend.getId()>0){
				
				String sqlDelete = "DELETE FROM chat_blocked WHERE id = " + blockedObjUserSend.getId();
				DBUtil.executeSQL("gateway", sqlDelete);
			}
			
			String infoReturn = "";

			if (resultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT
					|| resultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT) {

				int numberMTSend = chComm.getNumberUserSent(msgObject
						.getUserid());
				if (numberMTSend <= 0) {
					infoReturn = "Hien tai khoan cua ban khong du de su dung dich vu Chat,"
							+ " ket noi, Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)."
							+ " DTHT:9244.Chi tiet:http://m.mplus.vn";
				} else {
					infoReturn = "Hien tai khoan cua ban khong du de chat voi ban be moi,"
							+ " tu thue bao 31 tro di cuoc phi bi tru 3000d/30 thue bao tiep theo."
							+ " Nap tien de su dung dich vu Chat, ket noi voi ban be qua SMS khong gioi han:"
							+ " CHAT_SDT_Noidung gui 9209 (3000vnd/ngay). DTHT:9244.Chi tiet:http://m.mplus.vn";
				}

				msgObject.setUsertext(infoReturn);
				dbInsert.sendMT(msgObject);

				return null;

			} else if (resultCharge == ResultCode.OK
					|| resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
				
				MlistInfo mlistObj = chComm.getMlistInfoObject(tblMlist, msgObject.getUserid(),
						keyword.getServiceid(),msgObject.getServiceName());
				chComm.increaseMlistNumberCharge(mlistObj.getId(), tblMlist);
				chComm.updateMlistActive(tblMlist, mlistObj.getId(), 1);
				
				ChatSendDailyObj sendObj = chComm.getChatDailyObj(msgObject
						.getUserid(), userReceive);
				if (sendObj.getId() <= 0) {

					ChatSendDailyObj response = chComm.getChatDailyObj(
							userReceive, msgObject.getUserid());
					int isResponse = 0;
					if (response.getId() > 0) {
						isResponse = 1;
					}

					chComm.insertChatSendDaily(msgObject.getUserid(),
							userReceive, isResponse, 1);
					chComm.updateIsResponse(response.getId(), 1);

					infoReturn = msgObject.getUserid()
							+ ": "
							+ chatContent
							+ ". De Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)."
							+ " DTHT:9244.Chi tiet:http://m.mplus.vn";

					msgObject.setUserid(userReceive);
					msgObject.setUsertext(infoReturn);
					dbInsert.sendMT(msgObject);
					return null;

				} else {

					chComm.increaseNumberSms(sendObj.getId());

					if (sendObj.getNumberSms() == 1) {
						infoReturn = msgObject.getUserid()
								+ ": "
								+ chatContent
								+ ". De Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)."
								+ " Khong muon tiep tuc nhan tin nhan tu xxxx moi soan: CHAT_CHAN_SDT gửi 9209.DTHT:9244.Chi tiet:http://m.mplus.vn";
						msgObject.setUserid(userReceive);
						msgObject.setUsertext(infoReturn);
						dbInsert.sendMT(msgObject);
						chComm.increaseNumberSms(sendObj.getId());

						return null;
					}

					if (sendObj.getNumberSms() >= 2) {
						infoReturn = msgObject.getUserid() + ": " + chatContent;
						msgObject.setUserid(userReceive);
						msgObject.setUsertext(infoReturn);
						dbInsert.sendMT(msgObject);
						chComm.increaseNumberSms(sendObj.getId());
						return null;
					}
				}

			} else {
				msgObject
						.setUsertext("He thong khong tru tien cua ban, xin vui long thu lai!");
				dbInsert.sendMT(msgObject);
			}
		} catch (Exception ex) {
			Util.logger.error("ChatMTHandle ::: " + ex.getMessage());
		}

		return null;
	}
	
	

}
