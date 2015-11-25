package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.ResultCode;

public class BLGroupMTCharging extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String userText = msgObject.getUsertext();
		if(!userText.startsWith("BLN_CHARGING")) return null;
		
		String[] arrTmp = userText.split(";");
		int memberId = Integer.parseInt(arrTmp[1].split("=")[1]);
		
		int resultCharge = msgObject.getChargeResult(); 
		Ph4Common cmmObj = new Ph4Common();
		
		if(resultCharge == ResultCode.OK ||
				resultCharge == ResultCode.NOK_ACCOUNT_NOT_FOUND) {
			cmmObj.updateChargingMember(memberId, 1);
		}else{
			cmmObj.updateChargingMember(memberId, 0);
		}
		
		
		return null;
	}

}
