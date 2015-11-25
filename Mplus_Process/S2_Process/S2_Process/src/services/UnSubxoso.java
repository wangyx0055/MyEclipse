package services;

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

import icom.Keyword;
import icom.MsgObject;
 
import icom.QuestionManager;
 
import icom.common.DBUtil;


import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class UnSubxoso extends QuestionManager {

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