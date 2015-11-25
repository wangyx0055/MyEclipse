package phase2;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class UnSubPhase2ICom extends QuestionManager{

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {		
		
		msgObject.setSubCP(1); // 0: VMS ; other: ICOM		
		return DBUtil.UnRegisterServices(msgObject, keyword);
	}
		
}
