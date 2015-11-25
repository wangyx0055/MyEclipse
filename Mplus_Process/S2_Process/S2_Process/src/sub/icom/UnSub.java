package sub.icom;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class UnSub extends QuestionManager {
/****
 * 2010-11-07: PhuongDT: update
 * ***/
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		/****
		 * Set subCP=1: vi la dich vu tu ICOM
		 * **/
		msgObject.setSubCP(1);
		return DBUtil.UnRegisterServices(msgObject, keyword);
	}
}
