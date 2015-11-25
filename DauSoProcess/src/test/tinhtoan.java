package test;

import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class tinhtoan extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String message;
		message = msgObject.getUsertext();
		String[] arrInfo = message.split(" ");
		int result = 0;
		String resultString = "";
		try {
			int x = Integer.parseInt(arrInfo[1]);
			int y = Integer.parseInt(arrInfo[3]);
			if ("+".equals(arrInfo[2])) {
				result = x + y;
			} else if ("-".equals(arrInfo[2])) {
				result = x - y;
			} else if ("*".equals(arrInfo[2])) {
				result = x * y;
			} else if ("/".equals(arrInfo[2])) {
				result = x / y;
			}
			
			resultString = resultString + x + arrInfo[2] + y + "=" + result;

		} catch (Exception e) {
			e.printStackTrace();
			resultString = "Tin nhan khong dung cu phap";
		}
		msgObject.setUsertext(resultString);
		msgObject.setMsgtype(1);
		messages.add(new MsgObject(msgObject));
		return messages;
	}

}
