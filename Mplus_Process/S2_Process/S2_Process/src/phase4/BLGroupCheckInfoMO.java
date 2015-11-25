package phase4;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;

public class BLGroupCheckInfoMO extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		String[] arrInfo = msgObject.getUsertext().split(" ");
		
		Ph4Common cmmObj = new Ph4Common();
		msgObject.setAmount(keyword.getAmount());
		
		if(cmmObj.isFreeList(msgObject.getUserid(), "BLN")){
			cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
		}else{
			cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
		}
						
		return null;
		
	}
	
	

}
