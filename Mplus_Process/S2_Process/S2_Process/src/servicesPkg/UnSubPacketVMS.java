package servicesPkg;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class UnSubPacketVMS extends QuestionManager{

	private ServiceMng serviceMng = new ServiceMng();
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {		
		//System.out.println(" ========================== GO HERE ============================");
		
		msgObject.setSubCP(0); // 0: VMS ; other: ICOM
		msgObject.setPkgService(true);
		
		return serviceMng.UnRegisterHandle(msgObject, keyword);
	}
		
}