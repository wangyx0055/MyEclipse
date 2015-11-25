package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.ResultCode;
import icom.common.Util;

public class BLGroupSendMT extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		Util.logger.info("BLGroupSendMT - Handle Charge Result:: user_id = " 
				+ msgObject.getUserid() + "; info = " + msgObject.getUsertext()
				+ "; result charge = " + msgObject.getChargeResult());
		
		String[] arrInfo = msgObject.getUsertext().split(" ");
		DBInsert dbInsert = new DBInsert();
		Ph4Common cmmObj = new Ph4Common();
		
		if(arrInfo.length<=2) return null;
		
		int resultCharge = msgObject.getChargeResult();
		
		
		
		if(resultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT || 
					resultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT) {
			msgObject.setUsertext(keyword.getNotEnoughMoneyMsg());
			dbInsert.sendMT(msgObject);

			String groupName = arrInfo[1];
			BinhLuanGroupObj groupObj = cmmObj.getBLGroupObj(groupName);
			if (groupObj.getId() > 0) {
				BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(groupObj.getId(), msgObject.getUserid());
				cmmObj.updateChargingMember(memberObj.getId(), 0);
			}

		}else if(resultCharge == ResultCode.OK ||
				resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND){
			try {
				String groupName = arrInfo[1];
				
				BinhLuanGroupObj groupObj = cmmObj.getBLGroupObj(groupName);
				if(groupObj.getId()<=0) return null;
				
				cmmObj.insertBLGroupContent(msgObject.getUserid(), groupName, msgObject.getUsertext());
				
				String infoReturn = BinhLuanConstant.MT_SEND_TO_GROUP_SUCCESS;
				infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
				
				BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(groupObj.getId(), msgObject.getUserid());
				cmmObj.updateChargingMember(memberObj.getId(), 1);
				
				msgObject.setUsertext(infoReturn);
				dbInsert.sendMT(msgObject);
				
			}catch(Exception ex){
				Util.logger.error(ex.getMessage());
			}
		}else{
			String groupName = arrInfo[1];
			BinhLuanGroupObj groupObj = cmmObj.getBLGroupObj(groupName);
			if (groupObj.getId() > 0) {
				BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(groupObj.getId(), msgObject.getUserid());
				cmmObj.updateChargingMember(memberObj.getId(), 0);
			}
			
			msgObject.setUsertext(keyword.getErrMsg());
			dbInsert.sendMT(msgObject);
		}
						
		return null;
	}

}
