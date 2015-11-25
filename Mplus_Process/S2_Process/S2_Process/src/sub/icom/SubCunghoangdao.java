package sub.icom;

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

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class SubCunghoangdao extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {
		return DBUtil.RegisterServices(msgObject, keyword, Constants.TYPE_OF_SERVICE_HOROSCOPE,services);
	}
}