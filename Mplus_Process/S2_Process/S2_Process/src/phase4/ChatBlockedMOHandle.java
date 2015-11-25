package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBInsert;

public class ChatBlockedMOHandle extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		DBInsert dbInsert = new DBInsert();
		Ph4Common cmmObj = new Ph4Common();
		ChatCommon chCmm = new ChatCommon();
		
		String[] arrInfo = msgObject.getUsertext().trim().split(" ");
		
		String infoReturn = "";
		
		if(arrInfo.length < 3){
			infoReturn = "Tin nhan chua dung cu phap. BL, ket noi," +
			" Chat voi ban be qua SMS khong gioi han: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay)." +
			" DTHT:9244.Chi tiet:http://m.mplus.vn";
			msgObject.setUsertext(infoReturn);
			dbInsert.sendMT(msgObject);
			return null;
		}
		
		String userReceive = cmmObj.validPhoneNumber(arrInfo[2]);

		// check number phone
		if(!cmmObj.isValidPhoneNumber(userReceive)){
			infoReturn = "Ban vui long kiem tra lai, so dien thoai gui den chua dung." +
					" Binh luan,Ket noi,Chat voi ban be qua SMS khong gioi han:" +
					" CHAT_SDT_Noidung gui 9209 (3000vnd/ngay). DTHT:9244.Chi tiet:http://m.mplus.vn";
			msgObject.setUsertext(infoReturn);
			dbInsert.sendMT(msgObject);
			return null;
		}
		
		// insert to chat_blocked
		
		ChatBlockedObj blockObj = chCmm.getBlockObj(msgObject.getUserid(), userReceive);
		if(blockObj.getId() <=0){
			chCmm.insertChatBlocked(msgObject.getUserid(), userReceive);
		}
		
		infoReturn = "Ban da chan thanh cong sdt " + userReceive +
				" gui tin nhan tu dich vu Chat den ban." +
				" De ket noi voi ban be qua SMS khong gioi han," +
				" soan: CHAT_SDT_Noidung gui 9209 (3000vnd/ngay). DTHT:9244.Chi tiet:http://m.mplus.vn";
		
		msgObject.setUsertext(infoReturn);
		
		dbInsert.sendMT(msgObject);
		
		return null;
	}

}
