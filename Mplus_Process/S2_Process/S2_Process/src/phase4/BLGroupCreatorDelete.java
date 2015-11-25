package phase4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBDelete;
import icom.common.DBInsert;
import icom.common.DBUtil;
import icom.common.Util;

public class BLGroupCreatorDelete extends QuestionManager {

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
			return DBUtil.UnRegisterServices(msgObject, keyword);
		}else if(arrInfo.length == 3){
			// Huy nhom
			//1. Check 
			
			groupName = arrInfo[2].trim();
			Util.logger.info("REMOVE GROUP:: userId = " + userId + ";; group_name = " + groupName);
			
			int groupId = cmmObj.isUserCreate(groupName, userId);			
			
			if(groupId > 0 ){				
				cmmObj.moveToBLGroupCancel(groupName, userId);
				DBDelete dbDelete = new DBDelete();
				dbDelete.deleteByID(groupId, "bl_group","gateway");
				
				cmmObj.moveAllToMemberCancel(groupId);
				cmmObj.deleteAllMember(groupId);
				
				infoReturn = BinhLuanConstant.MT_CREATOR_DELETE_GROUP_SUCCESS;
				infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);				
			}else{
				infoReturn = BinhLuanConstant.MT_CREATOR_DELETE_GROUP_WRONG;
			}
			
		}else if(arrInfo.length > 3){
			// Remove Member
			groupName = arrInfo[2].trim();
			BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName, userId);
			int groupId = blGroupObj.getId();
			
			if(groupId > 0 ){
				ArrayList<String> arrPhoneMember = new ArrayList<String>();

				for (int i = 3; i < arrInfo.length; i++) {
					String phoneMember = arrInfo[i];
					phoneMember = cmmObj.validPhoneNumber(phoneMember);
					if (cmmObj.isValidPhoneNumber(phoneMember)) {
						arrPhoneMember.add(phoneMember);
					}
				}
				
				if(arrPhoneMember.size() <= 0){
					infoReturn = BinhLuanConstant.MT_WRONG_PHONE;
				}else{					
					// delete member
					
					String memberDelete = "";
					for(int i = 0;i<arrPhoneMember.size();i++){						
						if(i>0) memberDelete = memberDelete + ",";
						memberDelete = memberDelete + arrPhoneMember.get(i);
						
						BLGroupMemberObj memberObj = cmmObj.getBLGroupMemberObj(groupId, arrPhoneMember.get(i));
						if(memberObj.getId()>0){
							cmmObj.moveToMemberCancel(memberObj.getId());
						}
						
					}
					
					cmmObj.deleteMember(groupId, memberDelete);
					
					infoReturn = BinhLuanConstant.MT_CREATOR_DELETE_MEMBER;
					infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_LIST_MEMBERS, memberDelete);
					infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
																				
				}
				
			}else{
				infoReturn = BinhLuanConstant.MT_CREATOR_DELETE_GROUP_WRONG;
			}
						
		}
		
		msgObject.setUsertext(infoReturn);
		dbInsert.sendMT(msgObject);
		
		return null;
	}

}
