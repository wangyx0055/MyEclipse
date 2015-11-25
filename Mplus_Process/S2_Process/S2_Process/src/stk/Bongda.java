package stk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.Util;

public class Bongda extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		try {

			int msg1mt = Integer.parseInt(Constants.MT_PUSH);
			int msg2mt = Integer.parseInt(Constants.MT_PUSH);

			if (keyword.getService_type() == Constants.PACKAGE_SERVICE) {
				msg1mt = Integer.parseInt(Constants.MT_CHARGING);
				msg2mt = Integer.parseInt(Constants.MT_PUSH);

			} else if (keyword.getService_type() == Constants.DAILY_SERVICE) {
				msg2mt = Integer.parseInt(Constants.MT_CHARGING);
				msg1mt = Integer.parseInt(Constants.MT_NOCHARGE);
			}

			Util.logger.info("MsgText: " + msgObject.getUsertext());
			Util.logger.info("Keyword: " + msgObject.getKeyword());
			Util.logger.info("MsgType: " + msgObject.getMsgtype());
			MsgObject msgObj = msgObject;
			messages.add(new MsgObject(msgObj));
			return messages;

		}

		catch (Exception e) {
			Util.logger.printStackTrace(e);

		}

		return null;
	}

}
