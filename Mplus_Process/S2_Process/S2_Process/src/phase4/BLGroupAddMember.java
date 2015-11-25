package phase4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;

public class BLGroupAddMember extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		String[] arrInfo = msgObject.getUsertext().split(" ");

		DBInsert dbInsert = new DBInsert();
		
		Ph4Common cmmObj = new Ph4Common();

		if (arrInfo.length < 3) {
			String userText = keyword.getErrMsg();
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;
		}

		// create new BINH LUAN GROUP
		// 1. Check Valid Group Name
		// 2. check group name is exist?
		String groupName = arrInfo[1].trim();
		String userId = msgObject.getUserid();

		BinhLuanGroupObj blGroupObj = cmmObj.getBLGroupObj(groupName, userId);

		if (blGroupObj.getId() <= 0) {
			// Send Invalid MT
			String userText = BinhLuanConstant.MT_ADD_MEMBER_WRONG;
			msgObject.setUsertext(userText);
			dbInsert.sendMT(msgObject);
			return null;

		}

		// 1. Get phone number and check phone number
		// 2. insert binh luan group

		ArrayList<String> arrPhoneMember = new ArrayList<String>();

		for (int i = 2; i < arrInfo.length; i++) {
			String phoneMember = arrInfo[i];
			phoneMember = cmmObj.validPhoneNumber(phoneMember);
			if (cmmObj.isValidPhoneNumber(phoneMember)) {
				arrPhoneMember.add(phoneMember);
			}
		}

		String userText = "";
		if (arrPhoneMember.size() <= 0) {
			// Send MT Error
			userText = "Quy khach vui long kiem tra lai, giua cac so dien thoai co dau cach." +
					" Dich vu chi dung cho cac thue bao mang MobiFone." +
					" DTHT:9244.Chi tiet:http://m.mplus.vn";
		} else {
			String sMembersAdd = "";
			for (int i = 0; i < arrPhoneMember.size(); i++) {
				if (i == 0) {
					sMembersAdd = arrPhoneMember.get(i);
				} else {
					sMembersAdd = sMembersAdd + "," + arrPhoneMember.get(i);
				}
			}
			
			userText = BinhLuanConstant.MT_ADD_MEMBER_SUCCESS;
			userText = userText.replace(BinhLuanConstant.REPLACE_LIST_MEMBERS, sMembersAdd);
			userText = userText.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);

		}
		
		msgObject.setUsertext(userText);
		dbInsert.sendMT(msgObject);
		
		String infoInvite = BinhLuanConstant.MT_GROUP_INVITE;
		infoInvite = infoInvite.replace(BinhLuanConstant.REPLACE_USER_CREATOR, msgObject.getUserid());
		infoInvite = infoInvite.replace(BinhLuanConstant.REPLACE_GROUP_NAME, groupName);
		for(int i=0;i<arrPhoneMember.size();i++){
			
			msgObject.setUsertext(infoInvite);
			msgObject.setUserid(arrPhoneMember.get(i));
			dbInsert.sendMT(msgObject);
			
		}

		return null;
	}

}
