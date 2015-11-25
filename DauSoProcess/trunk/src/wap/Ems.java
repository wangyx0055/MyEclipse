package wap;

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
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.process.Constants;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

public class Ems extends ContentAbstract {

	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		Collection messages = new ArrayList();

		HashMap _option = new HashMap();

		String options = keyword.getOptions();

		_option = getParametersAsString(options);

		String info = getOption(_option, "info", "0");
		String contenttype = getOption(_option, "contenttype", "0");

		msgObject.setUsertext(info);
		msgObject.setContenttype(Integer.parseInt(contenttype));
		msgObject.setMsgtype(1);

		messages.add(new MsgObject(msgObject));

		return messages;
	}

	String getOption(HashMap _option1, String item, String defaultvalue) {
		try {
			return ((String) _option1.get(item)).toUpperCase();
		} catch (Exception e) {
			// TODO: handle exception
			return defaultvalue;
		}

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
}
