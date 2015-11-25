package phase4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;
import icom.common.ResultCode;
import icom.common.Util;

public class BLGroupCheckInfoMT extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		Util.logger.info("Handle BLN_KT Charge result, user_id = "
				+ msgObject.getUserid() + "; info = " + msgObject.getUsertext());
		
		String[] arrInfo = msgObject.getUsertext().split(" ");		
		Ph4Common cmmObj = new Ph4Common();
		DBInsert dbInsert = new DBInsert();
		
		String infoReturn = "";
		int resultCharge = msgObject.getChargeResult();
		
		if(resultCharge == ResultCode.NOK_NOT_ENOUGH_CREDIT || 
				resultCharge == ResultCode.NOK_NO_MORE_AVAILABLE_CREDIT){
			msgObject.setUsertext(keyword.getNotEnoughMoneyMsg());
			dbInsert.sendMT(msgObject);
		
		}else if(resultCharge == ResultCode.OK ||
				resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND){
			
			if(arrInfo.length == 2){
				ArrayList<BinhLuanGroupObj> arrGroup = cmmObj.getArrGroup(msgObject.getUserid());
				if(arrGroup.size() == 0){
					infoReturn = BinhLuanConstant.MT_CHECK_GROUPINFO_NOGROUP;
				}else{
					infoReturn = BinhLuanConstant.MT_CHECK_GROUPINFO;
					infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_NUMBER_GROUP, arrGroup.size()+"");
					
					String listGroup = "";
					for(int i = 0;i<arrGroup.size();i++){
						if(i>0) listGroup = listGroup + ",";
						listGroup = listGroup + arrGroup.get(i).getGroupName();
					}
					
					infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_LIST_GROUPNAME,listGroup);
				}
			}else if(arrInfo.length >2){
				
				String groupName = arrInfo[2];
				BinhLuanGroupObj groupObj = cmmObj.getBLGroupObj(groupName);
				if(groupObj.getId() <=0){
					infoReturn = BinhLuanConstant.MT_CHECK_MEMBER_INFO_ERROR;
				}else{
					
					ArrayList<BLGroupMemberObj> arrMember = cmmObj.getArrayMember(groupObj.getId());
					if(arrMember.size()<=0){
						infoReturn = BinhLuanConstant.MT_CHECK_MEMBER_ZERO;
						infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
					}else{
						infoReturn = BinhLuanConstant.MT_CHECK_MEMBER_SUCCESS;
						infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_NUMBER_MEMBER, arrMember.size()+"");
						String listMember = "";
						for(int i=0;i<arrMember.size();i++){
							if(i>0) listMember = listMember + ",";
							listMember = listMember + arrMember.get(i).getUserId();
						}
						infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_LIST_MEMBERS, listMember);
						infoReturn = infoReturn.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
					}
					
				}
				
				
			}
			
			msgObject.setUsertext(infoReturn);
			dbInsert.sendMT(msgObject);
			
		}else{
			msgObject.setUsertext(keyword.getErrMsg());
			dbInsert.sendMT(msgObject);
		}
								
		return null;
	}

}
