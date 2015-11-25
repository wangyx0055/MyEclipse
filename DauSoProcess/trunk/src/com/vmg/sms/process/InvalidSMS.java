package com.vmg.sms.process;

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

import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class InvalidSMS extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		msgObject.setUsertext(Constants.INV_INFO);
		msgObject.setKeyword(Constants.INV_KEYWORD);
		msgObject.setMsgtype(2);

		messages.add(new MsgObject(msgObject));

		return messages;
	}
}
