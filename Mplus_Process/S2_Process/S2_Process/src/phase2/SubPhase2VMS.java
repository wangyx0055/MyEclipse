package phase2;

import icom.Constants;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

public class SubPhase2VMS extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception {

		msgObject.setSubCP(0); // 0: VMS ; other: ICOM

		String sLast_code = msgObject.getLast_code();

		Collection messages = DBUtil.RegisterServices(msgObject, keyword,
				Constants.TYPE_OF_SERVICE_TEXTBASE, services);

		Iterator iter = messages.iterator();
		for (int i = 1; iter.hasNext(); i++) {

			MsgObject ems = (MsgObject) iter.next();

			if (ems.getCommandCode().toUpperCase().trim().equals("SKB")
					|| ems.getCommandCode().toUpperCase().trim().equals(
							"SKBSKY")) {

				String sOld = "Ban da dang ky thanh cong goi dich vu Suc khoe me va be";
				int iLastCode = -1;
				try {
					iLastCode = Integer.parseInt(sLast_code);
				} catch (Exception ex) {
				}

				if (iLastCode > 0 && iLastCode < 40) {

					String sReplace = sOld + " tuan thai " + iLastCode;
					String info = ems.getUsertext();
					info = info.replaceAll(sOld, sReplace);
					ems.setUsertext(info);

				}

			}

		}

		return messages;
	}

}