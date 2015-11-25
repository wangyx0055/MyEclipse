package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.Util;

public class Information extends QuestionManager
{
	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword, Hashtable services) throws Exception
	{
		Collection messages = new ArrayList();
		try
		{
			String options = keyword.getOptions();
			HashMap _option = new HashMap();
			_option = getParametersAsString(options);
			// System.out.println("option:" + options);
			// MLIST = ((String) _option.get("mlist"));
			String content = getStringfromHashMap(_option, "content",
					"Tin nhan sai cu phap. Soan HOT gui 9209 de tra cuu cac dich vu hot nhat.");
			msgObject.setUsertext(content);
			msgObject.setContenttype(0);
			msgObject.setMsgtype(0);
			messages.add(new MsgObject(msgObject));
			return messages;
		}
		catch (Exception e)
		{
			Util.logger.printStackTrace(e);
		}
		return messages;
	}

	public HashMap getParametersAsString(String params)
	{
		if (params == null)
			return null;
		HashMap _params = new HashMap();
		StringTokenizer tok = new StringTokenizer(params, "&");
		while (tok.hasMoreTokens())
		{
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

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval)
	{
		try
		{
			String temp = ((String) _map.get(_key));
			if (temp == null)
			{
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)
					|| temp == null)
			{
				return _defaultval;
			}
			return temp;
		}
		catch (Exception e)
		{
			return _defaultval;
		}
	}
}
