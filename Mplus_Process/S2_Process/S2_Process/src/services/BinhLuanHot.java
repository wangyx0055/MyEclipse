package services;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

import phase4.Ph4Common;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

public class BinhLuanHot extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {
		
		msgObject.setAmount(500);
		Ph4Common cmmObj = new Ph4Common();
		
		if(cmmObj.isFreeList(msgObject.getUserid(),msgObject.getCommandCode())){
			cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE_RESULT, msgObject);
		}else{
			cmmObj.insertChargeOnlineNew(Ph4Common.TBL_CHARGE_ONLINE, msgObject);
		}
		
		msgObject.setAmount(keyword.getAmount());
		DBUtil.RegisterServices(msgObject, keyword, Constants.TYPE_OF_SERVICE_TEXTBASE,services);
		
		return null;
	}

}
