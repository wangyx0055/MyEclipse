package phase2;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class SubPhase2ICom extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {		
	//	System.out.println(" ========================== GO HERE ============================");
		
		msgObject.setSubCP(1); // 0: VMS ; other: ICOM
		
		return DBUtil.RegisterServices(msgObject, keyword,Constants.TYPE_OF_SERVICE_TEXTBASE, services);
		
	}
	

}
