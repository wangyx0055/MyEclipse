package phase2;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class UnSubPhase2VMS extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {		
		//System.out.println(" ========================== GO HERE ============================");
		
		msgObject.setSubCP(0); // 0: VMS ; other: ICOM
		//msgObject.setPkgService(true);
		
		return DBUtil.UnRegisterServices(msgObject, keyword);
	}
		
}