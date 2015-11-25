package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;

public class BLGroupMemberRemove extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
						
		String[] arrInfo = msgObject.getUsertext().trim().split(" ");

		DBInsert dbInsert = new DBInsert();

		Ph4Common cmmObj = new Ph4Common();
		
		String groupName = "";
		String userId = msgObject.getUserid();
		String infoReturn = "";
		
		if(arrInfo.length == 2){
			infoReturn = BinhLuanConstant.MT_MEMBER_REMOVE_WRONG;
		}else if(arrInfo.length >= 3){
			// Huy nhom
			//1. Check 
			groupName = arrInfo[2].trim();
			BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName);			
			
			if(blGroupObj.getId() > 0 ){				
				
				BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(blGroupObj.getId(), userId);
				if(memberObj.getId()>0){
					cmmObj.moveToMemberCancel(memberObj.getId());
					cmmObj.deleteMember(blGroupObj.getId(), userId);
				}
				
				infoReturn = BinhLuanConstant.MT_MEMBER_REMOVE_SUCCESS;
				infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
				String userCreater = blGroupObj.getUserCreate();
				infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_USER_CREATOR, userCreater);
												
			}else{
				infoReturn = BinhLuanConstant.MT_MEMBER_REMOVE_WRONG;
			}
						
		}
		
		msgObject.setUsertext(infoReturn);
		msgObject.setKeyword("BLN");
		dbInsert.sendMT(msgObject);
				
		return null;

	}
	
	

}
