package test;

import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class service1 extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		// TODO Auto-generated method stub
		Collection messages = new ArrayList();
		String message;
		message = msgObject.getUsertext();
		String[] keywords;
		String[] arrInfo = message.split(" ");
		int ketqua = 0;
		String resultString = "";
		try {
			int x = Integer.parseInt(arrInfo[1]);
			int y = Integer.parseInt(arrInfo[3]);
			String z = arrInfo[2];
			if ("+".equals(z)) {
				ketqua = x + y;
			} else if ("-".equals(z)) {
				ketqua = x - y;
			} else if ("*".equals(z)) {
				ketqua = x * y;
			} else if ("/".equals(z)) {
				ketqua = x / y;
			}
			resultString = resultString + x + z + y + "=" + ketqua;

		} catch (Exception e) {
			e.printStackTrace();
			resultString = "Ban nhap khong dung cu phap";
		}
		
		msgObject.setUsertext(resultString);
		msgObject.setMsgtype(1);
		messages.add(new MsgObject(msgObject));
		String mt2 = "De dang ky tu van giam can ban hay nhan tin : GC gui 6754";
		msgObject.setUsertext(mt2);
		msgObject.setMsgtype(0);
		messages.add(new MsgObject(msgObject));
		return messages;

	}
}
