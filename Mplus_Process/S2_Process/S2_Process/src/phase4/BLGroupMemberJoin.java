package phase4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;

public class BLGroupMemberJoin extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String[] arrInfo = msgObject.getUsertext().trim().split(" ");

		DBInsert dbInsert = new DBInsert();

		Ph4Common cmmObj = new Ph4Common();
		
		String groupName = "";
		String userId = msgObject.getUserid();
		String infoReturn = "";
		
		if(arrInfo.length <= 1){
			infoReturn = BinhLuanConstant.MT_MEMBER_JOIN_GROUP_WRONG;
		}else if(arrInfo.length >= 2){
			// Huy nhom
			//1. Check 
			groupName = arrInfo[1].trim();
			BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName);			
			
			if(blGroupObj.getId() > 0 ){				
				
				ArrayList<String> arrMember = new ArrayList<String>();
				arrMember.add(userId);
				if(!cmmObj.isMember(blGroupObj.getId(), userId)){
					cmmObj.insertBLGroupMember(blGroupObj.getId(), arrMember);
				}
				
				infoReturn = BinhLuanConstant.MT_MEMBER_JOIN_GROUP_SUCCESS;
				infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
			
			}else{
				infoReturn = BinhLuanConstant.MT_MEMBER_JOIN_GROUP_WRONG;
			}
						
		}
		
		msgObject.setUsertext(infoReturn);
		dbInsert.sendMT(msgObject);
				
		return null;

	}

	
	
}
