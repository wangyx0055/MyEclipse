package services.textbases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Tamngung extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();
		HashMap _option = new HashMap();
		String options = keyword.getOptions();
		
		String reply = "Cam on ban da su dung dich vu.Dich vu tam ngung hoat dong, vui long quay lai sau.";

		String mobile_operator = "";
		_option = getParametersAsString(options);
		reply = getString(_option, "mt", reply);
		
		mobile_operator = msgObject.getMobileoperator();

		
			msgObject.setUsertext(reply);
			msgObject.setMsgtype(2);
			messages.add(new MsgObject(msgObject));
			
		
		return messages;

	}
	public HashMap getParametersAsString(String params) {
		if (params == null)
			return null;
		HashMap _params = new HashMap();

		StringTokenizer tok = new StringTokenizer(params, "&");

		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			int ix = token.indexOf('=');
			if (ix == -1 || ix == token.length() - 1)
				continue;

			String key = token.substring(0, ix);
			String value = token.substring(ix + 1);

			// setParameter(key, value);
			_params.put(key, value);
		}

		return _params;
	}
	String getString(HashMap _option1, String field, String defaultvalue) {
		try {
			String temp = ((String) _option1.get(field));
			if ((temp == null) || "".equalsIgnoreCase(temp)) {
				return defaultvalue;
			}
			return temp;
		} catch (Exception e) {
			return defaultvalue;
		}
	}



}
