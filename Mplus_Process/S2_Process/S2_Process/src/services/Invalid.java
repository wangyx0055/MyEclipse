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

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;

import java.util.Properties;

public class Invalid extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		String mtret = "Tin nhan chua dung cu phap.Soan tin DK gui 9209 de duoc ho tro ve cu phap cac dich vu khac cua mPlus.De huy dich vu soan HUY madichvu gui 9209.Dien thoai ho tro 18001090.Chi tiet tai www.mplus.vn";

		try {
			mtret = Constants._prop.getProperty("INVALID", "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		msgObject.setMsgtype(Integer.parseInt(Constants.MT_NOCHARGE));
		msgObject.setContenttype(0);
		msgObject.setUsertext(mtret);
		
		messages.add(new MsgObject(msgObject));
		Iterator iter = messages.iterator();
		for (int i = 1; iter.hasNext(); i++) {
			MsgObject msgMT = (MsgObject) iter.next();
			Subgeneric.sendMT2sendqueue(msgMT);
		}
		return null;

	}
}
