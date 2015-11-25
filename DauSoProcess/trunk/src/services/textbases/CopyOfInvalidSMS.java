package services.textbases;

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

import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class CopyOfInvalidSMS extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		String info = "Tin nhan sai cu phap"
				+ "\n*Tuong thuat TT KQXS: XS<matinh> gui 8551"
				+ "\n*Cai dat GPRS tu dong: GPRS gui 8751"
				+ "\n*Suu tam OMNIA: TE gui 8551"
				+ "\n*Tham gia Sieu Toc: AC gui 8551";
		msgObject.setUsertext(info);
		msgObject.setKeyword(Constants.INV_KEYWORD);
		msgObject.setMsgtype(1);

		messages.add(new MsgObject(msgObject));

		return messages;
	}
}
