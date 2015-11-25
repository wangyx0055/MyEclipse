package phase4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import oracle.jdbc.dbaccess.DBType;

import servicesPkg.MlistInfo;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.DBUtil;
import icom.common.Util;

public class ChatMOHandle extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String[] arrInfo = msgObject.getUsertext().trim().split(" ");
		if(arrInfo.length == 1){
			return DBUtil.RegisterServices(msgObject, keyword, Constants.TYPE_OF_SERVICE_TEXTBASE,services);
		}
		
		msgObject.setAmount(keyword.getAmount());
		
		Ph4Common cmmObj = new Ph4Common();
		DBInsert dbInsert = new DBInsert();
		String infoReturn = "";
		
		if(arrInfo.length == 2){
			infoReturn = "Tin nhan chua dung cu phap. " +
					"BL, ket noi, Chat voi ban be qua SMS khong gioi han: " +
					"CHAT_SDT_Noidung gui 9209 (3000vnd/ngay). DTHT:9244.Chi tiet:http://m.mplus.vn";
			msgObject.setUsertext(infoReturn);
			dbInsert.sendMT(msgObject);
			return null;
		}
						
		String userReceive = cmmObj.validPhoneNumber(arrInfo[1]);
		
		// check number phone
		if(!cmmObj.isValidPhoneNumber(userReceive)){
			infoReturn = "Ban vui long kiem tra lai, so dien thoai gui den chua dung." +
					" Binh luan,Ket noi,Chat voi ban be qua SMS khong gioi han:" +
					" CHAT_SDT_Noidung gui 9209 (3000vnd/ngay). DTHT:9244.Chi tiet:http://m.mplus.vn";
			msgObject.setUsertext(infoReturn);
			dbInsert.sendMT(msgObject);
			return null;
		}
		
		String chatContent = msgObject.getUsertext().substring(arrInfo[0].length() + 1 + arrInfo[1].length());
		
		ChatCommon chCmm = new ChatCommon();
		
		HashMap hoption = new HashMap();
		String options = keyword.getOptions();
		hoption = Util.getParametersAsString(options);
		String tblMlist = Util.getStringfromHashMap(hoption, "mlist", "");
		
		if(tblMlist.trim().equals("")){
			Util.logger.error("CHAT --- Keyword.option not input mlist table!!!!!!");
			return null;
		}
		
		MlistInfo mlistObj = chCmm.getMlistInfoObject(tblMlist, msgObject.getUserid(),
					msgObject.getServiceid(), keyword.getService_ss_id());
		
		if(mlistObj.getId()<=0){
			String userText = msgObject.getUsertext();
			DBUtil.RegisterServices(msgObject, keyword, Constants.TYPE_OF_SERVICE_TEXTBASE,services);
			mlistObj = chCmm.getMlistInfoObject(tblMlist, msgObject.getUserid(),
					msgObject.getServiceid(), keyword.getService_ss_id());
			
			msgObject.setUsertext(userText);
			
			if(mlistObj.getId()<=0) return null;
			
			chCmm.updateMlistActive(tblMlist, mlistObj.getId(), 2);
			
			msgObject.setAmount(keyword.getAmount());
			if(cmmObj.isFreeList(msgObject.getUserid(), msgObject.getServiceName())){
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
			}else{
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
			}
			
			return null;
		}
		
		if(mlistObj.getActive() == 0){
			
			msgObject.setAmount(keyword.getAmount());
			if(cmmObj.isFreeList(msgObject.getUserid(), msgObject.getServiceName())){
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
			}else{
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
				chCmm.updateMlistActive(tblMlist, mlistObj.getId(), 2);
			}
			return null;
		}
		
		if(mlistObj.getActive() == 2){
			return null;
		}
		
		if(mlistObj.getActive() == 1){
			
			// Check Blocked
			
			ChatBlockedObj blockedObj = chCmm.getBlockObj(userReceive, msgObject.getUserid());
			if(blockedObj.getId()>0){
				return null;
			}
			
			ChatBlockedObj blockedObjUserSend = chCmm.getBlockObj(msgObject.getUserid(), userReceive);
			if(blockedObjUserSend.getId()>0){
				
				String sqlDelete = "DELETE FROM chat_blocked WHERE id = " + blockedObjUserSend.getId();
				DBUtil.executeSQL("gateway", sqlDelete);
			}
			
			int numberUserSent = chCmm.getNumberUserSent(msgObject.getUserid());
			if(mlistObj.getNumberCharge() >= (numberUserSent/30)){ // charge ok
				//1. check response
				ChatSendDailyObj sendObj = chCmm.getChatDailyObj(msgObject.getUserid(), userReceive);
				if(sendObj.getId()<=0){ // chua co trong bang chat_send_daily
					
					ChatSendDailyObj userReceiveSend = chCmm.getChatDailyObj(userReceive, msgObject.getUserid());
					if(userReceiveSend.getId()>0){
						if(userReceiveSend.getIsResponse() == 0){
							chCmm.updateIsResponse(userReceiveSend.getId(), 1);
							chCmm.insertChatSendDaily(msgObject.getUserid(), userReceive, 1, 1);
						}
					}else{
						chCmm.insertChatSendDaily(msgObject.getUserid(), userReceive, 0, 1);
					}
					
					infoReturn = msgObject.getUserid() + ": " + chatContent 
							+ ". De Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)." +
							" DTHT:9244.Chi tiet:http://m.mplus.vn";
					msgObject.setUserid(userReceive);
					msgObject.setUsertext(infoReturn);
					dbInsert.sendMT(msgObject);
					return null;
				}else{
					// is response?
					if(sendObj.getIsResponse() == 0) return null;
					if(sendObj.getNumberSms() == 1){
						infoReturn = msgObject.getUserid() + ": " + chatContent 
						+ ". De Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)." +
						 " Khong muon tiep tuc nhan tin nhan tu xxxx moi soan: CHAT_CHAN_SDT gui 9209.DTHT:9244.Chi tiet:http://m.mplus.vn";
						msgObject.setUserid(userReceive);
						msgObject.setUsertext(infoReturn);
						dbInsert.sendMT(msgObject);
						chCmm.increaseNumberSms(sendObj.getId());
						
						return null;
					}
					
					if(sendObj.getNumberSms() >= 2){
						infoReturn = msgObject.getUserid() + ": " + chatContent;
						msgObject.setUserid(userReceive);
						msgObject.setUsertext(infoReturn);
						dbInsert.sendMT(msgObject);
						chCmm.increaseNumberSms(sendObj.getId());
						return null;
					}
					
				}
			}else{
				// need charging
				chCmm.updateMlistActive(tblMlist, mlistObj.getId(), 2);
				msgObject.setAmount(keyword.getAmount());
				if(cmmObj.isFreeList(msgObject.getUserid(), msgObject.getServiceName())){
					cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
				}else{
					cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
				}
				return null;
			}
			
		}
		
		// check money (mlist.active)
		
		
		
		
		return null;
	}
	
	

}
