package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.DBPool;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;

import cs.ExecuteADVCR;

public class boicongiap extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {
		try {

			Collection messages = new ArrayList();
			String reply = "";
			String GAME_ID = "";
			String sKeyword = msgObject.getKeyword();
			String serviceid = msgObject.getServiceid();
			String info = msgObject.getUsertext();
			info = replaceAllWhiteWithOne(info);
			String[] sTokens = info.split(" ");

			// Lay option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			GAME_ID = ((String) _option.get("game_id")).toUpperCase();
			Util.logger.info("GAME ID: " + GAME_ID);

			if (sTokens.length < 2) {
				reply = "Tin nhan ban gui khong dung cu phap. Soan tin "
						+ sKeyword + " [congiap]" + " gui " + serviceid
						+ " . Trong do " + sKeyword
						+ " la chia khoa chuong trinh"
						+ " congiap la ten con giap ma ban cam tinh";
				msgObject.setUsertext(reply);
				msgObject.setMsgtype(1);
				messages.add(new MsgObject(msgObject));
				return messages;

			} else {
				String congiap = sTokens[1];
				int ma = check(congiap);
				Util.logger.info("MA :" + ma);

				if (ma == 0) {

					reply = "Tin nhan ban gui khong dung cu phap. Soan tin "
							+ sKeyword + " [congiap]" + " gui " + serviceid
							+ " . Trong do " + sKeyword
							+ " la tu khoa chuong trinh"
							+ " congiap la ten con giap ma ban cam tinh";
					msgObject.setUsertext(reply);
					msgObject.setMsgtype(1);
					messages.add(new MsgObject(msgObject));

					return messages;
				} else {

					String content = getContent(ma, GAME_ID);
					Util.logger.info("CONTENT: " + content);
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

	public int check(String str) {

		if (str.equalsIgnoreCase("chuot") || str.equalsIgnoreCase("ti"))
			return 1;
		else if (str.equalsIgnoreCase("suu") || str.equalsIgnoreCase("trau"))
			return 2;
		else if (str.equalsIgnoreCase("dan") || str.equalsIgnoreCase("ho"))
			return 3;
		else if (str.equalsIgnoreCase("mao") || str.equalsIgnoreCase("meo")
				|| str.equalsIgnoreCase("tho"))
			return 4;
		else if (str.equalsIgnoreCase("thin") || str.equalsIgnoreCase("rong"))
			return 5;
		else if (str.equalsIgnoreCase("ty") || str.equalsIgnoreCase("ran"))
			return 6;
		else if (str.equalsIgnoreCase("ngo") || str.equalsIgnoreCase("ngua"))
			return 7;
		else if (str.equalsIgnoreCase("mui") || str.equalsIgnoreCase("de"))
			return 8;
		else if (str.equalsIgnoreCase("than") || str.equalsIgnoreCase("khi"))
			return 9;
		else if (str.equalsIgnoreCase("dau") || str.equalsIgnoreCase("ga"))
			return 10;
		else if (str.equalsIgnoreCase("tuat") || str.equalsIgnoreCase("cho"))
			return 11;
		else if (str.equalsIgnoreCase("hoi") || str.equalsIgnoreCase("lon")
				|| str.equalsIgnoreCase("heo"))
			return 12;
		else {
			return 0;
		}
	}

	public String getContent(int ma, String gameid) {
		String maso = "" + ma;
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String sqlSelect = "SELECT content FROM icom_textbase_data WHERE upper(gameid) = '"
					+ gameid.toUpperCase()
					+ "' && upper(subcode1) = '"
					+ maso.toUpperCase() + "'";
			Util.logger.info("QUERY: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();

				while (rs.next()) {
					content = rs.getString(1);
					Util.logger.info("CONTENT: " + content);
				}
			}
		} catch (SQLException e) {

			System.out.println(e);
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}

		return content;
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

			_params.put(key, value);
		}

		return _params;

	}

}
