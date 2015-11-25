package ckc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class Timdiemgiamgia extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();
		try {

			String serviceid = msgObject.getServiceid();
			String userid = msgObject.getUserid();
			String usertext = msgObject.getUsertext();
			String sKeyword = msgObject.getKeyword();

			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			String gameid = "";
			String subcode1 = "";
			String subcode2 = "";

			try {

				Util.logger.sysLog(2, this.getClass().getName(), "options: "
						+ options);
				_option = getParametersAsString(options);
				gameid = (String) _option.get("game_id");

			}

			catch (Exception e) {
				Util.logger.sysLog(2, this.getClass().getName(), "Error: "
						+ e.toString());
				throw new Exception("Wrong config in options");
			}

			if ("6157".equalsIgnoreCase(serviceid)) {
				msgObject
						.setUsertext("De tra cuu diem uu dai ban hay soan tin: "
								+ sKeyword
								+ " X Y gui 6357. Trong do X la dia diem ban dang o. Y la linh vuc ban quan tam vi du: cafe, thoi trang...");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				String[] sTokens = usertext.split(" ");
				if (sTokens.length < 3) {
					msgObject
							.setUsertext("Ban da gui tin sai cu phap. De tra cuu diem uu dai ban hay soan tin "
									+ sKeyword + " X Y gui " + serviceid);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
				} else {
					subcode1 = sTokens[1];
					subcode2 = sTokens[2];
					String content = findContent(gameid, subcode1, subcode2);

					if (!"".equalsIgnoreCase(content)) {
						String[] mtsend = content.split("###");
						for (int i = 0; i < mtsend.length; i++) {
							if (!"".equalsIgnoreCase(mtsend[i])) {
								msgObject.setUsertext(mtsend[i]);
								if (i == 0) {
									msgObject.setMsgtype(1);
								} else {
									msgObject.setMsgtype(0);
								}
								msgObject.setContenttype(0);
								messages.add(new MsgObject(msgObject));
							}
						}
					} else {
						msgObject
								.setUsertext("Ban da gui tin sai cu phap. De tra cuu diem uu dai ban hay soan tin "
										+ sKeyword + " X Y gui " + serviceid);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;
					}
				}

			}

		} catch (Exception e) {

			Util.logger.sysLog(2, this.getClass().getName(), "Exception:"
					+ e.getMessage());
			return null;

		} finally {

			ExecuteADVCR.add2queueADV(msgObject.getMsgtype(), msgObject
					.getServiceid(), msgObject.getUserid(), msgObject
					.getKeyword(), msgObject.getRequestid(), msgObject
					.getTTimes(), msgObject.getMobileoperator());
		}
		return null;
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

	// Replace ____ with _
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

	private static String findContent(String gameid, String subcode1,
			String subcode2) {

		String result = "";
		// String musicid = "";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		DBPool dbpool = new DBPool();

		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return result;
			}

			String sqlSelect = "SELECT content FROM icom_textbase_data WHERE ( upper(gameid) = '"
					+ gameid.toUpperCase()
					+ "') AND ( upper(subcode1) = '"
					+ subcode1.toUpperCase()
					+ "') AND ( upper(subcode2) = '"
					+ subcode2.toUpperCase() + "')";

			Util.logger.info("SEARCH DIEM GIAM GIA : " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);

			if (statement.execute()) {
				rs = statement.getResultSet();
				while (rs.next()) {
					result = rs.getString(1);
				}
			}

			return result;
		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return result;
		} finally {
			dbpool.cleanup(rs);
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

}