package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;

public class BLGroupSendMO extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String[] arrInfo = msgObject.getUsertext().split(" ");

		DBInsert dbInsert = new DBInsert();
		
		Ph4Common cmmObj = new Ph4Common();
		
		if(arrInfo.length<3){
			String userText = keyword.getErrMsg();
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;
		}
		
		String groupName = arrInfo[1];
		
		BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName);
		
		if(blGroupObj.getId()<=0){
			String userText = BinhLuanConstant.MT_SEND_GROUP_WRONG_NAME;
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;
		}
		
		BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(blGroupObj.getId(), msgObject.getUserid());
		
		if(memberObj.getId()<=0){
			msgObject.setAmount(500);
			cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);			
		}else{
			msgObject.setAmount(keyword.getAmount());
			
			// check free list
			if(cmmObj.isFreeList(msgObject.getUserid(), "BLN")){
				cmmObj.insertBLGroupContent(msgObject.getUserid(), groupName, msgObject.getUsertext());
				msgObject.setUsertext(keyword.getSubMsg());
				dbInsert.sendMT(msgObject);
				return null;
			}
			
			if(memberObj.getIsCharging() == 2){
				msgObject.setUsertext("Binh Luan Nhom: Tin ban gui len nhom khong thanh cong. Xin vui long thu lai!");
				dbInsert.sendMT(msgObject);
				return null;
			}
			
			if(memberObj.getIsCharging() == 0){
				int result = cmmObj.updateChargingMember(memberObj.getId(), 2);
				if(result == -1){
					msgObject.setUsertext(" Tin ban gui len nhom khong thanh cong. Xin vui long thu lai!");
					dbInsert.sendMT(msgObject);
					return null;
				}
				
				msgObject.setAmount(keyword.getAmount());
				
				cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
				return null;
			}
			
			if(memberObj.getIsCharging() == 1){
				cmmObj.insertBLGroupContent(msgObject.getUserid(), groupName, msgObject.getUsertext());
				msgObject.setUsertext(keyword.getSubMsg());
				dbInsert.sendMT(msgObject);
			}
			
		}
		
		
		return null;
	}

}
