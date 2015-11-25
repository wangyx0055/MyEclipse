package tuannguyen;

import java.util.ArrayList;
import java.util.Collection;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Invalid extends ContentAbstract {

	/**
	 * getMessages.<br>
	 * 
	 * <pre>
	 *
	 * ◆ Processing order
	 *
	 * ◆ Handle exception
	 *   ・
	 * </pre>
	 *
	 * @param msgObject
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String reply = "Cam on ban da su dung dich vu cua chung toi! yeu cau ban chua hop le, ban vui long kiem tra lai cu phap hoac lien he tong dai 1900571594 de duoc huong dan su dung dich vu.";

		msgObject.setUsertext(reply);
		msgObject.setMsgtype(1);
		messages.add(new MsgObject(msgObject));
		return messages;

	}

}
