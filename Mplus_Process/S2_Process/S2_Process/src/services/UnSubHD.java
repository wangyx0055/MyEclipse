package services;

import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class UnSubHD  extends QuestionManager{
	/****
	 * 2010-11-07: PhuongDT: update
	 * ***/
		@Override
		protected Collection getMessages(Properties prop, MsgObject msgObject,
				Keyword keyword,Hashtable services) throws Exception {			
			return DBUtil.UnRegisterServices(msgObject, keyword);
		}
}
