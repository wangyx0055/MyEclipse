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

public class teencatinh extends ContentAbstract {

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
			String GAME_ID = "";

			// Lay option
			HashMap _option = new HashMap();
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			GAME_ID = ((String) _option.get("game_id")).toUpperCase();
			Util.logger.info("GAME ID: " + GAME_ID);

			String sKeyword = msgObject.getKeyword();
			String serviceid = msgObject.getServiceid();

			// Lay noi dung khach hang gui
			String usertext = msgObject.getUsertext();
			usertext = replaceAllWhiteWithOne(usertext);
			String[] sTokens = usertext.split(" ");
			if (sTokens.length < 2) {
				msgObject
						.setUsertext("Ban gui tin sai cu phap. De ngay cang tre trung, xinh dep, ca tinh. Soan "
								+ sKeyword + " [Ma so] gui " + serviceid);
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {

				String groupid = sTokens[1];
				Util.logger.info("Ma so: " + groupid);

				// Lay noi dung cua maso
				String content = getContent(GAME_ID, groupid);

				if (!"".equalsIgnoreCase(content)) {
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
				} else {
					msgObject
							.setUsertext("Ma so ban gui khong dung. De ngay cang tre trung, xinh dep, ca tinh. Soan "
									+ sKeyword + " [Ma so] gui " + serviceid);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;
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

	public String getContent(String gameid, String maso) {
		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String query1 = "SELECT content FROM icom_textbase_data WHERE upper(gameid) = '"
					+ gameid.toUpperCase()
					+ "' AND upper(subcode1) = '"
					+ maso.toUpperCase() + "'";
			ps = connection.prepareStatement(query1);
			if (ps.execute()) {
				rs = ps.getResultSet();
				while (rs.next()) {
					content = rs.getString(1);
				}
			}
		} catch (SQLException e) {
			Util.logger.info("Loi o day chu o dau");
			return content;
		} finally {
			dbpool.cleanup(ps);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}

		return content;
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
