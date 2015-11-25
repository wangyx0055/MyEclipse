package ckc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class boikieu extends ContentAbstract {

	/**
	 * getMessages.<br> ◆ Processing order ◆ Handle exception
	 * 
	 * @param msgObject
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		try {
			Collection messages = new ArrayList();
			String reply = "";

			String info = msgObject.getUsertext();
			info = replaceAllWhiteWithOne(info);
			String[] st = info.split(" ");

			// Lay option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String GAME_ID = ((String) _option.get("game_id")).toUpperCase();

			// 
			String sKeyword = msgObject.getKeyword();
			String serviceid = msgObject.getServiceid();

			if (st.length < 3) {
				reply = "Tin nhan ban gui khong dung cu phap. Soan tin "
						+ sKeyword
						+ " [X] [Y] gui "
						+ serviceid
						+ " trong do X la ten nguoi Nu , Y la ten nguoi Nam de nhan ket qua bai boi";
				msgObject.setUsertext(reply);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				String nu = st[1].toUpperCase();
				String nam = st[2].toUpperCase();

				int _nu = getfromname(nu);
				int _nam = getfromname(nam);

				int _queboi = getqueboi(_nu, _nam);
				String queboi = "" + _queboi;

				String content = getContent(queboi, GAME_ID);
				String[] sContent = content.split("###");

				for (int i = 0; i < sContent.length; i++) {
					if (!"".equalsIgnoreCase(sContent[i])) {
						msgObject.setUsertext(sContent[i]);
						if (i == 0) {
							msgObject.setMsgtype(1);
						} else {
							msgObject.setMsgtype(0);
						}
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
					}
				}
			}
			return messages;

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception: "
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}

	}

	// Lay noi dung que boi
	public String getContent(String maque, String gameid) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "select content from icom_textbase_data where upper(gameid) = '"
					+ gameid.toUpperCase() + "' && subcode1 = '" + maque + "'";
			stmt = connection.prepareStatement(query1);
			if (stmt.execute()) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					content = rs.getString(1);
				}
			}
			return content;
		} catch (SQLException e) {
			System.out.println(e);
			return content;
		} finally {
			dbpool.cleanup(stmt);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}
	}

	// Tra ve que boi tuong ung voi ten 2 nguoi
	public int getqueboi(int nu, int nam) {
		int sum = 0;
		String str = "" + (nu + nam);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			sum = sum + Integer.parseInt("" + c);
		}

		while (sum >= 10) {
			str = "" + sum;
			sum = 0;
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				sum = sum + Integer.parseInt("" + c);
			}
		}
		return sum;
	}

	// Tra ve so tuong ung voi ten cua ban
	public int getfromname(String ten) {
		int sum = 0;
		for (int i = 0; i < ten.length(); i++) {
			sum = sum + getfromchar(ten.charAt(i));
		}
		return sum;
	}

	// Tra ve so tuong ung voi mot chu cai
	public int getfromchar(char c) {
		int num = 0;
		switch (c) {
		case 'A':
		case 'J':
		case 'S':
			num = 1;
			break;

		case 'B':
		case 'K':
		case 'T':
			num = 2;
			break;
		case 'C':
		case 'L':
		case 'U':
			num = 3;
			break;
		case 'D':
		case 'M':
		case 'V':
			num = 4;
			break;
		case 'E':
		case 'N':
		case 'W':
			num = 5;
			break;
		case 'F':
		case 'O':
		case 'X':
			num = 6;
			break;
		case 'G':
		case 'P':
		case 'Y':
			num = 7;
			break;
		case 'H':
		case 'Q':
		case 'Z':
			num = 8;
			break;
		case 'I':
		case 'R':
			num = 9;
			break;

		default:
			break;
		}
		return num;

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
}
