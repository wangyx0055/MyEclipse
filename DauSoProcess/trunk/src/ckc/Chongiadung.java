package ckc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.vmg.sms.process.ContentAbstract;
import com.vmg.sms.process.Keyword;
import com.vmg.sms.process.MsgObject;
import com.vmg.sms.common.DBUtil;
import com.vmg.sms.common.Util;
import com.vmg.sms.process.Constants;
import com.vmg.sms.process.DBPool;

public class Chongiadung extends ContentAbstract {

	@Override
	protected Collection getMessages(MsgObject msgObject, Keyword keyword)
			throws Exception {

		Collection messages = new ArrayList();

		try {

			HashMap _option = new HashMap();

			// Get info of client
			String sInfo = msgObject.getUsertext();
			String userText = replaceAllSpaceWithOne(sInfo);
			String userid = msgObject.getUserid();
			String serviceid = msgObject.getServiceid();
			String sKeyword = msgObject.getKeyword();
			Timestamp timesend = msgObject.getTTimes();
			BigDecimal requestid = msgObject.getRequestid();
			String operator = msgObject.getMobileoperator();

			// Get option
			String options = keyword.getOptions();
			_option = getParametersAsString(options);
			String GAME_ID = ((String) _option.get("game_id")).toUpperCase();
			String productid = getContent(GAME_ID);

			String WRONG_SYTAX = "Ban da nhan tin sai cu phap. Vui long soan tin "
					+ sKeyword.toUpperCase()
					+ " "
					+ productid
					+ " Y gui "
					+ serviceid
					+ ".Trong do Y la gia san pham cua ban dau gia.";

			String[] sTokens = userText.split(" ");

			if ("".equalsIgnoreCase(productid)) {
				msgObject
						.setUsertext("Chuong trinh \"Hay chon gia dung \" da het thoi gian tham gia. Ban vui long quay lai sau");
				msgObject.setMsgtype(1);
				msgObject.setContenttype(0);
				messages.add(new MsgObject(msgObject));
				return messages;
			} else {
				// Neu nhan tin sai cu phap
				if (sTokens.length == 1) {

					msgObject.setUsertext(WRONG_SYTAX);
					msgObject.setMsgtype(1);
					msgObject.setContenttype(0);
					messages.add(new MsgObject(msgObject));
					return messages;

				} else if (sTokens.length == 2) {

					if (!productid.equalsIgnoreCase(sTokens[1])) {
						msgObject.setUsertext(WRONG_SYTAX);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;

					} else {

						msgObject
								.setUsertext("Cam on ban da tham gia \"Hay chon gia dung\". Hay giu lai tin nhan de lam can cu khi ban trung thuong. Hay tiep tuc tham gia de co them co hoi may man");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);

						// Luu thong tin khach hang vao icom_daugia
						saveClient(userid, serviceid, sKeyword, operator,
								requestid, timesend, 1, productid);

						messages.add(new MsgObject(msgObject));
						return messages;

					}

				} else {

					// Khach hang nhan tin dung cu phap
					if (!productid.equalsIgnoreCase(sTokens[1])) {
						msgObject.setUsertext(WRONG_SYTAX);
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);
						messages.add(new MsgObject(msgObject));
						return messages;

					} else {
						int price = Integer.parseInt(sTokens[2]);
						msgObject
								.setUsertext("Cam on ban da tham gia \"Hay chon gia dung\". Hay giu lai tin nhan de lam can cu khi ban trung thuong. Hay tiep tuc tham gia de co them co hoi may man");
						msgObject.setMsgtype(1);
						msgObject.setContenttype(0);

						// Luu thong tin khach hang vao icom_daugia
						saveClient(userid, serviceid, sKeyword, operator,
								requestid, timesend, price, productid);

						messages.add(new MsgObject(msgObject));
						return messages;
					}
				}
			}
		} catch (Exception ex) {
			Util.logger.error("Exception : AAAA ");
			ex.printStackTrace();
			msgObject
					.setUsertext("Ban da nhan tin sai cu phap. De tham gia chuong trinh hay chon gia dung. Soan tin "
							+ msgObject.getKeyword()
							+ " X Y gui "
							+ msgObject.getServiceid()
							+ ". Trong do X la ma san pham. Y la gia san pham ma ban chon.");
			msgObject.setMsgtype(1);
			msgObject.setContenttype(0);
			messages.add(new MsgObject(msgObject));
		}
		return messages;
	}

	// Replace ____ with _
	public static String replaceAllSpaceWithOne(String sInput) {
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

	private static boolean saveClient(String userid, String serviceid,
			String keyword, String operator, BigDecimal requestid,
			Timestamp timesend, int price, String productid) {

		Connection connection = null;
		PreparedStatement statement = null;

		DBPool dbpool = new DBPool();
		try {
			connection = dbpool.getConnectionGateway();
			if (connection == null) {
				Util.logger.error("Impossible to connect to DB");
				return false;
			}
			String sqlInsert = "INSERT INTO icom_daugia "
					+ "( userid, serviceid, keyword, operator, requestid, timesend, price, productid)"
					+ "VALUES(?,?,?,?,?,?,?,?)";

			statement = connection.prepareStatement(sqlInsert);
			statement.setString(1, userid);
			statement.setString(2, serviceid);
			statement.setString(3, keyword);
			statement.setString(4, operator);
			statement.setBigDecimal(5, requestid);
			statement.setTimestamp(6, timesend);
			statement.setInt(7, price);
			statement.setString(8, productid);

			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} catch (Exception e) {
			Util.logger.error(": Error:" + e.toString());
			return false;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(connection);
		}
	}

	public String getContent(String gameid) {

		String content = "";
		Connection connection = null;
		DBPool dbpool = new DBPool();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {

			connection = dbpool.getConnectionGateway();
			String sqlSelect = "SELECT content FROM icom_textbase_data WHERE upper(gameid) = '"
					+ gameid.toUpperCase() + "'";
			Util.logger.info("QUERY: " + sqlSelect);
			statement = connection.prepareStatement(sqlSelect);
			if (statement.execute()) {
				rs = statement.getResultSet();

				while (rs.next()) {
					content = rs.getString(1);
					Util.logger.info("Productid: " + content);
					return content;
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
			return content;
		} finally {
			dbpool.cleanup(statement);
			dbpool.cleanup(rs);
			dbpool.cleanup(connection);

		}

		return content;
	}
}
