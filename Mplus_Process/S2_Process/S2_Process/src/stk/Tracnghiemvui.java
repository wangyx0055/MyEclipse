package stk;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import icom.DBPool;
import icom.Keyword;
import icom.MsgObject;
import icom.QuestionManager;
import icom.common.DBUtil;
import icom.common.Util;

public class Tracnghiemvui extends QuestionManager {

	@Override
	protected Collection getMessages(Properties prop, MsgObject msgObject,
			Keyword keyword,Hashtable services) throws Exception {

		Collection messages = new ArrayList();

		try {
			HashMap _option = new HashMap();

			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String infoid = getStringfromHashMap(_option, "infoid",
					"Tracnghiem");
			Util.logger.info("infoid: " + infoid);
			String poolname = getStringfromHashMap(_option, "pool", "content");

			String subcode1 = getStringfromHashMap(_option, "subcode1", "");
			Util.logger.info("subcode1: " + subcode1);
			String subcode2 = getStringfromHashMap(_option, "subcode2", "");
			Util.logger.info("subcode2: " + subcode2);
			String numcnt = getStringfromHashMap(_option, "numcnt", "9");
			Util.logger.info("numcnt: " + numcnt);
			String typesubcode = getStringfromHashMap(_option, "typesubcode",
					"3");
			Util.logger.info("typesubcode: " + typesubcode);

			String typedb = getStringfromHashMap(_option, "typedb", "1");

			String usertext = msgObject.getUsertext();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");

			int number = Integer.parseInt(numcnt);

			int isub2 = Integer.parseInt(subcode2);

			String ssub1 = "";
			String ssub2 = "";
			
			Util.logger.info("Length: " + sTokens.length);

			if (sTokens.length < isub2) {
				msgObject.setUsertext("Tin nhan sai cu phap.");
				msgObject.setContenttype(0);
				msgObject.setMsgtype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			}

			try {
				ssub2 = sTokens[isub2];
			} catch (Exception e) {
				ssub1 = "";
			} finally {
			}
			
			Util.logger.info("ssub2:" + ssub2);
			String subCode = convertsubcode(ssub2, typesubcode, number);
			Util.logger.info("Subcode: " + subCode);
			String content = getContent6(poolname, typedb, subcode1, subCode,
					infoid);

			if ((content == null) || "".equalsIgnoreCase(content)) {
				return null;
			}

			if ("1".equalsIgnoreCase(typedb)) {
				String[] contents = content.split("###");
				for (int i = 0; i < contents.length; i++) {
					if (!"".equalsIgnoreCase(contents[i])) {
						if (i == 0) {
							msgObject.setMsgtype(1);
						}
						msgObject.setUsertext(contents[i]);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
				}
				return messages;
			}
			msgObject.setUsertext(content);
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
			return messages;

		} catch (Exception e) {
			Util.logger.printStackTrace(e);

		}

		return null;
	}

	public String getContent6(String database, String type, String subcode1,
			String subcode2, String newstypecode) {

		Connection connection = null;
		DBPool dbpool = new DBPool();
		String content = "content_short";
		String strResult = "";

		// Xac dinh bang nao can lay du lieu
		if ("3".equalsIgnoreCase(type)) {
			content = "content_vi";
		} else if ("2".equalsIgnoreCase(type)) {
			content = "content";
		}

		// String query =
		// "SELECT ID,CONTENT FROM icom_textbase_data WHERE upper(gameid) = '"
		// + textbaseid.toUpperCase() + "'";
		String query = "SELECT "
				+ content
				+ " FROM ( SELECT TOP 1 * FROM newsagri_news_info WHERE status=1 AND type=2 AND  upper(newstypecode) = '"
				+ newstypecode.toUpperCase() + "'";

		// query = query + " and order =" + lastcode + " ";
		if (!"".equalsIgnoreCase(subcode1)) {
			query += " AND upper(subcode1)='" + subcode1.toUpperCase() + "'";
		}
		if (!"".equalsIgnoreCase(subcode2)) {
			query += " AND upper(subcode2)='" + subcode2.toUpperCase() + "'";
		}
		query += ")x";

		Util.logger.info("Query Content 6: " + query);

		try {
			connection = dbpool.getConnection(database);
			Vector result = DBUtil.getVectorTable(connection, query);

			Util.logger.info(this.getClass().getName()
					+ "getContent: queryStatement:" + query);

			if (result.size() > 0) {

				// record = (String[]) result.get(0);

				Vector item = (Vector) result.elementAt(0);
				strResult = (String) item.elementAt(0);
				return strResult;
			}

			return strResult;
		} catch (Exception ex) {
			Util.logger.info(this.getClass().getName() + "getContent: Failed"
					+ ex.getMessage());
			Util.logger.printStackTrace(ex);
			return strResult;
		} finally {
			dbpool.cleanup(connection);
		}
	}

	/*
	 * Thay nhieu dau _____ -> _
	 */
	public static String replaceAllWhiteWithOne(String sInput) {
		String strTmp = sInput.trim();
		String strResult = "";
		for (int i = 0; i < strTmp.length(); i++) {
			char ch = strTmp.charAt(i);
			if (ch == ' ') {
				for (int j = i; j < strTmp.length(); j++) {
					char ch2 = strTmp.charAt(j);
					if (ch2 != ' ') {
						i = j;
						strResult = strResult + ' ' + ch2;
						break;
					}
				}
			} else {
				strResult = strResult + ch;
			}

		}
		return strResult;
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

	public String getStringfromHashMap(HashMap _map, String _key,
			String _defaultval) {
		try {
			String temp = ((String) _map.get(_key));

			if (temp == null) {
				return _defaultval;
			}
			if ("null".equalsIgnoreCase(temp) || "".equalsIgnoreCase(temp)) {
				return _defaultval;
			}

			return temp;
		} catch (Exception e) {
			return _defaultval;
		}
	}

	public static String convertsubcode(String subcode, String typesubcode,
			int numcnt) {
		if ("1".equalsIgnoreCase(typesubcode)) {
			return subcode.toUpperCase();
		} else if ("2".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				sum += getNumber(subcode, i);
			}
			if (numcnt == 0) {
				return sum + "";
			} else {

				while (sum > numcnt) {
					String ssum = sum + "";
					sum = 0;
					for (int i = 0; i < ssum.length(); i++) {
						sum += getNumber(ssum, i);
					}
				}
				return sum + "";
			}
		} else if ("3".equalsIgnoreCase(typesubcode)) {
			int sum = 0;
			for (int i = 0; i < subcode.length(); i++) {
				if ((subcode.substring(i, i + 1).equalsIgnoreCase("A"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("J"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("S"))) {
					sum += 1;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("B"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("K"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("T"))) {
					sum += 2;
				}

				else if ((subcode.substring(i, i + 1).equalsIgnoreCase("C"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("L"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("U"))) {
					sum += 3;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("D"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("M"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("V"))) {
					sum += 4;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("E"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("N"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("W"))) {
					sum += 5;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("F"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("O"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("X"))) {
					sum += 6;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("G"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("P"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Y"))) {
					sum += 7;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("H"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Q"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("Z"))) {
					sum += 8;
				} else if ((subcode.substring(i, i + 1).equalsIgnoreCase("I"))
						|| (subcode.substring(i, i + 1).equalsIgnoreCase("R"))) {
					sum += 9;
				} else {
					sum += 0;
				}

			}
			if (numcnt == 0) {
				return sum + "";
			}

			while (sum > numcnt) {
				String ssum = sum + "";
				sum = 0;
				for (int i = 0; i < ssum.length(); i++) {
					sum += getNumber(ssum, i);
				}
			}
			return sum + "";
		}
		return subcode.toUpperCase();
	}

	public static int getNumber(String subcode, int index) {
		try {
			return Integer.parseInt(subcode.substring(index, index + 1));
		} catch (Exception e) {
			return 0;
		}

	}

}
